package com.cibernet.splatcraft.recipes;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeSubtype
{
	ItemStack output;
	String name;
	List<ItemStack> ingredients;
	
	public RecipeSubtype(RecipeType mainType, ItemStack output, String name, ItemStack... ingredients)
	{
		this.output = output;
		this.name = name;
		this.ingredients = new ArrayList<ItemStack>(){{addAll(Arrays.asList(ingredients));}};
		
		mainType.subtypes.add(this);
		
		RecipesWeaponStation.addToRecipeList(this);
	}
	
	public RecipeSubtype(RecipeType mainType, Item output, String name, ItemStack... ingredients) {this(mainType, new ItemStack(output), name, ingredients);}
	
	public ItemStack getOutput() {return output;}
	public String getName() {return "weaponType."+name;}
	public List<ItemStack> getIngredients() {return ingredients;}
}