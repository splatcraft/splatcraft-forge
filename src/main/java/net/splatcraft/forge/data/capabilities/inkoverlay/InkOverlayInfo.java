package net.splatcraft.forge.data.capabilities.inkoverlay;

import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.splatcraft.forge.util.ColorUtils;
import net.minecraft.nbt.CompoundNBT;

public class InkOverlayInfo implements IInkOverlayInfo
{
    private int color = ColorUtils.DEFAULT;
    private float amount = 0;

    private double squidRot;
    private double squidRotO;
    private Vector3d prevPos = null;

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
    public double getSquidRot() {
        return squidRot;
    }

    @Override
    public double getSquidRotO() {
        return squidRotO;
    }

    @Override
    public void setSquidRot(double v)
    {
        squidRotO = squidRot;
        squidRot = v;
    }

    @Override
    public Vector3d getPrevPosOrDefault(Vector3d def)
    {
        return prevPos == null ? def : prevPos;
    }

    @Override
    public void setPrevPos(Vector3d v)
    {
        prevPos = v;
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
