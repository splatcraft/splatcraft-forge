package com.cibernet.splatcraft.capabilities;

import net.minecraft.nbt.CompoundNBT;

public interface IPlayerInfo
{
	public int getColor();
	public void setColor(int color);
	
	boolean isSquid();
	void setIsSquid(boolean isSquid);
	
	CompoundNBT writeNBT(CompoundNBT nbt);
	void readNBT(CompoundNBT nbt);
}
