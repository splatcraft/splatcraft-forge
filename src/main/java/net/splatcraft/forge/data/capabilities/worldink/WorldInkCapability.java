package net.splatcraft.forge.data.capabilities.worldink;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WorldInkCapability implements ICapabilityProvider, INBTSerializable<CompoundTag>
{
    public static Capability<WorldInk> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    private WorldInk worldInk = null;
    private final LazyOptional<WorldInk> opt = LazyOptional.of(() ->
            worldInk == null ? (worldInk = new WorldInk()) : worldInk);

    public static WorldInk get(Level level, BlockPos pos) throws NullPointerException
    {
        return get(level.getChunkAt(pos));
    }

    public static WorldInk get(LevelChunk chunk) throws NullPointerException
    {
        return chunk.getCapability(CAPABILITY).orElseThrow(() -> new NullPointerException("Couldn't find WorldInk capability!"));
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == CAPABILITY ? opt.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return opt.orElse(null).writeNBT(new CompoundTag());
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        opt.orElse(null).readNBT(nbt);
    }
}
