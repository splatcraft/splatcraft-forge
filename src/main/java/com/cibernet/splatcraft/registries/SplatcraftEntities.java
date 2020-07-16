package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.client.renderer.InkProjectileRenderer;
import com.cibernet.splatcraft.entities.InkProjectileEntity;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Splatcraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SplatcraftEntities
{
	
	public static final EntityType<InkProjectileEntity> INK_PROJECTILE = create("ink_projectile", InkProjectileEntity::new, EntityClassification.MISC);
	
	@SubscribeEvent
	public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event)
	{
		IForgeRegistry<EntityType<?>> registry = event.getRegistry();
		
		registry.register(INK_PROJECTILE);
	}
	
	private static <T extends Entity> EntityType<T> create(String name, EntityType.IFactory<T> supplier, EntityClassification classification, int width, int height)
	{
		EntityType<T> type =  EntityType.Builder.create(supplier, classification).size(width,height).build(Splatcraft.MODID+":"+name);
		type.setRegistryName(name);
		return type;
	}
	
	private static <T extends Entity> EntityType<T> create(String name, EntityType.IFactory<T> supplier, EntityClassification classification)
	{
		return create(name, supplier, classification,1, 1);
	}
	
	public static void bindRenderers()
	{
		RenderingRegistry.registerEntityRenderingHandler(INK_PROJECTILE, InkProjectileRenderer::new);
	}
}
