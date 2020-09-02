package com.cibernet.splatcraft.crafting;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WeaponWorkbenchRecipe extends AbstractWeaponWorkbenchRecipe
{
	private final ResourceLocation tab;
	private final NonNullList<WeaponWorkbenchSubtypeRecipe> subtypes = NonNullList.create();
	
	public WeaponWorkbenchRecipe(ResourceLocation id, String name, ResourceLocation tab, ItemStack recipeOutput, NonNullList<Ingredient> recipeItems)
	{
		super(id, name, recipeOutput, recipeItems);
		this.tab = tab;
	}
	
	public WeaponWorkbenchTab getTab(World world)
	{
		IRecipe tab = world.getRecipeManager().getRecipe(this.tab).get();
		return tab instanceof WeaponWorkbenchTab ? (WeaponWorkbenchTab) tab : null;
	}
	
	public void updateSubtypes(World world)
	{
		subtypes.clear();
		List<IRecipe<?>> stream = world.getRecipeManager().getRecipes().stream().filter(recipe -> recipe instanceof WeaponWorkbenchSubtypeRecipe && ((WeaponWorkbenchSubtypeRecipe) recipe).getParentRecipe(world).equals(this)).collect(Collectors.toList());
		
		stream.forEach(recipe -> subtypes.add((WeaponWorkbenchSubtypeRecipe) recipe));
	}
	
	public AbstractWeaponWorkbenchRecipe getRecipeFromIndex(int index)
	{
		if(index == 0)
			return this;
		return subtypes.get(index+1);
	}
	
	public static class Serializer extends AbstractWeaponWorkbenchRecipe.Serializer<WeaponWorkbenchRecipe>
	{
		
		public Serializer(String name)
		{
			super(name);
		}
		
		@Override
		public WeaponWorkbenchRecipe read(ResourceLocation recipeId, JsonObject json)
		{
			ResourceLocation tab = new ResourceLocation(JSONUtils.getString(json,"tab"));
			ItemStack output = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
			NonNullList<Ingredient> input = readIngredients(json.getAsJsonArray("ingredients"));
			String name = JSONUtils.getString(json, "name");
			
			return new WeaponWorkbenchRecipe(recipeId, name, tab, output, input);
		}
		
		@Nullable
		@Override
		public WeaponWorkbenchRecipe read(ResourceLocation recipeId, PacketBuffer buffer)
		{
			int i = buffer.readVarInt();
			NonNullList<Ingredient> input = NonNullList.withSize(i, Ingredient.EMPTY);
			
			for(int j = 0; j < input.size(); ++j)
				input.set(j, Ingredient.read(buffer));
			
			return new WeaponWorkbenchRecipe(recipeId, buffer.readString(), buffer.readResourceLocation(), buffer.readItemStack(), input);
		}
		
		@Override
		public void write(PacketBuffer buffer, WeaponWorkbenchRecipe recipe)
		{
			buffer.writeVarInt(recipe.recipeItems.size());
			for(Ingredient ingredient : recipe.recipeItems)
				ingredient.write(buffer);
			buffer.writeString(recipe.name);
			buffer.writeResourceLocation(recipe.tab);
			buffer.writeItemStack(recipe.recipeOutput);
		
			
		}
	}
}
