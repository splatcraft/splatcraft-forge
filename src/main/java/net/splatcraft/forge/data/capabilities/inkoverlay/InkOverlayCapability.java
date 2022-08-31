package net.splatcraft.forge.data.capabilities.inkoverlay;

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

public class InkOverlayCapability implements ICapabilitySerializable<CompoundNBT>
{
    @CapabilityInject(IInkOverlayInfo.class)
    public static final Capability<IInkOverlayInfo> CAPABILITY = null;
    private static final IInkOverlayInfo DEFAULT = new InkOverlayInfo();
    private final LazyOptional<IInkOverlayInfo> instance = LazyOptional.of(CAPABILITY::getDefaultInstance);

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IInkOverlayInfo.class, new InkOverlayStorage(), InkOverlayInfo::new);
    }

    public static IInkOverlayInfo get(LivingEntity entity) throws NullPointerException
    {
        return entity.getCapability(CAPABILITY).orElse(null);
    }

    public static boolean hasCapability(LivingEntity entity)
    {
        return entity.getCapability(CAPABILITY).isPresent();
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
