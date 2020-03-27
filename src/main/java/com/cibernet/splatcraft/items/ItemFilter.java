package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.utils.TabSplatCraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemFilter extends Item
{
	private boolean hasGlint;
	public ItemFilter(String unlocName, String registryName, boolean hasGlint)
	{
		setUnlocalizedName(unlocName);
		setRegistryName(registryName);
		setMaxStackSize(1);
		setCreativeTab(TabSplatCraft.main);
		
		this.hasGlint = hasGlint;
	}
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		super.addInformation(stack, worldIn, tooltip, flagIn);
		if(I18n.canTranslate("item.filter.tooltip"))
			tooltip.add(I18n.translateToLocal("item.filter.tooltip"));
	}
	
	@Override
	public boolean hasEffect(ItemStack stack)
	{
		return hasGlint;
	}
	
	@Override
	public EnumRarity getRarity(ItemStack stack)
	{
		return hasGlint ? EnumRarity.RARE : super.getRarity(stack);
	}
}
