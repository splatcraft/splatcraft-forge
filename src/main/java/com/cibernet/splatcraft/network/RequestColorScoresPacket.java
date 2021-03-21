package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.handlers.ScoreboardHandler;
import com.cibernet.splatcraft.network.base.PlayToServerPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;

public class RequestColorScoresPacket extends PlayToServerPacket {

    public RequestColorScoresPacket() {

    }

    @Override
    public void execute(PlayerEntity player) {
        int[] colors = new int[ScoreboardHandler.getCriteriaKeySet().size()];
        int i = 0;
        for (int c : ScoreboardHandler.getCriteriaKeySet())
            colors[i++] = c;
        SplatcraftPacketHandler.sendToPlayer(new UpdateColorScoresPacket(true, true, colors), (ServerPlayerEntity) player);
    }

    public static RequestColorScoresPacket decode(PacketBuffer buffer) {
        return new RequestColorScoresPacket();
    }

    @Override
    public void encode(PacketBuffer buffer) {

    }
}
