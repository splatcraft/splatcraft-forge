package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.SplatCraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

public class SplatCraftSounds
{
	private static List<SoundEvent> sounds = new ArrayList<>();
	
	public static SoundEvent shooterShot;
	public static SoundEvent chargerCharge;
	public static SoundEvent chargerShot;
	
	public static void initSounds()
	{
		//shooterShot = createSoundEvent("shooterFiring");
		//chargerCharge = createSoundEvent("chargerCharge");
	}
	
	private static SoundEvent createSoundEvent(String id)
	{
		ResourceLocation loc = new ResourceLocation(SplatCraft.MODID, id);
		SoundEvent sound = new SoundEvent(loc).setRegistryName(loc);
		sounds.add(sound);
		return sound;
	}
	
	@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> event)
	{
		IForgeRegistry<SoundEvent> registry = event.getRegistry();
		for(SoundEvent sound : sounds)
			registry.register(sound);
	}
}
