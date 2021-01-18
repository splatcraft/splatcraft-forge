package com.cibernet.splatcraft;

import com.cibernet.splatcraft.data.SplatcraftData;
import com.cibernet.splatcraft.data.tags.SplatcraftTags;
import com.cibernet.splatcraft.entities.InkSquidEntity;
import com.cibernet.splatcraft.handlers.ScoreboardHandler;
import com.cibernet.splatcraft.handlers.SplatcraftCommonHandler;
import com.cibernet.splatcraft.handlers.WeaponHandler;
import com.cibernet.splatcraft.handlers.client.ClientSetupHandler;
import com.cibernet.splatcraft.handlers.client.PlayerMovementHandler;
import com.cibernet.splatcraft.handlers.client.SplatcraftKeyHandler;
import com.cibernet.splatcraft.network.SplatcraftPacketHandler;
import com.cibernet.splatcraft.registries.*;
import com.cibernet.splatcraft.wolrd.gen.SplatcraftOreGen;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Splatcraft.MODID)
public class Splatcraft
{
	// Directly reference a log4j logger.
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String MODID = "splatcraft";
	public static final String MODNAME = "Splatcraft";
	public static final String SHORT = "SC";
	public static final String VERSION = "2.3.0";
	
	public Splatcraft()
	{
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SplatcraftConfig.clientConfig);
		SplatcraftConfig.loadConfig(SplatcraftConfig.clientConfig, FMLPaths.CONFIGDIR.get().resolve(Splatcraft.MODID + "-client.toml").toString());

		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
		
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	private void commonSetup(final FMLCommonSetupEvent event)
	{
		SplatcraftCapabilities.registerCapabilities();
		SplatcraftPacketHandler.registerMessages();
		
		DeferredWorkQueue.runLater(() ->
		{
			SplatcraftEntities.setEntityAttributes();
			SplatcraftGameRules.registerGamerules();
		});
		
		SplatcraftSounds.initSounds();
		SplatcraftTags.register();
		SplatcraftStats.register();
		ScoreboardHandler.register();
		SplatcraftCommands.registerArguments();

		SplatcraftOreGen.registerOres();
	}
	
	private void clientSetup(final FMLClientSetupEvent event)
	{
		SplatcraftEntities.bindRenderers();
		SplatcraftKeyHandler.registerKeys();
		SplatcraftBlocks.setRenderLayers();
		SplatcraftTileEntitites.bindTESR();
		
		
		DeferredWorkQueue.runLater(() ->
		{
			SplatcraftItems.registerModelProperties();
			SplatcraftItems.registerArmorModels();
			ClientSetupHandler.bindScreenContainers();
		});
	}
	
	@SubscribeEvent
	public void onServerAboutToStart(FMLServerAboutToStartEvent event)
	{
	
	}
	
	@SubscribeEvent
	public void onServerStarted(FMLServerStartedEvent event)
	{
		for(Map.Entry<Integer, Boolean> rule : SplatcraftGameRules.booleanRules.entrySet())
			SplatcraftGameRules.booleanRules.put(rule.getKey(), event.getServer().getGameRules().getBoolean(SplatcraftGameRules.getRuleFromIndex(rule.getKey())));
	}
	
	@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
	public static class RegistryEvents
	{
		@SubscribeEvent
		public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent)
		{
		}
	}
}
