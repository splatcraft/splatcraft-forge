package com.cibernet.splatcraft.tileentities;

import com.cibernet.splatcraft.blocks.InkVatBlock;
import com.cibernet.splatcraft.data.SplatcraftTags;
import com.cibernet.splatcraft.items.FilterItem;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import com.cibernet.splatcraft.registries.SplatcraftTileEntitites;
import com.cibernet.splatcraft.tileentities.container.InkVatContainer;
import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class InkVatTileEntity extends LockableTileEntity implements ISidedInventory, ITickableTileEntity
{
    private static final int[] INPUT_SLOTS = new int[]{0, 1, 2, 3};
    private static final int[] OUTPUT_SLOTS = new int[]{4};
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);
    public int pointer = -1;
    net.minecraftforge.common.util.LazyOptional<? extends net.minecraftforge.items.IItemHandler>[] handlers =
            net.minecraftforge.items.wrapper.SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);
    private int color = -1;
    private int recipeEntries = 0;

    public InkVatTileEntity()
    {
        super(SplatcraftTileEntitites.inkVatTileEntity);
    }

    @Override
    public int[] getSlotsForFace(Direction side)
    {
        return side == Direction.UP ? INPUT_SLOTS : OUTPUT_SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, @Nullable Direction direction)
    {
        return canPlaceItem(index, itemStackIn);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction)
    {
        return index == 4;
    }

    @Override
    public int getContainerSize()
    {
        return inventory.size();
    }

    @Override
    public boolean isEmpty()
    {
        return inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int index)
    {
        return inventory.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count)
    {
        if (index == 4 && !consumeIngredients(count))
        {
            return ItemStack.EMPTY;
        }

        ItemStack itemstack = ItemStackHelper.removeItem(inventory, index, count);
        if (!itemstack.isEmpty())
        {
            this.setChanged();
        }

        return itemstack;
    }

    public boolean consumeIngredients(int count)
    {
        if (inventory.get(0).getCount() >= count && inventory.get(1).getCount() >= count && inventory.get(2).getCount() >= count)
        {
            removeItem(0, count);
            removeItem(1, count);
            removeItem(2, count);
            return true;
        }
        return false;
    }

    public void updateRecipeOutput()
    {
        if (hasRcipe())
        {
            setItem(4, ColorUtils.setColorLocked(ColorUtils.setInkColor(new ItemStack(SplatcraftItems.inkwell, Math.min(SplatcraftItems.inkwell.getMaxStackSize(),
                    Math.min(Math.min(inventory.get(0).getCount(), inventory.get(1).getCount()), inventory.get(2).getCount()))), getColor()), true));
        } else setItem(4, ItemStack.EMPTY);
    }

    public boolean hasRcipe()
    {
        return !inventory.get(0).isEmpty() && !inventory.get(1).isEmpty() && !inventory.get(2).isEmpty() && getColor() != -1;
    }

    public boolean hasOmniFilter()
    {
        Item filter = inventory.get(3).getItem();
        if (filter instanceof FilterItem)
        {
            return ((FilterItem) filter).isOmni();
        }
        return false;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index)
    {
        return ItemStackHelper.takeItem(inventory, index);
    }

    @Override
    public void setItem(int index, ItemStack stack)
    {
        inventory.set(index, stack);
        if (stack.getCount() > this.getMaxStackSize())
        {
            stack.setCount(this.getMaxStackSize());
        }

        this.setChanged();
    }

    @Override
    public boolean stillValid(PlayerEntity player)
    {
        if (this.level.getBlockEntity(this.getBlockPos()) != this)
        {
            return false;
        }
        return !(player.distanceToSqr((double) this.getBlockPos().getX() + 0.5D, (double) this.getBlockPos().getY() + 0.5D, (double) this.getBlockPos().getZ() + 0.5D) > 64.0D);
    }

    @Override
    public void clearContent()
    {
        inventory.clear();
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack)
    {
        switch (index)
        {
            case 0:
                return ItemStack.isSame(stack, new ItemStack(Items.INK_SAC));
            case 1:
                return ItemStack.isSame(stack, new ItemStack(SplatcraftItems.powerEgg));
            case 2:
                return ItemStack.isSame(stack, new ItemStack(SplatcraftItems.emptyInkwell));
            case 3:
                return SplatcraftTags.Items.FILTERS.contains(stack.getItem());
        }

        return false;
    }

    public NonNullList<ItemStack> getInventory()
    {
        return inventory;
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt)
    {
        nbt.putInt("Color", color);
        nbt.putInt("Pointer", pointer);
        nbt.putInt("RecipeEntries", recipeEntries);
        ItemStackHelper.saveAllItems(nbt, inventory);
        return super.save(nbt);
    }

    @Override
    protected ITextComponent getDefaultName()
    {
        return new TranslationTextComponent("container.ink_vat");
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player)
    {
        return new InkVatContainer(id, player, this, false);
    }

    //Nbt Read
    @Override
    public void load(BlockState state, CompoundNBT nbt)
    {
        super.load(state, nbt);
        color = ColorUtils.getColorFromNbt(nbt);
        pointer = nbt.getInt("Pointer");
        recipeEntries = nbt.getInt("RecipeEntries");

        clearContent();
        ItemStackHelper.loadAllItems(nbt, inventory);
    }

    @Override
    public CompoundNBT getUpdateTag()
    {
        return this.save(new CompoundNBT());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag)
    {
        this.load(state, tag);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        return new SUpdateTileEntityPacket(getBlockPos(), 2, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
    {
        if (level != null)
        {
            BlockState state = level.getBlockState(getBlockPos());
            level.sendBlockUpdated(getBlockPos(), state, state, 2);
            handleUpdateTag(state, pkt.getTag());
        }
    }

    public void onRedstonePulse()
    {
        if (hasRcipe())
        {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);
            if (pointer != -1 && recipeEntries > 0)
            {
                pointer = (pointer + 1) % recipeEntries;
                setColor(InkVatContainer.sortRecipeList(InkVatContainer.getAvailableRecipes(this)).get(pointer));
            }
        }
    }

    public int getColor()
    {
        return color;
    }

    public void setColor(int color)
    {
        this.color = color;
    }

    @Override
    public void tick()
    {
        updateRecipeOutput();
        if (!level.isClientSide)
        {
            level.setBlock(getBlockPos(), getBlockState().setValue(InkVatBlock.ACTIVE, hasRcipe()), 3);
        }
    }

    public int getRecipeEntries()
    {
        return recipeEntries;
    }

    public void setRecipeEntries(int v)
    {
        recipeEntries = v;
    }

    @Override
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing)
    {
        if (!this.isRemoved() && facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            if (facing == Direction.UP)
            {
                return handlers[0].cast();
            } else if (facing == Direction.DOWN)
            {
                return handlers[1].cast();
            } else
            {
                return handlers[2].cast();
            }
        }
        return super.getCapability(capability, facing);
    }

    /**
     * invalidates a tile entity
     */
    @Override
    public void setRemoved()
    {
        super.setRemoved();
        for (LazyOptional<? extends IItemHandler> handler : handlers)
        {
            handler.invalidate();
        }
    }

    public boolean setColorAndUpdate(int color)
    {
        boolean changeState = Math.min(color, 0) != Math.min(getColor(), 0);
        setColor(color);
        if (level != null)
        {
            if (changeState)
            {
                level.setBlock(getBlockPos(), getBlockState().setValue(InkVatBlock.ACTIVE, hasRcipe()), 2);
            } else
            {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);
            }
        }
        return true;
    }
}
