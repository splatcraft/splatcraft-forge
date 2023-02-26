package net.splatcraft.forge.blocks;

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
import net.splatcraft.forge.registries.SplatcraftBlocks;
import net.splatcraft.forge.registries.SplatcraftTileEntities;
import net.splatcraft.forge.tileentities.InkColorTileEntity;
import net.splatcraft.forge.util.ColorUtils;
import org.jetbrains.annotations.Nullable;

public class InkwellBlock extends Block implements IColoredBlock, IWaterLoggable
{
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final SoundType SOUND_TYPE = new ForgeSoundType(1.0F, 1.0F, () -> SoundEvents.STONE_BREAK, () -> SoundEvents.SLIME_BLOCK_STEP, () -> SoundEvents.GLASS_PLACE, () -> SoundEvents.GLASS_HIT, () -> SoundEvents.SLIME_BLOCK_FALL);
    private static final VoxelShape SHAPE = VoxelShapes.or(
            box(0, 0, 0, 16, 12, 16),
            box(1, 12, 1, 14 / 16f, 13, 14),
            box(0, 13, 0, 16, 16, 16));

    public InkwellBlock()
    {
        super(Properties.of(Material.GLASS).strength(0.35f).harvestTool(ToolType.PICKAXE).sound(SOUND_TYPE));
        this.registerDefaultState(this.getStateDefinition().any().setValue(WATERLOGGED, false));

        SplatcraftBlocks.inkColoredBlocks.add(this);
    }

    @Nullable
    @Override
    public float[] getBeaconColorMultiplier(BlockState state, IWorldReader level, BlockPos pos, BlockPos beaconPos)
    {
        return ColorUtils.hexToRGB(getColor((World) level, pos));
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader level, BlockPos pos, PlayerEntity player)
    {
        return ColorUtils.setColorLocked(ColorUtils.setInkColor(super.getPickBlock(state, target, level, pos, player), getColor((World) level, pos)), true);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {

        return defaultBlockState().setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld levelIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (stateIn.getValue(WATERLOGGED))
        {
            levelIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelIn));
        }

        return super.updateShape(stateIn, facing, facingState, levelIn, currentPos, facingPos);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader levelIn, BlockPos pos, ISelectionContext context)
    {
        return SHAPE;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state)
    {
        return PushReaction.DESTROY;
    }

    @Override
    public ItemStack getCloneItemStack(IBlockReader reader, BlockPos pos, BlockState state)
    {
        ItemStack stack = super.getCloneItemStack(reader, pos, state);

        if (reader.getBlockEntity(pos) instanceof InkColorTileEntity)
            ColorUtils.setColorLocked(ColorUtils.setInkColor(stack, ColorUtils.getInkColor(reader.getBlockEntity(pos))), true);

        return stack;
    }

    @Override
    public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
        return false;
    }

    @Override
    public boolean isPossibleToRespawnInThis()
    {
        return true;
    }

    @Override
    public void setPlacedBy(World level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack)
    {
        if (!level.isClientSide && stack.getTag() != null && level.getBlockEntity(pos) instanceof InkColorTileEntity)
        {
            ColorUtils.setInkColor(level.getBlockEntity(pos), ColorUtils.getInkColor(stack));
        }
        super.setPlacedBy(level, pos, state, entity, stack);
    }

    @Override
    public boolean shouldCheckWeakPower(BlockState state, IWorldReader level, BlockPos pos, Direction side)
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
    public TileEntity createTileEntity(BlockState state, IBlockReader level)
    {
        return SplatcraftTileEntities.inkwellTileEntity.create();
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
    public int getColor(World level, BlockPos pos)
    {
        if (level.getBlockEntity(pos) instanceof InkColorTileEntity)
        {
            InkColorTileEntity tileEntity = (InkColorTileEntity) level.getBlockEntity(pos);
            if (tileEntity != null)
            {
                return tileEntity.getColor();
            }
        }
        return -1;
    }

    @Override
    public boolean remoteColorChange(World level, BlockPos pos, int newColor)
    {
        BlockState state = level.getBlockState(pos);
        TileEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof InkColorTileEntity && ((InkColorTileEntity) tileEntity).getColor() != newColor)
        {
            ((InkColorTileEntity) tileEntity).setColor(newColor);
            level.sendBlockUpdated(pos, state, state, 3);
            state.updateNeighbourShapes(level, pos, 3);
            return true;
        }
        return false;
    }

    @Override
    public boolean remoteInkClear(World level, BlockPos pos)
    {
        return false;
    }
}
