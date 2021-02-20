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
	
	public WeaponWorkbenchSubtypeRecipe(ResourceLocation id, String name, ItemStack recipeOutput, NonNullList<StackedIngredient> recipeItems)
	{
		super(id, name, recipeOutput, recipeItems);
	}

	public static WeaponWorkbenchSubtypeRecipe fromJson(ResourceLocation recipeId, JsonObject json)
	{

		ItemStack output = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
		NonNullList<StackedIngredient> input = Serialzier.readIngredients(json.getAsJsonArray("ingredients"));
		String name = JSONUtils.hasField(json, "name") ? JSONUtils.getString(json, "name") : "null";

		return new WeaponWorkbenchSubtypeRecipe(recipeId, name, output, input);
	}

	public static WeaponWorkbenchSubtypeRecipe fromBuffer(ResourceLocation recipeId, PacketBuffer buffer)
	{
		int i = buffer.readVarInt();
		NonNullList<StackedIngredient> input = NonNullList.withSize(i, StackedIngredient.EMPTY);

		for(int j = 0; j < input.size(); ++j)
			input.set(j, new StackedIngredient(Ingredient.read(buffer), buffer.readInt()));

		return new WeaponWorkbenchSubtypeRecipe(recipeId, buffer.readString(), buffer.readItemStack(), input);
	}

	public void toBuffer(PacketBuffer buffer)
	{
		buffer.writeVarInt(this.recipeItems.size());
		for(StackedIngredient ingredient : this.recipeItems)
		{
			ingredient.getIngredient().write(buffer);
			buffer.writeInt(ingredient.getCount());
		}
		buffer.writeString(this.name);
		buffer.writeItemStack(this.recipeOutput);


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
			ItemStack output = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
			NonNullList<StackedIngredient> input = readIngredients(json.getAsJsonArray("ingredients"));
			String name = JSONUtils.getString(json, "name");

			return new WeaponWorkbenchSubtypeRecipe(recipeId, name, output, input);
		}
		
		@Nullable
		@Override
		public WeaponWorkbenchSubtypeRecipe read(ResourceLocation recipeId, PacketBuffer buffer)
		{
			return fromBuffer(recipeId, buffer);
		}
		
		@Override
		public void write(PacketBuffer buffer, WeaponWorkbenchSubtypeRecipe recipe)
		{
			recipe.toBuffer(buffer);
			
		}
	}
}
