package com.cibernet.splatcraft.data.capabilities.playerinfo;

import com.cibernet.splatcraft.entities.InkSquidEntity;
import com.cibernet.splatcraft.registries.SplatcraftInkColors;
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

public class PlayerInfoCapability implements ICapabilitySerializable<CompoundNBT> {
    @CapabilityInject(IPlayerInfo.class)
    public static final Capability<IPlayerInfo> CAPABILITY = null;
    private final LazyOptional<IPlayerInfo> instance = LazyOptional.of(CAPABILITY::getDefaultInstance);
    private static final IPlayerInfo DEFAULT = new PlayerInfo(SplatcraftInkColors.undyed.getColor());


    public static void register() {
        CapabilityManager.INSTANCE.register(IPlayerInfo.class, new PlayerInfoStorage(), PlayerInfo::new);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return CAPABILITY.orEmpty(cap, instance);
    }

    @Override
    public CompoundNBT serializeNBT() {
        return (CompoundNBT) CAPABILITY.getStorage().writeNBT(CAPABILITY, instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional cannot be empty!")), null);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        CAPABILITY.getStorage().readNBT(CAPABILITY, instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional cannot be empty!")), null, nbt);
    }

    public static IPlayerInfo get(LivingEntity entity) throws NullPointerException {
        return entity.getCapability(CAPABILITY).orElse(null);
    }

    public static boolean hasCapability(LivingEntity entity) {
        try {
            get(entity);
            return true;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static boolean isSquid(LivingEntity entity) {
        if (entity instanceof InkSquidEntity)
            return true;

        try {
            return get(entity).isSquid();
        } catch (NullPointerException e) {
            return false;
        }
    }
}
