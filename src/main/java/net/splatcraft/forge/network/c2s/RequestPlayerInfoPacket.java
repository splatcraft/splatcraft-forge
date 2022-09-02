package net.splatcraft.forge.network.c2s;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.s2c.UpdatePlayerInfoPacket;

import java.util.UUID;

public class RequestPlayerInfoPacket extends PlayToServerPacket
{
    UUID target;

    public RequestPlayerInfoPacket(PlayerEntity target)
    {
        this.target = target.getUUID();
    }

    private RequestPlayerInfoPacket(UUID target)
    {
        this.target = target;
    }

    public static RequestPlayerInfoPacket decode(PacketBuffer buffer)
    {
        return new RequestPlayerInfoPacket(buffer.readUUID());
    }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeUUID(target);
    }

    @Override
    public void execute(PlayerEntity player)
    {
        ServerPlayerEntity target = (ServerPlayerEntity) player.level.getPlayerByUUID(this.target);
        if (target != null)
        {
            SplatcraftPacketHandler.sendToPlayer(new UpdatePlayerInfoPacket(target), (ServerPlayerEntity) player);
        }
    }

}
