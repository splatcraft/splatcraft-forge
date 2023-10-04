package net.splatcraft.forge.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.splatcraft.forge.registries.SplatcraftBlocks;
import net.splatcraft.forge.registries.SplatcraftGameRules;
import net.splatcraft.forge.registries.SplatcraftTileEntities;
import net.splatcraft.forge.tileentities.InkColorTileEntity;
import net.splatcraft.forge.tileentities.InkedBlockTileEntity;
import net.splatcraft.forge.util.BlockInkedResult;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.InkBlockUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

import static net.splatcraft.forge.blocks.InkStainedBlock.COLORED;

public class InkStainedStairBlock extends StairBlock implements IColoredBlock, EntityBlock
{
    public InkStainedStairBlock(Supplier<BlockState> parent, Properties properties)
    {
        super(parent, properties);
        SplatcraftBlocks.inkColoredBlocks.add(this);

    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state)
    {
        return ColorUtils.setColorLocked(ColorUtils.setInkColor(super.getCloneItemStack(level, pos, state), getColor((Level) level, pos)), true);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack)
    {
        if (!level.isClientSide && stack.getTag() != null && level.getBlockEntity(pos) instanceof InkColorTileEntity)
        {
            ColorUtils.setInkColor(level.getBlockEntity(pos), ColorUtils.getInkColor(stack));
        }
        super.setPlacedBy(level, pos, state, entity, stack);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return SplatcraftTileEntities.colorTileEntity.get().create(pos, state);
    }

    @Override
    public boolean canClimb()
    {
        return false;
    }

    @Override
    public boolean canSwim()
    {
        return false;
    }

    @Override
    public boolean canDamage()
    {
        return false;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return super.getStateForPlacement(context);
    }

    @Override
    public boolean setColor(Level level, BlockPos pos, int color)
    {
        return IColoredBlock.super.setColor(level, pos, color);
    }

    @Override
    public int getColor(Level level, BlockPos pos)
    {
        if (level.getBlockEntity(pos) instanceof InkColorTileEntity)
        {
            return ((InkColorTileEntity) level.getBlockEntity(pos)).getColor();
        }
        return -1;
    }

    @Override
    public boolean remoteColorChange(Level level, BlockPos pos, int newColor)
    {
        BlockState state = level.getBlockState(pos);

        if (level.getBlockEntity(pos) instanceof InkColorTileEntity && ((InkColorTileEntity) level.getBlockEntity(pos)).getColor() != newColor)
        {
            ((InkColorTileEntity) level.getBlockEntity(pos)).setColor(newColor);
            level.sendBlockUpdated(pos, state, state, 2);
            return true;
        }
        return false;
    }

    @Override
    public BlockInkedResult inkBlock(Level level, BlockPos pos, int color, float damage, InkBlockUtils.InkType inkType)
    {
        if (InkedBlock.isTouchingLiquid(level, pos) || !SplatcraftGameRules.getLocalizedRule(level, pos, SplatcraftGameRules.INKABLE_GROUND))
        {
            return BlockInkedResult.FAIL;
        }

        int woolColor = -1;

        if (level.getBlockEntity(pos) instanceof InkColorTileEntity)
        {
            woolColor = ((InkColorTileEntity) level.getBlockEntity(pos)).getColor();
        }

        BlockState state = level.getBlockState(pos);
        BlockState inkState = InkBlockUtils.getInkState(inkType);
        level.setBlock(pos, inkState, 3);
        level.setBlockEntity(Objects.requireNonNull(SplatcraftBlocks.inkedBlock.get().newBlockEntity(pos, inkState)));
        InkedBlockTileEntity inkte = (InkedBlockTileEntity) level.getBlockEntity(pos);
        if (inkte == null)
        {
            return BlockInkedResult.FAIL;
        }
        inkte.setColor(color);
        inkte.setSavedState(state);
        inkte.setSavedColor(woolColor);

        return BlockInkedResult.SUCCESS;
    }

    @Override
    public boolean remoteInkClear(Level level, BlockPos pos)
    {
        return false;
    }

    public static class WithUninkedVariant extends InkStainedStairBlock
    {
        public WithUninkedVariant(Supplier<BlockState> parent, Properties properties)
        {
            super(parent, properties);

            registerDefaultState(defaultBlockState().setValue(COLORED, false));
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
        {
            super.createBlockStateDefinition(builder);
            builder.add(COLORED);
        }

        @Override
        public boolean remoteColorChange(Level level, BlockPos pos, int newColor)
        {
            if(!level.getBlockState(pos).getValue(COLORED))
                return false;

            return super.remoteColorChange(level, pos, newColor);
        }

        @Override
        public boolean setColor(Level level, BlockPos pos, int color)
        {
            level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(COLORED, color >= 0));
            return super.setColor(level, pos, color);
        }

        @Override
        public @Nullable BlockState getStateForPlacement(BlockPlaceContext context)
        {
            return super.getStateForPlacement(context).setValue(COLORED, ColorUtils.getInkColor(context.getItemInHand()) >= 0);
        }
    }
}
