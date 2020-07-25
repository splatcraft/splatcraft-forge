package com.cibernet.splatcraft.crafting;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

public abstract class AbstractWeaponRecipe implements IRecipe<IInventory>
{
	private final ResourceLocation id;
	
	protected AbstractWeaponRecipe(ResourceLocation id) { this.id = id; }
}
