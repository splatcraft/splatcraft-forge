package net.splatcraft.forge.data.capabilities.inkoverlay;

import net.minecraft.nbt.CompoundNBT;

public interface IInkOverlayInfo
{
    int getColor();

    void setColor(int color);

    float getAmount();

    void setAmount(float v);

    void addAmount(float v);

    CompoundNBT writeNBT(CompoundNBT nbt);

    void readNBT(CompoundNBT nbt);

}
