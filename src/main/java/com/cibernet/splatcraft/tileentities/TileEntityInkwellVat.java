package com.cibernet.splatcraft.tileentities;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.items.ItemWeaponBase;
import com.cibernet.splatcraft.recipes.RecipesInkwellVat;
import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.registries.SplatCraftItems;
import com.cibernet.splatcraft.utils.InkColors;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.List;

public class TileEntityInkwellVat extends TileEntity implements IInventory
{

    private NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);
    private String customName;
    public int selectedColor = -1;
    
    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        inventory = NonNullList.withSize(5, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, inventory);
        selectedColor = compound.getInteger("selectedColor");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        ItemStackHelper.saveAllItems(compound, inventory);
        compound.setInteger("selectedColor", selectedColor);
        return super.writeToNBT(compound);
    }

    public void setOutput(InkColors color)
    {
        int countA = getStackInSlot(0).getCount();
        int countB = getStackInSlot(1).getCount();
        int countC = getStackInSlot(2).getCount();
        int itemCount = Math.min(Math.max(0, Math.min(countA, Math.min(countB, countC))), Item.getItemFromBlock(SplatCraftBlocks.inkwell).getItemStackLimit());
        if(countA > 0 && countB > 0 && countC > 0)
            setInventorySlotContents(4, ItemWeaponBase.setInkColor(new ItemStack(SplatCraftBlocks.inkwell, itemCount), color.getColor()));
        else removeOutput();
    }

    public List<InkColors> getColorList()
    {
        ItemStack stack = getRecipeStack();
        int countA = getStackInSlot(0).getCount();
        int countB = getStackInSlot(1).getCount();
        int countC = getStackInSlot(2).getCount();
        if(countA <= 0 || countB <= 0 || countC <= 0)
            return null;
        return RecipesInkwellVat.getOutput(ItemStack.EMPTY);
    }

    public ItemStack getRecipeStack() {return getStackInSlot(3);}
    public void removeOutput() {removeStackFromSlot(3);}

    public void dropInventoryItems()
    {
        for (int i = 0; i < getSizeInventory(); ++i)
        {
            if(i == 3) continue;
            ItemStack itemstack = getStackInSlot(i);

            if (!itemstack.isEmpty())
            {
                InventoryHelper.spawnItemStack(getWorld(), getPos().getX(), getPos().getY(), getPos().getZ(), itemstack);
            }
        }
    }


    @Override
    public int getSizeInventory()
    {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty()
    {
        for (ItemStack itemstack : this.inventory)
        {
            if (!itemstack.isEmpty())
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the stack in the given slot.
     */
    @Override
    public ItemStack getStackInSlot(int index)
    {
        return index >= this.getSizeInventory() ? ItemStack.EMPTY : (ItemStack)this.inventory.get(index);
    }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     */
    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        ItemStack itemstack = ItemStackHelper.getAndSplit(this.inventory, index, count);

        if(index == 4)
        {
            decrStackSize(0, count);
            decrStackSize(1, count);
            decrStackSize(2, count);
            decrStackSize(3, count);
        }

        return itemstack;
    }

    /**
     * Removes a stack from the given slot and returns it.
     */
    @Override
    public ItemStack removeStackFromSlot(int index)
    {
        return ItemStackHelper.getAndRemove(this.inventory, index);
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        this.inventory.set(index, stack);
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
     */
    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public void markDirty()
    {

    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player)
    {
        return true;
    }

    @Override
    public void openInventory(EntityPlayer player)
    {

    }

    @Override
    public void closeInventory(EntityPlayer player)
    {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        switch(index)
        {
            case 0:
                return stack.isItemEqual(new ItemStack(Items.DYE, 1, 0));
            case 1:
                return stack.isItemEqual(new ItemStack(SplatCraftItems.powerEgg));
            case 2:
                return stack.isItemEqual(new ItemStack(SplatCraftBlocks.emptyInkwell));
            case 3:
                return true;
        }
        return false;
    }

    @Override
    public int getField(int id)
    {
        return 0;
    }

    @Override
    public void setField(int id, int value)
    {

    }

    @Override
    public int getFieldCount()
    {
        return 0;
    }

    @Override
    public void clear() { inventory.clear(); }

    @Override
    public String getName()
    {
        return "container.inkwellVat";
    }

    @Override
    public boolean hasCustomName()
    {
        return false;
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return (this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName(), new Object[0]));
    }
}
