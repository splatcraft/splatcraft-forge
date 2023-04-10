package net.splatcraft.forge.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class StackedIngredient implements Predicate<ItemStack>
{
    public static final StackedIngredient EMPTY = new StackedIngredient(Ingredient.EMPTY, 0);
    protected final Ingredient ingredient;
    protected final int count;

    protected StackedIngredient(Ingredient ingredient, int count)
    {
        this.ingredient = ingredient;
        this.count = count;
    }

    public static StackedIngredient deserialize(@Nullable JsonElement json)
    {
        if (json != null && !json.isJsonNull() && json.isJsonObject())
        {
            return new StackedIngredient(Ingredient.fromJson(json), GsonHelper.getAsInt(json.getAsJsonObject(), "count"));
        } else
        {
            throw new JsonSyntaxException("Item cannot be null");
        }
    }

    public Ingredient getIngredient()
    {
        return ingredient;
    }

    public int getCount()
    {
        return count;
    }

    @Override
    public boolean test(ItemStack itemStack)
    {
        return getIngredient().test(itemStack);
    }
}
