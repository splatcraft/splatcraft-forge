package com.cibernet.splatcraft;


import com.cibernet.splatcraft.commands.CommandTurfWar;
import com.cibernet.splatcraft.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(name = SplatCraft.NAME, modid = SplatCraft.MODID, version = SplatCraft.VERSION)
public class SplatCraft
{
	public static final String MODID = "splatcraft";
	public static final String NAME = "SplatCraft";
	public static final String VERSION = "SplatCraft";
	public static final String SHORT = "MSM";
	
	@Mod.Instance(SplatCraft.MODID)
	public static SplatCraft instance;
	
	@SidedProxy
			(
					clientSide ="com.cibernet.splatcraft.proxy.ClientProxy",
					serverSide ="com.cibernet.splatcraft.proxy.CommonProxy"
			)
	public static CommonProxy proxy;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		proxy.preInit();
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.init();
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		proxy.postInit();
	}
	
	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandTurfWar());
	}
}
