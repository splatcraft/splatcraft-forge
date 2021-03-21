package com.cibernet.splatcraft.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class GrateRampBlock extends AbstractSquidPassthroughBlock implements IWaterLoggable
{

    public static final EnumProperty<Direction> FACING = HorizontalBlock.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape START = makeCuboidShape(0, 0, 0, 3, 3, 16);
    private static final VoxelShape END = makeCuboidShape(13, 13, 0, 16, 16, 16);
    private static final VoxelShape SEGMENT = makeCuboidShape(1, 2, 0, 4, 5, 16);
    public static final VoxelShape[] SHAPES = makeVoxelShape(START, END, SEGMENT);

    public GrateRampBlock(String name)
    {
        super(GrateBlock.PROPERTIES);
        setRegistryName(name);
        setDefaultState(getDefaultState().with(FACING, Direction.NORTH).with(WATERLOGGED, false));
    }

    private static VoxelShape[] makeVoxelShape(VoxelShape start, VoxelShape end, VoxelShape segment)
    {
        VoxelShape[] shapes = new VoxelShape[8];

        for (int i = 0; i < 6; i++)
        {
            shapes[i] = segment.withOffset(.125 * i, .125 * i, 0);
        }

        shapes[6] = start;
        shapes[7] = end;

        return createVoxelShapes(shapes);
    }

    protected static VoxelShape modifyShapeForDirection(Direction facing, VoxelShape shape)
    {
        AxisAlignedBB bb = shape.getBoundingBox();

        switch (facing)
        {
            case SOUTH:
                return VoxelShapes.create(new AxisAlignedBB(1 - bb.maxZ, bb.minY, bb.minX, 1 - bb.minZ, bb.maxY, bb.maxX));
            case EAST:
                return VoxelShapes.create(new AxisAlignedBB(1 - bb.maxX, bb.minY, 1 - bb.maxZ, 1 - bb.minX, bb.maxY, 1 - bb.minZ));
            case WEST:
                return VoxelShapes.create(new AxisAlignedBB(bb.minZ, bb.minY, 1 - bb.maxX, bb.maxZ, bb.maxY, 1 - bb.minX));
        }
        return shape;
    }

    protected static VoxelShape[] createVoxelShapes(VoxelShape... shapes)
    {
        VoxelShape[] result = new VoxelShape[4];

        for (int i = 0; i < 4; i++)
        {
            result[i] = VoxelShapes.empty();
            for (VoxelShape shape : shapes)
            {
                result[i] = VoxelShapes.or(result[i], modifyShapeForDirection(Direction.byHorizontalIndex(i), shape));
            }

        }

        return result;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return SHAPES[state.get(FACING).ordinal() - 2];
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockPos blockPos = context.getPos();
        Direction direction = context.getFace();
        FluidState fluidstate = context.getWorld().getFluidState(blockPos);
        boolean flip = direction != Direction.DOWN && (direction == Direction.UP || !(context.getHitVec().y - (double) blockPos.getY() > 0.5D));
        return getDefaultState().with(FACING, flip ? context.getPlacementHorizontalFacing().getOpposite() : context.getPlacementHorizontalFacing()).with(WATERLOGGED, fluidstate.getFluid() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }
}
