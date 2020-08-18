package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.capabilities.saveinfo.SaveInfoCapability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.Dimension;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Splatcraft.MODID)
public class SplatcraftCapabilities
{
	public static void registerCapabilities()
	{
		PlayerInfoCapability.register();
		SaveInfoCapability.register();
	}
	
	@SubscribeEvent
	public static void attachEntityCapabilities(final AttachCapabilitiesEvent<Entity> event)
	{
		if(event.getObject() instanceof PlayerEntity)
			event.addCapability(new ResourceLocation(Splatcraft.MODID, "player_info"), new PlayerInfoCapability());
		
	}
	
	@SubscribeEvent
	public static void attachWorldCapabilities(final AttachCapabilitiesEvent<World> event)
	{
		if(event.getObject().func_234923_W_() == World.field_234918_g_)
			event.addCapability(new ResourceLocation(Splatcraft.MODID, "save_info"), new SaveInfoCapability());
	}
}
