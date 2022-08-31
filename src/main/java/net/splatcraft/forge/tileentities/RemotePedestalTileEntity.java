package net.splatcraft.forge.tileentities;

import net.splatcraft.forge.data.SplatcraftTags;
import net.splatcraft.forge.items.remotes.RemoteItem;
import net.splatcraft.forge.registries.SplatcraftTileEntitites;
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

        RemoteItem.RemoteResult result = ((RemoteItem) remote.getItem()).onRemoteUse(level, remote, getColor());
        signal = result.getComparatorResult();

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
    public int[] getSlotsForFace(Direction direction) {
        return new int[] {0};
    }

    @Override
    public boolean canPlaceItemThroughFace(int i, ItemStack itemStack, @Nullable Direction direction) {
        return itemStack.getItem().is(SplatcraftTags.Items.REMOTES);
    }

    @Override
    public boolean canTakeItemThroughFace(int i, ItemStack itemStack, Direction direction) {
        return true;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return remote.isEmpty();
    }

    @Override
    public ItemStack getItem(int i) {
        return remote;
    }

    @Override
    public ItemStack removeItem(int i, int count) {
        return remote.split(count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int i)
    {
        ItemStack copy = remote.copy();
        remote = ItemStack.EMPTY;
        return copy;
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        remote = itemStack;
    }

    @Override
    public boolean stillValid(PlayerEntity player)
    {
        if (this.level.getBlockEntity(this.getBlockPos()) != this)
            return false;
        return !(player.distanceToSqr((double) this.getBlockPos().getX() + 0.5D, (double) this.getBlockPos().getY() + 0.5D, (double) this.getBlockPos().getZ() + 0.5D) > 64.0D);
    }

    @Override
    public void clearContent()
    {
        remote = ItemStack.EMPTY;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt)
    {
        super.load(state, nbt);

        signal = nbt.getInt("Signal");

        if(nbt.contains("Remote"))
            remote = ItemStack.of(nbt.getCompound("Remote"));
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt)
    {
        nbt.putInt("Signal", signal);

        if(!remote.isEmpty())
            nbt.put("Remote", remote.save(new CompoundNBT()));
        return super.save(nbt);
    }

    public int getSignal() {
        return signal;
    }
}
