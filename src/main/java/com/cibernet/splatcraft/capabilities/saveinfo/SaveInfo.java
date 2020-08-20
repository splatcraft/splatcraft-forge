package com.cibernet.splatcraft.capabilities.saveinfo;

import com.cibernet.splatcraft.handlers.ScoreboardHandler;
import net.minecraft.nbt.CompoundNBT;

import java.util.ArrayList;
import java.util.Collection;

public class SaveInfo implements ISaveInfo
{
	private ArrayList<Integer> colorScores = new ArrayList<>();
	
	
	@Override
	public Collection<Integer> getInitializedColorScores()
	{
		return colorScores;
	}
	
	@Override
	public void addInitializedColorScores(Integer... colors)
	{
		for(Integer color : colors)
			if(!colorScores.contains(color))
				colorScores.add(color);
	}
	
	@Override
	public void removeColorScore(Integer color)
	{
		if(colorScores.contains(color))
			colorScores.remove(color);
	}
	
	@Override
	public CompoundNBT writeNBT(CompoundNBT nbt)
	{
		int[] arr = new int[colorScores.size()];
		for(int i = 0; i < colorScores.size(); i++)
			arr[i] = colorScores.get(i);
		
		nbt.putIntArray("StoredCriteria", arr);
		
		return nbt;
	}
	
	@Override
	public void readNBT(CompoundNBT nbt)
	{
		colorScores = new ArrayList<>();
		ScoreboardHandler.clearColorCriteria();
		
		for(int i : nbt.getIntArray("StoredCriteria"))
		{
			colorScores.add(i);
			ScoreboardHandler.createColorCriterion(i);
		}
		
	}
}
