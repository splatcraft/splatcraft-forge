package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.crafting.InkVatColorRecipe;
import com.cibernet.splatcraft.handlers.ScoreboardHandler;
import com.cibernet.splatcraft.network.base.PlayToClientPacket;
import net.minecraft.network.PacketBuffer;

public class UpdateColorScoresPacket extends PlayToClientPacket {
    int[] colors;
    boolean add;
    boolean clear;

    public UpdateColorScoresPacket(boolean clear, boolean add, int[] color) {
        this.clear = clear;
        this.colors = color;
        this.add = add;
    }

    @Override
    public void execute() {
        if (clear) {
            ScoreboardHandler.clearColorCriteria();
            InkVatColorRecipe.getOmniList().clear();
        }

        if (add) {
            for (int color : colors)
                ScoreboardHandler.createColorCriterion(color);
        } else {
            for (int color : colors)
                ScoreboardHandler.removeColorCriterion(color);
        }
    }

    public static UpdateColorScoresPacket decode(PacketBuffer buffer) {
        return new UpdateColorScoresPacket(buffer.readBoolean(), buffer.readBoolean(), buffer.readVarIntArray());
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeBoolean(clear);
        buffer.writeBoolean(add);
        buffer.writeVarIntArray(colors);
    }
}
