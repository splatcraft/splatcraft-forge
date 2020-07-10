package com.cibernet.splatcraft.capabilities;

import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.nbt.CompoundNBT;

public class PlayerInfo implements IPlayerInfo
{
	private int color = ColorUtils.getRandomStarterColor();
	private boolean isSquid = false;
	
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
		return nbt;
	}
	
	@Override
	public void readNBT(CompoundNBT nbt)
	{
		setColor(nbt.getInt("Color"));
	}
}
