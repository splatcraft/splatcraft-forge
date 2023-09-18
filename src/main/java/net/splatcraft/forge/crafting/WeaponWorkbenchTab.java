package net.splatcraft.forge.crafting;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WeaponWorkbenchTab implements Recipe<Container>, Comparable<WeaponWorkbenchTab>
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
    public boolean matches(Container inv, Level levelIn)
    {
        return true;
    }

    @Override
    public ItemStack assemble(Container inv)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return false;
    }

    @Override
    public ItemStack getResultItem()
    {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return SplatcraftRecipeTypes.WEAPON_STATION_TAB;
    }

    @Override
    public RecipeType<?> getType()
    {
        return SplatcraftRecipeTypes.WEAPON_STATION_TAB_TYPE;
    }

    public List<WeaponWorkbenchRecipe> getTabRecipes(Level level, Player player)
    {
        List<Recipe<?>> stream = level.getRecipeManager().getRecipes().stream().filter(recipe -> recipe instanceof WeaponWorkbenchRecipe wwRecipe && wwRecipe.getTab(level).equals(this) && !wwRecipe.getAvailableRecipes(player).isEmpty()).collect(Collectors.toList());
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

    public static class WeaponWorkbenchTabSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<WeaponWorkbenchTab>
    {

        public WeaponWorkbenchTabSerializer(String name)
        {
            super();
            setRegistryName(name);
        }

        @Override
        public WeaponWorkbenchTab fromJson(ResourceLocation recipeId, JsonObject json)
        {
            return new WeaponWorkbenchTab(recipeId, new ResourceLocation(GsonHelper.getAsString(json, "icon")), json.has("pos") ? GsonHelper.getAsInt(json, "pos") : Integer.MAX_VALUE);
        }

        @Nullable
        @Override
        public WeaponWorkbenchTab fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            return new WeaponWorkbenchTab(recipeId, buffer.readResourceLocation(), buffer.readInt());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, WeaponWorkbenchTab recipe)
        {
            buffer.writeResourceLocation(recipe.iconLoc);
            buffer.writeInt(recipe.pos);
        }
    }
}
