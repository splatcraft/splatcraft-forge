package com.cibernet.splatcraft.blocks;

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

public class BarrierBarBlock extends Block implements IWaterLoggable {
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
    public static final EnumProperty<StairsShape> SHAPE = BlockStateProperties.STAIRS_SHAPE;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final AxisAlignedBB STRAIGHT_AABB = new AxisAlignedBB(0, 13 / 16f, 13 / 16f, 1, 1, 1);
    protected static final AxisAlignedBB EDGE_AABB = new AxisAlignedBB(0, 13 / 16f, 13 / 16f, 3 / 16f, 1, 1);
    protected static final AxisAlignedBB ROTATED_STRAIGHT_AABB = modifyShapeForDirection(Direction.EAST, VoxelShapes.create(STRAIGHT_AABB)).getBoundingBox();
    protected static final AxisAlignedBB TOP_AABB = new AxisAlignedBB(0, 13 / 16f, 0, 1, 1, 1);

    protected static final VoxelShape NU_STRAIGHT = Block.makeCuboidShape(0, 13, 0, 16, 16, 3);
    protected static final VoxelShape SU_STRAIGHT = modifyShapeForDirection(Direction.SOUTH, NU_STRAIGHT);
    protected static final VoxelShape WU_STRAIGHT = modifyShapeForDirection(Direction.WEST, NU_STRAIGHT);
    protected static final VoxelShape EU_STRAIGHT = modifyShapeForDirection(Direction.EAST, NU_STRAIGHT);
    protected static final VoxelShape ND_STRAIGHT = mirrorShapeY(NU_STRAIGHT);
    protected static final VoxelShape SD_STRAIGHT = mirrorShapeY(SU_STRAIGHT);
    protected static final VoxelShape WD_STRAIGHT = mirrorShapeY(WU_STRAIGHT);
    protected static final VoxelShape ED_STRAIGHT = mirrorShapeY(EU_STRAIGHT);


    protected static final VoxelShape NU_CORNER = Block.makeCuboidShape(0, 13, 0, 3, 16, 3);
    protected static final VoxelShape SU_CORNER = modifyShapeForDirection(Direction.SOUTH, NU_CORNER);
    protected static final VoxelShape WU_CORNER = modifyShapeForDirection(Direction.WEST, NU_CORNER);
    protected static final VoxelShape EU_CORNER = modifyShapeForDirection(Direction.EAST, NU_CORNER);
    protected static final VoxelShape ND_CORNER = mirrorShapeY(NU_CORNER);
    protected static final VoxelShape SD_CORNER = mirrorShapeY(SU_CORNER);
    protected static final VoxelShape WD_CORNER = mirrorShapeY(WU_CORNER);
    protected static final VoxelShape ED_CORNER = mirrorShapeY(EU_CORNER);

    protected static final VoxelShape[] TOP_SHAPES = new VoxelShape[]{NU_STRAIGHT, SU_STRAIGHT, WU_STRAIGHT, EU_STRAIGHT, NU_CORNER, SU_CORNER, WU_CORNER, EU_CORNER};
    protected static final VoxelShape[] BOTTOM_SHAPES = new VoxelShape[]{ND_STRAIGHT, SD_STRAIGHT, WD_STRAIGHT, ED_STRAIGHT, ND_CORNER, SD_CORNER, WD_CORNER, ED_CORNER};

    public BarrierBarBlock(String name) {
        super(Properties.create(Material.IRON, MaterialColor.AIR).hardnessAndResistance(3.0f).harvestTool(ToolType.PICKAXE).setRequiresTool());
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(HALF, Half.BOTTOM).with(SHAPE, StairsShape.STRAIGHT).with(WATERLOGGED, false));
        setRegistryName(name);
    }


    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        VoxelShape[] shapeArray = state.get(HALF).equals(Half.TOP) ? TOP_SHAPES : BOTTOM_SHAPES;
        int dirIndex = state.get(FACING).ordinal() - 2;
        int rotatedDirIndex = state.get(FACING).rotateY().ordinal() - 2;
        int rotatedCCWDirIndex = state.get(FACING).rotateYCCW().ordinal() - 2;

        switch (state.get(SHAPE)) {
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

        return Block.makeCuboidShape(1, 1, 1, 15, 15, 15);
    }

    @Override
    public boolean isTransparent(BlockState state) {
        return true;
    }

    protected static VoxelShape modifyShapeForDirection(Direction facing, VoxelShape shape) {
        AxisAlignedBB bb = shape.getBoundingBox();

        switch (facing) {
            case EAST:
                return VoxelShapes.create(new AxisAlignedBB(1 - bb.maxZ, bb.minY, bb.minX, 1 - bb.minZ, bb.maxY, bb.maxX));
            case SOUTH:
                return VoxelShapes.create(new AxisAlignedBB(1 - bb.maxX, bb.minY, 1 - bb.maxZ, 1 - bb.minX, bb.maxY, 1 - bb.minZ));
            case WEST:
                return VoxelShapes.create(new AxisAlignedBB(bb.minZ, bb.minY, 1 - bb.maxX, bb.maxZ, bb.maxY, 1 - bb.minX));
        }
        return shape;
    }

    public static VoxelShape mirrorShapeY(VoxelShape shape) {
        AxisAlignedBB bb = shape.getBoundingBox();

        return VoxelShapes.create(new AxisAlignedBB(bb.minX, 1 - bb.minY, bb.minZ, bb.maxX, 1 - bb.maxY, bb.maxZ));
    }

    public static VoxelShape mirrorShapeX(VoxelShape shape) {
        AxisAlignedBB bb = shape.getBoundingBox();

        return VoxelShapes.create(new AxisAlignedBB(1 - bb.minX, bb.minY, bb.minZ, 1 - bb.maxX, bb.maxY, bb.maxZ));
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction direction = context.getFace();
        BlockPos blockpos = context.getPos();
        FluidState fluidstate = context.getWorld().getFluidState(blockpos);
        BlockState blockstate = this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing()).with(HALF, direction != Direction.DOWN && (direction == Direction.UP || !(context.getHitVec().y - (double) blockpos.getY() > 0.5D)) ? Half.BOTTOM : Half.TOP).with(WATERLOGGED, fluidstate.getFluid() == Fluids.WATER);
        return blockstate.with(SHAPE, getShapeProperty(blockstate, context.getWorld(), blockpos));
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.get(WATERLOGGED)) {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }

        return facing.getAxis().isHorizontal() ? stateIn.with(SHAPE, getShapeProperty(stateIn, worldIn, currentPos)) : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    /**
     * Returns a stair shape property based on the surrounding stairs from the given blockstate and position
     */
    private static StairsShape getShapeProperty(BlockState state, IBlockReader worldIn, BlockPos pos) {
        Direction direction = state.get(FACING);
        BlockState blockstate = worldIn.getBlockState(pos.offset(direction));
        if (isBar(blockstate) && state.get(HALF) == blockstate.get(HALF)) {
            Direction direction1 = blockstate.get(FACING);
            if (direction1.getAxis() != state.get(FACING).getAxis() && isDifferentBar(state, worldIn, pos, direction1.getOpposite())) {
                if (direction1 == direction.rotateYCCW()) {
                    return StairsShape.OUTER_LEFT;
                }

                return StairsShape.OUTER_RIGHT;
            }
        }

        BlockState blockstate1 = worldIn.getBlockState(pos.offset(direction.getOpposite()));
        if (isBar(blockstate1) && state.get(HALF) == blockstate1.get(HALF)) {
            Direction direction2 = blockstate1.get(FACING);
            if (direction2.getAxis() != state.get(FACING).getAxis() && isDifferentBar(state, worldIn, pos, direction2)) {
                if (direction2 == direction.rotateYCCW()) {
                    return StairsShape.INNER_LEFT;
                }

                return StairsShape.INNER_RIGHT;
            }
        }

        return StairsShape.STRAIGHT;
    }

    private static boolean isDifferentBar(BlockState state, IBlockReader worldIn, BlockPos pos, Direction face) {
        BlockState blockstate = worldIn.getBlockState(pos.offset(face));
        return !isBar(blockstate) || blockstate.get(FACING) != state.get(FACING) || blockstate.get(HALF) != state.get(HALF);
    }

    public static boolean isBar(BlockState state) {
        return state.getBlock() instanceof BarrierBarBlock;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        Direction direction = state.get(FACING);
        StairsShape stairsshape = state.get(SHAPE);
        switch (mirrorIn) {
            case LEFT_RIGHT:
                if (direction.getAxis() == Direction.Axis.Z) {
                    switch (stairsshape) {
                        case INNER_LEFT:
                            return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.INNER_RIGHT);
                        case INNER_RIGHT:
                            return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.INNER_LEFT);
                        case OUTER_LEFT:
                            return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.OUTER_RIGHT);
                        case OUTER_RIGHT:
                            return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.OUTER_LEFT);
                        default:
                            return state.rotate(Rotation.CLOCKWISE_180);
                    }
                }
                break;
            case FRONT_BACK:
                if (direction.getAxis() == Direction.Axis.X) {
                    switch (stairsshape) {
                        case INNER_LEFT:
                            return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.INNER_LEFT);
                        case INNER_RIGHT:
                            return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.INNER_RIGHT);
                        case OUTER_LEFT:
                            return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.OUTER_RIGHT);
                        case OUTER_RIGHT:
                            return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.OUTER_LEFT);
                        case STRAIGHT:
                            return state.rotate(Rotation.CLOCKWISE_180);
                    }
                }
        }

        return super.mirror(state, mirrorIn);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF, SHAPE, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }


}
