package com.cibernet.splatcraft.crafting;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WeaponWorkbenchRecipe implements IRecipe<IInventory>, Comparable<WeaponWorkbenchRecipe>
{
	protected final ResourceLocation id;
	protected final ResourceLocation tab;
	protected final List<WeaponWorkbenchSubtypeRecipe> subRecipes;
	protected final int pos;

	public WeaponWorkbenchRecipe(ResourceLocation id, ResourceLocation tab, int pos, List<WeaponWorkbenchSubtypeRecipe> subRecipes) {
		this.id = id;
		this.pos = pos;
		this.tab = tab;
		this.subRecipes = subRecipes;
	}

	@Override
	public boolean matches(IInventory inv, World worldIn) {
		return true;
	}

	@Override
	public ItemStack getCraftingResult(IInventory inv) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canFit(int width, int height) {
		return false;
	}

	@Override
	public ItemStack getRecipeOutput()
	{
		return subRecipes.isEmpty() ? ItemStack.EMPTY : subRecipes.get(0).getOutput();
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return SplatcraftRecipeTypes.WEAPON_STATION_TAB;
	}

	@Override
	public IRecipeType<?> getType() {
		return SplatcraftRecipeTypes.WEAPON_STATION_TYPE;
	}

	@Override
	public int compareTo(WeaponWorkbenchRecipe o) {
		return pos - o.pos;
	}

	public WeaponWorkbenchTab getTab(World world)
	{
		return (WeaponWorkbenchTab) world.getRecipeManager().getRecipe(tab).get();
	}

	public WeaponWorkbenchSubtypeRecipe getRecipeFromIndex(int subTypePos)
	{
		return subRecipes.get(subTypePos);
	}
	public int getTotalRecipes()
	{
		return subRecipes.size();
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<WeaponWorkbenchRecipe> {

		public Serializer(String name) {
			super();
			setRegistryName(name);
		}

		@Override
		public WeaponWorkbenchRecipe read(ResourceLocation recipeId, JsonObject json)
		{
			List<WeaponWorkbenchSubtypeRecipe> recipes = new ArrayList<>();
			JsonArray arr = json.getAsJsonArray("recipes");

			for(int i = 0; i < arr.size(); i++)
			{
				ResourceLocation id = new ResourceLocation(recipeId.getNamespace(), recipeId.getPath()+"subtype"+i);
				recipes.add(WeaponWorkbenchSubtypeRecipe.fromJson(id, arr.get(i).getAsJsonObject()));
			}

			return new WeaponWorkbenchRecipe(recipeId, new ResourceLocation(JSONUtils.getString(json, "tab")), json.has("pos") ? JSONUtils.getInt(json, "pos") : Integer.MAX_VALUE, recipes);
		}

		@Nullable
		@Override
		public WeaponWorkbenchRecipe read(ResourceLocation recipeId, PacketBuffer buffer)
		{

			List<WeaponWorkbenchSubtypeRecipe> s = new ArrayList<>();
			for(int i = 0; i < buffer.readInt(); i++)
				s.add(WeaponWorkbenchSubtypeRecipe.fromBuffer(buffer.readResourceLocation(), buffer));

			return new WeaponWorkbenchRecipe(recipeId, buffer.readResourceLocation(), buffer.readInt(), s);
		}

		@Override
		public void write(PacketBuffer buffer, WeaponWorkbenchRecipe recipe)
		{
			buffer.writeInt(recipe.subRecipes.size());

			for(WeaponWorkbenchSubtypeRecipe s : recipe.subRecipes)
			{
				buffer.writeResourceLocation(s.id);
				s.toBuffer(buffer);
			}

			buffer.writeResourceLocation(recipe.tab);
			buffer.writeInt(recipe.pos);
		}
	}
}