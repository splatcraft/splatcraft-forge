package com.cibernet.splatcraft.crafting;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class WeaponWorkbenchSubtypeRecipe extends AbstractWeaponWorkbenchRecipe
{
	private final ResourceLocation parentRecipe;
	
	public WeaponWorkbenchSubtypeRecipe(ResourceLocation id, String name, ResourceLocation parentRecipe, ItemStack recipeOutput, NonNullList<Ingredient> recipeItems)
	{
		super(id, name, recipeOutput, recipeItems);
		this.parentRecipe = parentRecipe;
	}
	
	public WeaponWorkbenchRecipe getParentRecipe(World world)
	{
		IRecipe recipe = world.getRecipeManager().getRecipe(parentRecipe).get();
		return recipe instanceof WeaponWorkbenchRecipe ? (WeaponWorkbenchRecipe) recipe : null;
	}
	
	public static class Serialzier extends AbstractWeaponWorkbenchRecipe.Serializer<WeaponWorkbenchSubtypeRecipe>
	{
		
		public Serialzier(String name)
		{
			super(name);
		}
		
		@Override
		public WeaponWorkbenchSubtypeRecipe read(ResourceLocation recipeId, JsonObject json)
		{
			ResourceLocation parent = new ResourceLocation(JSONUtils.getString(json,"parent"));
			ItemStack output = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
			NonNullList<Ingredient> input = readIngredients(json.getAsJsonArray("ingredients"));
			String name = JSONUtils.getString(json, "name");
			
			return new WeaponWorkbenchSubtypeRecipe(recipeId, name, parent, output, input);
		}
		
		@Nullable
		@Override
		public WeaponWorkbenchSubtypeRecipe read(ResourceLocation recipeId, PacketBuffer buffer)
		{
			int i = buffer.readVarInt();
			NonNullList<Ingredient> input = NonNullList.withSize(i, Ingredient.EMPTY);
			
			for(int j = 0; j < input.size(); ++j)
				input.set(j, Ingredient.read(buffer));
			
			return new WeaponWorkbenchSubtypeRecipe(recipeId, buffer.readString(), buffer.readResourceLocation(), buffer.readItemStack(), input);
		}
		
		@Override
		public void write(PacketBuffer buffer, WeaponWorkbenchSubtypeRecipe recipe)
		{
			buffer.writeVarInt(recipe.recipeItems.size());
			for(Ingredient ingredient : recipe.recipeItems)
				ingredient.write(buffer);
			buffer.writeString(recipe.name);
			buffer.writeResourceLocation(recipe.parentRecipe);
			buffer.writeItemStack(recipe.recipeOutput);
			
			
		}
	}
}
