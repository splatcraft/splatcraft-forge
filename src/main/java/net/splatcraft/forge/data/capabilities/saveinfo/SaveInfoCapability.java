package net.splatcraft.forge.data.capabilities.saveinfo;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SaveInfoCapability implements ICapabilitySerializable<CompoundNBT>
{
    @CapabilityInject(ISaveInfo.class)
    public static final Capability<ISaveInfo> CAPABILITY = null;
    private static final ISaveInfo DEFAULT = new SaveInfo();
    private final LazyOptional<ISaveInfo> instance = LazyOptional.of(CAPABILITY::getDefaultInstance);

    public static void register()
    {
        CapabilityManager.INSTANCE.register(ISaveInfo.class, new SaveInfoStorage(), SaveInfo::new);
    }

    public static ISaveInfo get(MinecraftServer server) throws NullPointerException
    {
        return server.getLevel(World.OVERWORLD).getCapability(CAPABILITY).orElseThrow(() -> new NullPointerException("Couldn't find WorldData capability!"));
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
    {
        return CAPABILITY.orEmpty(cap, instance);
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        return (CompoundNBT) CAPABILITY.getStorage().writeNBT(CAPABILITY, instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional cannot be empty!")), null);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        CAPABILITY.getStorage().readNBT(CAPABILITY, instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional cannot be empty!")), null, nbt);
    }

}
