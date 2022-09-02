package net.splatcraft.forge.tileentities.container;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.NonNullList;
import net.splatcraft.forge.blocks.InkVatBlock;
import net.splatcraft.forge.crafting.InkVatColorRecipe;
import net.splatcraft.forge.crafting.SplatcraftRecipeTypes;
import net.splatcraft.forge.data.SplatcraftTags;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.c2s.UpdateBlockColorPacket;
import net.splatcraft.forge.registries.SplatcraftBlocks;
import net.splatcraft.forge.registries.SplatcraftInkColors;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.registries.SplatcraftStats;
import net.splatcraft.forge.registries.SplatcraftTileEntitites;
import net.splatcraft.forge.tileentities.InkVatTileEntity;
import net.splatcraft.forge.util.InkColor;

public class InkVatContainer extends Container
{

    public final InkVatTileEntity te;
    private final IWorldPosCallable callableInteract;
    private List<Integer> recipes = Lists.newArrayList();

    public InkVatContainer(final int windowId, final PlayerInventory playerInv, final InkVatTileEntity te, boolean updateSelectedRecipe)
    {
        super(SplatcraftTileEntitites.inkVatContainer, windowId);
        this.te = te;
        this.callableInteract = IWorldPosCallable.create(te.getLevel(), te.getBlockPos());

        addSlot(new SlotInput(new ItemStack(Items.INK_SAC, 1), te, 0, 26, 70));
        addSlot(new SlotInput(new ItemStack(SplatcraftItems.powerEgg), te, 1, 46, 70));
        addSlot(new SlotInput(new ItemStack(SplatcraftBlocks.emptyInkwell), te, 2, 92, 82));
        addSlot(new SlotFilter(te, 3, 36, 89));
        addSlot(new SlotOutput(playerInv.player, te, 4, 112, 82));

        for (int xx = 0; xx < 9; xx++)
        {
            for (int yy = 0; yy < 3; yy++)
            {
                addSlot(new Slot(playerInv, xx + yy * 9 + 9, 8 + xx * 18, 126 + yy * 18));
            }
        }
        for (int xx = 0; xx < 9; xx++)
        {
            addSlot(new Slot(playerInv, xx, 8 + xx * 18, 184));
        }

        if (updateSelectedRecipe)
        {
            updateSelectedRecipe();
        }
    }

    public InkVatContainer(final int windowId, final PlayerInventory inv, final PacketBuffer buffer)
    {
        this(windowId, inv, getBlockEntity(inv, buffer), true);
    }

    private static InkVatTileEntity getBlockEntity(PlayerInventory inventory, PacketBuffer buffer)
    {
        Objects.requireNonNull(inventory);
        Objects.requireNonNull(buffer);

        final TileEntity te = inventory.player.level.getBlockEntity(buffer.readBlockPos());

        if (te instanceof InkVatTileEntity)
        {
            return (InkVatTileEntity) te;
        }
        throw new IllegalStateException("TileEntity is not correct " + te);
    }

    public static List<Integer> getRecipeList(InkVatTileEntity te)
    {
        return hasIngredients(te) ? getAvailableRecipes(te) : Collections.EMPTY_LIST;
    }

    public static boolean hasIngredients(InkVatTileEntity te)
    {
        return !te.getItem(0).isEmpty() && !te.getItem(1).isEmpty() && !te.getItem(2).isEmpty();
    }

    public static List<Integer> sortRecipeList(List<Integer> list)
    {
        list.sort((o1, o2) ->
        {
            if (InkColor.getByHex(o1) != null)
            {
                if (InkColor.getByHex(o2) != null)
                {
                    return InkColor.getByHex(o1).compareTo(InkColor.getByHex(o2));
                }
                return -1;
            } else if (InkColor.getByHex(o2) != null)
            {
                return 1;
            }
            return o1 - o2;
        });

        return list;
    }

    public static List<Integer> getAvailableRecipes(InkVatTileEntity te)
    {
        List<Integer> recipes = Lists.newArrayList();
        if (te.hasOmniFilter())
        {
            recipes = getOmniList();
        } else
        {
            for (InkVatColorRecipe recipe : te.getLevel().getRecipeManager().getRecipesFor(SplatcraftRecipeTypes.INK_VAT_COLOR_CRAFTING_TYPE, te, te.getLevel()))
            {
                if (recipe.matches(te, te.getLevel()))
                {
                    recipes.add(recipe.getOutputColor());
                }
            }
        }

        return recipes;
    }

    public static List<Integer> getOmniList()
    {
        List<Integer> list = Lists.newArrayList();
        list.addAll(InkVatColorRecipe.getOmniList());

        for (InkColor color : SplatcraftInkColors.REGISTRY)
        {
            int c = color.getColor();
            if (!list.contains(c))
            {
                list.add(c);
            }
        }

        return list;
    }

    @Override
    public boolean clickMenuButton(PlayerEntity playerIn, int id)
    {
        if (this.isIndexInBounds(id))
        {
            te.pointer = id;
            this.updateRecipeResult();
        }

        return true;
    }

    public void updateSelectedRecipe()
    {
        int i = 0;
        int teColor = te.getColor();

        updateInkVatColor(te.pointer, te.pointer == -1 ? -1 : teColor);
    }

    public void updateInkVatColor(int pointer, int color)
    {
        te.pointer = pointer;

        if (te.getLevel().isClientSide)
        {
            SplatcraftPacketHandler.sendToServer(new UpdateBlockColorPacket(te.getBlockPos(), color, pointer));
        } else if (te.getBlockState().getBlock() instanceof InkVatBlock)
        {
            ((InkVatBlock) te.getBlockState().getBlock()).setColor(te.getLevel(), te.getBlockPos(), color);
        }

    }

    public int getSelectedRecipe()
    {
        return te.pointer;
    }

    public List<Integer> getRecipeList()
    {
        return hasIngredients(te) ? this.recipes : Collections.EMPTY_LIST;
    }

    public int getRecipeListSize()
    {
        return this.recipes.size();
    }

    public List<Integer> sortRecipeList()
    {
        return sortRecipeList(getRecipeList());
    }

    private void updateAvailableRecipes()
    {
        te.pointer = -1;
        te.setColorAndUpdate(-1);

        recipes = getAvailableRecipes(te);
        te.setRecipeEntries(recipes.size());
    }

    private void updateRecipeResult()
    {
        if (!this.recipes.isEmpty() && this.isIndexInBounds(te.pointer))
        {
            te.setColorAndUpdate(this.recipes.get(te.pointer));
        } else
        {
            te.setColorAndUpdate(-1);
        }

        this.broadcastChanges();
    }

    private boolean isIndexInBounds(int i)
    {
        return i >= 0 && i < this.recipes.size();
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn)
    {
        return stillValid(callableInteract, playerIn, SplatcraftBlocks.inkVat);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem())
        {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index == 4)
            {
                NonNullList<ItemStack> inv = getItems();
                int countA = inv.get(0).getCount();
                int countB = inv.get(1).getCount();
                int countC = inv.get(2).getCount();
                int itemCount = Math.min(Math.max(0, Math.min(countA, Math.min(countB, countC))), new ItemStack(SplatcraftBlocks.inkwell).getMaxStackSize());
                itemstack1.setCount(itemCount);

                if (this.moveItemStackTo(itemstack1, 5, this.slots.size(), true) && itemCount > 0)
                {
                    te.removeItem(0, itemCount);
                    te.removeItem(1, itemCount);
                    te.removeItem(2, itemCount);
                    playerIn.awardStat(SplatcraftStats.INKWELLS_CRAFTED, itemCount);
                }
                return ItemStack.EMPTY;
            } else if (index < 4)
            {
                if (!this.moveItemStackTo(itemstack1, 5, this.slots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 5, false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.set(ItemStack.EMPTY);
            } else
            {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    static class SlotInput extends Slot
    {
        final ItemStack validItem;

        public SlotInput(ItemStack validItem, IInventory inventoryIn, int index, int xPosition, int yPosition)
        {
            super(inventoryIn, index, xPosition, yPosition);
            this.validItem = validItem;
        }

        @Override
        public boolean mayPlace(ItemStack stack)
        {
            return stack.equals(validItem, false);
        }
    }

    static class SlotOutput extends Slot
    {
        PlayerEntity player;

        public SlotOutput(PlayerEntity player, IInventory inventoryIn, int index, int xPosition, int yPosition)
        {
            super(inventoryIn, index, xPosition, yPosition);
            this.player = player;
        }

        @Override
        public boolean mayPlace(ItemStack stack)
        {
            return false;
        }

        @Override
        public ItemStack remove(int amount)
        {
            player.awardStat(SplatcraftStats.INKWELLS_CRAFTED, amount);
            return super.remove(amount);
        }
    }

    class SlotFilter extends Slot
    {
        public SlotFilter(IInventory inventoryIn, int index, int xPosition, int yPosition)
        {
            super(inventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack)
        {
            return SplatcraftTags.Items.FILTERS.contains(stack.getItem());
        }

        @Override
        public void setChanged()
        {
            super.setChanged();
            updateAvailableRecipes();
        }
    }
}
