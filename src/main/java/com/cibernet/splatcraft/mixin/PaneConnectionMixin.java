package com.cibernet.splatcraft.mixin;

import com.cibernet.splatcraft.tileentities.InkedBlockTileEntity;
import net.minecraft.block.*;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(PaneBlock.class)
public class PaneConnectionMixin
{

    private static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = SixWayBlock.FACING_TO_PROPERTY_MAP.entrySet().stream().filter((facingProperty) -> {
        return facingProperty.getKey().getAxis().isHorizontal();
    }).collect(Util.toMapCollector());

    @Inject(at= @At("TAIL"), method = "updatePostPlacement", cancellable = true)
    private void updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos, CallbackInfoReturnable<BlockState> callback)
    {
        BlockState state = callback.getReturnValue();

        if(worldIn instanceof World)
        {
            callback.setReturnValue(facing.getAxis().getPlane() == Direction.Plane.HORIZONTAL ? state.with(FACING_TO_PROPERTY_MAP.get(facing), state.get(FACING_TO_PROPERTY_MAP.get(facing)) ||
                    Boolean.valueOf(this.canConnect((World) worldIn, facingPos, facing))) : state);
        }
    }

    @Inject(at= @At("TAIL"), method = "getStateForPlacement", cancellable = true)
    private void getStateForPlacement(BlockItemUseContext context, CallbackInfoReturnable<BlockState> callback)
    {
        BlockState state = callback.getReturnValue();

        for(Direction dir : Direction.values())
        {
            if(dir.getAxis().getPlane() == Direction.Plane.VERTICAL)
                continue;

            state = state.with(FACING_TO_PROPERTY_MAP.get(dir), state.get(FACING_TO_PROPERTY_MAP.get(dir)) || this.canConnect(context.getWorld(), context.getPos().offset(dir), dir));
        }

        callback.setReturnValue(state);
    }

    private boolean canConnect(World world, BlockPos pos, Direction direction)
    {

        if(!(world.getTileEntity(pos) instanceof InkedBlockTileEntity))
            return false;

        InkedBlockTileEntity te = (InkedBlockTileEntity) world.getTileEntity(pos);
        BlockState state = te.getSavedState();
        Block block = state.getBlock();

        return !Block.cannotAttach(block) || block instanceof PaneBlock || block.isIn(BlockTags.WALLS);
    }
}
