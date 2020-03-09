package com.cibernet.splatcraft.recipes;

import com.cibernet.splatcraft.utils.InkColors;
import static com.cibernet.splatcraft.utils.InkColors.*;

import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

import java.util.*;

public class RecipesInkwellVat
{
	private static final Hashtable<ItemStack, List<InkColors>> recipes = new Hashtable<>();
	
	public static void registerRecipes()
	{
		addRecipe(ItemStack.EMPTY, ORANGE, BLUE, GREEN, PINK);
		addRecipe(new ItemStack(Items.DYE, 1, 0), INK_BLACK);
		addRecipe(new ItemStack(Items.GOLDEN_APPLE, 1, 1), MOJANG);
	}
	
	public static void addRecipe(ItemStack input, InkColors... colors)
	{
		List<InkColors> list = Arrays.asList(colors);
		
		if(recipes.containsKey(input))
			recipes.get(input).addAll(list);
		else recipes.put(input, list);
	}
	
	public static List<InkColors> getOutput(ItemStack input)
	{
		if(recipes.containsKey(input))
			return recipes.get(input);
		else return new ArrayList<>();
	}
	
	public static boolean hasOutput(ItemStack input) {return recipes.containsKey(input);}
	
	public static Hashtable<ItemStack, List<InkColors>> getRecipes() {return recipes;}
}
