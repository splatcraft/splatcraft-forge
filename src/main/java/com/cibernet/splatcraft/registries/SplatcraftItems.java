package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.client.model.ArmoredInkTankModel;
import com.cibernet.splatcraft.client.model.ClassicInkTankModel;
import com.cibernet.splatcraft.client.model.InkTankJrModel;
import com.cibernet.splatcraft.client.model.InkTankModel;
import com.cibernet.splatcraft.dispenser.PlaceBlockDispenseBehavior;
import com.cibernet.splatcraft.items.BlockItem;
import com.cibernet.splatcraft.items.*;
import com.cibernet.splatcraft.items.remotes.ColorChangerItem;
import com.cibernet.splatcraft.items.remotes.InkDisruptorItem;
import com.cibernet.splatcraft.items.remotes.RemoteItem;
import com.cibernet.splatcraft.items.remotes.TurfScannerItem;
import com.cibernet.splatcraft.items.weapons.*;
import com.cibernet.splatcraft.util.SplatcraftArmorMaterial;
import net.minecraft.block.Block;
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
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.cibernet.splatcraft.registries.SplatcraftItemGroups.GROUP_GENERAL;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SplatcraftItems
{
    public static final List<Item> weapons = new ArrayList<>();
    public static final ArrayList<Item> inkColoredItems = new ArrayList<>();
    public static final UUID SPEED_MOD_UUID = UUID.fromString("dc65cedb-19d2-4731-a492-ee930c8234df");

    //Attributes
    public static final Attribute INK_SWIM_SPEED = createAttribute("ink_swim_speed", new RangedAttribute("attribute.splatcraft.ink_swim_speed", 0.7F, 0.0D, 1024.0D).setShouldWatch(true));

    //Armor Materials
    public static final IArmorMaterial INK_CLOTH = new SplatcraftArmorMaterial("ink_cloth", SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0, 0, 0);
    public static final IArmorMaterial ARMORED_INK_TANK = new SplatcraftArmorMaterial("armored_ink_tank", SoundEvents.ITEM_ARMOR_EQUIP_IRON, 3, 0, 0.05f);

    //Shooters
    public static final ShooterItem splattershot = new ShooterItem("splattershot", 1.05f, 0.75f, 12f, 4, 8f, 0.9f);
    public static final ShooterItem tentatekSplattershot = new ShooterItem("tentatek_splattershot", splattershot);
    public static final ShooterItem wasabiSplattershot = new ShooterItem("wasabi_splattershot", splattershot);
    public static final ShooterItem ancientSplattershot = new ShooterItem("ancient_splattershot", splattershot).setSecret();
    public static final ShooterItem splattershotJr = new ShooterItem("splattershot_jr", 1f, 0.55f, 13.5f, 4, 6.5f, 0.5f);
    public static final ShooterItem kensaSplattershotJr = new ShooterItem("kensa_splattershot_jr", splattershotJr);
    public static final ShooterItem aerosprayMG = new ShooterItem("aerospray_mg", 1.3f, 0.45f, 26f, 2, 4.8f, 0.5f);
    public static final ShooterItem getAerosprayRG = new ShooterItem("aerospray_rg", aerosprayMG);
    public static final ShooterItem gal52 = new ShooterItem("52_gal", 1.2f, 0.78f, 16f, 9, 10.4f, 1.3f);
    public static final ShooterItem gal52Deco = new ShooterItem("52_gal_deco", gal52);
    public static final ShooterItem kensaGal52 = new ShooterItem("kensa_52_gal", gal52);
    public static final ShooterItem gal96 = new ShooterItem("96_gal", 1.3f, 0.85f, 12.5f, 11, 12.4f, 2.5f);
    public static final ShooterItem gal96Deco = new ShooterItem("96_gal_deco", gal96);

    //Blasters
    public static final BlasterItem blaster = new BlasterItem("blaster", 2.3f, 1f, 5f, 4, 20, 25f, 10f, 10f, 6);
    public static final BlasterItem grimBlaster = new BlasterItem("grim_blaster", blaster);
    public static final BlasterItem clashBlaster = new BlasterItem("clash_blaster", 1.8f, 1.2f, 5f, 1, 10, 12f, 6f, 4, 4);
    public static final BlasterItem clashBlasterNeo = new BlasterItem("clash_blaster_neo", clashBlaster);

    //Rollers
    public static final RollerItem splatRoller = new RollerItem("splat_roller", 3, 0.06f, 25, 1.08f, false).setDashStats(1.32, 0.3f, 30)
            .setSwingStats(0.48, 9, 16, 0.55f, 6);
    public static final RollerItem krakOnSplatRoller = new RollerItem("krak_on_splat_roller", splatRoller);
    public static final RollerItem coroCoroSplatRoller = new RollerItem("corocoro_splat_roller", splatRoller);
    public static final RollerItem carbonRoller = new RollerItem("carbon_roller", 2, 0.06f, 14, 1.28f, false).setDashStats(1.52, 0.3f, 10)
            .setSwingStats(0.6, 4, 20, 0.45f, 3, 4, 24, 0.58f, 4);
    public static final RollerItem inkbrush = new RollerItem("inkbrush", 1, 0.4f, 4, 1.92f, true)
            .setSwingStats(0.24, 2f, 4, 0.54f, 2);
    public static final RollerItem octobrush = new RollerItem("octobrush", 2, 0.54f, 5, 1.92f, true)
            .setSwingStats(0.24, 3.2f, 2, 0.6f, 3);
    public static final RollerItem kensaOctobrush = new RollerItem("kensa_octobrush", octobrush);

    //Chargers
    public static final ChargerItem splatCharger = new ChargerItem("splat_charger", 0.87f, 1.8f, 13, 20, 40, 32f, 2.25f, 18f, 0.4, false, 1.1f);
    public static final ChargerItem bentoSplatCharger = new ChargerItem("bento_splat_charger", splatCharger);
    public static final ChargerItem kelpSplatCharger = new ChargerItem("kelp_splat_charger", splatCharger);
    public static final ChargerItem eLiter4K = new ChargerItem("e_liter_4k", 0.95f, 2.4f, 16, 35, 40, 36f, 2.25f, 25f, 0.15, false, 1.0f);
    public static final ChargerItem bamboozler14mk1 = new ChargerItem("bamboozler_14_mk1", 0.86f, 1.9f, 8, 4, 0, 16, 2.8f, 7, 0.8, true, 1.1f);
    public static final ChargerItem bamboozler14mk2 = new ChargerItem("bamboozler_14_mk2", bamboozler14mk1);

    //Dualies
    public static final DualieItem splatDualie = new DualieItem("splat_dualies", 1f, 0.65f, 10, 8, 6, 0.75f, 1, 0.7f, 9, 8, 30);
    public static final DualieItem enperrySplatDualie = new DualieItem("enperry_splat_dualies", splatDualie);
    public static final DualieItem dualieSquelcher = new DualieItem("dualie_squelchers", 0.9f, 0.74f, 11.5f, 12, 4.4f, 1.2f, 1, 0.7f, 5, 6, 14);

    //Sloshers
    public static final SlosherItem slosher = new SlosherItem("slosher", 1.6f, 0.48f, 2, 8, 14, 3, 7f);
    public static final SlosherItem classicSlosher = new SlosherItem("classic_slosher", slosher);
    public static final SlosherItem sodaSlosher = new SlosherItem("soda_slosher", slosher);
    public static final SlosherItem triSlosher = new SlosherItem("tri_slosher", 1.65f, 0.444f, 3, 20, 12.4f, 4, 6f);

    //Ink Tanks
    public static final InkTankItem inkTank = new InkTankItem("ink_tank", 100);
    public static final InkTankItem classicInkTank = new InkTankItem("classic_ink_tank", inkTank);
    public static final InkTankItem inkTankJr = new InkTankItem("ink_tank_jr", 110);
    public static final InkTankItem armoredInkTank = new InkTankItem("armored_ink_tank", 85, ARMORED_INK_TANK);

    //Sub Weapons

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
    public static final Item kensaPin = new Item(new Item.Properties().group(GROUP_GENERAL).rarity(Rarity.UNCOMMON)).setRegistryName("toni_kensa_pin");
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
    public static final FilterItem overgrownFilter = new FilterItem("overgrown_filter");
    public static final FilterItem midnightFilter = new FilterItem("midnight_filter");
    public static final FilterItem creativeFilter = new FilterItem("creative_filter", true, true);

    //Crafting Stations
    public static final Item inkVat = new BlockItem(SplatcraftBlocks.inkVat).setRegistryName("ink_vat");
    public static final Item weaponWorkbench = new BlockItem(SplatcraftBlocks.weaponWorkbench).setRegistryName("ammo_knights_workbench");

    //Map Items
    public static final Item inkwell = new ColoredBlockItem(SplatcraftBlocks.inkwell, "inkwell", 16, emptyInkwell).addStarterColors();
    public static final Item grate = new BlockItem(SplatcraftBlocks.grate).setRegistryName("grate");
    public static final Item grateRamp = new BlockItem(SplatcraftBlocks.grateRamp).setRegistryName("grate_ramp");
    public static final Item barrierBar = new BlockItem(SplatcraftBlocks.barrierBar).setRegistryName("barrier_bar");
    public static final Item platedBarrierBar = new BlockItem(SplatcraftBlocks.platedBarrierBar).setRegistryName("plated_barrier_bar");
    public static final Item cautionBarrierBar = new BlockItem(SplatcraftBlocks.cautionBarrierBar).setRegistryName("caution_barrier_bar");
    public static final Item canvas = new BlockItem(SplatcraftBlocks.canvas).setRegistryName("canvas");
    public static final Item squidBumper = new SquidBumperItem("squid_bumper");
    public static final Item sunkenCrate = new BlockItem(SplatcraftBlocks.sunkenCrate).setRegistryName("sunken_crate");
    public static final Item crate = new BlockItem(SplatcraftBlocks.crate).setRegistryName("crate");

    //Redstone Components
    public static final Item remotePedestal = new ColoredBlockItem(SplatcraftBlocks.remotePedestal, "remote_pedestal");
    public static final Item splatSwitch = new BlockItem(SplatcraftBlocks.splatSwitch).setRegistryName("splat_switch");

    //Ink Stained Blocks
    public static final Item inkedWool = new ColoredBlockItem(SplatcraftBlocks.inkedWool, "ink_stained_wool", new Item.Properties().group(GROUP_GENERAL), Items.WHITE_WOOL);
    public static final Item inkedCarpet = new ColoredBlockItem(SplatcraftBlocks.inkedCarpet, "ink_stained_carpet", new Item.Properties().group(GROUP_GENERAL), Items.WHITE_CARPET);
    public static final Item inkedGlass = new ColoredBlockItem(SplatcraftBlocks.inkedGlass, "ink_stained_glass", new Item.Properties().group(GROUP_GENERAL), Items.GLASS);
    public static final Item inkedGlassPane = new ColoredBlockItem(SplatcraftBlocks.inkedGlassPane, "ink_stained_glass_pane", new Item.Properties().group(GROUP_GENERAL), Items.GLASS_PANE);

    //Barriers
    public static final Item stageBarrier = new BlockItem(SplatcraftBlocks.stageBarrier).setRegistryName("stage_barrier");
    public static final Item stageVoid = new BlockItem(SplatcraftBlocks.stageVoid).setRegistryName("stage_void");
    public static final Item allowedColorBarrier = new ColoredBlockItem(SplatcraftBlocks.allowedColorBarrier, "allowed_color_barrier").addStarters(false);
    public static final Item deniedColorBarrier = new ColoredBlockItem(SplatcraftBlocks.deniedColorBarrier, "denied_color_barrier").addStarters(false);

    //Misc

    @SubscribeEvent
    public static void itemInit(final RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> registry = event.getRegistry();

        for (Item item : weapons)
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
        registry.register(kensaPin);

        registry.register(turfScanner);
        registry.register(inkDisruptor);
        registry.register(colorChanger);

        for (Item item : FilterItem.filters)
            registry.register(item);

        registry.register(inkVat);
        registry.register(weaponWorkbench);

        registry.register(emptyInkwell);
        registry.register(inkwell);
        registry.register(grate);
        registry.register(grateRamp);
        registry.register(barrierBar);
        registry.register(platedBarrierBar);
        registry.register(cautionBarrierBar);

        registry.register(remotePedestal);
        registry.register(splatSwitch);

        registry.register(crate);
        registry.register(sunkenCrate);
        registry.register(canvas);

        registry.register(inkedWool);
        registry.register(inkedCarpet);
        registry.register(inkedGlass);
        registry.register(inkedGlassPane);
        registry.register(squidBumper);

        registry.register(stageBarrier);
        registry.register(stageVoid);
        registry.register(allowedColorBarrier);
        registry.register(deniedColorBarrier);

        registry.register(new net.minecraft.item.BlockItem(Blocks.IRON_BARS, new Item.Properties().group(ItemGroup.DECORATIONS)).setRegistryName("minecraft", "iron_bars"));
        registry.register(new net.minecraft.item.BlockItem(Blocks.CHAIN, new Item.Properties().group(ItemGroup.DECORATIONS)).setRegistryName("minecraft", "chain"));


        DispenserBlock.registerDispenseBehavior(inkwell, new PlaceBlockDispenseBehavior());
        DispenserBlock.registerDispenseBehavior(emptyInkwell, new PlaceBlockDispenseBehavior());
    }


    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class Missmaps
    {
        private static final HashMap<String, Item> remaps = new HashMap<String, Item>()
        {{
            put("inked_wool", inkedWool);
            put("inked_carpet", inkedCarpet);
            put("inked_glass", inkedGlass);
            put("inked_glass_pane", inkedGlassPane);
            put("weapon_workbench", weaponWorkbench);
        }};

        @SubscribeEvent
        public static void onMissingMappings(final RegistryEvent.MissingMappings<Item> event)
        {
            for(RegistryEvent.MissingMappings.Mapping<Item> item : event.getMappings(Splatcraft.MODID))
            {
                String key = item.key.getPath();
                if(remaps.containsKey(key))
                    item.remap(remaps.get(key));
            }
        }
    }

    public static void registerModelProperties()
    {
        ResourceLocation activeProperty = new ResourceLocation(Splatcraft.MODID, "active");
        ResourceLocation modeProperty = new ResourceLocation(Splatcraft.MODID, "mode");
        ResourceLocation inkProperty = new ResourceLocation(Splatcraft.MODID, "ink");
        ResourceLocation isLeftProperty = new ResourceLocation(Splatcraft.MODID, "is_left");
        ResourceLocation unfoldedProperty = new ResourceLocation(Splatcraft.MODID, "unfolded");

        for (RemoteItem remote : RemoteItem.remotes)
        {
            ItemModelsProperties.registerProperty(remote, activeProperty, remote.getActiveProperty());
            ItemModelsProperties.registerProperty(remote, modeProperty, remote.getModeProperty());
        }

        for (InkTankItem tank : InkTankItem.inkTanks)
        {
            ItemModelsProperties.registerProperty(tank, inkProperty, (stack, world, entity) -> InkTankItem.getInkAmount(stack) / tank.capacity);
        }

        for (DualieItem dualie : DualieItem.dualies)
        {
            ItemModelsProperties.registerProperty(dualie, isLeftProperty, dualie.getIsLeft());
        }
        for (RollerItem roller : RollerItem.rollers)
        {
            ItemModelsProperties.registerProperty(roller, unfoldedProperty, roller.getUnfolded());
        }
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
