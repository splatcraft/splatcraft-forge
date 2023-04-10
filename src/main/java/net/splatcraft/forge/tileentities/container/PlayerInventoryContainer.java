package net.splatcraft.forge.tileentities.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.splatcraft.forge.registries.SplatcraftBlocks;

public abstract class PlayerInventoryContainer<T extends PlayerInventoryContainer<?>> extends AbstractContainerMenu
{
    protected final ContainerLevelAccess levelPosCallable;
    int xPos;
    int yPos;

    public PlayerInventoryContainer(MenuType<T> containerType, Inventory player, ContainerLevelAccess levelPosCallable, int invX, int invY, int id)
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
    public boolean stillValid(Player playerIn)
    {
        return stillValid(this.levelPosCallable, playerIn, SplatcraftBlocks.weaponWorkbench.get());
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index)
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
