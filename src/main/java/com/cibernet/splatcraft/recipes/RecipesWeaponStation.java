package com.cibernet.splatcraft.recipes;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.registries.SplatCraftItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.*;

import static com.cibernet.splatcraft.recipes.WeaponStationTabs.*;
import static com.cibernet.splatcraft.registries.SplatCraftItems.*;
import static net.minecraft.init.Blocks.*;
import static net.minecraft.init.Items.*;

public class RecipesWeaponStation
{
	public static Hashtable<WeaponStationTabs, List<RecipeType>> recipeTabs = new Hashtable<>();
	private static List<RecipeSubtype> recipeList = new ArrayList<>();
	
	public static RecipeType recipeSplattershot = new RecipeType(splattershot, "original", new ItemStack(sardinium, 5), new ItemStack(powerEgg, 14), new ItemStack(DYE, 8, 0));
	public static RecipeType recipeSplattershotJr = new RecipeType(splattershotJr, "original", new ItemStack(sardinium, 4), new ItemStack(powerEgg, 12), new ItemStack(GLASS, 4), new ItemStack(DYE, 4, 0));
	public static RecipeType recipeAerosprayMg = new RecipeType(aerosprayMG, "mg", new ItemStack(sardinium, 5), new ItemStack(powerEgg, 20), new ItemStack(DYE, 16, 0), new ItemStack(IRON_INGOT, 5)).setDisplayName("aerospray");
	public static RecipeType recipeSplatRoller = new RecipeType(splatRoller, "original", new ItemStack(sardinium, 5), new ItemStack(powerEgg, 14), new ItemStack(WOOL, 4), new ItemStack(DYE, 14, 0));
	public static RecipeType recipeInkbrush = new RecipeType(inkbrush, "original", new ItemStack(sardinium, 7), new ItemStack(powerEgg, 18), new ItemStack(Items.WHEAT, 3), new ItemStack(DYE, 7, 0));
	public static RecipeType recipeSplatCharger = new RecipeType(splatCharger, "original", new ItemStack(sardinium, 6), new ItemStack(powerEgg, 20), new ItemStack(DYE, 10, 0));
	public static RecipeType recipeELiter4K = new RecipeType(eLiter4K, "4K", new ItemStack(sardinium, 10), new ItemStack(powerEgg, 18), new ItemStack(DYE, 10, 0), new ItemStack(IRON_INGOT, 5)).setDisplayName("e_liter");
	
	public static RecipeSubtype recipeSplattershotCstm = new RecipeSubtype(recipeSplattershot, splatCharger, "custom", recipeSplattershot.concatIngredients(new ItemStack(GLOWSTONE_DUST)));
	
	public static void registerRecipes()
	{
		register(TAB_SHOOTER, recipeSplattershot);
		register(TAB_SHOOTER, recipeSplattershotJr);
		register(TAB_SHOOTER, recipeAerosprayMg);
		register(TAB_ROLLER, recipeSplatRoller);
		register(TAB_ROLLER, recipeInkbrush);
		register(TAB_CHARGER, recipeSplatCharger);
		register(TAB_CHARGER, recipeELiter4K);
		
	}
	
	public static void register(WeaponStationTabs tab, RecipeType recipeType)
	{
		if(recipeTabs.containsKey(tab))
			recipeTabs.get(tab).add(recipeType);
		else recipeTabs.put(tab, new ArrayList<RecipeType>(){{add(recipeType);}});
	}
	
	public static boolean getItem(EntityPlayer player, ItemStack stack, boolean decr)
	{
		stack = stack.copy();
		for(int i = 0; i < player.inventory.getSizeInventory(); ++i)
		{
			ItemStack invStack = player.inventory.getStackInSlot(i);
			if(!decr)
				invStack = invStack.copy();
			
			if(invStack.isItemEqual(stack))
			{
				if(stack.getCount() > invStack.getCount())
				{
					stack.setCount(stack.getCount() - invStack.getCount());
					invStack.setCount(0);
				}
				else
				{
					invStack.setCount(invStack.getCount()-stack.getCount());
					return true;
				}
			}
		}
		return false;
	}
	
	private static ItemStack findItemstack(EntityPlayer player, ItemStack stack)
	{
		for (int i = 0; i < player.inventory.getSizeInventory(); ++i)
		{
			ItemStack itemstack = player.inventory.getStackInSlot(i);
			
			if (itemstack.isItemEqual(stack))
			{
				return itemstack;
			}
		}
		
		return ItemStack.EMPTY;
	}
	
	@Deprecated
	public static void addToRecipeList(RecipeSubtype recipe) {recipeList.add(recipe);}
	
	public static int getRecipeID(RecipeSubtype recipe)
	{
		if(!recipeList.contains(recipe))
		{
			SplatCraft.logger.info("The requested recipe is not registered in the recipe list!");
			return -1;
		}
		
		return recipeList.indexOf(recipe);
	}
	
	public static RecipeSubtype getRecipeByID(int id)
	{
		if(id < 0 || id >= recipeList.size())
		{
			SplatCraft.logger.info("the id specified seems to be out of bounds!");
			return null;
		}
		
		return recipeList.get(id);
	}
}
