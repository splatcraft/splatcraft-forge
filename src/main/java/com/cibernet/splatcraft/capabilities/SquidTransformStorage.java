package com.cibernet.splatcraft.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class SquidTransformStorage implements Capability.IStorage<CabailitySquidTransform>
{
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<CabailitySquidTransform> capability, CabailitySquidTransform instance, EnumFacing side)
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setBoolean("squidTransform", instance.getValue());
        return nbt;
    }

    @Override
    public void readNBT(Capability<CabailitySquidTransform> capability, CabailitySquidTransform instance, EnumFacing side, NBTBase nbt)
    {
        instance.setValue(((NBTTagCompound)nbt).getBoolean("squidTransform"));
    }
}
