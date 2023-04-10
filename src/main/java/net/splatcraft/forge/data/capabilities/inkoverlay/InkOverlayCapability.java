package net.splatcraft.forge.data.capabilities.inkoverlay;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InkOverlayCapability implements ICapabilityProvider, INBTSerializable<CompoundTag>
{
    public static Capability<InkOverlayInfo> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    private InkOverlayInfo inkOverlayInfo = null;
    private final LazyOptional<InkOverlayInfo> opt = LazyOptional.of(() ->
            inkOverlayInfo == null ? (inkOverlayInfo = new InkOverlayInfo()) : inkOverlayInfo);

    public static InkOverlayInfo get(LivingEntity entity) throws NullPointerException
    {
        return entity.getCapability(CAPABILITY).orElseThrow(NullPointerException::new);
    }

    public static boolean hasCapability(LivingEntity entity)
    {
        return entity.getCapability(CAPABILITY).isPresent();
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == CAPABILITY ? opt.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT()
    {
        return opt.orElse(null).writeNBT(new CompoundTag());
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        opt.orElse(null).readNBT(nbt);
    }
}
