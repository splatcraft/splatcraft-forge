package net.splatcraft.forge.crafting;

import net.splatcraft.forge.blocks.InkwellBlock;
import net.splatcraft.forge.data.SplatcraftTags;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.util.ColorUtils;
import net.minecraft.block.Block;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class SingleUseSubRecipe extends SpecialRecipe
{
    public SingleUseSubRecipe(ResourceLocation idIn) {
        super(idIn);
    }

    @Override
    public boolean matches(CraftingInventory inv, World levelIn) {
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
                    if (!itemstack.getItem().is(SplatcraftTags.Items.SUB_WEAPONS))
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
    public ItemStack assemble(CraftingInventory inv)
    {

        ItemStack itemstack = ItemStack.EMPTY;
        int color = 0xFFFFFF;

        for(int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack itemstack1 = inv.getItem(i);
            if (!itemstack1.isEmpty()) {
                Item item = itemstack1.getItem();
                if (item.is(SplatcraftTags.Items.SUB_WEAPONS))
                    itemstack = itemstack1;
                else if(Block.byItem(item) instanceof InkwellBlock)
                    color = ColorUtils.getInkColor(itemstack1);
            }
        }

        ItemStack result = ColorUtils.setInkColor(itemstack.copy(), color);
        ColorUtils.setColorLocked(result, color != -1);
        result.getOrCreateTag().putBoolean("SingleUse", true);

        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv)
    {
        NonNullList<ItemStack> restult = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

        for(int i = 0; i < inv.getContainerSize(); ++i)
        {
            ItemStack stack = inv.getItem(i);
            Item item = stack.getItem();
            if(Block.byItem(item) instanceof InkwellBlock)
                restult.set(i, new ItemStack(SplatcraftItems.emptyInkwell));
            else if(item.is(SplatcraftTags.Items.SUB_WEAPONS))
                restult.set(i, stack.copy());
        }

        return restult;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 3;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SplatcraftRecipeTypes.SINGLE_USE_SUB;
    }
}
