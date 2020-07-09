package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.capabilities.PlayerColorCapability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class SplatcraftCapabilities
{
	public static void registerCapabilities()
	{
		PlayerColorCapability.register();
		System.out.println("did thing a");
	}
	
	@SubscribeEvent
	public void attachCapabilitiesEntity(final AttachCapabilitiesEvent<Entity> event)
	{
		System.out.println("did thing b");
		if(event.getObject() instanceof PlayerEntity)
		{
			event.addCapability(new ResourceLocation(Splatcraft.MODID, "player_ink_color"), new PlayerColorCapability());
			System.out.println("did thing c");
		}
	}
}
