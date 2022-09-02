package net.splatcraft.forge.network.c2s;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.splatcraft.forge.handlers.ScoreboardHandler;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.s2c.UpdateColorScoresPacket;

public class RequestColorScoresPacket extends PlayToServerPacket
{

    public RequestColorScoresPacket()
    {

    }

    public static RequestColorScoresPacket decode(PacketBuffer buffer)
    {
        return new RequestColorScoresPacket();
    }

    @Override
    public void execute(PlayerEntity player)
    {
        int[] colors = new int[ScoreboardHandler.getCriteriaKeySet().size()];
        int i = 0;
        for (int c : ScoreboardHandler.getCriteriaKeySet())
        {
            colors[i++] = c;
        }
        SplatcraftPacketHandler.sendToPlayer(new UpdateColorScoresPacket(true, true, colors), (ServerPlayerEntity) player);
    }

    @Override
    public void encode(PacketBuffer buffer)
    {

    }
}
