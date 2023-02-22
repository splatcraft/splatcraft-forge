package net.splatcraft.forge.crafting;

import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.splatcraft.forge.items.ColoredBlockItem;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.util.ColorUtils;

public class ColoredShapedRecipe extends ShapedRecipe
{
	public ColoredShapedRecipe(ResourceLocation p_i48162_1_, String p_i48162_2_, int p_i48162_3_, int p_i48162_4_, NonNullList<Ingredient> p_i48162_5_, ItemStack p_i48162_6_)
	{
		super(p_i48162_1_, p_i48162_2_, p_i48162_3_, p_i48162_4_, p_i48162_5_, p_i48162_6_);
	}


	@Override
	public ItemStack assemble(CraftingInventory inventory)
	{
		int color = 0, j = 0, curColor = 0;
		boolean colorLock = false;

		for(int i = 0; i < inventory.getContainerSize(); i++)
		{
			ItemStack stack = inventory.getItem(i);

			if(stack.getItem() == SplatcraftItems.inkwell && ColorUtils.getInkColor(stack) != -1)
			{
				color += ColorUtils.getInkColor(stack);
				j++;

				if(ColorUtils.isColorLocked(stack))
					colorLock = true;
				else curColor = ColorUtils.getInkColor(stack);
			}
		}

		if(!colorLock)
			color = curColor;

		return ColorUtils.setColorLocked(ColorUtils.setInkColor(super.assemble(inventory), j == 0 ? -1 : color/j), colorLock);
	}

	@Override
	public boolean matches(CraftingInventory p_77569_1_, World p_77569_2_)
	{
		return super.matches(p_77569_1_, p_77569_2_);
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return super.getSerializer();
	}


	public static class Serializer extends ShapedRecipe.Serializer
	{
		public Serializer(String name)
		{
			super();
			setRegistryName(name);
		}

		@Override
		public ShapedRecipe fromJson(ResourceLocation p_199425_1_, JsonObject p_199425_2_)
		{
			ShapedRecipe recipe = super.fromJson(p_199425_1_, p_199425_2_);
			return new ColoredShapedRecipe(recipe.getId(), recipe.getGroup(), recipe.getRecipeWidth(), recipe.getRecipeHeight(), recipe.getIngredients(), recipe.getResultItem().copy());
		}

		@Override
		public ShapedRecipe fromNetwork(ResourceLocation p_199426_1_, PacketBuffer p_199426_2_)
		{
			ShapedRecipe recipe = super.fromNetwork(p_199426_1_, p_199426_2_);
			return new ColoredShapedRecipe(recipe.getId(), recipe.getGroup(), recipe.getRecipeWidth(), recipe.getRecipeHeight(), recipe.getIngredients(), recipe.getResultItem().copy());
		}
	}
}
