package net.splatcraft.forge.crafting;

import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.common.util.JsonUtils;

public class WeaponWorkbenchSubtypeRecipe extends AbstractWeaponWorkbenchRecipe
{

    public WeaponWorkbenchSubtypeRecipe(ResourceLocation id, String name, ItemStack recipeOutput, NonNullList<StackedIngredient> recipeItems)
    {
        super(id, name, recipeOutput, recipeItems);
    }

    public static WeaponWorkbenchSubtypeRecipe fromJson(ResourceLocation recipeId, JsonObject json)
    {

        JsonObject resultJson = GsonHelper.getAsJsonObject(json, "result");
        ItemStack output = ShapedRecipe.itemStackFromJson(resultJson);

        if (resultJson.has("nbt"))
            output.setTag(JsonUtils.readNBT(resultJson, "nbt"));

        NonNullList<StackedIngredient> input = readIngredients(json.getAsJsonArray("ingredients"));
        String name = json.has("name") ? GsonHelper.getAsString(json, "name") : "null";

        return new WeaponWorkbenchSubtypeRecipe(recipeId, name, output, input);
    }

    public static WeaponWorkbenchSubtypeRecipe fromBuffer(ResourceLocation recipeId, FriendlyByteBuf buffer)
    {
        int i = buffer.readVarInt();
        NonNullList<StackedIngredient> input = NonNullList.withSize(i, StackedIngredient.EMPTY);

        for (int j = 0; j < input.size(); ++j)
        {
            input.set(j, new StackedIngredient(Ingredient.fromNetwork(buffer), buffer.readInt()));
        }

        return new WeaponWorkbenchSubtypeRecipe(recipeId, buffer.readUtf(), buffer.readItem(), input);
    }

    public void toBuffer(FriendlyByteBuf buffer)
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
