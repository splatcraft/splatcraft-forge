package net.splatcraft.forge.data.capabilities.playerinfo;

import net.splatcraft.forge.entities.InkSquidEntity;
import net.splatcraft.forge.registries.SplatcraftInkColors;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlayerInfoCapability implements ICapabilitySerializable<CompoundNBT>
{
    @CapabilityInject(IPlayerInfo.class)
    public static final Capability<IPlayerInfo> CAPABILITY = null;
    private static final IPlayerInfo DEFAULT = new PlayerInfo(SplatcraftInkColors.undyed.getColor());
    private final LazyOptional<IPlayerInfo> instance = LazyOptional.of(CAPABILITY::getDefaultInstance);

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IPlayerInfo.class, new PlayerInfoStorage(), PlayerInfo::new);
    }

    public static IPlayerInfo get(LivingEntity entity) throws NullPointerException
    {
        return entity.getCapability(CAPABILITY).orElse(null);
    }

    public static boolean hasCapability(LivingEntity entity)
    {
        return CAPABILITY != null && entity.getCapability(CAPABILITY).isPresent();
    }

    public static boolean isSquid(LivingEntity entity)
    {
        if (entity instanceof InkSquidEntity)
            return true;

        return hasCapability(entity) && get(entity).isSquid();
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
