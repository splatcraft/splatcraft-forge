package net.splatcraft.forge.network.c2s;

import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.s2c.UpdatePlayerInfoPacket;

public class RequestPlayerInfoPacket extends PlayC2SPacket
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
