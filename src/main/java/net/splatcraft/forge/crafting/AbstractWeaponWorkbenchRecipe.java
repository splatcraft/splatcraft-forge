package net.splatcraft.forge.crafting;

import com.google.gson.JsonArray;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public abstract class AbstractWeaponWorkbenchRecipe implements Recipe<Container>
{
    protected final ResourceLocation id;
    protected final ItemStack recipeOutput;
    protected final NonNullList<StackedIngredient> recipeItems;
    protected final String name;

    public AbstractWeaponWorkbenchRecipe(ResourceLocation id, String name, ItemStack recipeOutput, NonNullList<StackedIngredient> recipeItems)
    {
        this.id = id;
        this.recipeOutput = recipeOutput;
        this.recipeItems = recipeItems;
        this.name = name;
    }


    @Override
    public boolean matches(Container inv, @NotNull Level levelIn)
    {
        List<ItemStack> inputs = new java.util.ArrayList<>();
        int i = 0;

        for (int j = 0; j < inv.getContainerSize(); ++j)
        {
            ItemStack itemstack = inv.getItem(j);
            if (!itemstack.isEmpty())
            {
                ++i;
                inputs.add(itemstack);
            }
        }

        return i == this.recipeItems.size() && net.minecraftforge.common.util.RecipeMatcher.findMatches(inputs, this.recipeItems) != null;
    }

    public Component getName()
    {
        return new TranslatableComponent(name);
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull Container inv)
    {
        return recipeOutput;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return false;
    }

    @Override
    public @NotNull ItemStack getResultItem()
    {
        return recipeOutput;
    }

    @Override
    public @NotNull ResourceLocation getId()
    {
        return id;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer()
    {
        return SplatcraftRecipeTypes.WEAPON_STATION;
    }

    @Override
    public @NotNull RecipeType<?> getType()
    {
        return SplatcraftRecipeTypes.WEAPON_STATION_TYPE;
    }

    public ItemStack getOutput()
    {
        return recipeOutput;
    }

    public NonNullList<StackedIngredient> getInput()
    {
        return recipeItems;
    }


    protected static NonNullList<StackedIngredient> readIngredients(JsonArray p_199568_0_)
    {
        NonNullList<StackedIngredient> nonnulllist = NonNullList.create();

        for (int i = 0; i < p_199568_0_.size(); ++i)
        {
            StackedIngredient ingredient = StackedIngredient.deserialize(p_199568_0_.get(i));
            if (!ingredient.getIngredient().isEmpty() && ingredient.getCount() > 0)
            {
                nonnulllist.add(ingredient);
            }
        }

        return nonnulllist;
    }
}
