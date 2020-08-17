package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.capabilities.playerinfo.PlayerInfoCapability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Splatcraft.MODID)
public class SplatcraftCapabilities
{
	public static void registerCapabilities()
	{
		PlayerInfoCapability.register();
	}
	
	@SubscribeEvent
	public static void attachCapabilitiesEntity(final AttachCapabilitiesEvent<Entity> event)
	{
		if(event.getObject() instanceof PlayerEntity)
			event.addCapability(new ResourceLocation(Splatcraft.MODID, "player_info"), new PlayerInfoCapability());
		
	}
}
