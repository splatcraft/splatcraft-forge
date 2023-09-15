package net.splatcraft.forge.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.HitResult;
import net.splatcraft.forge.registries.SplatcraftBlocks;
import net.splatcraft.forge.registries.SplatcraftGameRules;
import net.splatcraft.forge.registries.SplatcraftTileEntities;
import net.splatcraft.forge.tileentities.InkColorTileEntity;
import net.splatcraft.forge.tileentities.InkedBlockTileEntity;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.InkBlockUtils;
import org.jetbrains.annotations.Nullable;

public class InkedGlassBlock extends AbstractGlassBlock implements IColoredBlock, EntityBlock
{
    public InkedGlassBlock(String name)
    {
        super(Properties.of(Material.GLASS).strength(0.3F).sound(SoundType.GLASS).noOcclusion()
                .isValidSpawn((state, level, pos, entity) -> false)
                .isRedstoneConductor((state, level, pos) -> false)
                .isSuffocating((state, level, pos) -> false)
                .isViewBlocking((state, level, pos) -> false));
        SplatcraftBlocks.inkColoredBlocks.add(this);
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState p_220074_1_) {
        return true;
    }

    @Nullable
    @Override
    public float[] getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos)
    {
        return ColorUtils.hexToRGB(getColor((Level) level, pos));
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
    {
        return ColorUtils.setColorLocked(ColorUtils.setInkColor(super.getCloneItemStack(state, target, level, pos, player), getColor((Level) level, pos)), true);
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


    @Override
    public ItemStack getCloneItemStack(BlockGetter reader, BlockPos pos, BlockState state)
    {
        ItemStack stack = super.getCloneItemStack(reader, pos, state);

        if (reader.getBlockEntity(pos) instanceof InkColorTileEntity)
            ColorUtils.setColorLocked(ColorUtils.setInkColor(stack, ColorUtils.getInkColor(reader.getBlockEntity(pos))), true);

        return stack;
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
    public boolean inkBlock(Level level, BlockPos pos, int color, float damage, InkBlockUtils.InkType inkType)
    {
        if (InkedBlock.isTouchingLiquid(level, pos) || !SplatcraftGameRules.getLocalizedRule(level, pos, SplatcraftGameRules.INKABLE_GROUND))
        {
            return false;
        }

        int woolColor = -1;

        if (level.getBlockEntity(pos) instanceof InkColorTileEntity)
        {
            woolColor = ((InkColorTileEntity) level.getBlockEntity(pos)).getColor();
        }

        BlockState state = level.getBlockState(pos);
        BlockState inkState = InkBlockUtils.getInkState(inkType, level, pos);
        level.setBlock(pos, inkState, 3);
        level.setBlockEntity(SplatcraftBlocks.inkedBlock.get().newBlockEntity(pos, inkState));
        InkedBlockTileEntity inkte = (InkedBlockTileEntity) level.getBlockEntity(pos);
        if (inkte == null)
        {
            return false;
        }
        inkte.setColor(color);
        inkte.setSavedState(state);
        inkte.setSavedColor(woolColor);

        return true;
    }

    @Override
    public boolean remoteInkClear(Level level, BlockPos pos)
    {
        return false;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return SplatcraftTileEntities.colorTileEntity.get().create(pos, state);
    }
}
