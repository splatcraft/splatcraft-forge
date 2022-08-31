package net.splatcraft.forge.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraftforge.common.ToolType;

public class BarrierBarBlock extends Block implements IWaterLoggable
{
    public static final DirectionProperty FACING = HorizontalBlock.FACING;
    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
    public static final EnumProperty<StairsShape> SHAPE = BlockStateProperties.STAIRS_SHAPE;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final AxisAlignedBB STRAIGHT_AABB = new AxisAlignedBB(0, 13 / 16f, 13 / 16f, 1, 1, 1);
    protected static final AxisAlignedBB EDGE_AABB = new AxisAlignedBB(0, 13 / 16f, 13 / 16f, 3 / 16f, 1, 1);
    protected static final AxisAlignedBB ROTATED_STRAIGHT_AABB = modifyShapeForDirection(Direction.EAST, VoxelShapes.create(STRAIGHT_AABB)).bounds();
    protected static final AxisAlignedBB TOP_AABB = new AxisAlignedBB(0, 13 / 16f, 0, 1, 1, 1);

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

    public BarrierBarBlock(String name)
    {
        super(Properties.of(Material.METAL, MaterialColor.NONE).strength(3.0f).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops());
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(HALF, Half.BOTTOM).setValue(SHAPE, StairsShape.STRAIGHT).setValue(WATERLOGGED, false));
        setRegistryName(name);
    }

    protected static VoxelShape modifyShapeForDirection(Direction facing, VoxelShape shape)
    {
        AxisAlignedBB bb = shape.bounds();

        switch (facing)
        {
            case EAST:
                return VoxelShapes.create(new AxisAlignedBB(1 - bb.maxZ, bb.minY, bb.minX, 1 - bb.minZ, bb.maxY, bb.maxX));
            case SOUTH:
                return VoxelShapes.create(new AxisAlignedBB(1 - bb.maxX, bb.minY, 1 - bb.maxZ, 1 - bb.minX, bb.maxY, 1 - bb.minZ));
            case WEST:
                return VoxelShapes.create(new AxisAlignedBB(bb.minZ, bb.minY, 1 - bb.maxX, bb.maxZ, bb.maxY, 1 - bb.minX));
        }
        return shape;
    }

    public static VoxelShape mirrorShapeY(VoxelShape shape)
    {
        AxisAlignedBB bb = shape.bounds();

        return VoxelShapes.create(new AxisAlignedBB(bb.minX, 1 - bb.minY, bb.minZ, bb.maxX, 1 - bb.maxY, bb.maxZ));
    }

    public static VoxelShape mirrorShapeX(VoxelShape shape)
    {
        AxisAlignedBB bb = shape.bounds();

        return VoxelShapes.create(new AxisAlignedBB(1 - bb.minX, bb.minY, bb.minZ, 1 - bb.maxX, bb.maxY, bb.maxZ));
    }

    /**
     * Returns a stair shape property based on the surrounding stairs from the given blockstate and position
     */
    private static StairsShape getShapeProperty(BlockState state, IBlockReader levelIn, BlockPos pos)
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

    private static boolean isDifferentBar(BlockState state, IBlockReader levelIn, BlockPos pos, Direction face)
    {
        BlockState blockstate = levelIn.getBlockState(pos.relative(face));
        return !isBar(blockstate) || blockstate.getValue(FACING) != state.getValue(FACING) || blockstate.getValue(HALF) != state.getValue(HALF);
    }

    public static boolean isBar(BlockState state)
    {
        return state.getBlock() instanceof BarrierBarBlock;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader levelIn, BlockPos pos, ISelectionContext context)
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
                return VoxelShapes.or(shapeArray[dirIndex], shapeArray[rotatedCCWDirIndex]);
            case INNER_RIGHT:
                return VoxelShapes.or(shapeArray[dirIndex], shapeArray[rotatedDirIndex]);
        }

        return Block.box(1, 1, 1, 15, 15, 15);
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state)
    {
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        Direction direction = context.getClickedFace();
        BlockPos blockpos = context.getClickedPos();
        FluidState fluidstate = context.getLevel().getFluidState(blockpos);
        BlockState blockstate = this.defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(HALF, direction != Direction.DOWN && (direction == Direction.UP || !(context.getClickLocation().y - (double) blockpos.getY() > 0.5D)) ? Half.BOTTOM : Half.TOP).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
        return blockstate.setValue(SHAPE, getShapeProperty(blockstate, context.getLevel(), blockpos));
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld levelIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (stateIn.getValue(WATERLOGGED))
        {
            levelIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelIn));
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
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, HALF, SHAPE, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean isPathfindable(BlockState state, IBlockReader levelIn, BlockPos pos, PathType type)
    {
        return false;
    }


}
