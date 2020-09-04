package com.cibernet.splatcraft.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;

public class ClientPlayerSound extends SimpleSound
{
	public ClientPlayerSound(SoundEvent soundIn, SoundCategory categoryIn, float volumeIn, float pitchIn)
	{
		super(soundIn, categoryIn, volumeIn, pitchIn, new BlockPos(0,0,0));
	}
	
	@Override
	public double getX()
	{
		return Minecraft.getInstance().player.getPosX();
	}
	@Override
	public double getY()
	{
		return Minecraft.getInstance().player.getPosY();
	}
	@Override
	public double getZ()
	{
		return Minecraft.getInstance().player.getPosZ();
	}
}
