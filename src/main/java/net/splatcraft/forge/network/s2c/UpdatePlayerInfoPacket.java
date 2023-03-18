package net.splatcraft.forge.network.s2c;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.util.ClientUtils;
import net.splatcraft.forge.util.ColorUtils;

import java.util.UUID;

public class UpdatePlayerInfoPacket extends PlayToClientPacket
{
    UUID target;
    CompoundNBT nbt;


    protected UpdatePlayerInfoPacket(UUID player, CompoundNBT nbt)
    {
        this.target = player;
        this.nbt = nbt;
    }

    public UpdatePlayerInfoPacket(PlayerEntity target)
    {
        this(target.getUUID(), PlayerInfoCapability.get(target).writeNBT(new CompoundNBT()));
    }

    public static UpdatePlayerInfoPacket decode(PacketBuffer buffer)
    {
        return new UpdatePlayerInfoPacket(UUID.fromString(buffer.readUtf()), buffer.readNbt());
    }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeUtf(target.toString());
        buffer.writeNbt(nbt);
    }

    @Override
    public void execute()
    {
        PlayerEntity target = Minecraft.getInstance().level.getPlayerByUUID(this.target);

        if (target != null) {
            PlayerInfoCapability.get(target).readNBT(nbt);
            ClientUtils.setClientPlayerColor(target.getDisplayName().getString(), ColorUtils.getColorFromNbt(this.nbt));
        }
    }
}
