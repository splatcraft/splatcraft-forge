package com.cibernet.splatcraft.mixin;

import com.cibernet.splatcraft.tileentities.InkedBlockTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;

@Mixin(WallBlock.class)
public abstract class WallConnectionMixin
{
    @Shadow protected BlockState func_235626_a_(IWorldReader reader, BlockState state, BlockPos pos, BlockState collisionState, boolean connectedSouth, boolean connectedWest, boolean connectedNorth, boolean connectedEast)
    {
        throw new IllegalStateException("Mixin failed to shadow WallBlock#func_235626_a_()");
    }

    @Inject(at= @At("TAIL"), method = "getStateForPlacement", cancellable = true)
    private void getStateForPlacement(BlockItemUseContext context, CallbackInfoReturnable<BlockState> callback)
    {
        BlockState state = callback.getReturnValue();

        BlockState upState = context.getWorld().getBlockState(context.getPos().up());
        HashMap<Direction, Boolean> connectsSide = new HashMap<>();
        connectsSide.put(Direction.NORTH, false);
        connectsSide.put(Direction.SOUTH, false);
        connectsSide.put(Direction.WEST, false);
        connectsSide.put(Direction.EAST, false);

        for(Direction dir : Direction.values())
        {
            if(dir == Direction.DOWN || !(context.getWorld().getTileEntity(context.getPos().offset(dir)) instanceof InkedBlockTileEntity))
                continue;
            InkedBlockTileEntity te = (InkedBlockTileEntity) context.getWorld().getTileEntity(context.getPos().offset(dir));

            if(!te.hasSavedState())
                return;

            BlockState savedState = te.getSavedState();

            if(dir == Direction.UP)
                upState = savedState;
            else connectsSide.put(dir.getOpposite(), shouldConnect(savedState, savedState.isSolidSide(context.getWorld(), te.getPos(), dir.getOpposite()), dir));
        }

        callback.setReturnValue(this.func_235626_a_(context.getWorld(), state, context.getPos(), upState, connectsSide.get(Direction.SOUTH), connectsSide.get(Direction.WEST), connectsSide.get(Direction.NORTH), connectsSide.get(Direction.EAST)));
    }

    @Inject(at= @At("TAIL"), method = "updatePostPlacement", cancellable = true)
    private void updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos, CallbackInfoReturnable<BlockState> callback)
    {
        BlockState state = callback.getReturnValue();

        System.out.println("update " + facingPos + " " + currentPos + " " + worldIn.getTileEntity(facingPos));

        if(facing == Direction.DOWN || !(worldIn.getTileEntity(facingPos) instanceof InkedBlockTileEntity))
            return;
        InkedBlockTileEntity te = (InkedBlockTileEntity) worldIn.getTileEntity(facingPos);

        System.out.println(te.getSavedState());


        BlockState savedState = te.getSavedState();

        System.out.println(facing + "  " + savedState);

        callback.setReturnValue(facing == Direction.UP ? this.func_235625_a_(worldIn, state, facingPos, savedState) : this.func_235627_a_(worldIn, currentPos, state, facingPos, savedState, facing));
    }

    @Shadow protected abstract boolean shouldConnect(BlockState state, boolean sideSolid, Direction direction);

    @Shadow
    private BlockState func_235625_a_(IWorldReader reader, BlockState state1, BlockPos pos, BlockState state2)
    {
        throw new IllegalStateException("Mixin failed to shadow WallBlock#func_235625_a_()");
    }

    @Shadow
    private BlockState func_235627_a_(IWorldReader reader, BlockPos p_235627_2_, BlockState p_235627_3_, BlockPos p_235627_4_, BlockState p_235627_5_, Direction directionIn)
    {
        throw new IllegalStateException("Mixin failed to shadow WallBlock#func_235627_a_()");
    }
}
