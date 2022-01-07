package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.network.base.PlayToClientPacket;
import com.cibernet.splatcraft.util.ClientUtils;
import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

import java.util.UUID;

public class PlayerColorPacket extends PlayToClientPacket
{
    private final int color;
    UUID target;
    String playerName;

    public PlayerColorPacket(UUID player, String name, int color)
    {
        this.color = color;
        this.target = player;
        this.playerName = name;
    }

    public PlayerColorPacket(PlayerEntity player, int color)
    {
        this(player.getUUID(), player.getDisplayName().getString(), color);
    }

    public static PlayerColorPacket decode(PacketBuffer buffer)
    {
        int color = buffer.readInt();
        String name = buffer.readUtf();
        UUID player = buffer.readUUID();
        return new PlayerColorPacket(player, name, color);
    }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeInt(color);
        buffer.writeUtf(playerName);
        buffer.writeUUID(target);
    }

    @Override
    public void execute()
    {
        PlayerEntity player = Minecraft.getInstance().level.getPlayerByUUID(target);
        if (player != null)
        {
            ColorUtils.setPlayerColor(player, color, false);
        }
        ClientUtils.setClientPlayerColor(playerName, color);
    }

}
