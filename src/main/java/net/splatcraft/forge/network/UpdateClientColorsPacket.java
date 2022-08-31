package net.splatcraft.forge.network;

import net.splatcraft.forge.network.base.PlayToClientPacket;
import net.splatcraft.forge.util.ClientUtils;
import net.minecraft.network.PacketBuffer;

import java.util.TreeMap;

public class UpdateClientColorsPacket extends PlayToClientPacket
{
    final TreeMap<String, Integer> colors;
    final boolean reset;

    protected UpdateClientColorsPacket(TreeMap<String, Integer> colors, boolean reset)
    {
        this.colors = colors;
        this.reset = reset;
    }

    public UpdateClientColorsPacket(TreeMap<String, Integer> colors)
    {
        this(colors, true);
    }

    public UpdateClientColorsPacket(String player, int color)
    {
        this.colors = new TreeMap<>();
        colors.put(player, color);
        reset = false;
    }

    public static UpdateClientColorsPacket decode(PacketBuffer buffer)
    {
        TreeMap<String, Integer> colors = new TreeMap<>();

        boolean reset = buffer.readBoolean();
        int size = buffer.readVarInt();
        for (int i = 0; i < size; i++)
        {
            colors.put(buffer.readUtf(), buffer.readInt());
        }
        return new UpdateClientColorsPacket(colors, reset);
    }

    @Override
    public void execute()
    {
        if (reset)
        {
            ClientUtils.resetClientColors();
        }
        ClientUtils.putClientColors(colors);
    }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeBoolean(reset);
        buffer.writeVarInt(colors.entrySet().size());
        colors.forEach((key, value) ->
        {
            buffer.writeUtf(key);
            buffer.writeInt(value);
        });
    }
}
