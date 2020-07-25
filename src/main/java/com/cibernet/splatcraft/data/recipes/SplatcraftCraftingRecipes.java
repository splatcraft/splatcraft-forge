package com.cibernet.splatcraft.data.recipes;

import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;

import java.util.function.Consumer;

public class SplatcraftCraftingRecipes extends RecipeProvider
{
	public SplatcraftCraftingRecipes(DataGenerator generator)
	{
		super(generator);
	}
	
	@Override
	protected void registerRecipes(Consumer<IFinishedRecipe> consumer)
	{
		ShapedRecipeBuilder.shapedRecipe(SplatcraftBlocks.sardiniumBlock).patternLine("###").patternLine("###").patternLine("###").key('#', SplatcraftItems.sardinium)
				.setGroup("splatcraft").addCriterion("sardinium_to_block", InventoryChangeTrigger.Instance.forItems(SplatcraftItems.sardinium)).build(consumer);
	}
}
