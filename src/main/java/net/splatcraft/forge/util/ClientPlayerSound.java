package net.splatcraft.forge.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

public class ClientPlayerSound extends SimpleSound
{
    public ClientPlayerSound(SoundEvent soundIn, SoundCategory categoryIn, float volumeIn, float pitchIn)
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
