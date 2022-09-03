package net.splatcraft.forge.data.capabilities.playerinfo;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.Nullable;

public class PlayerInfoStorage implements Capability.IStorage<IPlayerInfo>
{
    @Nullable
    @Override
    public INBT writeNBT(Capability<IPlayerInfo> capability, IPlayerInfo instance, Direction side)
    {
        return instance.writeNBT(new CompoundNBT());
    }

    @Override
    public void readNBT(Capability<IPlayerInfo> capability, IPlayerInfo instance, Direction side, INBT nbt)
    {
        instance.readNBT((CompoundNBT) nbt);
    }
}
