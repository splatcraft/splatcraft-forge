package com.cibernet.splatcraft.capabilities;

import com.cibernet.splatcraft.util.ColorUtils;

public class PlayerColor implements IPlayerColor
{
	private int color = ColorUtils.getRandomStarterColor();
	
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
}
