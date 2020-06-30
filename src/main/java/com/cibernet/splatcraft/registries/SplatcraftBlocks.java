package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.blocks.LightBlock;
import com.cibernet.splatcraft.blocks.MetalBlock;
import com.cibernet.splatcraft.blocks.OreBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SplatcraftBlocks
{
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, Splatcraft.MODID);
	
	public static final RegistryObject<Block> sardiniumBlock = REGISTRY.register("sardinium_block", () -> new MetalBlock(Material.IRON, MaterialColor.WHITE_TERRACOTTA));
	public static final RegistryObject<Block> sardiniumOre = REGISTRY.register("sardinium_ore", () -> new OreBlock(1));
	public static final RegistryObject<Block> powerEggBlock = REGISTRY.register("power_egg_block", () -> new LightBlock(9, AbstractBlock.Properties.create(Material.IRON, DyeColor.ORANGE).harvestTool(ToolType.SHOVEL).sound(SoundType.SLIME).hardnessAndResistance(0.2f, 0)));
	
	public static void init()
	{
		REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
}
