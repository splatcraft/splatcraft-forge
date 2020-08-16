package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.util.InkColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegistryBuilder;

@ObjectHolder(Splatcraft.MODID)
@Mod.EventBusSubscriber(modid = Splatcraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SplatcraftInkColors
{
	public static IForgeRegistry<InkColor> REGISTRY;
	
	//Starter Colors
	public static final InkColor orange = new InkColor("orange", 0xDF641A);
	public static final InkColor blue = new InkColor("blue", 0x26229F);
	public static final InkColor pink = new InkColor("pink", 0xC83D79);
	public static final InkColor green = new InkColor("green", 0x409D3B);
	
	@SubscribeEvent
	public static void registerInkColors(final RegistryEvent.Register<InkColor> event)
	{
		IForgeRegistry<InkColor> registry = event.getRegistry();
		
		registry.register(orange);
		registry.register(blue);
		registry.register(pink);
		registry.register(green);
	}
	
	@SubscribeEvent
	public static void registerRegistry(final RegistryEvent.NewRegistry event)
	{
		REGISTRY = new RegistryBuilder<InkColor>()
				.setName(new ResourceLocation(Splatcraft.MODID, "ink_colors"))
				.setType(InkColor.class)
				.set(DummyFactory.INSTANCE)
				.create();
	}
	
	private static class DummyFactory implements IForgeRegistry.DummyFactory<InkColor>
	{
		private static final DummyFactory INSTANCE = new DummyFactory();
		
		@Override
		public InkColor createDummy(ResourceLocation key)
		{
			return new InkColor.DummyType().setRegistryName(key);
		}
	}
}
