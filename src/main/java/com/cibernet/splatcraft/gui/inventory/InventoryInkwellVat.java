package com.cibernet.splatcraft.gui.inventory;

import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.registries.SplatCraftItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class InventoryInkwellVat implements IInventory
{
	private final NonNullList<ItemStack> stackList = NonNullList.<ItemStack>withSize(5, ItemStack.EMPTY);
	
	/**
	 * Returns the number of slots in the inventory.
	 */
	@Override
	public int getSizeInventory()
	{
		return this.stackList.size();
	}
	
	@Override
	public boolean isEmpty()
	{
		for (ItemStack itemstack : this.stackList)
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
		return index >= this.getSizeInventory() ? ItemStack.EMPTY : (ItemStack)this.stackList.get(index);
	}
	
	/**
	 * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
	 */
	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		ItemStack itemstack = ItemStackHelper.getAndSplit(this.stackList, index, count);
		return itemstack;
	}
	
	/**
	 * Removes a stack from the given slot and returns it.
	 */
	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		return ItemStackHelper.getAndRemove(this.stackList, index);
	}
	
	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
	 */
	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		this.stackList.set(index, stack);
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
				return true;
			case 3:
				return stack.isItemEqual(new ItemStack(SplatCraftBlocks.emptyInkwell));
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
	public void clear() { stackList.clear(); }
	
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
