package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.network.base.PlayToClientPacket;
import com.cibernet.splatcraft.util.ClientUtils;
import net.minecraft.network.PacketBuffer;

import java.util.TreeMap;
import java.util.UUID;

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

    @Override
    public void execute()
    {
        if(reset)
            ClientUtils.resetClientColors();
        ClientUtils.putClientColors(colors);
    }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeBoolean(reset);
        buffer.writeVarInt(colors.entrySet().size());
        colors.entrySet().forEach((entry) ->
        {
            buffer.writeString(entry.getKey());
            buffer.writeInt(entry.getValue());
        });
    }

    public static UpdateClientColorsPacket decode(PacketBuffer buffer)
    {
        TreeMap<String, Integer> colors = new TreeMap<>();

        boolean reset = buffer.readBoolean();
        int size = buffer.readVarInt();
        for(int i = 0; i < size; i++)
            colors.put(buffer.readString(), buffer.readInt());
        return new UpdateClientColorsPacket(colors, reset);
    }
}
