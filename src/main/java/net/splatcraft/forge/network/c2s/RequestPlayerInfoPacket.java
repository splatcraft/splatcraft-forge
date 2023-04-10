package net.splatcraft.forge.network.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.s2c.UpdatePlayerInfoPacket;

import java.util.UUID;

public class RequestPlayerInfoPacket extends PlayToServerPacket
{
    UUID target;

    public RequestPlayerInfoPacket(Player target)
    {
        this.target = target.getUUID();
    }

    private RequestPlayerInfoPacket(UUID target)
    {
        this.target = target;
    }

    public static RequestPlayerInfoPacket decode(FriendlyByteBuf buffer)
    {
        return new RequestPlayerInfoPacket(buffer.readUUID());
    }

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeUUID(target);
    }

    @Override
    public void execute(Player player)
    {
        ServerPlayer target = (ServerPlayer) player.level.getPlayerByUUID(this.target);
        if (target != null)
        {
            SplatcraftPacketHandler.sendToPlayer(new UpdatePlayerInfoPacket(target), (ServerPlayer) player);
        }
    }

}
