package com.cibernet.splatcraft.crafting;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WeaponWorkbenchTab implements IRecipe<IInventory>, Comparable<WeaponWorkbenchTab>
{
    protected final ResourceLocation id;
    protected final ResourceLocation iconLoc;
    protected final int pos;

    public WeaponWorkbenchTab(ResourceLocation id, ResourceLocation iconLoc, int pos)
    {
        this.id = id;
        this.iconLoc = iconLoc;
        this.pos = pos;
    }

    @Override
    public boolean matches(IInventory inv, World worldIn)
    {
        return true;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height)
    {
        return false;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer()
    {
        return SplatcraftRecipeTypes.WEAPON_STATION_TAB;
    }

    @Override
    public IRecipeType<?> getType()
    {
        return SplatcraftRecipeTypes.WEAPON_STATION_TAB_TYPE;
    }

    public List<WeaponWorkbenchRecipe> getTabRecipes(World world)
    {
        List<IRecipe<?>> stream = world.getRecipeManager().getRecipes().stream().filter(recipe -> recipe instanceof WeaponWorkbenchRecipe && ((WeaponWorkbenchRecipe) recipe).getTab(world).equals(this)).collect(Collectors.toList());
        ArrayList<WeaponWorkbenchRecipe> recipes = Lists.newArrayList();

        stream.forEach(recipe -> recipes.add((WeaponWorkbenchRecipe) recipe));

        return recipes;
    }

    @Override
    public int compareTo(WeaponWorkbenchTab o)
    {
        return pos - o.pos;
    }

    public ResourceLocation getTabIcon()
    {
        return iconLoc;
    }

    @Override
    public String toString()
    {
        return getId().toString();
    }

    public static class WeaponWorkbenchTabSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<WeaponWorkbenchTab>
    {

        public WeaponWorkbenchTabSerializer(String name)
        {
            super();
            setRegistryName(name);
        }

        @Override
        public WeaponWorkbenchTab read(ResourceLocation recipeId, JsonObject json)
        {
            return new WeaponWorkbenchTab(recipeId, new ResourceLocation(JSONUtils.getString(json, "icon")), json.has("pos") ? JSONUtils.getInt(json, "pos") : Integer.MAX_VALUE);
        }

        @Nullable
        @Override
        public WeaponWorkbenchTab read(ResourceLocation recipeId, PacketBuffer buffer)
        {
            return new WeaponWorkbenchTab(recipeId, buffer.readResourceLocation(), buffer.readInt());
        }

        @Override
        public void write(PacketBuffer buffer, WeaponWorkbenchTab recipe)
        {
            buffer.writeResourceLocation(recipe.iconLoc);
            buffer.writeInt(recipe.pos);
        }
    }
}
