package net.splatcraft.forge.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BarrierBarBlock extends Block implements SimpleWaterloggedBlock
{
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
    public static final EnumProperty<StairsShape> SHAPE = BlockStateProperties.STAIRS_SHAPE;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final AABB STRAIGHT_AABB = new AABB(0, 13 / 16f, 13 / 16f, 1, 1, 1);
    protected static final AABB EDGE_AABB = new AABB(0, 13 / 16f, 13 / 16f, 3 / 16f, 1, 1);
    protected static final AABB ROTATED_STRAIGHT_AABB = modifyShapeForDirection(Direction.EAST, Shapes.create(STRAIGHT_AABB)).bounds();
    protected static final AABB TOP_AABB = new AABB(0, 13 / 16f, 0, 1, 1, 1);

    protected static final VoxelShape NU_STRAIGHT = Block.box(0, 13, 0, 16, 16, 3);
    protected static final VoxelShape SU_STRAIGHT = modifyShapeForDirection(Direction.SOUTH, NU_STRAIGHT);
    protected static final VoxelShape WU_STRAIGHT = modifyShapeForDirection(Direction.WEST, NU_STRAIGHT);
    protected static final VoxelShape EU_STRAIGHT = modifyShapeForDirection(Direction.EAST, NU_STRAIGHT);
    protected static final VoxelShape ND_STRAIGHT = mirrorShapeY(NU_STRAIGHT);
    protected static final VoxelShape SD_STRAIGHT = mirrorShapeY(SU_STRAIGHT);
    protected static final VoxelShape WD_STRAIGHT = mirrorShapeY(WU_STRAIGHT);
    protected static final VoxelShape ED_STRAIGHT = mirrorShapeY(EU_STRAIGHT);


    protected static final VoxelShape NU_CORNER = Block.box(0, 13, 0, 3, 16, 3);
    protected static final VoxelShape SU_CORNER = modifyShapeForDirection(Direction.SOUTH, NU_CORNER);
    protected static final VoxelShape WU_CORNER = modifyShapeForDirection(Direction.WEST, NU_CORNER);
    protected static final VoxelShape EU_CORNER = modifyShapeForDirection(Direction.EAST, NU_CORNER);
    protected static final VoxelShape ND_CORNER = mirrorShapeY(NU_CORNER);
    protected static final VoxelShape SD_CORNER = mirrorShapeY(SU_CORNER);
    protected static final VoxelShape WD_CORNER = mirrorShapeY(WU_CORNER);
    protected static final VoxelShape ED_CORNER = mirrorShapeY(EU_CORNER);

    protected static final VoxelShape[] TOP_SHAPES = new VoxelShape[]{NU_STRAIGHT, SU_STRAIGHT, WU_STRAIGHT, EU_STRAIGHT, NU_CORNER, SU_CORNER, WU_CORNER, EU_CORNER};
    protected static final VoxelShape[] BOTTOM_SHAPES = new VoxelShape[]{ND_STRAIGHT, SD_STRAIGHT, WD_STRAIGHT, ED_STRAIGHT, ND_CORNER, SD_CORNER, WD_CORNER, ED_CORNER};

    public BarrierBarBlock()
    {
        super(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.NONE).strength(3.0f).requiresCorrectToolForDrops());
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(HALF, Half.BOTTOM).setValue(SHAPE, StairsShape.STRAIGHT).setValue(WATERLOGGED, false));
    }

    protected static VoxelShape modifyShapeForDirection(Direction facing, VoxelShape shape)
    {
        AABB bb = shape.bounds();

        switch (facing)
        {
            case EAST:
                return Shapes.create(new AABB(1 - bb.maxZ, bb.minY, bb.minX, 1 - bb.minZ, bb.maxY, bb.maxX));
            case SOUTH:
                return Shapes.create(new AABB(1 - bb.maxX, bb.minY, 1 - bb.maxZ, 1 - bb.minX, bb.maxY, 1 - bb.minZ));
            case WEST:
                return Shapes.create(new AABB(bb.minZ, bb.minY, 1 - bb.maxX, bb.maxZ, bb.maxY, 1 - bb.minX));
        }
        return shape;
    }

    public static VoxelShape mirrorShapeY(VoxelShape shape)
    {
        AABB bb = shape.bounds();

        return Shapes.create(new AABB(bb.minX, 1 - bb.minY, bb.minZ, bb.maxX, 1 - bb.maxY, bb.maxZ));
    }

    public static VoxelShape mirrorShapeX(VoxelShape shape)
    {
        AABB bb = shape.bounds();

        return Shapes.create(new AABB(1 - bb.minX, bb.minY, bb.minZ, 1 - bb.maxX, bb.maxY, bb.maxZ));
    }

    /**
     * Returns a stair shape property based on the surrounding stairs from the given blockstate and position
     */
    private static StairsShape getShapeProperty(BlockState state, BlockGetter levelIn, BlockPos pos)
    {
        Direction direction = state.getValue(FACING);
        BlockState blockstate = levelIn.getBlockState(pos.relative(direction));
        if (isBar(blockstate) && state.getValue(HALF) == blockstate.getValue(HALF))
        {
            Direction direction1 = blockstate.getValue(FACING);
            if (direction1.getAxis() != state.getValue(FACING).getAxis() && isDifferentBar(state, levelIn, pos, direction1.getOpposite()))
            {
                if (direction1 == direction.getCounterClockWise())
                {
                    return StairsShape.OUTER_LEFT;
                }

                return StairsShape.OUTER_RIGHT;
            }
        }

        BlockState blockstate1 = levelIn.getBlockState(pos.relative(direction.getOpposite()));
        if (isBar(blockstate1) && state.getValue(HALF) == blockstate1.getValue(HALF))
        {
            Direction direction2 = blockstate1.getValue(FACING);
            if (direction2.getAxis() != state.getValue(FACING).getAxis() && isDifferentBar(state, levelIn, pos, direction2))
            {
                if (direction2 == direction.getCounterClockWise())
                {
                    return StairsShape.INNER_LEFT;
                }

                return StairsShape.INNER_RIGHT;
            }
        }

        return StairsShape.STRAIGHT;
    }

    private static boolean isDifferentBar(BlockState state, BlockGetter levelIn, BlockPos pos, Direction face)
    {
        BlockState blockstate = levelIn.getBlockState(pos.relative(face));
        return !isBar(blockstate) || blockstate.getValue(FACING) != state.getValue(FACING) || blockstate.getValue(HALF) != state.getValue(HALF);
    }

    public static boolean isBar(BlockState state)
    {
        return state.getBlock() instanceof BarrierBarBlock;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter levelIn, BlockPos pos, CollisionContext context)
    {
        VoxelShape[] shapeArray = state.getValue(HALF).equals(Half.TOP) ? TOP_SHAPES : BOTTOM_SHAPES;
        int dirIndex = state.getValue(FACING).ordinal() - 2;
        int rotatedDirIndex = state.getValue(FACING).getClockWise().ordinal() - 2;
        int rotatedCCWDirIndex = state.getValue(FACING).getCounterClockWise().ordinal() - 2;

        switch (state.getValue(SHAPE))
        {
            case STRAIGHT:
                return shapeArray[dirIndex];
            case OUTER_LEFT:
                return shapeArray[dirIndex + 4];
            case OUTER_RIGHT:
                return shapeArray[rotatedDirIndex + 4];
            case INNER_LEFT:
                return Shapes.or(shapeArray[dirIndex], shapeArray[rotatedCCWDirIndex]);
            case INNER_RIGHT:
                return Shapes.or(shapeArray[dirIndex], shapeArray[rotatedDirIndex]);
        }

        return Block.box(1, 1, 1, 15, 15, 15);
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state)
    {
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Direction direction = context.getClickedFace();
        BlockPos blockpos = context.getClickedPos();
        FluidState fluidstate = context.getLevel().getFluidState(blockpos);
        BlockState blockstate = this.defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(HALF, direction != Direction.DOWN && (direction == Direction.UP || !(context.getClickLocation().y - (double) blockpos.getY() > 0.5D)) ? Half.BOTTOM : Half.TOP).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
        return blockstate.setValue(SHAPE, getShapeProperty(blockstate, context.getLevel(), blockpos));
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor levelIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (stateIn.getValue(WATERLOGGED))
        {
            levelIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelIn));
        }

        return facing.getAxis().isHorizontal() ? stateIn.setValue(SHAPE, getShapeProperty(stateIn, levelIn, currentPos)) : super.updateShape(stateIn, facing, facingState, levelIn, currentPos, facingPos);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn)
    {
        Direction direction = state.getValue(FACING);
        StairsShape stairsshape = state.getValue(SHAPE);
        switch (mirrorIn)
        {
            case LEFT_RIGHT:
                if (direction.getAxis() == Direction.Axis.Z)
                {
                    switch (stairsshape)
                    {
                        case INNER_LEFT:
                            return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_RIGHT);
                        case INNER_RIGHT:
                            return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_LEFT);
                        case OUTER_LEFT:
                            return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_RIGHT);
                        case OUTER_RIGHT:
                            return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_LEFT);
                        default:
                            return state.rotate(Rotation.CLOCKWISE_180);
                    }
                }
                break;
            case FRONT_BACK:
                if (direction.getAxis() == Direction.Axis.X)
                {
                    switch (stairsshape)
                    {
                        case INNER_LEFT:
                            return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_LEFT);
                        case INNER_RIGHT:
                            return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_RIGHT);
                        case OUTER_LEFT:
                            return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_RIGHT);
                        case OUTER_RIGHT:
                            return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_LEFT);
                        case STRAIGHT:
                            return state.rotate(Rotation.CLOCKWISE_180);
                    }
                }
        }

        return super.mirror(state, mirrorIn);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, HALF, SHAPE, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter levelIn, BlockPos pos, PathComputationType type)
    {
        return false;
    }


}
