package net.splatcraft.forge.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class GrateRampBlock extends Block implements IWaterLoggable
{

    public static final EnumProperty<Direction> FACING = HorizontalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape START = box(0, 0, 0, 3, 3, 16);
    private static final VoxelShape END = box(13, 13, 0, 16, 16, 16);
    private static final VoxelShape SEGMENT = box(1, 2, 0, 4, 5, 16);
    public static final VoxelShape[] SHAPES = makeVoxelShape();

    public GrateRampBlock(String name)
    {
        super(GrateBlock.PROPERTIES);
        setRegistryName(name);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    private static VoxelShape[] makeVoxelShape()
    {
        VoxelShape[] shapes = new VoxelShape[8];

        for (int i = 0; i < 6; i++)
        {
            shapes[i] = GrateRampBlock.SEGMENT.move(.125 * i, .125 * i, 0);
        }

        shapes[6] = GrateRampBlock.START;
        shapes[7] = GrateRampBlock.END;

        return createVoxelShapes(shapes);
    }

    protected static VoxelShape modifyShapeForDirection(Direction facing, VoxelShape shape)
    {
        AxisAlignedBB bb = shape.bounds();

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
                result[i] = VoxelShapes.or(result[i], modifyShapeForDirection(Direction.from2DDataValue(i), shape));
            }

        }

        return result;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull IBlockReader levelIn, @NotNull BlockPos pos, @NotNull ISelectionContext context)
    {
        return SHAPES[state.getValue(FACING).ordinal() - 2];
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockPos blockPos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        FluidState fluidstate = context.getLevel().getFluidState(blockPos);
        boolean flip = direction != Direction.DOWN && (direction == Direction.UP || !(context.getClickLocation().y - (double) blockPos.getY() <= 0.5D));
        return defaultBlockState().setValue(FACING, flip ? context.getHorizontalDirection().getOpposite() : context.getHorizontalDirection()).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state)
    {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean isPathfindable(@NotNull BlockState state, @NotNull IBlockReader levelIn, @NotNull BlockPos pos, @NotNull PathType type) {
        return false;
    }
}
