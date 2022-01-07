package com.cibernet.splatcraft.crafting;

import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import com.cibernet.splatcraft.registries.SplatcraftInkColors;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import com.cibernet.splatcraft.util.ColorUtils;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;

public class InkVatColorRecipe implements IRecipe<IInventory>
{
    protected static final ArrayList<Integer> omniColors = Lists.newArrayList();
    protected final Ingredient ingredient;
    protected final int color;
    protected final boolean disableOmni;
    protected final ResourceLocation id;

    private static final Logger LOGGER = LogManager.getLogger();

    public InkVatColorRecipe(ResourceLocation id, Ingredient input, int outputColor, boolean disableOmni)
    {
        this.id = id;
        this.disableOmni = disableOmni;
        ingredient = input;
        color = outputColor;

        if (!disableOmni && !omniColors.contains(color))
        {
            omniColors.add(color);
        }
    }

    public static Collection<Integer> getOmniList()
    {
        return omniColors;
    }

    @Override
    public boolean matches(IInventory inv, World levelIn)
    {
        return ingredient.test(inv.getItem(3));
    }

    @Override
    public ItemStack assemble(IInventory inv)
    {
        return inv.getItem(0);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return true;
    }

    @Override
    public ItemStack getResultItem()
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

    @Override
    public ItemStack getToastSymbol()
    {
        return new ItemStack(SplatcraftItems.inkVat);
    }

    public static class InkVatColorSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<InkVatColorRecipe>
    {

        public InkVatColorSerializer(String name)
        {
            super();
            setRegistryName(name);
        }

        @Override
        public InkVatColorRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            Ingredient ingredient = json.has("filter") ? Ingredient.fromJson(json.get("filter")) : Ingredient.EMPTY;
            boolean disableOmni = json.has("not_on_omni_filter") && JSONUtils.getAsBoolean(json, "not_on_omni_filter");
            int color;

            try
            {
                color = JSONUtils.getAsInt(json, "color");
            } catch (JsonSyntaxException jse)
            {
                String colorStr = JSONUtils.getAsString(json, "color");
                try {
                    color = Integer.parseInt(colorStr, 16);
                } catch (NumberFormatException nfe)
                {
                    try
                    {
                        color = SplatcraftInkColors.REGISTRY.getValue(new ResourceLocation(colorStr)).getColor();
                    } catch (NullPointerException npe)
                    {
                        LOGGER.error("Parsing error loading recipe {}", recipeId, npe);
                        return null;
                    }
                }
            }


            return new InkVatColorRecipe(recipeId, ingredient, color, disableOmni);
        }

        @Nullable
        @Override
        public InkVatColorRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer)
        {
            return new InkVatColorRecipe(recipeId, Ingredient.fromNetwork(buffer), buffer.readInt(), buffer.readBoolean());
        }

        @Override
        public void toNetwork(PacketBuffer buffer, InkVatColorRecipe recipe)
        {
            recipe.ingredient.toNetwork(buffer);
            buffer.writeInt(recipe.color);
            buffer.writeBoolean(recipe.disableOmni);
        }
    }
}
