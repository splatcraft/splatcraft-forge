package com.cibernet.splatcraft.capabilities.saveinfo;

import net.minecraft.nbt.CompoundNBT;

import java.util.Collection;

public interface ISaveInfo
{
	Collection<Integer> getInitializedColorScores();
	void addInitializedColorScores(Integer... colors);
	void removeColorScore(Integer color);
	
	CompoundNBT writeNBT(CompoundNBT nbt);
	void readNBT(CompoundNBT nbt);
}
