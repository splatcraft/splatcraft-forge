package com.cibernet.splatcraft.data;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.data.tags.SplatcraftBlockTags;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

public class SplatcraftData
{
	@SubscribeEvent
	public void gatherData(GatherDataEvent event)
	{
		DataGenerator generator = event.getGenerator();
		
		if(event.includeClient())
		{
			generator.addProvider(new SplatcraftBlockTags(generator));
		}
	}
}
