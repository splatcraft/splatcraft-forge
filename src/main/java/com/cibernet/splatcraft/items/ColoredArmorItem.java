package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.blocks.InkwellBlock;
import com.cibernet.splatcraft.registries.SplatcraftItemGroups;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ColoredArmorItem extends ArmorItem implements IDyeableArmorItem
{
	public ColoredArmorItem(String name, IArmorMaterial material, EquipmentSlotType slot, Properties properties)
	{
		super(material, slot, properties);
		SplatcraftItems.inkColoredItems.add(this);
		setRegistryName(name);
	}
	
	public ColoredArmorItem(String name, IArmorMaterial material, EquipmentSlotType slot)
	{
		this(name, material, slot, new Properties().group(SplatcraftItemGroups.GROUP_WEAPONS).maxStackSize(1));
	}
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag)
	{
		super.addInformation(stack, world, tooltip, flag);
		
		if(I18n.hasKey(getTranslationKey()+".tooltip"))
			tooltip.add(new TranslationTextComponent(getTranslationKey()+".tooltip").mergeStyle(TextFormatting.GRAY));
		
		if(ColorUtils.isColorLocked(stack))
			tooltip.add(ColorUtils.getFormatedColorName(ColorUtils.getInkColor(stack),true));
		else if(I18n.hasKey(getTranslationKey()+".colorless_tooltip"))
			tooltip.add(new TranslationTextComponent(getTranslationKey()+".colorless_tooltip").mergeStyle(TextFormatting.GRAY));
	}
	
	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected)
	{
		super.inventoryTick(stack, world, entity, itemSlot, isSelected);
		
		if(entity instanceof PlayerEntity && !ColorUtils.isColorLocked(stack) && ColorUtils.getInkColor(stack) != ColorUtils.getPlayerColor((PlayerEntity) entity))
			ColorUtils.setInkColor(stack, ColorUtils.getPlayerColor((PlayerEntity) entity));
	}
	
	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity)
	{
		BlockPos pos = entity.getPosition().down();
		
		if(entity.world.getBlockState(pos).getBlock() instanceof InkwellBlock)
		{
			InkColorTileEntity te = (InkColorTileEntity) entity.world.getTileEntity(pos);
			
			if(ColorUtils.getInkColor(stack) != ColorUtils.getInkColor(te))
			{
				ColorUtils.setInkColor(entity.getItem(), ColorUtils.getInkColor(te));
				ColorUtils.setColorLocked(entity.getItem(), true);
			}
		}
		
		return false;
	}
	
	@Override
	public boolean hasColor(ItemStack stack)
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
	public void removeColor(ItemStack stack)
	{
		ColorUtils.setInkColor(stack, -1);
		ColorUtils.setColorLocked(stack, false);
	}
}
