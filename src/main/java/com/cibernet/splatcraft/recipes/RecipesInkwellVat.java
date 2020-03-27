package com.cibernet.splatcraft.recipes;

import com.cibernet.splatcraft.items.ItemFilter;
import com.cibernet.splatcraft.utils.InkColors;
import static com.cibernet.splatcraft.utils.InkColors.*;
import static com.cibernet.splatcraft.registries.SplatCraftItems.*;

import net.minecraft.block.material.MapColor;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.*;

public class RecipesInkwellVat
{
	private static final Hashtable<ItemFilter, List<InkColors>> recipes = new Hashtable<>();
	
	private static final ItemFilter EMPTY = new ItemFilter("", "", false);
	
	public static void registerRecipes()
	{
		addToEmpty(ORANGE, BLUE, GREEN, PINK, LIGHT_BLUE, YELLOW, TURQUOISE, LILAC, LEMON, PLUM);
		addRecipe(filterEmpty, INK_BLACK);
		addRecipe(filterEnchanted, MOJANG, COBALT, ICEARSTORM);
		addRecipe(filterNeon, NEON_GREEN, NEON_PINK);
		addRecipe(filterPastel, TANGERINE, CYAN, CHERRY, MINT);
		addRecipe(filterDye, DYE_ORANGE, DYE_BLUE, DYE_PINK, DYE_LIME, DYE_PURPLE, DYE_LIGHT_BLUE, DYE_MAGENTA, DYE_YELLOW, DYE_RED, DYE_GREEN, DYE_BROWN, DYE_CYAN, DYE_WHITE, DYE_GRAY, DYE_SILVER, DYE_BLACK);
		
		addRecipe(filterCreative, InkColors.values());
	}
	
	public static void addRecipe(ItemFilter item, InkColors... colors)
	{
		List<InkColors> list = Arrays.asList(colors);
		
		if(recipes.containsKey(item))
			recipes.get(item).addAll(list);
		else recipes.put(item, list);
	}
	
	public static void addToEmpty(InkColors... colors) {addRecipe(EMPTY, colors);}
	
	public static List<InkColors> getOutput(@Nonnull ItemStack item)
	{
		List<InkColors> colors = recipes.get(item.getItem());
		return colors == null ? recipes.get(EMPTY) : colors;
	}

	public static boolean hasOutput(ItemStack input) {return recipes.containsKey(input);}
	
	public static Hashtable<ItemFilter, List<InkColors>> getRecipes() {return recipes;}

	protected static String[] getDictionaryNames(@Nonnull ItemStack stack) {
		int[] itemIDs = OreDictionary.getOreIDs(stack);
		String[] itemNames = new String[itemIDs.length];

		for(int i = 0; i < itemIDs.length; ++i) {
			itemNames[i] = OreDictionary.getOreName(itemIDs[i]);
		}

		return itemNames;
	}
}
