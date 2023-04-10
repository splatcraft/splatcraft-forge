package net.splatcraft.forge.network.s2c;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.util.ClientUtils;
import net.splatcraft.forge.util.ColorUtils;

import java.util.UUID;

public class UpdatePlayerInfoPacket extends PlayToClientPacket
{
    UUID target;
    CompoundTag nbt;


    protected UpdatePlayerInfoPacket(UUID player, CompoundTag nbt)
    {
        this.target = player;
        this.nbt = nbt;
    }

    public UpdatePlayerInfoPacket(Player target)
    {
        this(target.getUUID(), PlayerInfoCapability.get(target).writeNBT(new CompoundTag()));
    }

    public static UpdatePlayerInfoPacket decode(FriendlyByteBuf buffer)
    {
        return new UpdatePlayerInfoPacket(UUID.fromString(buffer.readUtf()), buffer.readNbt());
    }

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeUtf(target.toString());
        buffer.writeNbt(nbt);
    }

    @Override
    public void execute()
    {
        Player target = Minecraft.getInstance().level.getPlayerByUUID(this.target);

        if (target != null) {
            PlayerInfoCapability.get(target).readNBT(nbt);
            ClientUtils.setClientPlayerColor(target.getDisplayName().getString(), ColorUtils.getColorFromNbt(this.nbt));
        }
    }
}
