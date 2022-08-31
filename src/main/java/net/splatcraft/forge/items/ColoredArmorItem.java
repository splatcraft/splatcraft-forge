package net.splatcraft.forge.items;

import net.splatcraft.forge.blocks.InkwellBlock;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.registries.SplatcraftItemGroups;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.tileentities.InkColorTileEntity;
import net.splatcraft.forge.util.ColorUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import java.util.List;

public class ColoredArmorItem extends ArmorItem implements IDyeableArmorItem, IColoredItem
{
    public ColoredArmorItem(String name, IArmorMaterial material, EquipmentSlotType slot, Properties properties)
    {
        super(material, slot, properties);
        SplatcraftItems.inkColoredItems.add(this);
        setRegistryName(name);
    }

    public ColoredArmorItem(String name, IArmorMaterial material, EquipmentSlotType slot)
    {
        this(name, material, slot, new Properties().tab(SplatcraftItemGroups.GROUP_WEAPONS).stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
        super.appendHoverText(stack, level, tooltip, flag);


        if (I18n.exists(getDescriptionId() + ".tooltip"))
            tooltip.add(new TranslationTextComponent(getDescriptionId() + ".tooltip").withStyle(TextFormatting.GRAY));

        if (ColorUtils.isColorLocked(stack))
            tooltip.add(ColorUtils.getFormatedColorName(ColorUtils.getInkColor(stack), true));
        else
            tooltip.add(new TranslationTextComponent( "item.splatcraft.tooltip.matches_color").withStyle(TextFormatting.GRAY));
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
    public ActionResultType useOn(ItemUseContext context)
    {
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        if (ColorUtils.isColorLocked(context.getItemInHand()) && state.getBlock() instanceof CauldronBlock && context.getPlayer() != null && !context.getPlayer().isCrouching())
        {
            int i = state.getValue(CauldronBlock.LEVEL);

            if (i > 0)
            {
                World level = context.getLevel();
                PlayerEntity player = context.getPlayer();
                ColorUtils.setColorLocked(context.getItemInHand(), false);

                context.getPlayer().awardStat(Stats.USE_CAULDRON);

                if (!player.isCreative())
                {level.setBlock(context.getClickedPos(), state.setValue(CauldronBlock.LEVEL, MathHelper.clamp(i - 1, 0, 3)), 2);
                    level.updateNeighbourForOutputSignal(context.getClickedPos(), state.getBlock());
                }

                return ActionResultType.SUCCESS;
            }

        }

        return super.useOn(context);
    }

    @Override
    public boolean hasCustomColor(ItemStack stack)
    {
        return true;
    }

    @Override
    public int getColor(ItemStack stack)
    {
        return ColorUtils.getInkColor(stack);
    }

    @Override
    public void setColor(ItemStack stack, int color)
    {
        ColorUtils.setInkColor(stack, color);
    }

    @Override
    public void clearColor(ItemStack stack)
    {
        ColorUtils.setInkColor(stack, -1);
        ColorUtils.setColorLocked(stack, false);
    }
}
