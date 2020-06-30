package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.Splatcraft;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SplatcraftEntities
{
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITIES, Splatcraft.MODID);
	
	public static void init()
	{
		REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
}
