package net.splatcraft.forge.items.remotes;

import net.splatcraft.forge.blocks.IColoredBlock;
import net.splatcraft.forge.blocks.InkwellBlock;
import net.splatcraft.forge.commands.InkColorCommand;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.items.IColoredItem;
import net.splatcraft.forge.registries.SplatcraftItemGroups;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.tileentities.InkColorTileEntity;
import net.splatcraft.forge.util.ColorUtils;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ColorChangerItem extends RemoteItem implements IColoredItem
{
    public ColorChangerItem(String name)
    {
        super(name, new Properties().tab(SplatcraftItemGroups.GROUP_GENERAL).stacksTo(1).rarity(Rarity.UNCOMMON), 3);
        SplatcraftItems.inkColoredItems.add(this);
    }

    @SuppressWarnings("deprecation")
    public static RemoteResult replaceColor(World level, BlockPos from, BlockPos to, int color, int mode, int affectedColor)
    {
        BlockPos blockpos2 = new BlockPos(Math.min(from.getX(), to.getX()), Math.min(to.getY(), from.getY()), Math.min(from.getZ(), to.getZ()));
        BlockPos blockpos3 = new BlockPos(Math.max(from.getX(), to.getX()), Math.max(to.getY(), from.getY()), Math.max(from.getZ(), to.getZ()));

        if (!(blockpos2.getY() >= 0 && blockpos3.getY() < 256))
        {
            return createResult(false, new TranslationTextComponent("status.change_color.out_of_level"));
        }


        for (int j = blockpos2.getZ(); j <= blockpos3.getZ(); j += 16)
        {
            for (int k = blockpos2.getX(); k <= blockpos3.getX(); k += 16)
            {
                if (!level.isLoaded(new BlockPos(k, blockpos3.getY() - blockpos2.getY(), j)))
                {
                    return createResult(false, new TranslationTextComponent("status.change_color.out_of_level"));
                }
            }
        }
        int count = 0;
        int blockTotal = 0;
        for (int x = blockpos2.getX(); x <= blockpos3.getX(); x++)
        {
            for (int y = blockpos2.getY(); y <= blockpos3.getY(); y++)
            {
                for (int z = blockpos2.getZ(); z <= blockpos3.getZ(); z++)
                {
                    BlockPos pos = new BlockPos(x, y, z);
                    Block block = level.getBlockState(pos).getBlock();
                    TileEntity tileEntity = level.getBlockEntity(pos);
                    if (block instanceof IColoredBlock && tileEntity instanceof InkColorTileEntity)
                    {
                        int teColor = ((InkColorTileEntity) tileEntity).getColor();

                        if (((IColoredBlock) block).canRemoteColorChange(level, pos, teColor, color) && (mode == 0 || mode == 1 && teColor == affectedColor || mode == 2 && teColor != affectedColor) && ((IColoredBlock) block).remoteColorChange(level, pos, color))
                        {
                            count++;
                        }
                    }
                    blockTotal++;
                }
            }
        }

        return createResult(true, new TranslationTextComponent("status.change_color.success", count, level.isClientSide ? ColorUtils.getFormatedColorName(color, false) : InkColorCommand.getColorName(color))).setIntResults(count, count * 15 / blockTotal);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
        super.appendHoverText(stack, level, tooltip, flag);

        if (ColorUtils.isColorLocked(stack))
        {
            tooltip.add(ColorUtils.getFormatedColorName(ColorUtils.getInkColor(stack), true));
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World level, Entity entity, int itemSlot, boolean isSelected)
    {
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);

        if (entity instanceof PlayerEntity && !ColorUtils.isColorLocked(stack) && ColorUtils.getInkColor(stack) != ColorUtils.getPlayerColor((PlayerEntity) entity)
                && PlayerInfoCapability.hasCapability((LivingEntity) entity))
        {
            ColorUtils.setInkColor(stack, ColorUtils.getPlayerColor((PlayerEntity) entity));
        }
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity)
    {
        BlockPos pos = entity.blockPosition().below();

        if (entity.level.getBlockState(pos).getBlock() instanceof InkwellBlock)
        {
            InkColorTileEntity te = (InkColorTileEntity) entity.level.getBlockEntity(pos);

            if (ColorUtils.getInkColor(stack) != ColorUtils.getInkColor(te))
            {
                ColorUtils.setInkColor(entity.getItem(), ColorUtils.getInkColor(te));
                ColorUtils.setColorLocked(entity.getItem(), true);
            }
        }

        return false;
    }

    @Override
    public RemoteResult onRemoteUse(World usedOnWorld, BlockPos from, BlockPos to, ItemStack stack, int colorIn, int mode)
    {
        return replaceColor(getLevel(usedOnWorld, stack), from, to, ColorUtils.getInkColor(stack), mode, colorIn);
    }
}
