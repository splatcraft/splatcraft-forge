package com.cibernet.splatcraft.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemInkTankWhtelisted extends ItemInkTank
{
	private static final List<Item> allowedWeapons = new ArrayList<>();
	
	public ItemInkTankWhtelisted(String unlocalizedName, String registryName, float capacity, ArmorMaterial materialIn)
	{
		super(unlocalizedName, registryName, capacity, materialIn);
	}
	
	public ItemInkTankWhtelisted(String unlocalizedName, String registryName, float capacity, int armorPoints)
	{
		super(unlocalizedName, registryName, capacity, armorPoints);
	}
	
	public ItemInkTankWhtelisted(String unlocalizedName, String registryName, float capacity)
	{
		super(unlocalizedName, registryName, capacity);
	}
	
	public ItemInkTankWhtelisted addAllowedWeapons(Item... weapons)
	{
		allowedWeapons.addAll(Arrays.asList(weapons));
		return this;
	}
	
	@Override
	public float getInkAmount(ItemStack stack, ItemStack weapon)
	{
		if(allowedWeapons.contains(weapon.getItem()))
			return super.getInkAmount(stack, weapon);
		return 0;
	}
}
