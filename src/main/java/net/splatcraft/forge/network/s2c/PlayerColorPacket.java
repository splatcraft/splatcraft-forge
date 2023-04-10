package net.splatcraft.forge.network.s2c;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.splatcraft.forge.util.ClientUtils;
import net.splatcraft.forge.util.ColorUtils;

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

    public PlayerColorPacket(Player player, int color)
    {
        this(player.getUUID(), player.getDisplayName().getString(), color);
    }

    public static PlayerColorPacket decode(FriendlyByteBuf buffer)
    {
        int color = buffer.readInt();
        String name = buffer.readUtf();
        UUID player = buffer.readUUID();
        return new PlayerColorPacket(player, name, color);
    }

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeInt(color);
        buffer.writeUtf(playerName);
        buffer.writeUUID(target);
    }

    @Override
    public void execute()
    {
        Player player = Minecraft.getInstance().level.getPlayerByUUID(target);
        if (player != null)
        {
            ColorUtils.setPlayerColor(player, color, false);
        }
        ClientUtils.setClientPlayerColor(playerName, color);
    }

}
