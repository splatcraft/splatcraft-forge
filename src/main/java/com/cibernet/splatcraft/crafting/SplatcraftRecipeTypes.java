package com.cibernet.splatcraft.crafting;


import com.cibernet.splatcraft.Splatcraft;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

public class SplatcraftRecipeTypes
{
	public static IRecipeType<AbstractWeaponRecipe> WEAPON_STATION_TYPE;
	public static IRecipeType<InkVatColorRecipe> INK_VAT_COLOR_CRAFTING_TYPE;
	
	public static final IRecipeSerializer<InkVatColorRecipe> INK_VAT_COLOR_CRAFTING = new InkVatColorRecipe.InkVatColorSerializer("ink_vat_color");
	
	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class Subscriber
	{
		@SubscribeEvent
		public static void registerSerializers(final RegistryEvent.Register<IRecipeSerializer<?>> event)
		{
			IForgeRegistry<IRecipeSerializer<?>> registry = event.getRegistry();
			
			WEAPON_STATION_TYPE = IRecipeType.register(Splatcraft.MODID + ":weapon_station");
			INK_VAT_COLOR_CRAFTING_TYPE = IRecipeType.register(Splatcraft.MODID + ":ink_vat_color");
			
			registry.register(INK_VAT_COLOR_CRAFTING);
		}
	}
	
	
}
