package net.splatcraft.forge.tileentities;

import net.minecraft.block.BlockState;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.splatcraft.forge.data.SplatcraftTags;
import net.splatcraft.forge.items.remotes.RemoteItem;
import net.splatcraft.forge.registries.SplatcraftTileEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class RemotePedestalTileEntity extends InkColorTileEntity implements ISidedInventory, ICommandSource
{
    protected ItemStack remote = ItemStack.EMPTY;
    protected int signal = 0;
    protected int remoteResult = 0;

    public RemotePedestalTileEntity() {
        super(SplatcraftTileEntities.remotePedestalTileEntity);
    }

    public void onPowered()
    {
        if(!(remote.getItem() instanceof RemoteItem))
        {
            signal = 0;
            return;
        }

        RemoteItem.RemoteResult result = ((RemoteItem) remote.getItem()).onRemoteUse(level, remote, getColor(), Vector3d.atCenterOf(worldPosition), null);
        signal = result.getComparatorResult();
        remoteResult = result.getCommandResult();
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
    public void load(@NotNull BlockState state, @NotNull CompoundNBT nbt)
    {
        super.load(state, nbt);

        signal = nbt.getInt("Signal");

        if(nbt.contains("Remote"))
            remote = ItemStack.of(nbt.getCompound("Remote"));

        if(nbt.contains("RemoteResult"))
            remoteResult = nbt.getInt("RemoteResult");
    }

    @Override
    public @NotNull CompoundNBT save(CompoundNBT nbt)
    {
        nbt.putInt("Signal", signal);

        if(!remote.isEmpty())
            nbt.put("Remote", remote.save(new CompoundNBT()));
        if(remoteResult != 0)
            nbt.putInt("RemoteResult", remoteResult);

        return super.save(nbt);
    }

    public int getSignal() {
        return signal;
    }

    private final ITextComponent name = new TranslationTextComponent("");

    @Override
    public void sendMessage(ITextComponent p_145747_1_, UUID p_145747_2_) {}

    @Override
    public boolean acceptsSuccess() {
        return false;
    }

    @Override
    public boolean acceptsFailure() {
        return false;
    }

    @Override
    public boolean shouldInformAdmins() {
        return false;
    }
}
