package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.client.model.ArmoredInkTankModel;
import com.cibernet.splatcraft.client.model.ClassicInkTankModel;
import com.cibernet.splatcraft.client.model.InkTankJrModel;
import com.cibernet.splatcraft.client.model.InkTankModel;
import com.cibernet.splatcraft.dispenser.PlaceBlockDispenseBehavior;
import com.cibernet.splatcraft.items.*;
import com.cibernet.splatcraft.items.BlockItem;
import com.cibernet.splatcraft.items.remotes.ColorChangerItem;
import com.cibernet.splatcraft.items.remotes.InkDisruptorItem;
import com.cibernet.splatcraft.items.remotes.RemoteItem;
import com.cibernet.splatcraft.items.remotes.TurfScannerItem;
import com.cibernet.splatcraft.items.weapons.*;
import com.cibernet.splatcraft.util.SplatcraftArmorMaterial;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

import static com.cibernet.splatcraft.registries.SplatcraftItemGroups.*;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class SplatcraftItems
{
	public static final List<Item> weapons = new ArrayList<>();
	public static final ArrayList<Item> inkColoredItems = new ArrayList<>();
	
	//Attributes
	public static final Attribute INK_SWIM_SPEED = createAttribute("ink_swim_speed", (new RangedAttribute("attribute.splatcraft.ink_swim_speed", (double)0.7F, 0.0D, 1024.0D)).setShouldWatch(true));
	
	//Armor Materials
	public static final IArmorMaterial INK_CLOTH = new SplatcraftArmorMaterial("ink_cloth", SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0, 0, 0);
	public static final IArmorMaterial ARMORED_INK_TANK = new SplatcraftArmorMaterial("armored_ink_tank", SoundEvents.ITEM_ARMOR_EQUIP_IRON, 3, 0, 0.05f);
	
	//Shooters
	public static final ShooterItem splattershot = new ShooterItem("splattershot", 1.05f, 0.75f, 12f, 4, 8f, 0.9f);
	public static final ShooterItem tentatekSplattershot = new ShooterItem("tentatek_splattershot", splattershot);
	public static final ShooterItem wasabiSplattershot = new ShooterItem("wasabi_splattershot", splattershot);
	public static final ShooterItem splattershotJr = new ShooterItem("splattershot_jr", 1f, 0.55f, 13.5f, 4, 6.5f, 0.5f);
	public static final ShooterItem aerosprayMG = new ShooterItem("aerospray_mg", 1.3f, 0.45f, 26f, 2, 4.8f, 0.5f);
	public static final ShooterItem getAerosprayRG = new ShooterItem("aerospray_rg", aerosprayMG);
	public static final ShooterItem gal52 = new ShooterItem("52_gal", 1.2f, 0.78f, 16f, 9, 10.4f, 1.3f);
	public static final ShooterItem gal52Deco = new ShooterItem("52_gal_deco", gal52);
	public static final ShooterItem gal96 = new ShooterItem("96_gal", 1.3f, 0.85f, 12.5f, 11, 12.4f, 2.5f);
	public static final ShooterItem gal96Deco = new ShooterItem("96_gal_deco", gal96);
	
	//Blasters
	public static final BlasterItem blaster = new BlasterItem("blaster", 2.3f, 1f, 5f, 4, 20, 25f, 10f, 10f, 6);
	public static final BlasterItem grimBlaster = new BlasterItem("grim_blaster", blaster);
	public static final BlasterItem clashBlaster = new BlasterItem("clash_blaster", 1.8f, 1.2f, 5f, 1, 10, 12f, 6f, 4, 4);
	public static final BlasterItem clashBlasterNeo = new BlasterItem("clash_blaster_neo", clashBlaster);
	
	//Rollers
	
	//Chargers
	public static ChargerItem splatCharger = new ChargerItem("splat_charger", 0.85f, 1.8f, 13, 20, 40, 32f, 2.25f, 18f, 0.4);
	public static ChargerItem bentoSplatCharger = new ChargerItem("bento_splat_charger", splatCharger);
	public static ChargerItem kelpSplatCharger = new ChargerItem("kelp_splat_charger", splatCharger);
	public static ChargerItem eLiter4K = new ChargerItem("e_liter_4k", 0.95f, 2.4f, 16, 35, 40, 36f, 2.25f, 25f, 0.15);
	public static ChargerItem bamboozler14mk1 = new ChargerItem("bamboozler_14_mk1", 0, 0, 0, 4, 0, 16, 2.8f, 7, 0.8);
	
	//Dualies
	public static final DualieItem splatDualie = new DualieItem("splat_dualies", 1f, 0.65f, 10, 8, 6, 0.75f, 1, 0.7f, 9, 8, 30);
	public static final DualieItem enperrySplatDualie = new DualieItem("enperry_splat_dualies", splatDualie);
	public static final DualieItem dualieSquelcher = new DualieItem("dualie_squelchers", 0.9f, 0.74f, 11.5f, 12, 4.4f, 1.2f, 1, 0.7f, 5, 6, 14);
	
	//Sloshers
	public static final SlosherItem slosher = new SlosherItem("slosher", 1.6f, 0.48f, 2, 8,14, 3, 7f);
	public static final SlosherItem classicSlosher = new SlosherItem("classic_slosher", slosher);
	public static final SlosherItem sodaSlosher = new SlosherItem("soda_slosher", slosher);
	public static final SlosherItem triSlosher = new SlosherItem("tri_slosher", 1.65f, 0.444f, 3, 20,12.4f, 4, 6f);
	
	
	//Ink Tanks
	public static final InkTankItem inkTank = new InkTankItem("ink_tank", 100);
	public static final InkTankItem classicInkTank = new InkTankItem("classic_ink_tank", inkTank);
	public static final InkTankItem inkTankJr = new InkTankItem("ink_tank_jr", 110);
	public static final InkTankItem armoredInkTank = new InkTankItem("armored_ink_tank", 85, ARMORED_INK_TANK);
	
	//Vanity
	public static final Item inkClothHelmet = new ColoredArmorItem("ink_cloth_helmet", INK_CLOTH, EquipmentSlotType.HEAD);
	public static final Item inkClothChestplate = new ColoredArmorItem("ink_cloth_chestplate", INK_CLOTH, EquipmentSlotType.CHEST);
	public static final Item inkClothLeggings = new ColoredArmorItem("ink_cloth_leggings", INK_CLOTH, EquipmentSlotType.LEGS);
	public static final Item inkClothBoots = new ColoredArmorItem("ink_cloth_boots", INK_CLOTH, EquipmentSlotType.FEET);
	public static final Item splatfestBand = new Item(new Item.Properties().maxStackSize(1)).setRegistryName("splatfest_band");
	
	//Materials
	public static final Item sardinium = new Item(new Item.Properties().group(GROUP_GENERAL)).setRegistryName("sardinium");
	public static final Item sardiniumBlock = new BlockItem(SplatcraftBlocks.sardiniumBlock).setRegistryName("sardinium_block");
	public static final Item sardiniumOre = new BlockItem(SplatcraftBlocks.sardiniumOre).setRegistryName("sardinium_ore");
	public static final Item powerEgg = new Item(new Item.Properties().group(GROUP_GENERAL)).setRegistryName("power_egg");
	public static final Item powerEggCan = new PowerEggCanItem("power_egg_can");
	public static final Item powerEggBlock = new BlockItem(SplatcraftBlocks.powerEggBlock).setRegistryName("power_egg_block");
	public static final Item emptyInkwell = new BlockItem(SplatcraftBlocks.emptyInkwell).setRegistryName("empty_inkwell");
	
	//Remotes
	public static final RemoteItem turfScanner = new TurfScannerItem("turf_scanner");
	public static final RemoteItem inkDisruptor = new InkDisruptorItem("ink_disruptor");
	public static final RemoteItem colorChanger = new ColorChangerItem("color_changer");
	
	//Filters
	public static final FilterItem emptyFilter = new FilterItem("filter");
	public static final FilterItem pastelFilter = new FilterItem("pastel_filter");
	public static final FilterItem organicFilter = new FilterItem("organic_filter");
	public static final FilterItem neonFilter = new FilterItem("neon_filter");
	public static final FilterItem enchantedFilter = new FilterItem("enchanted_filter", true, false);
	public static final FilterItem creativeFilter = new FilterItem("creative_filter", true, true);
	
	//Crafting Stations
	public static final Item inkVat = new BlockItem(SplatcraftBlocks.inkVat).setRegistryName("ink_vat");
	public static final Item weaponWorkbench = new BlockItem(SplatcraftBlocks.weaponWorkbench).setRegistryName("weapon_workbench");
	
	//Map Items
	public static final Item inkwell = new ColoredBlockItem(SplatcraftBlocks.inkwell, "inkwell", 16, emptyInkwell);
	public static final Item grate = new BlockItem(SplatcraftBlocks.grate).setRegistryName("grate");
	public static final Item grateRamp = new BlockItem(SplatcraftBlocks.grateRamp).setRegistryName("grate_ramp");
	public static final Item barrierBar = new BlockItem(SplatcraftBlocks.barrierBar).setRegistryName("barrier_bar");
	public static final Item inkedWool = new ColoredBlockItem(SplatcraftBlocks.inkedWool, "inked_wool", new Item.Properties(), Items.WHITE_WOOL);
	public static final Item canvas = new BlockItem(SplatcraftBlocks.canvas).setRegistryName("canvas");
	public static final Item squidBumper = new SquidBumperItem("squid_bumper");
	public static final Item sunkenCrate = new BlockItem(SplatcraftBlocks.sunkenCrate).setRegistryName("sunken_crate");
	public static final Item crate = new BlockItem(SplatcraftBlocks.crate).setRegistryName("crate");
	public static final Item stageBarrier = new BlockItem(SplatcraftBlocks.stageBarrier).setRegistryName("stage_barrier");
	public static final Item stageVoid = new BlockItem(SplatcraftBlocks.stageVoid).setRegistryName("stage_void");
	
	//Misc
	
	@SubscribeEvent
	public static void itemInit(final RegistryEvent.Register<Item> event)
	{
		IForgeRegistry<Item> registry = event.getRegistry();
		
		for(Item item : weapons)
			registry.register(item);
		
		registry.register(inkClothHelmet);
		registry.register(inkClothChestplate);
		registry.register(inkClothLeggings);
		registry.register(inkClothBoots);
		registry.register(splatfestBand);
		
		registry.register(sardinium);
		registry.register(sardiniumBlock);
		registry.register(sardiniumOre);
		registry.register(powerEgg);
		registry.register(powerEggCan);
		registry.register(powerEggBlock);
		
		registry.register(turfScanner);
		registry.register(inkDisruptor);
		registry.register(colorChanger);
		
		for(Item item : FilterItem.filters)
			registry.register(item);
		
		registry.register(inkVat);
		registry.register(weaponWorkbench);
		
		registry.register(emptyInkwell);
		registry.register(inkwell);
		registry.register(grate);
		registry.register(grateRamp);
		registry.register(barrierBar);
		registry.register(inkedWool);
		registry.register(canvas);
		registry.register(squidBumper);
		registry.register(sunkenCrate);
		registry.register(crate);
		registry.register(stageBarrier);
		registry.register(stageVoid);
		
		registry.register(new net.minecraft.item.BlockItem(Blocks.IRON_BARS, new Item.Properties().group(ItemGroup.DECORATIONS)).setRegistryName("minecraft","iron_bars"));
		
		
		DispenserBlock.registerDispenseBehavior(inkwell, new PlaceBlockDispenseBehavior());
		DispenserBlock.registerDispenseBehavior(emptyInkwell, new PlaceBlockDispenseBehavior());
	}
	
	public static void registerModelProperties()
	{
		ResourceLocation activeProperty = new ResourceLocation(Splatcraft.MODID,"active");
		ResourceLocation modeProperty = new ResourceLocation(Splatcraft.MODID,"mode");
		ResourceLocation inkProperty = new ResourceLocation(Splatcraft.MODID,"ink");
		ResourceLocation isLeftProperty = new ResourceLocation(Splatcraft.MODID,"is_left");
		
		for(RemoteItem remote : RemoteItem.remotes)
		{
			ItemModelsProperties.registerProperty(remote, activeProperty, remote.getActiveProperty());
			ItemModelsProperties.registerProperty(remote, modeProperty, remote.getModeProperty());
		}
		
		for(InkTankItem tank : InkTankItem.inkTanks)
			ItemModelsProperties.registerProperty(tank, inkProperty, (stack, world, entity) -> (InkTankItem.getInkAmount(stack) / tank.capacity));
		
		for(DualieItem dualie : DualieItem.dualies)
			ItemModelsProperties.registerProperty(dualie, isLeftProperty, dualie.getIsLeft());
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void registerArmorModels()
	{
		inkTank.setArmorModel(new InkTankModel());
		classicInkTank.setArmorModel(new ClassicInkTankModel());
		inkTankJr.setArmorModel(new InkTankJrModel());
		armoredInkTank.setArmorModel(new ArmoredInkTankModel());
	}
	
	@SubscribeEvent
	public static void registerAttributes(final RegistryEvent.Register<Attribute> event)
	{
		IForgeRegistry<Attribute> registry = event.getRegistry();
		registry.register(INK_SWIM_SPEED);
		
	}
	
	private static Attribute createAttribute(String id, Attribute attribute)
	{
		attribute.setRegistryName(id);
		return attribute;
	}
}
