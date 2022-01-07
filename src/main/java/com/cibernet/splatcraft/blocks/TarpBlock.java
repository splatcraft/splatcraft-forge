package com.cibernet.splatcraft.blocks;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SixWayBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TarpBlock extends Block implements IWaterLoggable
{

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty UP = SixWayBlock.UP;
    public static final BooleanProperty DOWN = SixWayBlock.DOWN;
    public static final BooleanProperty NORTH = SixWayBlock.NORTH;
    public static final BooleanProperty EAST = SixWayBlock.EAST;
    public static final BooleanProperty SOUTH = SixWayBlock.SOUTH;
    public static final BooleanProperty WEST = SixWayBlock.WEST;
    public static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = SixWayBlock.PROPERTY_BY_DIRECTION.entrySet().stream().collect(Util.toMap());
    private static final VoxelShape UP_AABB = Block.box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape DOWN_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    private static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
    private static final VoxelShape WEST_AABB = Block.box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
    private static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
    private final Map<BlockState, VoxelShape> stateToShapeMap;

    public TarpBlock()
    {
        super(Properties.of(Material.WOOL));
        this.registerDefaultState(defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(false)).setValue(DOWN, Boolean.valueOf(false)).setValue(UP, Boolean.valueOf(false)).setValue(NORTH, Boolean.valueOf(false)).setValue(EAST, Boolean.valueOf(false)).setValue(SOUTH, Boolean.valueOf(false)).setValue(WEST, Boolean.valueOf(false)));
        this.stateToShapeMap = ImmutableMap.copyOf(this.getStateDefinition().getPossibleStates().stream().collect(Collectors.toMap(Function.identity(), TarpBlock::getShapeForState)));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(UP, DOWN, NORTH, SOUTH, WEST, EAST, WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = context.getLevel().getBlockState(context.getClickedPos()).is(this) ? context.getLevel().getBlockState(context.getClickedPos()) :
                super.getStateForPlacement(context).setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);

        state = state.setValue(FACING_TO_PROPERTY_MAP.get(context.getClickedFace().getOpposite()), true);

        for(Direction direction : Direction.values())
            if(state.getValue(FACING_TO_PROPERTY_MAP.get(direction)))
                return state;

        return state.setValue(DOWN, true);
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockItemUseContext context)
    {
        for(Direction direction : Direction.values())
            if(state.getValue(FACING_TO_PROPERTY_MAP.get(direction)))
                return (context.getItemInHand().getItem().equals(asItem()) && !state.getValue(FACING_TO_PROPERTY_MAP.get(context.getClickedFace().getOpposite()))) ? true : state.canBeReplaced(context);
        return true;
    }


    @Override
    public void playerDestroy(World levelIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack)
    {
        player.awardStat(Stats.BLOCK_MINED.get(this));
        player.causeFoodExhaustion(0.005F);

        for(Direction dir : Direction.values())
            if(state.getValue(FACING_TO_PROPERTY_MAP.get(dir)))
                dropResources(state, levelIn, pos, te, player, stack);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context)
    {
        return stateToShapeMap.get(state);
    }

    private static VoxelShape getShapeForState(BlockState state)
    {
        VoxelShape voxelshape = VoxelShapes.empty();

        if (state.getValue(UP))
            voxelshape = UP_AABB;

        if (state.getValue(DOWN))
            voxelshape = VoxelShapes.or(voxelshape, DOWN_AABB);

        if (state.getValue(NORTH))
            voxelshape = VoxelShapes.or(voxelshape, SOUTH_AABB);

        if (state.getValue(SOUTH))
            voxelshape = VoxelShapes.or(voxelshape, NORTH_AABB);

        if (state.getValue(EAST))
            voxelshape = VoxelShapes.or(voxelshape, WEST_AABB);

        if (state.getValue(WEST))
            voxelshape = VoxelShapes.or(voxelshape, EAST_AABB);

        return voxelshape;
    }
}
