package com.cibernet.splatcraft.capabilities;

import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.vector.Vector3d;

public class PlayerInfo implements IPlayerInfo
{
	private int color = ColorUtils.getRandomStarterColor();
	private boolean isSquid = false;
	private boolean initialized = false;
	
	@Override
	public boolean isInitialized()
	{
		return initialized;
	}
	
	@Override
	public void setInitialized(boolean init)
	{
		initialized = init;
	}
	
	@Override
	public int getColor()
	{
		return color;
	}
	
	@Override
	public void setColor(int color)
	{
		this.color = color;
	}
	
	@Override
	public boolean isSquid()
	{
		return isSquid;
	}
	
	@Override
	public void setIsSquid(boolean isSquid)
	{
		this.isSquid = isSquid;
	}
	
	@Override
	public CompoundNBT writeNBT(CompoundNBT nbt)
	{
		nbt.putInt("Color",getColor());
		nbt.putBoolean("IsSquid", isSquid());
		nbt.putBoolean("Initialized", initialized);
		return nbt;
	}
	
	@Override
	public void readNBT(CompoundNBT nbt)
	{
		setColor(nbt.getInt("Color"));
		setIsSquid(nbt.getBoolean("IsSquid"));
		setInitialized(nbt.getBoolean("Initialized"));
	}
}
