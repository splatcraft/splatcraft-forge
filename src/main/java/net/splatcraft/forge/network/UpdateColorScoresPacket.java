package net.splatcraft.forge.network;

import net.splatcraft.forge.crafting.InkVatColorRecipe;
import net.splatcraft.forge.handlers.ScoreboardHandler;
import net.splatcraft.forge.network.base.PlayToClientPacket;
import net.minecraft.network.PacketBuffer;

public class UpdateColorScoresPacket extends PlayToClientPacket
{
    int[] colors;
    boolean add;
    boolean clear;

    public UpdateColorScoresPacket(boolean clear, boolean add, int[] color)
    {
        this.clear = clear;
        this.colors = color;
        this.add = add;
    }

    public static UpdateColorScoresPacket decode(PacketBuffer buffer)
    {
        return new UpdateColorScoresPacket(buffer.readBoolean(), buffer.readBoolean(), buffer.readVarIntArray());
    }

    @Override
    public void execute()
    {
        if (clear)
        {
            ScoreboardHandler.clearColorCriteria();
            InkVatColorRecipe.getOmniList().clear();
        }

        if (add)
        {
            for (int color : colors)
            {
                ScoreboardHandler.createColorCriterion(color);
            }
        } else
        {
            for (int color : colors)
            {
                ScoreboardHandler.removeColorCriterion(color);
            }
        }
    }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeBoolean(clear);
        buffer.writeBoolean(add);
        buffer.writeVarIntArray(colors);
    }
}
