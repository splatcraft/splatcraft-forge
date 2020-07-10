package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.tileentities.InkedBlockTileEntity;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

import static com.cibernet.splatcraft.registries.SplatcraftBlocks.*;

public class SplatcraftTileEntitites
{
	public static final DeferredRegister<TileEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Splatcraft.MODID);
	
	public static final RegistryObject<TileEntityType<InkColorTileEntity>> colorTileEntity = registerTileEntity("color", InkColorTileEntity::new, inkwell);
	public static final RegistryObject<TileEntityType<InkedBlockTileEntity>> inkedTileEntity = registerTileEntity("inked_block", InkedBlockTileEntity::new, inkedBlock);
	
	public static void init()
	{
		REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	private static <T extends TileEntity> RegistryObject<TileEntityType<T>> registerTileEntity(String name, Supplier<T> factoryIn, Block... allowedBlocks)
	{
		return REGISTRY.register(name, () -> TileEntityType.Builder.create(factoryIn, allowedBlocks).build(null));
	}
}
