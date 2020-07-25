package com.cibernet.splatcraft.data;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.data.recipes.SplatcraftCraftingRecipes;
import com.cibernet.splatcraft.data.tags.SplatcraftBlockTags;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

public class SplatcraftData
{
	
	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class GatherDataSubscriber {
		@SubscribeEvent
		public static void gatherData(GatherDataEvent event) {
			DataGenerator generator = event.getGenerator();
			if (event.includeServer())
			{
				generator.addProvider(new SplatcraftBlockTags(generator));
				generator.addProvider(new SplatcraftCraftingRecipes(generator));
			}
		}
	}
}
