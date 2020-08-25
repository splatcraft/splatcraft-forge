package com.cibernet.splatcraft.crafting;

import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import com.cibernet.splatcraft.util.ColorUtils;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class InkVatColorRecipe implements IRecipe<IInventory>
{
	protected final Ingredient ingredient;
	protected final int color;
	protected final boolean disableOmni;
	protected final ResourceLocation id;
	
	protected static final ArrayList<Integer> omniColors = Lists.newArrayList();
	
	public InkVatColorRecipe(ResourceLocation id, Ingredient input, int outputColor, boolean disableOmni)
	{
		this.id = id;
		this.disableOmni = disableOmni;
		ingredient = input;
		color = outputColor;
		
		if(!disableOmni && !omniColors.contains(color))
			omniColors.add(color);
	}
	
	public static Collection<Integer> getOmniList()
	{
		return omniColors;
	}
	
	@Override
	public boolean matches(IInventory inv, World worldIn)
	{
		return ingredient.test(inv.getStackInSlot(3));
	}
	
	@Override
	public ItemStack getCraftingResult(IInventory inv)
	{
		return inv.getStackInSlot(0);
	}
	
	@Override
	public boolean canFit(int width, int height)
	{
		return true;
	}
	
	@Override
	public ItemStack getRecipeOutput()
	{
		return ColorUtils.setInkColor(new ItemStack(SplatcraftBlocks.inkwell), color);
	}
	
	public int getOutputColor()
	{
		return color;
	}
	
	@Override
	public ResourceLocation getId()
	{
		return id;
	}
	
	@Override
	public IRecipeSerializer<?> getSerializer()
	{
		return SplatcraftRecipeTypes.INK_VAT_COLOR_CRAFTING;
	}
	
	@Override
	public IRecipeType<?> getType()
	{
		return SplatcraftRecipeTypes.INK_VAT_COLOR_CRAFTING_TYPE;
	}
	
	public static class InkVatColorSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<InkVatColorRecipe>
	{
		
		public InkVatColorSerializer(String name)
		{
			super();
			setRegistryName(name);
		}
		
		@Override
		public InkVatColorRecipe read(ResourceLocation recipeId, JsonObject json)
		{
			Ingredient ingredient = json.has("filter") ? Ingredient.deserialize(json.get("filter")) : Ingredient.EMPTY;
			boolean disableOmni = json.has("not_on_omni_filter") ? JSONUtils.getBoolean(json, "not_on_omni_filter") : false;
			Integer color;
			
			try
			{
				color = Integer.parseInt(JSONUtils.getString(json, "color"), 16);
			} catch(NumberFormatException e)
			{
				color = JSONUtils.getInt(json, "color");
			}
			
			
			
			return new InkVatColorRecipe(recipeId, ingredient, color, disableOmni);
		}
		
		@Nullable
		@Override
		public InkVatColorRecipe read(ResourceLocation recipeId, PacketBuffer buffer)
		{
			return new InkVatColorRecipe(recipeId, Ingredient.read(buffer), buffer.readInt(), buffer.readBoolean());
		}
		
		@Override
		public void write(PacketBuffer buffer, InkVatColorRecipe recipe)
		{
			recipe.ingredient.write(buffer);
			buffer.writeInt(recipe.color);
			buffer.writeBoolean(recipe.disableOmni);
		}
	}
}
