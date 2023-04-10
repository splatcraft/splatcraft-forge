package net.splatcraft.forge.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.splatcraft.forge.blocks.InkwellBlock;
import net.splatcraft.forge.data.SplatcraftTags;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.util.ColorUtils;

public class SingleUseSubRecipe extends CustomRecipe
{
    public SingleUseSubRecipe(ResourceLocation idIn) {
        super(idIn);
    }

    @Override
    public boolean matches(CraftingContainer inv, Level levelIn) {
        int sub = 0;
        int inkwell = 0;
        int sardinium = 0;

        for(int k = 0; k < inv.getContainerSize(); ++k) {
            ItemStack itemstack = inv.getItem(k);
            if (!itemstack.isEmpty()) {
                if (Block.byItem(itemstack.getItem()) instanceof InkwellBlock)
                    ++inkwell;
                else
                if (itemstack.getItem().equals(SplatcraftItems.sardinium))
                    ++sardinium;
                else
                {
                    if (!itemstack.is(SplatcraftTags.Items.SUB_WEAPONS))
                        return false;
                    ++sub;
                }

                if (inkwell > 1 || sub > 1 || sardinium > 1)
                    return false;
            }
        }

        return sub == 1 && inkwell == 1 && sardinium == 1;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv)
    {

        ItemStack itemstack = ItemStack.EMPTY;
        int color = 0xFFFFFF;

        for(int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack itemstack1 = inv.getItem(i);
            if (!itemstack1.isEmpty())
            {
                if (itemstack1.is(SplatcraftTags.Items.SUB_WEAPONS))
                    itemstack = itemstack1;
                else if(Block.byItem(itemstack1.getItem()) instanceof InkwellBlock)
                    color = ColorUtils.getInkColor(itemstack1);
            }
        }

        ItemStack result = ColorUtils.setInkColor(itemstack.copy(), color);
        ColorUtils.setColorLocked(result, color != -1);
        result.getOrCreateTag().putBoolean("SingleUse", true);

        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv)
    {
        NonNullList<ItemStack> restult = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

        for(int i = 0; i < inv.getContainerSize(); ++i)
        {
            ItemStack stack = inv.getItem(i);
            if(Block.byItem(stack.getItem()) instanceof InkwellBlock)
                restult.set(i, new ItemStack(SplatcraftItems.emptyInkwell.get()));
            else if(stack.is(SplatcraftTags.Items.SUB_WEAPONS))
                restult.set(i, stack.copy());
        }

        return restult;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SplatcraftRecipeTypes.SINGLE_USE_SUB;
    }
}
