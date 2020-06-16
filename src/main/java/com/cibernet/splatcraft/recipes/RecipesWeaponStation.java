package com.cibernet.splatcraft.recipes;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.registries.SplatCraftBlocks;
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
	public static RecipeSubtype recipeTentatekSplattershot = new RecipeSubtype(recipeSplattershot, tentatekSplattershot, "tentatek", new ItemStack(sardinium, 7), new ItemStack(powerEgg, 10), new ItemStack(DYE, 10, 0));
	public static RecipeSubtype recipeWasabiSplattershot = new RecipeSubtype(recipeSplattershot, wasabiSplattershot, "wasabi", new ItemStack(sardinium, 5), new ItemStack(powerEgg, 14), new ItemStack(DYE, 4, 0), new ItemStack(DYE, 4, 2));
	public static RecipeType recipeSplattershotJr = new RecipeType(splattershotJr, "original", new ItemStack(sardinium, 4), new ItemStack(powerEgg, 12), new ItemStack(GLASS, 4), new ItemStack(DYE, 4, 0));
	public static RecipeType recipeAerosprayMg = new RecipeType(aerosprayMG, "mg", new ItemStack(sardinium, 5), new ItemStack(powerEgg, 20), new ItemStack(DYE, 16, 0), new ItemStack(IRON_INGOT, 5)).setDisplayName("aerospray");
	public static RecipeSubtype recipeAerosprayRg = new RecipeSubtype(recipeAerosprayMg, aerosprayRG, "rg", new ItemStack(sardinium, 5), new ItemStack(powerEgg, 20), new ItemStack(DYE, 16, 0), new ItemStack(GOLD_INGOT, 5));
	public static RecipeType recipe52Gal = new RecipeType(gal52, "original", new ItemStack(sardinium, 9), new ItemStack(powerEgg, 15), new ItemStack(DYE, 17, 0), new ItemStack(BUCKET));
	public static RecipeSubtype recipe52GalDeco = new RecipeSubtype(recipe52Gal, gal52Deco, "deco", new ItemStack(sardinium, 7), new ItemStack(powerEgg, 15), new ItemStack(DYE, 17, 0), new ItemStack(BUCKET), new ItemStack(DIAMOND));
	public static RecipeType recipe96Gal = new RecipeType(gal96, "original", new ItemStack(sardinium, 9), new ItemStack(powerEgg, 18), new ItemStack(DYE, 21, 0), new ItemStack(BUCKET));
	public static RecipeSubtype recipe96GalDeco = new RecipeSubtype(recipe96Gal, gal96Deco, "deco", new ItemStack(sardinium, 7), new ItemStack(powerEgg, 18), new ItemStack(DYE, 21, 0), new ItemStack(BUCKET), new ItemStack(DIAMOND));
	public static RecipeType recipeBlaster = new RecipeType(blaster, "original", new ItemStack(sardinium, 10), new ItemStack(powerEgg, 18), new ItemStack(DYE, 12, 0), new ItemStack(GUNPOWDER, 12));
	public static RecipeSubtype recipeGrimBlaster = new RecipeSubtype(recipeBlaster, grimBlaster, "grim", new ItemStack(sardinium, 6), new ItemStack(powerEgg, 22), new ItemStack(DYE, 12, 0), new ItemStack(GUNPOWDER, 13), new ItemStack(DYE, 4, 10));
	public static RecipeType recipeClashBlaster = new RecipeType(clashBlaster, "original", new ItemStack(sardinium, 12), new ItemStack(powerEgg, 20), new ItemStack(DYE, 6, 0), new ItemStack(DYE, 2, 4), new ItemStack(GUNPOWDER, 4));
	public static RecipeSubtype recipeClashBlasterNeo = new RecipeSubtype(recipeClashBlaster, clashBlasterNeo, "neo", new ItemStack(sardinium, 16), new ItemStack(powerEgg, 16), new ItemStack(DYE, 6, 0), new ItemStack(DYE, 2, 4), new ItemStack(GUNPOWDER, 5));
	
	public static RecipeType recipeSplatRoller = new RecipeType(splatRoller, "original", new ItemStack(sardinium, 5), new ItemStack(powerEgg, 14), new ItemStack(WOOL, 4), new ItemStack(DYE, 14, 0));
	public static RecipeSubtype recipeKrakOnSplatRoller = new RecipeSubtype(recipeSplatRoller, krakOnSplatRoller, "krakOn", new ItemStack(sardinium, 4), new ItemStack(powerEgg, 12), new ItemStack(WOOL, 4), new ItemStack(DYE, 14, 0), new ItemStack(DYE, 2, 4));
	public static RecipeSubtype recipeCoroCoroSplatRoller = new RecipeSubtype(recipeSplatRoller, coroCoroSplatRoller, "coroCoro", new ItemStack(sardinium, 7), new ItemStack(powerEgg, 12), new ItemStack(WOOL, 4), new ItemStack(DYE, 14, 0));
	public static RecipeType recipeCarbonRoller = new RecipeType(carbonRoller, "original", new ItemStack(sardinium, 6), new ItemStack(powerEgg, 10), new ItemStack(WOOL, 2), new ItemStack(DYE, 10, 0), new ItemStack(DYE, 4, 9));
	public static RecipeType recipeInkbrush = new RecipeType(inkbrush, "original", new ItemStack(sardinium, 7), new ItemStack(powerEgg, 18), new ItemStack(Items.WHEAT, 3), new ItemStack(DYE, 7, 0));
	public static RecipeType recipeOctobrush = new RecipeType(octobrush, "original", new ItemStack(sardinium, 4), new ItemStack(powerEgg, 20), new ItemStack(Items.WHEAT, 5), new ItemStack(DYE, 12, 0), new ItemStack(IRON_NUGGET, 18));
	
	public static RecipeType recipeSplatCharger = new RecipeType(splatCharger, "original", new ItemStack(sardinium, 6), new ItemStack(powerEgg, 20), new ItemStack(DYE, 10, 0));
	public static RecipeSubtype recipeBentoSplatCharger = new RecipeSubtype(recipeSplatCharger, bentoSplatCharger, "bento", new ItemStack(sardinium, 5), new ItemStack(powerEgg, 17), new ItemStack(DYE, 12, 0), new ItemStack(PAPER, 1	));
	public static RecipeType recipeELiter4K = new RecipeType(eLiter4K, "4K", new ItemStack(sardinium, 10), new ItemStack(powerEgg, 18), new ItemStack(DYE, 10, 0), new ItemStack(IRON_INGOT, 5)).setDisplayName("e_liter");
	
	public static RecipeType recipeSplatDualies = new RecipeType(splatDualie, "original", new ItemStack(sardinium, 4), new ItemStack(powerEgg, 7), new ItemStack(DYE, 3, 0), new ItemStack(GUNPOWDER, 1)).setDisplayName("splatDualies");
	public static RecipeSubtype recipeEnperrySplatDualies = new RecipeSubtype(recipeSplatDualies, enperrySplatDualie, "enperry", new ItemStack(sardinium, 2), new ItemStack(powerEgg, 7), new ItemStack(DYE, 3, 0), new ItemStack(GUNPOWDER, 1), new ItemStack(GOLD_INGOT, 2));
	public static RecipeType recipeDualieSquelchers = new RecipeType(dualieSquelcher, "original", new ItemStack(sardinium, 3), new ItemStack(powerEgg, 8), new ItemStack(DYE, 2, 0), new ItemStack(GUNPOWDER, 2), new ItemStack(DYE, 2, 1)).setDisplayName("dualieSquelchers");
	
	public static RecipeType recipeInkTank = new RecipeType(inkTank, "original", new ItemStack(sardinium, 11), new ItemStack(powerEgg, 14), new ItemStack(SplatCraftBlocks.emptyInkwell));
	public static RecipeSubtype recipeClassicInkTank = new RecipeSubtype(recipeInkTank, classicInkTank, "classic", new ItemStack(sardinium, 11), new ItemStack(powerEgg, 15), new ItemStack(SplatCraftBlocks.emptyInkwell));
	public static RecipeType recipeInkTankJr = new RecipeType(inkTankJr, "original", new ItemStack(sardinium, 10), new ItemStack(powerEgg, 10), new ItemStack(SplatCraftBlocks.emptyInkwell));
	public static RecipeType recipeArmoredInkTank = new RecipeType(armoredInkTank, "original", new ItemStack(sardinium, 10), new ItemStack(IRON_INGOT, 5), new ItemStack(powerEgg, 18), new ItemStack(SplatCraftBlocks.emptyInkwell));
	
	public static void registerRecipes()
	{
		register(TAB_SHOOTER, recipeSplattershot);
		register(TAB_SHOOTER, recipeSplattershotJr);
		register(TAB_SHOOTER, recipeAerosprayMg);
		register(TAB_SHOOTER, recipe52Gal);
		register(TAB_SHOOTER, recipe96Gal);
		register(TAB_SHOOTER, recipeBlaster);
		register(TAB_SHOOTER, recipeClashBlaster);
		
		register(TAB_ROLLER, recipeSplatRoller);
		register(TAB_ROLLER, recipeCarbonRoller);
		register(TAB_ROLLER, recipeInkbrush);
		register(TAB_ROLLER, recipeOctobrush);
		
		register(TAB_CHARGER, recipeSplatCharger);
		register(TAB_CHARGER, recipeELiter4K);
		
		register(TAB_DUALIES, recipeSplatDualies);
		register(TAB_DUALIES, recipeDualieSquelchers);
		
		register(TAB_INK_TANKS, recipeInkTank);
		register(TAB_INK_TANKS, recipeInkTankJr);
		register(TAB_INK_TANKS, recipeArmoredInkTank);
		
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
