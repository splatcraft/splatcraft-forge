package com.cibernet.splatcraft.world.save;

import com.cibernet.splatcraft.scoreboard.CommandColorScores;
import com.cibernet.splatcraft.scoreboard.SplatcraftScoreboardHandler;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SplatCraftSaveHandler
{
	@SubscribeEvent
	public void onWorldSave(WorldEvent.Save event)
	{
		if(event.getWorld().provider.getDimension() != 0)	//Only save one time each world-save instead of one per dimension each world-save.
			return;
		
		File dataFile = event.getWorld().getSaveHandler().getMapFileFromName("SplatCraftData");
		if (dataFile != null)
		{
			NBTTagCompound nbt = new NBTTagCompound();
			
			SplatCraftPlayerData.writeToNBT(nbt);
			SplatCraftGamerules.writeToNBT(nbt);
			SplatcraftScoreboardHandler.writeToNBT(nbt);
			
			try {
				CompressedStreamTools.writeCompressed(nbt, new FileOutputStream(dataFile));
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event)
	{
		if(event.getWorld().provider.getDimension() != 0 || event.getWorld().isRemote)
			return;
		ISaveHandler saveHandler = event.getWorld().getSaveHandler();
		File dataFile = saveHandler.getMapFileFromName("SplatCraftData");
		if(dataFile != null && dataFile.exists())
		{
			NBTTagCompound nbt = null;
			try
			{
				nbt = CompressedStreamTools.readCompressed(new FileInputStream(dataFile));
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			if(nbt != null)
			{
				SplatCraftPlayerData.readFromNBT(nbt);
				SplatCraftGamerules.readFromNBT(nbt);
				SplatcraftScoreboardHandler.readFromNBT(nbt);
							
				return;
			}
		}
		else SplatCraftGamerules.resetGamerules();
		
		
		SplatCraftPlayerData.readFromNBT(null);
	}
}
