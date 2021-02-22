package com.cibernet.splatcraft.tileentities.container;

import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import com.cibernet.splatcraft.registries.SplatcraftTileEntitites;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public abstract class PlayerInventoryContainer<T extends PlayerInventoryContainer> extends Container
{
	protected final IWorldPosCallable worldPosCallable;
	int xPos;
	int yPos;
	
	public PlayerInventoryContainer(ContainerType<T> containerType, PlayerInventory player, IWorldPosCallable worldPosCallable, int invX, int invY, int id)
	{
		super(containerType, id);
		this.worldPosCallable = worldPosCallable;
		this.xPos = invX;
		this.yPos = invY;
		
		for(int xx = 0; xx < 9; xx++)
			for(int yy = 0; yy < 3; yy++)
				addSlot(new Slot(player, xx+yy*9 + 9, xPos + xx*18, yPos + yy*18));
		
		for(int xx = 0; xx < 9; xx++)
			addSlot(new Slot(player, xx, xPos + xx*18, yPos + 58));
	}
	
	@Override
	public boolean canInteractWith(PlayerEntity playerIn)
	{
		return isWithinUsableDistance(this.worldPosCallable, playerIn, SplatcraftBlocks.weaponWorkbench);
	}
	
	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index)
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
