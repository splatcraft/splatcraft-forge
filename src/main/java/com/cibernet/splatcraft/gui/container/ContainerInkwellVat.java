package com.cibernet.splatcraft.gui.container;

import com.cibernet.splatcraft.gui.inventory.InventoryInkwellVat;
import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.registries.SplatCraftItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ContainerInkwellVat extends Container
{
	public InventoryInkwellVat inventory = new InventoryInkwellVat();
	public World world;
	public BlockPos pos;
	
	public ContainerInkwellVat(InventoryPlayer player, World world, BlockPos pos)
	{
		super();
		
		this.world = world;
		this.pos = pos;
		
		addSlotToContainer(new SlotInput(new ItemStack(Items.DYE, 1, 0), inventory, 0, 26, 70));
		addSlotToContainer(new SlotInput(new ItemStack(SplatCraftItems.powerEgg), inventory, 1, 46, 70));
		addSlotToContainer(new Slot(inventory, 2, 36, 89));
		addSlotToContainer(new SlotInput(new ItemStack(SplatCraftBlocks.emptyInkwell), inventory, 3, 92, 82));
		addSlotToContainer(new SlotOutput(inventory, 4, 112, 82));
		
		for(int xx = 0; xx < 9; xx++)
			for(int yy = 0; yy < 3; yy++)
				addSlotToContainer(new Slot(player, xx+yy*9 + 9, 8 + xx*18, 126 + yy*18));
			
		for(int xx = 0; xx < 9; xx++)
			addSlotToContainer(new Slot(player, xx, 8 + xx*18, 184));
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		return playerIn.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
		
	}
	
	@Override
	public void onContainerClosed(EntityPlayer playerIn)
	{
		super.onContainerClosed(playerIn);
		
		if (!world.isRemote)
		{
			this.clearContainer(playerIn, this.world, inventory);
		}
	}
	
	/**
	 * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
	 * inventory and the other inventory(s).
	 */
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
	{
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);
		
		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			
			if (index == 0)
			{
				itemstack1.getItem().onCreated(itemstack1, this.world, playerIn);
				
				if (!this.mergeItemStack(itemstack1, 10, 46, true))
				{
					return ItemStack.EMPTY;
				}
				
				slot.onSlotChange(itemstack1, itemstack);
			}
			else if (index >= 10 && index < 37)
			{
				if (!this.mergeItemStack(itemstack1, 37, 46, false))
				{
					return ItemStack.EMPTY;
				}
			}
			else if (index >= 37 && index < 46)
			{
				if (!this.mergeItemStack(itemstack1, 10, 37, false))
				{
					return ItemStack.EMPTY;
				}
			}
			else if (!this.mergeItemStack(itemstack1, 10, 46, false))
			{
				return ItemStack.EMPTY;
			}
			
			if (itemstack1.isEmpty())
			{
				slot.putStack(ItemStack.EMPTY);
			}
			else
			{
				slot.onSlotChanged();
			}
			
			if (itemstack1.getCount() == itemstack.getCount())
			{
				return ItemStack.EMPTY;
			}
			
			ItemStack itemstack2 = slot.onTake(playerIn, itemstack1);
			
			if (index == 0)
			{
				playerIn.dropItem(itemstack2, false);
			}
		}
		
		return itemstack;
	}
	
	/**
	 * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in
	 * is null for the initial slot that was double-clicked.
	 */
	public boolean canMergeSlot(ItemStack stack, Slot slotIn)
	{
		return this.canMergeSlot(stack, slotIn);
		//return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
	}
	
	class SlotInput extends Slot
	{
		ItemStack validItem;
		public SlotInput(ItemStack validItem, IInventory inventoryIn, int index, int xPosition, int yPosition)
		{
			super(inventoryIn, index, xPosition, yPosition);
			this.validItem = validItem;
		}
		
		@Override
		public boolean isItemValid(ItemStack stack)
		{
			return ItemStack.areItemStacksEqual(stack, validItem);
		}
	}
	class SlotOutput extends Slot
	{
		public SlotOutput(IInventory inventoryIn, int index, int xPosition, int yPosition)
		{
			super(inventoryIn, index, xPosition, yPosition);
		}
		
		@Override
		public boolean isItemValid(ItemStack stack)
		{
			return false;
		}
	}
}
