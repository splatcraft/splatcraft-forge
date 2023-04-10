package net.splatcraft.forge.mixin;

import net.minecraft.world.level.block.FenceBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FenceBlock.class)
public class FenceConnectionMixin
{
    /*
    private static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = SixWayBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter((facingProperty) -> facingProperty.getKey().getAxis().isHorizontal()).collect(Util.toMap());

    @Inject(at = @At("TAIL"), method = "updateShape", cancellable = true)
    private void updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor levelIn, BlockPos currentPos, BlockPos facingPos, CallbackInfoReturnable<BlockState> callback)
    {
        BlockState state = callback.getReturnValue();

        if(levelIn instanceof Level)
        {
            callback.setReturnValue(facing.getAxis().getPlane() == Direction.Plane.HORIZONTAL ? state.setValue(FACING_TO_PROPERTY_MAP.get(facing), state.getValue(FACING_TO_PROPERTY_MAP.get(facing)) || this.canConnect((Level) levelIn, facingPos, facing)) : state);
        }
    }

    @Inject(at = @At("TAIL"), method = "getStateForPlacement", cancellable = true)
    private void getStateForPlacement(BlockPlaceContext context, CallbackInfoReturnable<BlockState> callback)
    {
        BlockState state = callback.getReturnValue();

        for(Direction dir : Direction.values())
        {
            if(dir.getAxis().getPlane() == Direction.Plane.VERTICAL)
                continue;

            state = state.setValue(FACING_TO_PROPERTY_MAP.get(dir), state.getValue(FACING_TO_PROPERTY_MAP.get(dir)) || this.canConnect(context.getLevel(), context.getClickedPos().relative(dir), dir));
        }

        callback.setReturnValue(state);
    }

    private boolean canConnect(Level level, BlockPos pos, Direction direction)
    {

        if(!(level.getBlockEntity(pos) instanceof InkedBlockTileEntity))
            return false;

        InkedBlockTileEntity te = (InkedBlockTileEntity) level.getBlockEntity(pos);
        BlockState state = te.getSavedState();
        Block block = state.getBlock();

        boolean flag = this.isSameFence(block);
        boolean flag1 = block instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(state, direction);
        return !Block.isExceptionForConnection(block) && (flag || flag1);
    }

    @Shadow
    private boolean isSameFence(Block block)
    {
        throw new IllegalStateException("Mixin failed to shadow isWoodenFence()");
    }
    */
}
