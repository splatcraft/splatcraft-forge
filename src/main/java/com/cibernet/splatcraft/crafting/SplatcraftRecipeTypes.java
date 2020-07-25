package com.cibernet.splatcraft.crafting;


import com.cibernet.splatcraft.Splatcraft;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class SplatcraftRecipeTypes
{
	public static IRecipeType<AbstractWeaponRecipe> WEAPON_STATION;
	
	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class Subscriber
	{
		@SubscribeEvent
		public static void registerSerializers(final RegistryEvent.Register<IRecipeSerializer<?>> event)
		{
			WEAPON_STATION = IRecipeType.register(Splatcraft.MODID + ":weapon_station");
		}
	}
	
	
}
