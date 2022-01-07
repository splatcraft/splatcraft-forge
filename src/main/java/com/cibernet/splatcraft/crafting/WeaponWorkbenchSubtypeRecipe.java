package com.cibernet.splatcraft.crafting;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class WeaponWorkbenchSubtypeRecipe extends AbstractWeaponWorkbenchRecipe
{

    public WeaponWorkbenchSubtypeRecipe(ResourceLocation id, String name, ItemStack recipeOutput, NonNullList<StackedIngredient> recipeItems)
    {
        super(id, name, recipeOutput, recipeItems);
    }

    public static WeaponWorkbenchSubtypeRecipe fromJson(ResourceLocation recipeId, JsonObject json)
    {

        JsonObject resultJson = JSONUtils.getAsJsonObject(json, "result");
        ItemStack output = ShapedRecipe.itemFromJson(resultJson);
        try
        {
            if (resultJson.has("nbt"))
                output.setTag(new JsonToNBT(new StringReader(JSONUtils.convertToString(JSONUtils.getAsJsonObject(resultJson, "nbt"), "nbt"))).readStruct());
        } catch (CommandSyntaxException e)
        {
            e.printStackTrace();
        }

        NonNullList<StackedIngredient> input = readIngredients(json.getAsJsonArray("ingredients"));
        String name = json.has("name") ? JSONUtils.getAsString(json, "name") : "null";

        return new WeaponWorkbenchSubtypeRecipe(recipeId, name, output, input);
    }

    public static WeaponWorkbenchSubtypeRecipe fromBuffer(ResourceLocation recipeId, PacketBuffer buffer)
    {
        int i = buffer.readVarInt();
        NonNullList<StackedIngredient> input = NonNullList.withSize(i, StackedIngredient.EMPTY);

        for (int j = 0; j < input.size(); ++j)
        {
            input.set(j, new StackedIngredient(Ingredient.fromNetwork(buffer), buffer.readInt()));
        }

        return new WeaponWorkbenchSubtypeRecipe(recipeId, buffer.readUtf(), buffer.readItem(), input);
    }

    public void toBuffer(PacketBuffer buffer)
    {
        buffer.writeVarInt(this.recipeItems.size());
        for (StackedIngredient ingredient : this.recipeItems)
        {
            ingredient.getIngredient().toNetwork(buffer);
            buffer.writeInt(ingredient.getCount());
        }
        buffer.writeUtf(this.name);
        buffer.writeItem(this.recipeOutput);


    }
}
