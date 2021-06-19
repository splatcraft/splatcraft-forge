package com.cibernet.splatcraft.tileentities;

import com.cibernet.splatcraft.data.SplatcraftTags;
import com.cibernet.splatcraft.items.remotes.RemoteItem;
import com.cibernet.splatcraft.registries.SplatcraftTileEntitites;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;

public class RemotePedestalTileEntity extends InkColorTileEntity implements ISidedInventory
{
    protected ItemStack remote = ItemStack.EMPTY;
    protected int signal = 0;

    public RemotePedestalTileEntity() {
        super(SplatcraftTileEntitites.remotePedestalTileEntity);
    }

    public void onPowered()
    {
        if(!(remote.getItem() instanceof RemoteItem))
        {
            signal = 0;
            return;
        }

        RemoteItem.RemoteResult result = ((RemoteItem) remote.getItem()).onRemoteUse(world, remote, getColor());
        signal = result.getComparatorResult();

    }

    @Override
    public CompoundNBT getUpdateTag()
    {
        return this.write(new CompoundNBT());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag)
    {
        this.read(state, tag);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        return new SUpdateTileEntityPacket(getPos(), 2, getUpdateTag());
    }


    @Override
    public int[] getSlotsForFace(Direction direction) {
        return new int[] {0};
    }

    @Override
    public boolean canInsertItem(int i, ItemStack itemStack, @Nullable Direction direction) {
        return itemStack.getItem().isIn(SplatcraftTags.Items.REMOTES);
    }

    @Override
    public boolean canExtractItem(int i, ItemStack itemStack, Direction direction) {
        return true;
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return remote.isEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return remote;
    }

    @Override
    public ItemStack decrStackSize(int i, int count) {
        return remote.split(count);
    }

    @Override
    public ItemStack removeStackFromSlot(int i)
    {
        ItemStack copy = remote.copy();
        remote = ItemStack.EMPTY;
        return copy;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemStack) {
        remote = itemStack;
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player)
    {
        if (this.world.getTileEntity(this.pos) != this)
            return false;
        return !(player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) > 64.0D);
    }

    @Override
    public void clear()
    {
        remote = ItemStack.EMPTY;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt)
    {
        super.read(state, nbt);

        signal = nbt.getInt("Signal");

        if(nbt.contains("Remote"))
            remote = ItemStack.read(nbt.getCompound("Remote"));
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        nbt.putInt("Signal", signal);

        if(!remote.isEmpty())
            nbt.put("Remote", remote.write(new CompoundNBT()));
        return super.write(nbt);
    }

    public int getSignal() {
        return signal;
    }
}
