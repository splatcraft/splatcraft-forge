package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.Splatcraft;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SplatcraftTileEntitites
{
	public static final DeferredRegister<TileEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Splatcraft.MODID);
	
	public static void init()
	{
		REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
}
