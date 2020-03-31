package com.cibernet.splatcraft.gui.container;

import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

public class ContainerPlayerInv extends Container
{
	BlockPos pos;
	int xPos;
	int yPos;
	
	public ContainerPlayerInv(IInventory player, BlockPos  pos, int xPos, int yPos)
	{
		this.pos = pos;
		this.xPos = xPos;
		this.yPos = yPos;
		
		for(int xx = 0; xx < 9; xx++)
			for(int yy = 0; yy < 3; yy++)
				addSlotToContainer(new Slot(player, xx+yy*9 + 9, xPos + xx*18, yPos + yy*18));
		
		for(int xx = 0; xx < 9; xx++)
			addSlotToContainer(new Slot(player, xx, xPos + xx*18, yPos + 58));
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		return playerIn.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
	{
		Slot slot = this.inventorySlots.get(index);
		ItemStack stack = slot.getStack();
		ItemStack returnStack = stack.copy();
		
		if(slot == null || !slot.getHasStack())
			return ItemStack.EMPTY;
		
		if (index < this.inventorySlots.size()-9)
		{
			if (!this.mergeItemStack(stack, this.inventorySlots.size()-9, this.inventorySlots.size(), true))
			{
				return ItemStack.EMPTY;
			}
		}
		else
		{
			if (!this.mergeItemStack(stack, 0, this.inventorySlots.size()-9, false))
			{
				return ItemStack.EMPTY;
			}
		}
		return ItemStack.EMPTY;
	}
}
