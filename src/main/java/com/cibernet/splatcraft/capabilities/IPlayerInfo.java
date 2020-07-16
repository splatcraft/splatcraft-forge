package com.cibernet.splatcraft.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3d;

public interface IPlayerInfo
{
	int getColor();
	void setColor(int color);
	
	boolean isSquid();
	void setIsSquid(boolean isSquid);
	
	CompoundNBT writeNBT(CompoundNBT nbt);
	void readNBT(CompoundNBT nbt);
}
