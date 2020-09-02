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
	public static IRecipeType<AbstractWeaponWorkbenchRecipe> WEAPON_STATION_TYPE;
	public static IRecipeType<WeaponWorkbenchTab> WEAPON_STATION_TAB_TYPE;
	public static IRecipeType<WeaponWorkbenchSubtypeRecipe> WEAPON_STATION_SUB_TYPE;
	public static IRecipeType<InkVatColorRecipe> INK_VAT_COLOR_CRAFTING_TYPE;
	
	public static final IRecipeSerializer<InkVatColorRecipe> INK_VAT_COLOR_CRAFTING = new InkVatColorRecipe.InkVatColorSerializer("ink_vat_color");
	public static final IRecipeSerializer<WeaponWorkbenchTab> WEAPON_STATION_TAB = new WeaponWorkbenchTab.WeaponWorkbenchTabSerializer("weapon_workbench_tab");
	public static final IRecipeSerializer<WeaponWorkbenchRecipe> WEAPON_STATION = new WeaponWorkbenchRecipe.Serializer("weapon_workbench");
	public static final IRecipeSerializer<WeaponWorkbenchSubtypeRecipe> WEAPON_STATION_SUBTYPE = new WeaponWorkbenchSubtypeRecipe.Serializer("weapon_workbench_subtype");
	
	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class Subscriber
	{
		@SubscribeEvent
		public static void registerSerializers(final RegistryEvent.Register<IRecipeSerializer<?>> event)
		{
			IForgeRegistry<IRecipeSerializer<?>> registry = event.getRegistry();
			
			INK_VAT_COLOR_CRAFTING_TYPE = IRecipeType.register(Splatcraft.MODID + ":ink_vat_color");
			WEAPON_STATION_TAB_TYPE = IRecipeType.register(Splatcraft.MODID + ":weapon_workbench_tab");
			WEAPON_STATION_TYPE = IRecipeType.register(Splatcraft.MODID + ":weapon_workbench");
			WEAPON_STATION_SUB_TYPE = IRecipeType.register(Splatcraft.MODID + ":weapon_workbench_subtype");
			
			registry.register(INK_VAT_COLOR_CRAFTING);
			registry.register(WEAPON_STATION_TAB);
			registry.register(WEAPON_STATION);
			registry.register(WEAPON_STATION_SUBTYPE);
		}
	}
	
	
}
