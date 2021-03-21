package com.cibernet.splatcraft.items.remotes;

import com.cibernet.splatcraft.blocks.IColoredBlock;
import com.cibernet.splatcraft.blocks.InkwellBlock;
import com.cibernet.splatcraft.data.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.registries.SplatcraftItemGroups;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.util.ColorUtils;
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

public class ColorChangerItem extends RemoteItem {
    public ColorChangerItem(String name) {
        super(name, new Properties().group(SplatcraftItemGroups.GROUP_GENERAL).maxStackSize(1).rarity(Rarity.UNCOMMON), 3);
        SplatcraftItems.inkColoredItems.add(this);
    }


    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        if (ColorUtils.isColorLocked(stack))
            tooltip.add(ColorUtils.getFormatedColorName(ColorUtils.getInkColor(stack), true));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, world, entity, itemSlot, isSelected);

        if (entity instanceof PlayerEntity && !ColorUtils.isColorLocked(stack) && ColorUtils.getInkColor(stack) != ColorUtils.getPlayerColor((PlayerEntity) entity)
            && PlayerInfoCapability.hasCapability((LivingEntity) entity))
            ColorUtils.setInkColor(stack, ColorUtils.getPlayerColor((PlayerEntity) entity));
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        BlockPos pos = entity.getPosition().down();

        if (entity.world.getBlockState(pos).getBlock() instanceof InkwellBlock) {
            InkColorTileEntity te = (InkColorTileEntity) entity.world.getTileEntity(pos);

            if (ColorUtils.getInkColor(stack) != ColorUtils.getInkColor(te)) {
                ColorUtils.setInkColor(entity.getItem(), ColorUtils.getInkColor(te));
                ColorUtils.setColorLocked(entity.getItem(), true);
            }
        }

        return false;
    }


    @Override
    public RemoteResult onRemoteUse(World world, BlockPos from, BlockPos to, ItemStack stack, int colorIn, int mode) {
        return replaceColor(world, from, to, colorIn, mode, ColorUtils.getInkColor(stack));
    }

    @SuppressWarnings("deprecation")
    public static RemoteResult replaceColor(World world, BlockPos from, BlockPos to, int affectedColor, int mode, int color) {
        BlockPos blockpos2 = new BlockPos(Math.min(from.getX(), to.getX()), Math.min(to.getY(), from.getY()), Math.min(from.getZ(), to.getZ()));
        BlockPos blockpos3 = new BlockPos(Math.max(from.getX(), to.getX()), Math.max(to.getY(), from.getY()), Math.max(from.getZ(), to.getZ()));

        if (!(blockpos2.getY() >= 0 && blockpos3.getY() < 256))
            return createResult(false, new TranslationTextComponent("status.change_color.out_of_world"));


        for (int j = blockpos2.getZ(); j <= blockpos3.getZ(); j += 16) {
            for (int k = blockpos2.getX(); k <= blockpos3.getX(); k += 16) {
                if (!world.isBlockLoaded(new BlockPos(k, blockpos3.getY() - blockpos2.getY(), j))) {
                    return createResult(false, new TranslationTextComponent("status.change_color.out_of_world"));
                }
            }
        }
        int count = 0;
        int blockTotal = 0;
        for (int x = blockpos2.getX(); x <= blockpos3.getX(); x++)
            for (int y = blockpos2.getY(); y <= blockpos3.getY(); y++)
                for (int z = blockpos2.getZ(); z <= blockpos3.getZ(); z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    Block block = world.getBlockState(pos).getBlock();
                    TileEntity tileEntity = world.getTileEntity(pos);
                    if (block instanceof IColoredBlock && tileEntity instanceof InkColorTileEntity) {
                        int teColor = ((InkColorTileEntity) tileEntity).getColor();

                        if (teColor != affectedColor && (mode == 0 || mode == 1 && teColor == color || mode == 2 && teColor != color) && ((IColoredBlock) block).remoteColorChange(world, pos, affectedColor))
                            count++;
                    }
                    blockTotal++;
                }

        return createResult(true, new TranslationTextComponent("status.change_color.success", count, ColorUtils.getFormatedColorName(affectedColor, false))).setIntResults(count, count * 15 / blockTotal);
    }
}
