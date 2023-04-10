package net.splatcraft.forge.network.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.splatcraft.forge.crafting.InkVatColorRecipe;
import net.splatcraft.forge.handlers.ScoreboardHandler;

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

    public static UpdateColorScoresPacket decode(FriendlyByteBuf buffer)
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
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBoolean(clear);
        buffer.writeBoolean(add);
        buffer.writeVarIntArray(colors);
    }
}
