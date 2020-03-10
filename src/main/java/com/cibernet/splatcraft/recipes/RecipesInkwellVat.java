package com.cibernet.splatcraft.recipes;

import com.cibernet.splatcraft.utils.InkColors;
import static com.cibernet.splatcraft.utils.InkColors.*;

import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.*;

public class RecipesInkwellVat
{
	private static final Hashtable<List<Object>, List<InkColors>> recipes = new Hashtable<>();
	
	public static void registerRecipes()
	{
		addRecipe(ItemStack.EMPTY, ORANGE, BLUE, GREEN, PINK, LIGHT_BLUE, YELLOW, TURQUOISE, LILAC, LEMON, PLUM);
		addRecipe(new ItemStack(Items.DYE, 1, 0), INK_BLACK);
		addRecipe(new ItemStack(Items.GOLDEN_APPLE, 1, 1), MOJANG, COBALT);
		addRecipe(new ItemStack(Items.GLOWSTONE_DUST), NEON_GREEN, NEON_PINK);
		addRecipe(new ItemStack(Items.CAKE), TANGERINE, CYAN, CHERRY, MINT);
	}
	
	public static void addRecipe(ItemStack item, InkColors... colors)
	{
		List<Object> input = Arrays.asList(item.getItem(), item.getMetadata());
		List<InkColors> list = Arrays.asList(colors);
		
		if(recipes.containsKey(input))
			recipes.get(input).addAll(list);
		else recipes.put(input, list);
	}
	


	public static List<InkColors> getOutput(@Nonnull ItemStack item)
	{
		List<InkColors> colors;
		if((colors = recipes.get(Arrays.asList(item.getItem(),item.getItemDamage()))) != null);
		else if((colors = recipes.get(Arrays.asList(item.getItem(), OreDictionary.WILDCARD_VALUE))) != null);
		else if(item != null && !item.isEmpty())
		{
			String[] names = getDictionaryNames(item);
			for(String str : names)
				if((colors = recipes.get(Arrays.asList(str, OreDictionary.WILDCARD_VALUE))) != null)
					break;
		}
		return colors == null ? null : colors;
	}

	public static boolean hasOutput(ItemStack input) {return recipes.containsKey(input);}
	
	public static Hashtable<List<Object>, List<InkColors>> getRecipes() {return recipes;}

	protected static String[] getDictionaryNames(@Nonnull ItemStack stack) {
		int[] itemIDs = OreDictionary.getOreIDs(stack);
		String[] itemNames = new String[itemIDs.length];

		for(int i = 0; i < itemIDs.length; ++i) {
			itemNames[i] = OreDictionary.getOreName(itemIDs[i]);
		}

		return itemNames;
	}
}
