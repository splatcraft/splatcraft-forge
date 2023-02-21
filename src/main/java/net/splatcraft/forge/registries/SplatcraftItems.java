package net.splatcraft.forge.registries;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.client.model.inktanks.ArmoredInkTankModel;
import net.splatcraft.forge.client.model.inktanks.ClassicInkTankModel;
import net.splatcraft.forge.client.model.inktanks.InkTankJrModel;
import net.splatcraft.forge.client.model.inktanks.InkTankModel;
import net.splatcraft.forge.dispenser.PlaceBlockDispenseBehavior;
import net.splatcraft.forge.entities.subs.BurstBombEntity;
import net.splatcraft.forge.entities.subs.SplatBombEntity;
import net.splatcraft.forge.entities.subs.SuctionBombEntity;
import net.splatcraft.forge.items.BlockItem;
import net.splatcraft.forge.items.ColoredArmorItem;
import net.splatcraft.forge.items.ColoredBlockItem;
import net.splatcraft.forge.items.FilterItem;
import net.splatcraft.forge.items.InkTankItem;
import net.splatcraft.forge.items.InkWaxerItem;
import net.splatcraft.forge.items.PowerEggCanItem;
import net.splatcraft.forge.items.SquidBumperItem;
import net.splatcraft.forge.items.remotes.ColorChangerItem;
import net.splatcraft.forge.items.remotes.InkDisruptorItem;
import net.splatcraft.forge.items.remotes.RemoteItem;
import net.splatcraft.forge.items.remotes.TurfScannerItem;
import net.splatcraft.forge.items.weapons.BlasterItem;
import net.splatcraft.forge.items.weapons.ChargerItem;
import net.splatcraft.forge.items.weapons.DualieItem;
import net.splatcraft.forge.items.weapons.RollerItem;
import net.splatcraft.forge.items.weapons.ShooterItem;
import net.splatcraft.forge.items.weapons.SlosherItem;
import net.splatcraft.forge.items.weapons.SubWeaponItem;
import net.splatcraft.forge.util.SplatcraftArmorMaterial;
import net.splatcraft.forge.util.WeaponSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = Splatcraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SplatcraftItems {
    public static final List<Item> weapons = new ArrayList<>();
    public static final ArrayList<Item> inkColoredItems = new ArrayList<>();
    public static final UUID SPEED_MOD_UUID = UUID.fromString("dc65cedb-19d2-4731-a492-ee930c8234df");

    //Attributes
    public static final Attribute INK_SWIM_SPEED = createAttribute("ink_swim_speed", new RangedAttribute("attribute.splatcraft.ink_swim_speed", 0.7F, 0.0D, 1024.0D).setSyncable(true));

    //Armor Materials
    public static final IArmorMaterial INK_CLOTH = new SplatcraftArmorMaterial("ink_cloth", SoundEvents.ARMOR_EQUIP_LEATHER, 0, 0, 0);
    public static final IArmorMaterial ARMORED_INK_TANK = new SplatcraftArmorMaterial("armored_ink_tank", SoundEvents.ARMOR_EQUIP_IRON, 3, 0, 0.05f);

    //Shooters
    public static final ShooterItem splattershot = new ShooterItem(new WeaponSettings("splattershot")
            .setProjectileSize(1)
            .setProjectileSpeed(0.75f)
            .setFiringSpeed(3)
            .setGroundInaccuracy(6)
            .setAirInaccuracy(12)
            .setInkConsumption(0.9f)
            .setInkRecoveryCooldown(7)
            .setBaseDamage(8)
            .setMinDamage(4)
            .setDamageDecayStartTick(3)
            .setDamageDecayPerTick(1.7f));
    public static final ShooterItem tentatekSplattershot = new ShooterItem(splattershot.settings.changeName("tentatek_splattershot"));
    public static final ShooterItem wasabiSplattershot = new ShooterItem(splattershot.settings.changeName("wasabi_splattershot"));
    public static final ShooterItem ancientSplattershot = (ShooterItem) new ShooterItem(splattershot.settings.changeName("ancient_splattershot")).setSecret();
    public static final ShooterItem splattershotJr = new ShooterItem(new WeaponSettings("splattershot_jr")
            .setProjectileSize(0.95f)
            .setProjectileSpeed(0.55f)
            .setFiringSpeed(3)
            .setGroundInaccuracy(12)
            .setAirInaccuracy(15)
            .setInkConsumption(0.5f)
            .setInkRecoveryCooldown(5)
            .setBaseDamage(6.5f)
            .setMinDamage(3.3f)
            .setDamageDecayStartTick(3)
            .setDamageDecayPerTick(2.6f));
    public static final ShooterItem kensaSplattershotJr = new ShooterItem(splattershotJr.settings.changeName("kensa_splattershot_jr"));
    public static final ShooterItem aerosprayMG = new ShooterItem(new WeaponSettings("aerospray_mg")
            .setProjectileSize(1.2f)
            .setProjectileSpeed(0.45f)
            .setFiringSpeed(3)
            .setGroundInaccuracy(13)
            .setAirInaccuracy(16)
            .setInkConsumption(0.5f)
            .setInkRecoveryCooldown(5)
            .setBaseDamage(5)
            .setMinDamage(2.5f)
            .setDamageDecayStartTick(3)
            .setDamageDecayPerTick(2.25f));
    public static final ShooterItem aerosprayRG = new ShooterItem(aerosprayMG.settings.changeName("aerospray_rg"));
    public static final ShooterItem gal52 = new ShooterItem(new WeaponSettings("52_gal")
            .setProjectileSize(1.1f)
            .setProjectileSpeed(0.78f)
            .setFiringSpeed(6)
            .setGroundInaccuracy(6)
            .setAirInaccuracy(12)
            .setInkConsumption(1.3f)
            .setInkRecoveryCooldown(7)
            .setBaseDamage(10.4f)
            .setMinDamage(6)
            .setDamageDecayStartTick(4)
            .setDamageDecayPerTick(4));
    public static final ShooterItem gal52Deco = new ShooterItem(gal52.settings.changeName("52_gal_deco"));
    public static final ShooterItem kensaGal52 = new ShooterItem(gal52.settings.changeName("kensa_52_gal"));
    public static final ShooterItem gal96 = new ShooterItem("96_gal", 1.2f, 0.88f, 12.5f, 8, 12.4f, 2.5f);
    public static final ShooterItem gal96Deco = new ShooterItem("96_gal_deco", gal96);
    public static final ShooterItem nzap85 = new ShooterItem("n-zap85", 1f, 0.75f, 12f, 2, 5.9f, 0.8f);
    public static final ShooterItem nzap89 = new ShooterItem("n-zap89", nzap85);

    //Blasters
    public static final BlasterItem blaster = new BlasterItem("blaster", 2.25f, 1.1f, 5f, 4, 20, 25f, 10f, 10f, 5);
    public static final BlasterItem grimBlaster = new BlasterItem("grim_blaster", blaster);
    public static final BlasterItem clashBlaster = new BlasterItem("clash_blaster", 1.65f, 1.1f, 5f, 1, 10, 12f, 6f, 4, 4);
    public static final BlasterItem clashBlasterNeo = new BlasterItem("clash_blaster_neo", clashBlaster);

    //Rollers
    public static final RollerItem splatRoller = new RollerItem("splat_roller", 3, 0.06f, 25, 1.08f, false).setDashStats(1.32, 0.3f, 30)
            .setSwingStats(0.48, 9, 16, 0.55f, 6);
    public static final RollerItem krakOnSplatRoller = new RollerItem("krak_on_splat_roller", splatRoller);
    public static final RollerItem coroCoroSplatRoller = new RollerItem("corocoro_splat_roller", splatRoller);
    public static final RollerItem carbonRoller = new RollerItem("carbon_roller", 2, 0.06f, 14, 1.28f, false).setDashStats(1.52, 0.3f, 10)
            .setSwingStats(0.6, 4, 20, 0.45f, 3, 4, 24, 0.58f, 4);
    public static final RollerItem inkbrush = new RollerItem("inkbrush", 1, 0.4f, 4, 1.92f, true)
            .setSwingStats(0.24, 2f, 4, 0.6f, 2);
    public static final RollerItem octobrush = new RollerItem("octobrush", 2, 0.54f, 5, 1.92f, true)
            .setSwingStats(0.24, 3.2f, 2, 0.65f, 3);
    public static final RollerItem kensaOctobrush = new RollerItem("kensa_octobrush", octobrush);

    //Chargers
    public static final ChargerItem splatCharger = new ChargerItem("splat_charger", 0.7f, 1.8f, 13, 20, 40, 32f, 2.25f, 18f, 0.4, false, 1.1f);
    public static final ChargerItem bentoSplatCharger = new ChargerItem("bento_splat_charger", splatCharger);
    public static final ChargerItem kelpSplatCharger = new ChargerItem("kelp_splat_charger", splatCharger);
    public static final ChargerItem eLiter4K = new ChargerItem("e_liter_4k", 0.85f, 2.4f, 16, 35, 40, 36f, 2.25f, 25f, 0.15, false, 1.0f);
    public static final ChargerItem bamboozler14mk1 = new ChargerItem("bamboozler_14_mk1", 0.75f, 1.9f, 8, 4, 0, 16, 2.8f, 7, 0.8, true, 1.1f);
    public static final ChargerItem bamboozler14mk2 = new ChargerItem("bamboozler_14_mk2", bamboozler14mk1);

    //Dualies
    public static final DualieItem splatDualie = new DualieItem("splat_dualies", 0.9f, 0.65f, 10, 8, 6, 0.75f, 1, 0.7f, 9, 8, 30);
    public static final DualieItem enperrySplatDualie = new DualieItem("enperry_splat_dualies", splatDualie);
    public static final DualieItem dualieSquelcher = new DualieItem("dualie_squelchers", 0.85f, 0.74f, 11.5f, 10, 4.4f, 1.2f, 1, 0.7f, 5, 6, 14);
    public static final DualieItem gloogaDualie = new DualieItem("glooga_dualies", 0.8f, 0.72f, 12f, 7, 7.3f, 1.4f, 1, 0.7f, 8, 9, 24) {{
        rollDamage = 10.5f;
    }};
    public static final DualieItem gloogaDualieDeco = new DualieItem("glooga_dualies_deco", gloogaDualie);
    public static final DualieItem kensaGloogaDualie = new DualieItem("kensa_glooga_dualies", gloogaDualie);

    //Sloshers
    public static final SlosherItem slosher = new SlosherItem("slosher", 1.6f, 0.4f, 2, 8, 14, 3, 7f);
    public static final SlosherItem classicSlosher = new SlosherItem("classic_slosher", slosher);
    public static final SlosherItem sodaSlosher = new SlosherItem("soda_slosher", slosher);
    public static final SlosherItem triSlosher = new SlosherItem("tri_slosher", 1.55f, 0.444f, 3, 20, 12.4f, 4, 6f);
    public static final SlosherItem explosher = new SlosherItem("explosher", 2f, 0.75f, 1, 0, 11f, 12, 11.7f).setSlosherType(SlosherItem.Type.EXPLODING);

    //Ink Tanks
    public static final InkTankItem inkTank = new InkTankItem("ink_tank", 100);
    public static final InkTankItem classicInkTank = new InkTankItem("classic_ink_tank", inkTank);
    public static final InkTankItem inkTankJr = new InkTankItem("ink_tank_jr", 110);
    public static final InkTankItem armoredInkTank = new InkTankItem("armored_ink_tank", 85, ARMORED_INK_TANK);

    //Sub Weapons
    public static final SubWeaponItem splatBomb = new SubWeaponItem("splat_bomb", SplatcraftEntities.SPLAT_BOMB, SplatBombEntity.DIRECT_DAMAGE, SplatBombEntity.EXPLOSION_SIZE, 70);
    public static final SubWeaponItem splatBomb2 = (SubWeaponItem) new SubWeaponItem("splat_bomb_2", SplatcraftEntities.SPLAT_BOMB, SplatBombEntity.DIRECT_DAMAGE, SplatBombEntity.EXPLOSION_SIZE, 70).setSecret();
    public static final SubWeaponItem burstBomb = new SubWeaponItem("burst_bomb", SplatcraftEntities.BURST_BOMB, BurstBombEntity.DIRECT_DAMAGE, BurstBombEntity.EXPLOSION_SIZE, 40);
    public static final SubWeaponItem suctionBomb = new SubWeaponItem("suction_bomb", SplatcraftEntities.SUCTION_BOMB, SuctionBombEntity.DIRECT_DAMAGE, SuctionBombEntity.EXPLOSION_SIZE, 70);

    //Vanity
    public static final Item inkClothHelmet = new ColoredArmorItem("ink_cloth_helmet", INK_CLOTH, EquipmentSlotType.HEAD);
    public static final Item inkClothChestplate = new ColoredArmorItem("ink_cloth_chestplate", INK_CLOTH, EquipmentSlotType.CHEST);
    public static final Item inkClothLeggings = new ColoredArmorItem("ink_cloth_leggings", INK_CLOTH, EquipmentSlotType.LEGS);
    public static final Item inkClothBoots = new ColoredArmorItem("ink_cloth_boots", INK_CLOTH, EquipmentSlotType.FEET);

    //Materials
    public static final Item sardinium = new Item(new Item.Properties().tab(SplatcraftItemGroups.GROUP_GENERAL)).setRegistryName("sardinium");
    public static final Item sardiniumBlock = new BlockItem(SplatcraftBlocks.sardiniumBlock).setRegistryName("sardinium_block");
    public static final Item sardiniumOre = new BlockItem(SplatcraftBlocks.sardiniumOre).setRegistryName("sardinium_ore");
    public static final Item powerEgg = new Item(new Item.Properties().tab(SplatcraftItemGroups.GROUP_GENERAL)).setRegistryName("power_egg");
    public static final Item powerEggCan = new PowerEggCanItem("power_egg_can");
    public static final Item powerEggBlock = new BlockItem(SplatcraftBlocks.powerEggBlock).setRegistryName("power_egg_block");
    public static final Item kensaPin = new Item(new Item.Properties().tab(SplatcraftItemGroups.GROUP_GENERAL).rarity(Rarity.UNCOMMON)).setRegistryName("toni_kensa_pin");
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
    public static final Item tarp = new BlockItem(SplatcraftBlocks.tarp).setRegistryName("tarp");
    public static final Item canvas = new BlockItem(SplatcraftBlocks.canvas).setRegistryName("canvas");
    public static final Item squidBumper = new SquidBumperItem("squid_bumper");
    public static final Item sunkenCrate = new BlockItem(SplatcraftBlocks.sunkenCrate).setRegistryName("sunken_crate");
    public static final Item crate = new BlockItem(SplatcraftBlocks.crate).setRegistryName("crate");

    //Redstone Components
    public static final Item remotePedestal = new ColoredBlockItem(SplatcraftBlocks.remotePedestal, "remote_pedestal");
    public static final Item splatSwitch = new BlockItem(SplatcraftBlocks.splatSwitch).setRegistryName("splat_switch");

    //Ink Stained Blocks
    public static final Item inkedWool = new ColoredBlockItem(SplatcraftBlocks.inkedWool, "ink_stained_wool", new Item.Properties().tab(SplatcraftItemGroups.GROUP_GENERAL), Items.WHITE_WOOL);
    public static final Item inkedCarpet = new ColoredBlockItem(SplatcraftBlocks.inkedCarpet, "ink_stained_carpet", new Item.Properties().tab(SplatcraftItemGroups.GROUP_GENERAL), Items.WHITE_CARPET);
    public static final Item inkedGlass = new ColoredBlockItem(SplatcraftBlocks.inkedGlass, "ink_stained_glass", new Item.Properties().tab(SplatcraftItemGroups.GROUP_GENERAL), Items.GLASS);
    public static final Item inkedGlassPane = new ColoredBlockItem(SplatcraftBlocks.inkedGlassPane, "ink_stained_glass_pane", new Item.Properties().tab(SplatcraftItemGroups.GROUP_GENERAL), Items.GLASS_PANE);

    //Barriers
    public static final Item stageBarrier = new BlockItem(SplatcraftBlocks.stageBarrier).setRegistryName("stage_barrier");
    public static final Item stageVoid = new BlockItem(SplatcraftBlocks.stageVoid).setRegistryName("stage_void");
    public static final Item allowedColorBarrier = new ColoredBlockItem(SplatcraftBlocks.allowedColorBarrier, "allowed_color_barrier").addStarters(false);
    public static final Item deniedColorBarrier = new ColoredBlockItem(SplatcraftBlocks.deniedColorBarrier, "denied_color_barrier").addStarters(false);

    //Octarian Gear
    public static final Item splatfestBand = new Item(new Item.Properties().stacksTo(1).tab(SplatcraftItemGroups.GROUP_GENERAL)).setRegistryName("splatfest_band");
    public static final Item clearBand = new Item(new Item.Properties().stacksTo(1).tab(SplatcraftItemGroups.GROUP_GENERAL)).setRegistryName("clear_ink_band");
    public static final Item waxApplicator = new InkWaxerItem().setRegistryName("wax_applicator");

    //Misc

    @SubscribeEvent
    public static void itemInit(final RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        for (Item item : weapons)
            registry.register(item);

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
        registry.register(tarp);

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

        registry.register(waxApplicator);

        registry.register(inkClothHelmet);
        registry.register(inkClothChestplate);
        registry.register(inkClothLeggings);
        registry.register(inkClothBoots);
        registry.register(splatfestBand);
        registry.register(clearBand);

        DispenserBlock.registerBehavior(inkwell, new PlaceBlockDispenseBehavior());
        DispenserBlock.registerBehavior(emptyInkwell, new PlaceBlockDispenseBehavior());

        for(SubWeaponItem sub : SubWeaponItem.subs)
            DispenserBlock.registerBehavior(sub, new SubWeaponItem.DispenseBehavior());
    }

    public static void registerModelProperties() {
        ResourceLocation activeProperty = new ResourceLocation(Splatcraft.MODID, "active");
        ResourceLocation modeProperty = new ResourceLocation(Splatcraft.MODID, "mode");
        ResourceLocation inkProperty = new ResourceLocation(Splatcraft.MODID, "ink");
        ResourceLocation isLeftProperty = new ResourceLocation(Splatcraft.MODID, "is_left");
        ResourceLocation unfoldedProperty = new ResourceLocation(Splatcraft.MODID, "unfolded");

        for (RemoteItem remote : RemoteItem.remotes) {
            ItemModelsProperties.register(remote, activeProperty, remote.getActiveProperty());
            ItemModelsProperties.register(remote, modeProperty, remote.getModeProperty());
        }

        for (InkTankItem tank : InkTankItem.inkTanks) {
            ItemModelsProperties.register(tank, inkProperty, (stack, level, entity) -> InkTankItem.getInkAmount(stack) / tank.capacity);
        }

        for (DualieItem dualie : DualieItem.dualies) {
            ItemModelsProperties.register(dualie, isLeftProperty, dualie.getIsLeft());
        }
        for (RollerItem roller : RollerItem.rollers) {
            ItemModelsProperties.register(roller, unfoldedProperty, roller.getUnfolded());
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerArmorModels() {
        inkTank.setArmorModel(new InkTankModel());
        classicInkTank.setArmorModel(new ClassicInkTankModel());
        inkTankJr.setArmorModel(new InkTankJrModel());
        armoredInkTank.setArmorModel(new ArmoredInkTankModel());
    }

    @SubscribeEvent
    public static void registerAttributes(final RegistryEvent.Register<Attribute> event) {
        IForgeRegistry<Attribute> registry = event.getRegistry();
        registry.register(INK_SWIM_SPEED);

    }

    private static Attribute createAttribute(String id, Attribute attribute) {
        attribute.setRegistryName(id);
        return attribute;
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class Missmaps {
        private static final HashMap<String, Item> remaps = new HashMap<String, Item>() {{
            put("inked_wool", inkedWool);
            put("inked_carpet", inkedCarpet);
            put("inked_glass", inkedGlass);
            put("inked_glass_pane", inkedGlassPane);
            put("weapon_workbench", weaponWorkbench);
            put("ink_polisher", waxApplicator);
        }};

        @SubscribeEvent
        public static void onMissingMappings(final RegistryEvent.MissingMappings<Item> event) {
            for (RegistryEvent.MissingMappings.Mapping<Item> item : event.getMappings(Splatcraft.MODID)) {
                String key = item.key.getPath();
                if (remaps.containsKey(key))
                    item.remap(remaps.get(key));
            }
        }
    }
}
