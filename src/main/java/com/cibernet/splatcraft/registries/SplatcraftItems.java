package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.items.BlockItem;
import com.cibernet.splatcraft.items.ShooterItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.cibernet.splatcraft.registries.SplatcraftItemGroups.*;

public class SplatcraftItems
{
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, Splatcraft.MODID);
	
	//Shooters
	public static final RegistryObject<Item> splattershot = REGISTRY.register("splattershot", () -> new ShooterItem());
	
	//Rollers
	
	//Chargers
	
	//Dualies
	
	//Sloshers
	
	//Ink Tanks
	
	//Materials
	
	public static final RegistryObject<Item> sardinium = REGISTRY.register("sardinium", () -> new Item(new Item.Properties().group(GROUP_GENERAL)));
	public static final RegistryObject<Item> sardiniumBlock = REGISTRY.register("sardinium_block", () -> new BlockItem(SplatcraftBlocks.sardiniumBlock));
	public static final RegistryObject<Item> sardiniumOre = REGISTRY.register("sardinium_ore", () -> new BlockItem(SplatcraftBlocks.sardiniumOre));
	public static final RegistryObject<Item> powerEgg = REGISTRY.register("power_egg", () -> new Item(new Item.Properties().group(GROUP_GENERAL)));
	public static final RegistryObject<Item> powerEggBlock = REGISTRY.register("power_egg_block", () -> new BlockItem(SplatcraftBlocks.powerEggBlock));
	
	//Vanity
	
	//Remotes
	
	//Misc
	
	
	public static void init()
	{
		REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
}
