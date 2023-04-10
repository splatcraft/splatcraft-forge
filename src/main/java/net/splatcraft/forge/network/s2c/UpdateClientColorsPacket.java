package net.splatcraft.forge.network.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.splatcraft.forge.util.ClientUtils;

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

    public static UpdateClientColorsPacket decode(FriendlyByteBuf buffer)
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
    public void encode(FriendlyByteBuf buffer)
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
