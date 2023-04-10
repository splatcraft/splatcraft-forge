package net.splatcraft.forge.tileentities;

import net.minecraft.commands.CommandSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.splatcraft.forge.data.SplatcraftTags;
import net.splatcraft.forge.items.remotes.RemoteItem;
import net.splatcraft.forge.registries.SplatcraftTileEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class RemotePedestalTileEntity extends InkColorTileEntity implements WorldlyContainer, CommandSource
{
    protected ItemStack remote = ItemStack.EMPTY;
    protected int signal = 0;
    protected int remoteResult = 0;

    public RemotePedestalTileEntity(BlockPos pos, BlockState state) {
        super(SplatcraftTileEntities.remotePedestalTileEntity.get(), pos, state);
    }

    public void onPowered()
    {
        if(!(remote.getItem() instanceof RemoteItem))
        {
            signal = 0;
            return;
        }

        RemoteItem.RemoteResult result = ((RemoteItem) remote.getItem()).onRemoteUse(level, remote, getColor(), Vec3.atCenterOf(worldPosition), null);
        signal = result.getComparatorResult();
        remoteResult = result.getCommandResult();
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        return new CompoundTag(){{saveAdditional(this);}};
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        // Will get tag from #getUpdateTag
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt)
    {
        if (level != null)
        {
            BlockState state = level.getBlockState(getBlockPos());
            level.sendBlockUpdated(getBlockPos(), state, state, 2);
            handleUpdateTag(pkt.getTag());
        }
    }



    @Override
    public int[] getSlotsForFace(Direction direction) {
        return new int[] {0};
    }

    @Override
    public boolean canPlaceItemThroughFace(int i, ItemStack itemStack, @Nullable Direction direction) {
        return itemStack.is(SplatcraftTags.Items.REMOTES);
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
    public boolean stillValid(Player player)
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
    public void load(@NotNull CompoundTag nbt)
    {
        super.load(nbt);

        signal = nbt.getInt("Signal");

        if(nbt.contains("Remote"))
            remote = ItemStack.of(nbt.getCompound("Remote"));

        if(nbt.contains("RemoteResult"))
            remoteResult = nbt.getInt("RemoteResult");
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.putInt("Signal", signal);

        if(!remote.isEmpty())
            nbt.put("Remote", remote.save(new CompoundTag()));
        if(remoteResult != 0)
            nbt.putInt("RemoteResult", remoteResult);

        super.saveAdditional(nbt);
    }

    public int getSignal() {
        return signal;
    }

    @Override
    public void sendMessage(Component p_145747_1_, UUID p_145747_2_) {}

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
