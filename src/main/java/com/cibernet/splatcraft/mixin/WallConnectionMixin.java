package com.cibernet.splatcraft.mixin;

import com.cibernet.splatcraft.tileentities.InkedBlockTileEntity;
import net.minecraft.block.*;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.block.WallBlock.*;

@Mixin(WallBlock.class)
public abstract class WallConnectionMixin
{
    @Shadow protected abstract boolean shouldConnect(BlockState state, boolean sideSolid, Direction direction);

    @Shadow protected abstract BlockState func_235626_a_(IWorldReader reader, BlockState state, BlockPos pos, BlockState collisionState, boolean connectedSouth, boolean connectedWest, boolean connectedNorth, boolean connectedEast);

    @Shadow @Final public static EnumProperty<WallHeight> WALL_HEIGHT_EAST;

    @Shadow @Final public static EnumProperty<WallHeight> WALL_HEIGHT_SOUTH;

    @Shadow @Final public static EnumProperty<WallHeight> WALL_HEIGHT_WEST;

    @Inject(at=@At("TAIL"), method = "func_235627_a_", cancellable = true)
    private void func_235627_a_(IWorldReader reader, BlockPos pos, BlockState state, BlockPos facingPos, BlockState facingState, Direction facing, CallbackInfoReturnable<BlockState> callback)
    {

        if(reader instanceof World)
        {
            state = callback.getReturnValue();
            boolean flag = facing == Direction.NORTH ? hasHeightForProperty(state, WALL_HEIGHT_NORTH) || canConnect((World) reader, facingPos, facing) : hasHeightForProperty(state, WALL_HEIGHT_NORTH);
            boolean flag1 = facing == Direction.EAST ? hasHeightForProperty(state, WALL_HEIGHT_EAST) ||  canConnect((World) reader, facingPos, facing) : hasHeightForProperty(state, WALL_HEIGHT_EAST);
            boolean flag2 = facing == Direction.SOUTH ? hasHeightForProperty(state, WALL_HEIGHT_SOUTH) ||  canConnect((World) reader, facingPos, facing) : hasHeightForProperty(state, WALL_HEIGHT_SOUTH);
            boolean flag3 = facing == Direction.WEST ? hasHeightForProperty(state, WALL_HEIGHT_WEST) ||  canConnect((World) reader, facingPos, facing) : hasHeightForProperty(state, WALL_HEIGHT_WEST);


            BlockPos up = pos.up();
            BlockState blockstate = reader.getBlockState(up);
            callback.setReturnValue(func_235626_a_(reader, state, up, blockstate, flag, flag1, flag2, flag3));
        }
    }


    private boolean canConnect(World world, BlockPos pos, Direction direction)
    {

        if(!(world.getTileEntity(pos) instanceof InkedBlockTileEntity))
            return false;

        InkedBlockTileEntity te = (InkedBlockTileEntity) world.getTileEntity(pos);
        BlockState state = te.getSavedState();
        Block block = state.getBlock();
        boolean sideSolid = state.isSolidSide(world, pos, direction);

        boolean flag = block instanceof FenceGateBlock && FenceGateBlock.isParallel(state, direction);
        return state.isIn(BlockTags.WALLS) || !cannotAttach(block) && sideSolid || block instanceof PaneBlock || flag;
    }


    private static boolean hasHeightForProperty(BlockState state, Property<WallHeight> heightProperty) {
        return state.get(heightProperty) != WallHeight.NONE;
    }



}
