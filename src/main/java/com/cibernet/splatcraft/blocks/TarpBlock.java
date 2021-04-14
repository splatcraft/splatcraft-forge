package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.registries.SplatcraftGameRules;
import com.cibernet.splatcraft.tileentities.CrateTileEntity;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
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
    public static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = SixWayBlock.FACING_TO_PROPERTY_MAP.entrySet().stream().collect(Util.toMapCollector());
    private static final VoxelShape UP_AABB = Block.makeCuboidShape(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape DOWN_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    private static final VoxelShape EAST_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
    private static final VoxelShape WEST_AABB = Block.makeCuboidShape(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape SOUTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
    private static final VoxelShape NORTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
    private final Map<BlockState, VoxelShape> stateToShapeMap;

    public TarpBlock()
    {
        super(Properties.create(Material.WOOL));
        this.setDefaultState(getDefaultState().with(WATERLOGGED, Boolean.valueOf(false)).with(DOWN, Boolean.valueOf(false)).with(UP, Boolean.valueOf(false)).with(NORTH, Boolean.valueOf(false)).with(EAST, Boolean.valueOf(false)).with(SOUTH, Boolean.valueOf(false)).with(WEST, Boolean.valueOf(false)));
        this.stateToShapeMap = ImmutableMap.copyOf(this.stateContainer.getValidStates().stream().collect(Collectors.toMap(Function.identity(), TarpBlock::getShapeForState)));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(UP, DOWN, NORTH, SOUTH, WEST, EAST, WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = context.getWorld().getBlockState(context.getPos()).isIn(this) ? context.getWorld().getBlockState(context.getPos()) :
                super.getStateForPlacement(context).with(WATERLOGGED, context.getWorld().getFluidState(context.getPos()).getFluid() == Fluids.WATER);

        state = state.with(FACING_TO_PROPERTY_MAP.get(context.getFace().getOpposite()), true);

        for(Direction direction : Direction.values())
            if(state.get(FACING_TO_PROPERTY_MAP.get(direction)))
                return state;

        return state.with(DOWN, true);
    }

    @Override
    public boolean isReplaceable(BlockState state, BlockItemUseContext context)
    {
        for(Direction direction : Direction.values())
            if(state.get(FACING_TO_PROPERTY_MAP.get(direction)))
                return (context.getItem().getItem().equals(asItem()) && !state.get(FACING_TO_PROPERTY_MAP.get(context.getFace().getOpposite()))) ? true : super.isReplaceable(state, context);
        return true;
    }


    @Override
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack)
    {
        player.addStat(Stats.BLOCK_MINED.get(this));
        player.addExhaustion(0.005F);

        for(Direction dir : Direction.values())
            if(state.get(FACING_TO_PROPERTY_MAP.get(dir)))
                spawnDrops(state, worldIn, pos, te, player, stack);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
    {
        return stateToShapeMap.get(state);
    }

    private static VoxelShape getShapeForState(BlockState state)
    {
        VoxelShape voxelshape = VoxelShapes.empty();

        if (state.get(UP))
            voxelshape = UP_AABB;

        if (state.get(DOWN))
            voxelshape = VoxelShapes.or(voxelshape, DOWN_AABB);

        if (state.get(NORTH))
            voxelshape = VoxelShapes.or(voxelshape, SOUTH_AABB);

        if (state.get(SOUTH))
            voxelshape = VoxelShapes.or(voxelshape, NORTH_AABB);

        if (state.get(EAST))
            voxelshape = VoxelShapes.or(voxelshape, WEST_AABB);

        if (state.get(WEST))
            voxelshape = VoxelShapes.or(voxelshape, EAST_AABB);

        return voxelshape;
    }
}
