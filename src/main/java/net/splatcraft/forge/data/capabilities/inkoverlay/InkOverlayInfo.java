package net.splatcraft.forge.data.capabilities.inkoverlay;

import net.splatcraft.forge.util.ColorUtils;
import net.minecraft.nbt.CompoundNBT;

public class InkOverlayInfo implements IInkOverlayInfo
{
    private int color = ColorUtils.DEFAULT;
    private float amount = 0;

    public InkOverlayInfo()
    {
    }

    @Override
    public int getColor()
    {
        return color;
    }

    @Override
    public void setColor(int color)
    {
        this.color = color;
    }

    @Override
    public float getAmount()
    {
        return amount;
    }

    @Override
    public void setAmount(float v)
    {
        amount = Math.max(0, v);
    }

    @Override
    public void addAmount(float v)
    {
        setAmount(amount + v);
    }

    @Override
    public CompoundNBT writeNBT(CompoundNBT nbt)
    {
        nbt.putInt("Color", getColor());
        nbt.putFloat("Amount", getAmount());
        return nbt;
    }

    @Override
    public void readNBT(CompoundNBT nbt)
    {
        setColor(ColorUtils.getColorFromNbt(nbt));
        setAmount(nbt.getFloat("Amount"));
    }

    @Override
    public String toString()
    {
        return "Color: " + color + " Amount: " + amount;
    }
}
