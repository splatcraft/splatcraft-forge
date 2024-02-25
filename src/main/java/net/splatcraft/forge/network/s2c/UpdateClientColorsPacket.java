package net.splatcraft.forge.network.s2c;

import java.util.TreeMap;
import java.util.UUID;

import net.minecraft.network.FriendlyByteBuf;
import net.splatcraft.forge.util.ClientUtils;

public class UpdateClientColorsPacket extends PlayS2CPacket
{
    final TreeMap<UUID, Integer> colors;
    final boolean reset;

    protected UpdateClientColorsPacket(TreeMap<UUID, Integer> colors, boolean reset)
    {
        this.colors = colors;
        this.reset = reset;
    }

    public UpdateClientColorsPacket(TreeMap<UUID, Integer> colors)
    {
        this(colors, true);
    }

    public UpdateClientColorsPacket(UUID player, int color)
    {
        this.colors = new TreeMap<>();
        colors.put(player, color);
        reset = false;
    }

    public static UpdateClientColorsPacket decode(FriendlyByteBuf buffer)
    {
        TreeMap<UUID, Integer> colors = new TreeMap<>();

        boolean reset = buffer.readBoolean();
        int size = buffer.readVarInt();
        for (int i = 0; i < size; i++)
        {
            colors.put(buffer.readUUID(), buffer.readInt());
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
            buffer.writeUUID(key);
            buffer.writeInt(value);
        });
    }
}
