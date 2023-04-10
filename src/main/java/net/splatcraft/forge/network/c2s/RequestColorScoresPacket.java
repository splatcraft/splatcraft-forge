package net.splatcraft.forge.network.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.splatcraft.forge.handlers.ScoreboardHandler;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.s2c.UpdateColorScoresPacket;

public class RequestColorScoresPacket extends PlayToServerPacket
{

    public RequestColorScoresPacket()
    {

    }

    public static RequestColorScoresPacket decode(FriendlyByteBuf buffer)
    {
        return new RequestColorScoresPacket();
    }

    @Override
    public void execute(Player player)
    {
        int[] colors = new int[ScoreboardHandler.getCriteriaKeySet().size()];
        int i = 0;
        for (int c : ScoreboardHandler.getCriteriaKeySet())
        {
            colors[i++] = c;
        }
        SplatcraftPacketHandler.sendToPlayer(new UpdateColorScoresPacket(true, true, colors), (ServerPlayer) player);
    }

    @Override
    public void encode(FriendlyByteBuf buffer)
    {

    }
}
