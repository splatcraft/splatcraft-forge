package net.splatcraft.forge.data.capabilities.saveinfo;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.Nullable;

public class SaveInfoStorage implements Capability.IStorage<ISaveInfo>
{
    @Nullable
    @Override
    public INBT writeNBT(Capability<ISaveInfo> capability, ISaveInfo instance, Direction side)
    {
        return instance.writeNBT(new CompoundNBT());
    }

    @Override
    public void readNBT(Capability<ISaveInfo> capability, ISaveInfo instance, Direction side, INBT nbt)
    {
        instance.readNBT((CompoundNBT) nbt);
    }
}
