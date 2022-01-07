package com.cibernet.splatcraft.tileentities.container;

import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;

public abstract class PlayerInventoryContainer<T extends PlayerInventoryContainer<?>> extends Container
{
    protected final IWorldPosCallable levelPosCallable;
    int xPos;
    int yPos;

    public PlayerInventoryContainer(ContainerType<T> containerType, PlayerInventory player, IWorldPosCallable levelPosCallable, int invX, int invY, int id)
    {
        super(containerType, id);
        this.levelPosCallable = levelPosCallable;
        this.xPos = invX;
        this.yPos = invY;

        for (int xx = 0; xx < 9; xx++)
        {
            for (int yy = 0; yy < 3; yy++)
            {
                addSlot(new Slot(player, xx + yy * 9 + 9, xPos + xx * 18, yPos + yy * 18));
            }
        }

        for (int xx = 0; xx < 9; xx++)
        {
            addSlot(new Slot(player, xx, xPos + xx * 18, yPos + 58));
        }
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn)
    {
        return stillValid(this.levelPosCallable, playerIn, SplatcraftBlocks.weaponWorkbench);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity playerIn, int index)
    {
        Slot slot = this.slots.get(index);
        ItemStack stack = slot.getItem();

        if (!slot.hasItem())
        {
            return ItemStack.EMPTY;
        }

        if (index < this.slots.size() - 9)
        {
            if (!this.moveItemStackTo(stack, this.slots.size() - 9, this.slots.size(), true))
            {
                return ItemStack.EMPTY;
            }
        } else
        {
            if (!this.moveItemStackTo(stack, 0, this.slots.size() - 9, false))
            {
                return ItemStack.EMPTY;
            }
        }
        return ItemStack.EMPTY;
    }
}
