package com.cibernet.splatcraft.data.capabilities.inkoverlay;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class InkOverlayStorage implements Capability.IStorage<IInkOverlayInfo>
{
    @Nullable
    @Override
    public INBT writeNBT(Capability<IInkOverlayInfo> capability, IInkOverlayInfo instance, Direction side)
    {
        return instance.writeNBT(new CompoundNBT());
    }

    @Override
    public void readNBT(Capability<IInkOverlayInfo> capability, IInkOverlayInfo instance, Direction side, INBT nbt)
    {
        instance.readNBT((CompoundNBT) nbt);
    }
}
