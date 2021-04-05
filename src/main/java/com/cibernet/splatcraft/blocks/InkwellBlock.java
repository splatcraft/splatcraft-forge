package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import com.cibernet.splatcraft.registries.SplatcraftTileEntitites;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.ForgeSoundType;

import javax.annotation.Nullable;

public class InkwellBlock extends Block implements IColoredBlock, IWaterLoggable
{
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final SoundType SOUND_TYPE = new ForgeSoundType(1.0F, 1.0F, () -> SoundEvents.BLOCK_STONE_BREAK, () -> SoundEvents.BLOCK_SLIME_BLOCK_STEP, () -> SoundEvents.BLOCK_GLASS_PLACE, () -> SoundEvents.BLOCK_GLASS_HIT, () -> SoundEvents.BLOCK_SLIME_BLOCK_FALL);
    private static final VoxelShape SHAPE = VoxelShapes.or(
            makeCuboidShape(0, 0, 0, 16, 12, 16),
            makeCuboidShape(1, 12, 1, 14 / 16f, 13, 14),
            makeCuboidShape(0, 13, 0, 16, 16, 16));

    public InkwellBlock()
    {
        super(Properties.create(Material.GLASS).hardnessAndResistance(0.35f).harvestTool(ToolType.PICKAXE).sound(SOUND_TYPE));
        this.setDefaultState(this.stateContainer.getBaseState().with(WATERLOGGED, false));

        SplatcraftBlocks.inkColoredBlocks.add(this);
    }

    @Nullable
    @Override
    public float[] getBeaconColorMultiplier(BlockState state, IWorldReader world, BlockPos pos, BlockPos beaconPos)
    {
        return ColorUtils.hexToRGB(getColor((World) world, pos));
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
    {
        return ColorUtils.setInkColor(super.getPickBlock(state, target, world, pos, player), getColor((World) world, pos));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {

        return getDefaultState().with(WATERLOGGED, context.getWorld().getFluidState(context.getPos()).getFluid() == Fluids.WATER);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (stateIn.get(WATERLOGGED))
        {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }

        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return SHAPE;
    }

    @Override
    public PushReaction getPushReaction(BlockState state)
    {
        return PushReaction.DESTROY;
    }

    @Override
    public ItemStack getItem(IBlockReader reader, BlockPos pos, BlockState state)
    {
        ItemStack stack = super.getItem(reader, pos, state);

        if (reader.getTileEntity(pos) instanceof InkColorTileEntity)
        {
            ColorUtils.setInkColor(stack, ColorUtils.getInkColor(reader.getTileEntity(pos)));
        }

        return stack;
    }

    @Override
    public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack)
    {
        if (!world.isRemote && stack.getTag() != null && world.getTileEntity(pos) instanceof InkColorTileEntity)
        {
            ColorUtils.setInkColor(world.getTileEntity(pos), ColorUtils.getInkColor(stack));
        }
        super.onBlockPlacedBy(world, pos, state, entity, stack);
    }

    @Override
    public boolean shouldCheckWeakPower(BlockState state, IWorldReader world, BlockPos pos, Direction side)
    {
        return true;
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return SplatcraftTileEntitites.inkwellTileEntity.create();
    }

    @Override
    public boolean canClimb()
    {
        return false;
    }

    @Override
    public boolean canSwim()
    {
        return true;
    }

    @Override
    public boolean canDamage()
    {
        return false;
    }

    @Override
    public int getColor(World world, BlockPos pos)
    {
        if (world.getTileEntity(pos) instanceof InkColorTileEntity)
        {
            InkColorTileEntity tileEntity = (InkColorTileEntity) world.getTileEntity(pos);
            if (tileEntity != null)
            {
                return tileEntity.getColor();
            }
        }
        return -1;
    }

    @Override
    public boolean remoteColorChange(World world, BlockPos pos, int newColor)
    {
        BlockState state = world.getBlockState(pos);
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof InkColorTileEntity && ((InkColorTileEntity) tileEntity).getColor() != newColor)
        {
            ((InkColorTileEntity) tileEntity).setColor(newColor);
            world.notifyBlockUpdate(pos, state, state, 2);
            return true;
        }
        return false;
    }

    @Override
    public boolean remoteInkClear(World world, BlockPos pos)
    {
        return false;
    }
}
