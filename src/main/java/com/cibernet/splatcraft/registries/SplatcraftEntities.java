package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.client.renderer.InkProjectileRenderer;
import com.cibernet.splatcraft.client.renderer.InkSquidRenderer;
import com.cibernet.splatcraft.client.renderer.SquidBumperRenderer;
import com.cibernet.splatcraft.entities.InkProjectileEntity;
import com.cibernet.splatcraft.entities.InkSquidEntity;
import com.cibernet.splatcraft.entities.SquidBumperEntity;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.*;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Splatcraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SplatcraftEntities
{
	
	public static final EntityType<InkProjectileEntity> INK_PROJECTILE = create("ink_projectile", InkProjectileEntity::new, EntityClassification.MISC);
	public static final EntityType<InkSquidEntity> INK_SQUID = create("ink_squid", InkSquidEntity::new, EntityClassification.AMBIENT, 0.6f, 0.5f);
	public static final EntityType<SquidBumperEntity> SQUID_BUMPER = create("squid_bumper", SquidBumperEntity::new, EntityClassification.MISC, 0.6f, 1.8f);
	
	@SubscribeEvent
	public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event)
	{
		IForgeRegistry<EntityType<?>> registry = event.getRegistry();
		
		registry.register(INK_PROJECTILE);
		registry.register(INK_SQUID);
		registry.register(SQUID_BUMPER);
	}
	
	private static <T extends Entity> EntityType<T> create(String name, EntityType.IFactory<T> supplier, EntityClassification classification, float width, float height)
	{
		EntityType<T> type =  EntityType.Builder.create(supplier, classification).size(width,height).build(Splatcraft.MODID+":"+name);
		
		type.setRegistryName(name);
		return type;
	}
	
	private static <T extends LivingEntity> EntityType<T> createLiving(String name, EntityType.IFactory<T> supplier, EntityClassification classification, float width, float height, Consumer<AttributeModifierMap> map)
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
		RenderingRegistry.registerEntityRenderingHandler(INK_SQUID, InkSquidRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(SQUID_BUMPER, SquidBumperRenderer::new);
	}
	
	public static void setEntityAttributes()
	{
		GlobalEntityTypeAttributes.put(SplatcraftEntities.INK_SQUID, InkSquidEntity.setCustomAttributes().create());
		GlobalEntityTypeAttributes.put(SplatcraftEntities.SQUID_BUMPER, SquidBumperEntity.setCustomAttributes().create());
		
		GlobalEntityTypeAttributes.put(EntityType.PLAYER, getAttributeMutableMap(EntityType.PLAYER).createMutableAttribute(SplatcraftItems.INK_SWIM_SPEED, 0.075).create());
	}
	
	protected static AttributeModifierMap.MutableAttribute getAttributeMutableMap(EntityType<? extends LivingEntity> entityType)
	{
		AttributeModifierMap.MutableAttribute result = new AttributeModifierMap.MutableAttribute();
		
		Object obj = ObfuscationReflectionHelper.getPrivateValue(AttributeModifierMap.class, GlobalEntityTypeAttributes.getAttributesForEntity(entityType), "field_233802_a_");
		
		if(obj instanceof Map)
		{
			Map<Attribute, ModifiableAttributeInstance> map = (Map<Attribute, ModifiableAttributeInstance>) obj;
			for(Map.Entry<Attribute, ModifiableAttributeInstance> entry : map.entrySet())
				result.createMutableAttribute(entry.getKey(), entry.getValue().getValue());
		}
		
		return result;
	}
}
