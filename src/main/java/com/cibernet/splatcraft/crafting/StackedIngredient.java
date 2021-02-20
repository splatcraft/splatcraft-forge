package com.cibernet.splatcraft.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JSONUtils;

import javax.annotation.Nullable;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StackedIngredient implements Predicate<ItemStack> {
    protected final Ingredient ingredient;
    protected final int count;

    public static final StackedIngredient EMPTY = new StackedIngredient(Ingredient.EMPTY, 0);

    protected StackedIngredient(Ingredient ingredient, int count)
    {
        this.ingredient = ingredient;
        this.count = count;
    }

    public Ingredient getIngredient()
    {
        return ingredient;
    }

    public int getCount()
    {
        return count;
    }

    public static StackedIngredient deserialize(@Nullable JsonElement json) {
        if (json != null && !json.isJsonNull() && json.isJsonObject())
            return new StackedIngredient(Ingredient.deserialize(json), JSONUtils.getInt(json.getAsJsonObject(), "count"));
        else throw new JsonSyntaxException("Item cannot be null");
    }

    @Override
    public boolean test(ItemStack itemStack)
    {
        return getIngredient().test(itemStack);
    }
}
