package net.splatcraft.forge.crafting;

import com.google.gson.JsonArray;
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

public class WeaponWorkbenchRecipe implements Recipe<Container>, Comparable<WeaponWorkbenchRecipe>
{
    protected final ResourceLocation id;
    protected final ResourceLocation tab;
    protected final List<WeaponWorkbenchSubtypeRecipe> subRecipes;
    protected final int pos;

    public WeaponWorkbenchRecipe(ResourceLocation id, ResourceLocation tab, int pos, List<WeaponWorkbenchSubtypeRecipe> subRecipes)
    {
        this.id = id;
        this.pos = pos;
        this.tab = tab;
        this.subRecipes = subRecipes;
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
        return subRecipes.isEmpty() ? ItemStack.EMPTY : subRecipes.get(0).getOutput().copy();
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return SplatcraftRecipeTypes.WEAPON_STATION;
    }

    @Override
    public RecipeType<?> getType()
    {
        return SplatcraftRecipeTypes.WEAPON_STATION_TYPE;
    }

    @Override
    public int compareTo(WeaponWorkbenchRecipe o)
    {
        return pos - o.pos;
    }

    @Nullable
    public WeaponWorkbenchTab getTab(Level level)
    {
        return (WeaponWorkbenchTab) level.getRecipeManager().byKey(tab).orElse(null);
    }

    public WeaponWorkbenchSubtypeRecipe getRecipeFromIndex(Player player, int subTypePos)
    {
        return getAvailableRecipes(player).get(subTypePos);
    }

    public int getAvailableRecipesTotal(Player player)
    {
        return getAvailableRecipes(player).size();
    }

    public List<WeaponWorkbenchSubtypeRecipe> getAvailableRecipes(Player player)
    {
        return subRecipes.stream().filter(weaponWorkbenchSubtypeRecipe -> weaponWorkbenchSubtypeRecipe.isAvailable(player)).toList();
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<WeaponWorkbenchRecipe>
    {

        public Serializer(String name)
        {
            super();
            setRegistryName(name);
        }

        @Override
        public WeaponWorkbenchRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            List<WeaponWorkbenchSubtypeRecipe> recipes = new ArrayList<>();
            JsonArray arr = json.getAsJsonArray("recipes");

            for (int i = 0; i < arr.size(); i++)
            {
                ResourceLocation id = new ResourceLocation(recipeId.getNamespace(), recipeId.getPath() + "subtype" + i);
                recipes.add(WeaponWorkbenchSubtypeRecipe.fromJson(id, arr.get(i).getAsJsonObject()));
            }

            for(WeaponWorkbenchSubtypeRecipe r : recipes)
            {
                r.siblings.clear();
                r.siblings.addAll(recipes);
                r.siblings.removeIf(o -> o.equals(r));
            }


            return new WeaponWorkbenchRecipe(recipeId, new ResourceLocation(GsonHelper.getAsString(json, "tab")), json.has("pos") ? GsonHelper.getAsInt(json, "pos") : Integer.MAX_VALUE, recipes);
        }

        @Nullable
        @Override
        public WeaponWorkbenchRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            List<WeaponWorkbenchSubtypeRecipe> s = new ArrayList<>();
            int count = buffer.readInt();
            for (int i = 0; i < count; i++)
            {
                ResourceLocation loc = buffer.readResourceLocation();
                s.add(WeaponWorkbenchSubtypeRecipe.fromBuffer(loc, buffer));
            }

            ResourceLocation loc = buffer.readResourceLocation();

            return new WeaponWorkbenchRecipe(recipeId, loc, buffer.readInt(), s);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, WeaponWorkbenchRecipe recipe)
        {
            buffer.writeInt(recipe.subRecipes.size());

            for (WeaponWorkbenchSubtypeRecipe s : recipe.subRecipes)
            {
                buffer.writeResourceLocation(s.id);
                s.toBuffer(buffer);
            }

            buffer.writeResourceLocation(recipe.tab);
            buffer.writeInt(recipe.pos);
        }
    }
}
