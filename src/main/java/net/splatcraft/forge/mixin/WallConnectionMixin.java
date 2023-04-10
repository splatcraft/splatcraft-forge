package net.splatcraft.forge.mixin;

import net.minecraft.world.level.block.WallBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WallBlock.class)
public abstract class WallConnectionMixin
{
    /*
    @Shadow protected abstract BlockState updateShape(LevelReader reader, BlockState state, BlockPos pos, BlockState collisionState, boolean connectedSouth, boolean connectedWest, boolean connectedNorth, boolean connectedEast);

    @Shadow @Final public static EnumProperty<WallHeight> EAST_WALL;

    @Shadow @Final public static EnumProperty<WallHeight> SOUTH_WALL;

    @Shadow @Final public static EnumProperty<WallHeight> WEST_WALL;

    @Shadow @Final public static EnumProperty<WallHeight> NORTH_WALL;

    @Inject(at = @At("TAIL"), method = "sideUpdate", cancellable = true)
    private void sideUpdate(LevelReader reader, BlockPos pos, BlockState state, BlockPos facingPos, BlockState facingState, Direction facing, CallbackInfoReturnable<BlockState> callback)
    {

        if(reader instanceof Level)
        {
            state = callback.getReturnValue();
            boolean flag = facing == Direction.NORTH ? hasHeightForProperty(state, NORTH_WALL) || connectsTo((Level) reader, facingPos, facing) : hasHeightForProperty(state, NORTH_WALL);
            boolean flag1 = facing == Direction.EAST ? hasHeightForProperty(state, EAST_WALL) ||  connectsTo((Level) reader, facingPos, facing) : hasHeightForProperty(state, EAST_WALL);
            boolean flag2 = facing == Direction.SOUTH ? hasHeightForProperty(state, SOUTH_WALL) ||  connectsTo((Level) reader, facingPos, facing) : hasHeightForProperty(state, SOUTH_WALL);
            boolean flag3 = facing == Direction.WEST ? hasHeightForProperty(state, WEST_WALL) ||  connectsTo((Level) reader, facingPos, facing) : hasHeightForProperty(state, WEST_WALL);


            BlockPos up = pos.above();
            BlockState blockstate = reader.getBlockState(up);
            callback.setReturnValue(updateShape(reader, state, up, blockstate, flag, flag1, flag2, flag3));
        }
    }

    private boolean connectsTo(Level level, BlockPos pos, Direction direction)
    {

        if(!(level.getBlockEntity(pos) instanceof InkedBlockTileEntity))
            return false;

        InkedBlockTileEntity te = (InkedBlockTileEntity) level.getBlockEntity(pos);
        BlockState state = te.getSavedState();
        Block block = state.getBlock();
        boolean sideSolid = state.isFaceSturdy(level, pos, direction);

        boolean flag = block instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(state, direction);
        return state.is(BlockTags.WALLS) || !isExceptionForConnection(block) && sideSolid || block instanceof PaneBlock || flag;
    }


    private static boolean hasHeightForProperty(BlockState state, Property<WallHeight> heightProperty) {
        return state.getValue(heightProperty) != WallHeight.NONE;
    }



     */

}
