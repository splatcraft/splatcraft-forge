package com.cibernet.splatcraft.capabilities;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class PlayerColorStorage implements Capability.IStorage<IPlayerColor>
{
	@Nullable
	@Override
	public INBT writeNBT(Capability<IPlayerColor> capability, IPlayerColor instance, Direction side)
	{
		return IntNBT.valueOf(instance.getColor());
	}
	
	@Override
	public void readNBT(Capability<IPlayerColor> capability, IPlayerColor instance, Direction side, INBT nbt)
	{
		instance.setColor(((IntNBT)nbt).getInt());
	}
}
