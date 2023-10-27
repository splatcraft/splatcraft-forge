package net.splatcraft.forge.data.capabilities.playerinfo;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.splatcraft.forge.entities.InkSquidEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerInfoCapability implements ICapabilityProvider, INBTSerializable<CompoundTag>
{
    public static Capability<PlayerInfo> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    private PlayerInfo playerInfo = null;
    private final LazyOptional<PlayerInfo> opt = LazyOptional.of(() ->
            playerInfo == null ? (playerInfo = new PlayerInfo()) : playerInfo);

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
    {
        return cap == CAPABILITY ? opt.cast() : LazyOptional.empty();
    }

    public static PlayerInfo get(LivingEntity entity)
    {
        return entity.getCapability(CAPABILITY).orElseThrow(IllegalStateException::new);
    }

    @Override
    public CompoundTag serializeNBT() {
        return opt.orElseThrow(IllegalStateException::new).writeNBT(new CompoundTag());
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        opt.orElseThrow(IllegalStateException::new).readNBT(nbt);
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
}

/* implements ICapabilitySerializable<CompoundTag>
{
    @CapabilityInject(PlayerInfo.class)
    public static final Capability<PlayerInfo> CAPABILITY = null;
    private static final PlayerInfo DEFAULT = new PlayerInfo(SplatcraftInkColors.undyed.getColor());
    private final LazyOptional<PlayerInfo> instance = LazyOptional.of(CAPABILITY::getDefaultInstance);

    public static void register(RegisterCapabilitiesEvent event)
    {
        event.register(PlayerInfo.class);
    }

    public static PlayerInfo get(LivingEntity entity) throws NullPointerException
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

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
    {
        return CAPABILITY.orEmpty(cap, instance);
    }

    @Override
    public CompoundTag serializeNBT()
    {
        return (CompoundTag) CAPABILITY.getStorage().writeNBT(CAPABILITY, instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional cannot be empty!")), null);
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        CAPABILITY.getStorage().readNBT(CAPABILITY, instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional cannot be empty!")), null, nbt);
    }
} */
