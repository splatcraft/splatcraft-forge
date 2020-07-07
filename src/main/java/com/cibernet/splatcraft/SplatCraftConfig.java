package com.cibernet.splatcraft;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;

@Config(modid = SplatCraft.MODID)
@Config.LangKey("splatcraft.config.title")
public class SplatCraftConfig {
	
	@Config.Comment("Setting this to false makes it so that you don't have to hold the quid button to turn into a squid.")
	public static boolean holdKeyToSquid = true;
	@Config.Comment("Determines whether the durability bar on Splatcraft weapons that determines how much ink you have left matches its ink color or not")
	public static boolean dynamicInkDurabilityColor = true;
	
	
	@Mod.EventBusSubscriber(modid = SplatCraft.MODID)
	private static class EventHandler {
		
		/**
		 * Inject the new values and save to the config file when the config has been changed from the GUI.
		 *
		 * @param event The event
		 */
		@SubscribeEvent
		public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
			if (event.getModID().equals(SplatCraft.MODID)) {
				ConfigManager.sync(SplatCraft.MODID, Config.Type.INSTANCE);
			}
		}
	}
}