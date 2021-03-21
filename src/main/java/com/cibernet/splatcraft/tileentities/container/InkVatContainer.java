package com.cibernet.splatcraft.tileentities.container;

import com.cibernet.splatcraft.blocks.InkVatBlock;
import com.cibernet.splatcraft.crafting.InkVatColorRecipe;
import com.cibernet.splatcraft.crafting.SplatcraftRecipeTypes;
import com.cibernet.splatcraft.data.SplatcraftTags;
import com.cibernet.splatcraft.network.SplatcraftPacketHandler;
import com.cibernet.splatcraft.network.UpdateBlockColorPacket;
import com.cibernet.splatcraft.registries.*;
import com.cibernet.splatcraft.tileentities.InkVatTileEntity;
import com.cibernet.splatcraft.util.InkColor;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.NonNullList;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class InkVatContainer extends Container
{

    public final InkVatTileEntity te;
    private final IWorldPosCallable callableInteract;
    private List<Integer> recipes = Lists.newArrayList();

    public InkVatContainer(final int windowId, final PlayerInventory playerInv, final InkVatTileEntity te, boolean updateSelectedRecipe)
    {
        super(SplatcraftTileEntitites.inkVatContainer, windowId);
        this.te = te;
        this.callableInteract = IWorldPosCallable.of(te.getWorld(), te.getPos());

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
        this(windowId, inv, getTileEntity(inv, buffer), true);
    }

    private static InkVatTileEntity getTileEntity(PlayerInventory inventory, PacketBuffer buffer)
    {
        Objects.requireNonNull(inventory);
        Objects.requireNonNull(buffer);

        final TileEntity te = inventory.player.world.getTileEntity(buffer.readBlockPos());

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
        return !te.getStackInSlot(0).isEmpty() && !te.getStackInSlot(1).isEmpty() && !te.getStackInSlot(2).isEmpty();
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
            for (InkVatColorRecipe recipe : te.getWorld().getRecipeManager().getRecipes(SplatcraftRecipeTypes.INK_VAT_COLOR_CRAFTING_TYPE, te, te.getWorld()))
            {
                if (recipe.matches(te, te.getWorld()))
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
    public boolean enchantItem(PlayerEntity playerIn, int id)
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

        if (te.getWorld().isRemote)
        {
            SplatcraftPacketHandler.sendToServer(new UpdateBlockColorPacket(te.getPos(), color, pointer));
        } else if (te.getBlockState().getBlock() instanceof InkVatBlock)
        {
            ((InkVatBlock) te.getBlockState().getBlock()).setColor(te.getWorld(), te.getPos(), color);
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

        this.detectAndSendChanges();
    }

    private boolean isIndexInBounds(int i)
    {
        return i >= 0 && i < this.recipes.size();
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn)
    {
        return isWithinUsableDistance(callableInteract, playerIn, SplatcraftBlocks.inkVat);
    }

    @Override
    public void updateProgressBar(int id, int data)
    {
        super.updateProgressBar(id, data);
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn)
    {
        super.onCraftMatrixChanged(inventoryIn);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index == 4)
            {
                NonNullList<ItemStack> inv = getInventory();
                int countA = inv.get(0).getCount();
                int countB = inv.get(1).getCount();
                int countC = inv.get(2).getCount();
                int itemCount = Math.min(Math.max(0, Math.min(countA, Math.min(countB, countC))), Item.getItemFromBlock(SplatcraftBlocks.inkwell).getMaxStackSize());
                itemstack1.setCount(itemCount);

                if (this.mergeItemStack(itemstack1, 5, this.inventorySlots.size(), true) && itemCount > 0)
                {
                    te.decrStackSize(0, itemCount);
                    te.decrStackSize(1, itemCount);
                    te.decrStackSize(2, itemCount);
                    playerIn.addStat(SplatcraftStats.INKWELLS_CRAFTED, itemCount);
                }
                return ItemStack.EMPTY;
            } else if (index < 4)
            {
                if (!this.mergeItemStack(itemstack1, 5, this.inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, 5, false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            } else
            {
                slot.onSlotChanged();
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
        public boolean isItemValid(ItemStack stack)
        {
            return stack.isItemEqual(validItem);
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
        public boolean isItemValid(ItemStack stack)
        {
            return false;
        }

        @Override
        public ItemStack decrStackSize(int amount)
        {
            player.addStat(SplatcraftStats.INKWELLS_CRAFTED, amount);
            return super.decrStackSize(amount);
        }
    }

    class SlotFilter extends Slot
    {
        public SlotFilter(IInventory inventoryIn, int index, int xPosition, int yPosition)
        {
            super(inventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(ItemStack stack)
        {
            return SplatcraftTags.Items.FILTERS.contains(stack.getItem());
        }

        @Override
        public void onSlotChanged()
        {
            super.onSlotChanged();
            updateAvailableRecipes();
        }
    }
}
