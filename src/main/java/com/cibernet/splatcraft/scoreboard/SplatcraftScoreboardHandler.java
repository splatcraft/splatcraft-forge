package com.cibernet.splatcraft.scoreboard;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import com.cibernet.splatcraft.world.save.SplatCraftSaveHandler;
import static com.cibernet.splatcraft.world.save.SplatCraftSaveHandler.*;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.IScoreCriteria;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static com.cibernet.splatcraft.scoreboard.CommandColorScores.*;


public class SplatcraftScoreboardHandler
{
	
	public static final IScoreCriteria COLOR = new ScoreCriteria(SplatCraft.SHORT.toLowerCase() + ".inkColor");
	public static final IScoreCriteria INK = new ScoreCriteria(SplatCraft.SHORT.toLowerCase() + ".inkUnits");
	
	public static void updatePlayerColorScore(EntityPlayer player, int color)
	{
		for(ScoreObjective objective : player.getWorldScoreboard().getObjectivesFromCriteria(COLOR))
			player.getWorldScoreboard().getOrCreateScore(player.getName(), objective).setScorePoints(color);
	}
	
	protected static void createGoal(int color)
	{
		COLOR_GOALS.put(color, new ScoreCriteriaInkColor[]
				{
						new ScoreCriteriaInkColor("colorKills", color),
						new ScoreCriteriaInkColor("deathsAsColor", color),
						new ScoreCriteriaInkColor("killsAsColor", color),
						new ScoreCriteriaInkColor("colorWins", color),
						new ScoreCriteriaInkColor("colorLosses", color),
				});
	}
	
	public static void writeToNBT(NBTTagCompound nbt)
	{
		NBTTagCompound goalCompound = new NBTTagCompound();
		int[] colorArray = new int[COLOR_GOALS.keySet().size()];
		int i = 0;
		for(int color : COLOR_GOALS.keySet())
		{
			colorArray[i] = color;
			i++;
		}
		
		nbt.setIntArray("storedGoals", colorArray);
		
	}
	
	public static void readFromNBT(NBTTagCompound nbt)
	{
		if(nbt == null)
			return;
		
		COLOR_GOALS.clear();
		
		for(int color : nbt.getIntArray("storedGoals"))
			createGoal(color);
	}
	
	
	public static boolean hasGoal(int color) {return COLOR_GOALS.containsKey(color);}
	
	public static ScoreCriteriaInkColor getColorKills(int color) {return COLOR_GOALS.get(color)[0];}
	public static ScoreCriteriaInkColor getKillsAsColor(int color) {return COLOR_GOALS.get(color)[1];}
	public static ScoreCriteriaInkColor getDeathsAsColor(int color) {return COLOR_GOALS.get(color)[2];}
	public static ScoreCriteriaInkColor getColorWins(int color) {return COLOR_GOALS.get(color)[3];}
	public static ScoreCriteriaInkColor getColorLosses(int color) {return COLOR_GOALS.get(color)[4];}
}
