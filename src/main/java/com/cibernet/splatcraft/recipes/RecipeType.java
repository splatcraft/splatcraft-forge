package com.cibernet.splatcraft.recipes;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RecipeType
{
	List<RecipeSubtype> subtypes = new ArrayList<>();
	ItemStack displayStack;
	String displayName = "";
	
	public RecipeType(ItemStack stack, String name, ItemStack... ingredients)
	{
		displayStack = stack;
		
		new RecipeSubtype(this, stack, name, ingredients);
	}
	
	public RecipeType(Item item, String name, ItemStack... ingredients) { this(new ItemStack(item), name, ingredients); }
	
	public List<RecipeSubtype> getSubtypes() { return subtypes; }
	
	public RecipeType setDisplayName(String name) { displayName = name; return this;}
	
	public String getDisplayName()
	{
		return (displayName.isEmpty() ? displayStack.getUnlocalizedName() : ("recipe."+displayName))+".name";
	}
	
	public ItemStack getDisplayStack() {return displayStack;}
	
	public RecipeSubtype getMainRecipe() { return subtypes.get(0);}
	
	public ItemStack[] concatIngredients(ItemStack... stacks)
	{
		List<ItemStack> input = new ArrayList<ItemStack>() {{addAll(getMainRecipe().getIngredients());}};
		
		for(ItemStack stack : stacks)
		{
			boolean concated = false;
			for(int i = 0; i < input.size(); i++)
			{
				ItemStack check = input.get(i);
				if(check.isItemEqual(stack))
				{
					check.setCount(check.getCount() + stack.getCount());
					break;
				}
			}
			if(!concated)
				input.add(stack);
			
		}
		
		return input.toArray(new ItemStack[input.size()]);
	}
}
