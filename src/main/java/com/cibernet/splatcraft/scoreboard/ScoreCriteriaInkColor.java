package com.cibernet.splatcraft.scoreboard;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import net.minecraft.scoreboard.IScoreCriteria;

public class ScoreCriteriaInkColor implements IScoreCriteria
{
	private final String goalName;
	private final int color;
	
	public ScoreCriteriaInkColor(String name, int color)
	{
		this.goalName = SplatCraft.SHORT.toLowerCase() + "." + name + "." + SplatCraftUtils.getUnlocColorName(color);
		this.color = color;
		
		IScoreCriteria.INSTANCES.put(this.goalName, this);
	}
	
	public int getColor() { return color; }
	
	public void remove()
	{
		IScoreCriteria.INSTANCES.remove(goalName);
	}
	
	@Override
	public String getName()
	{
		return goalName;
	}
	
	@Override
	public boolean isReadOnly()
	{
		return false;
	}
	
	@Override
	public EnumRenderType getRenderType()
	{
		return EnumRenderType.INTEGER;
	}
}
