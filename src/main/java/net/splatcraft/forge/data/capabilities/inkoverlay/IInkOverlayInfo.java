package net.splatcraft.forge.data.capabilities.inkoverlay;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IInkOverlayInfo
{
    int getColor();

    void setColor(int color);

    float getAmount();

    void setAmount(float v);

    void addAmount(float v);

    //Sheep-exclusive values
    int getWoolColor();
    void setWoolColor(int v);

    //storing this here, doesn't feel necessary to make a new cap just for it
    @OnlyIn(Dist.CLIENT)
    double getSquidRot();
    @OnlyIn(Dist.CLIENT)
    double getSquidRotO();
    @OnlyIn(Dist.CLIENT)
    void setSquidRot(double v);
    @OnlyIn(Dist.CLIENT)
    Vector3d getPrevPosOrDefault(Vector3d def);
    @OnlyIn(Dist.CLIENT)
    void setPrevPos(Vector3d v);




    CompoundNBT writeNBT(CompoundNBT nbt);

    void readNBT(CompoundNBT nbt);

}
