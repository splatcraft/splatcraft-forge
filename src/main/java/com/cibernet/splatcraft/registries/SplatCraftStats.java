package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.recipes.RecipeSubtype;
import com.cibernet.splatcraft.recipes.RecipesWeaponStation;
import com.google.common.collect.Sets;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.stats.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.Set;

public class SplatCraftStats
{
	public static final StatBase BLOCKS_INKED = createStat("blocksInked");
	//public static final StatBase SUBS_USED = createStat("subsUsed");
	//public static final StatBase SPECIALS_USED = createStat("specialsUsed");
	public static final StatBase SQUID_TIME = createStat("squidTime", StatBase.timeStatType);
	public static final StatBase WEAPONS_CRAFTED = createStat("weaponsCrafted");
	public static final StatBase INKWELLS_CRAFTED = createStat("inkwellsCrafted");
	
	
	
	public static void registerStats()
	{
	}
	
	private static StatBase createStat(String name)
	{
		return (new StatBasic("stat."+ SplatCraft.SHORT.toLowerCase()+"."+name, new TextComponentTranslation("stat."+ SplatCraft.SHORT.toLowerCase()+"."+name))).initIndependentStat().registerStat();
	}
	private static StatBase createStat(String name, IStatType type)
	{
		return (new StatBasic("stat."+ SplatCraft.SHORT.toLowerCase()+"."+name, new TextComponentTranslation("stat."+ SplatCraft.SHORT.toLowerCase()+"."+name), type)).initIndependentStat().registerStat();
	}
	
	private static String getItemName(Item itemIn)
	{
		ResourceLocation resourcelocation = Item.REGISTRY.getNameForObject(itemIn);
		return resourcelocation != null ? resourcelocation.toString().replace(':', '.') : null;
	}
}
