package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.client.renderer.InkedBlockTileEntityRenderer;
import com.cibernet.splatcraft.client.renderer.StageBarrierTileEntityRenderer;
import com.cibernet.splatcraft.tileentities.*;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

import static com.cibernet.splatcraft.registries.SplatcraftBlocks.*;

public class SplatcraftTileEntitites
{
	public static final DeferredRegister<TileEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Splatcraft.MODID);
	
	public static final RegistryObject<TileEntityType<InkColorTileEntity>> colorTileEntity = registerTileEntity("color", InkColorTileEntity::new, inkedWool, canvas);
	public static final RegistryObject<TileEntityType<InkwellTileEntity>> inkwellTileEntity = registerTileEntity("inkwell", InkwellTileEntity::new, inkwell);
	public static final RegistryObject<TileEntityType<InkedBlockTileEntity>> inkedTileEntity = registerTileEntity("inked_block", InkedBlockTileEntity::new, inkedBlock);
	public static final RegistryObject<TileEntityType<CrateTileEntity>> crateTileEntity = registerTileEntity("crate", CrateTileEntity::new, crate, sunkenCrate);
	public static final RegistryObject<TileEntityType<StageBarrierTileEntity>> stageBarrierTileEntity = registerTileEntity("stage_barrier", StageBarrierTileEntity::new, stageBarrier, stageVoid);
	
	public static void init()
	{
		REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	private static <T extends TileEntity> RegistryObject<TileEntityType<T>> registerTileEntity(String name, Supplier<T> factoryIn, Block... allowedBlocks)
	{
		return REGISTRY.register(name, () -> TileEntityType.Builder.create(factoryIn, allowedBlocks).build(null));
	}
	
	public static void bindTESR()
	{
		//ClientRegistry.bindTileEntityRenderer(inkedTileEntity.get(), InkedBlockTileEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(stageBarrierTileEntity.get(), StageBarrierTileEntityRenderer::new);
	}
}
