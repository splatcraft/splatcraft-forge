package net.splatcraft.forge.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

import java.util.Objects;

public class ClientPlayerSound extends SimpleSoundInstance
{
    public ClientPlayerSound(SoundEvent soundIn, SoundSource categoryIn, float volumeIn, float pitchIn)
    {
        super(soundIn, categoryIn, volumeIn, pitchIn, new BlockPos(0, 0, 0));
    }

    @Override
    public double getX()
    {
        return Objects.requireNonNull(Minecraft.getInstance().player).getX();
    }

    @Override
    public double getY()
    {
        return Objects.requireNonNull(Minecraft.getInstance().player).getY();
    }

    @Override
    public double getZ()
    {
        return Objects.requireNonNull(Minecraft.getInstance().player).getZ();
    }
}
