package net.splatcraft.forge.registries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.client.models.inktanks.ArmoredInkTankModel;
import net.splatcraft.forge.client.models.inktanks.ClassicInkTankModel;
import net.splatcraft.forge.client.models.inktanks.InkTankJrModel;
import net.splatcraft.forge.client.models.inktanks.InkTankModel;
import net.splatcraft.forge.dispenser.PlaceBlockDispenseBehavior;
import net.splatcraft.forge.entities.subs.CurlingBombEntity;
import net.splatcraft.forge.items.*;
import net.splatcraft.forge.items.remotes.ColorChangerItem;
import net.splatcraft.forge.items.remotes.InkDisruptorItem;
import net.splatcraft.forge.items.remotes.RemoteItem;
import net.splatcraft.forge.items.remotes.TurfScannerItem;
import net.splatcraft.forge.items.weapons.*;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.SplatcraftArmorMaterial;

import static net.splatcraft.forge.Splatcraft.MODID;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = Splatcraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SplatcraftItems {
    protected static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final List<Item> weapons = new ArrayList<>();
    public static final ArrayList<Item> inkColoredItems = new ArrayList<>();
    public static final UUID SPEED_MOD_UUID = UUID.fromString("dc65cedb-19d2-4731-a492-ee930c8234df");

    //Armor Materials
    public static final ArmorMaterial INK_CLOTH = new SplatcraftArmorMaterial("ink_cloth", SoundEvents.ARMOR_EQUIP_LEATHER, 0, 0, 0);
    public static final ArmorMaterial ARMORED_INK_TANK = new SplatcraftArmorMaterial("armored_ink_tank", SoundEvents.ARMOR_EQUIP_IRON, 3, 0, 0.05f);

    //Shooters
    public static final RegistryObject<ShooterItem> splattershot = ShooterItem.create(REGISTRY, "splattershot", "splattershot");
    public static final RegistryObject<ShooterItem> tentatekSplattershot = ShooterItem.create(REGISTRY, "splattershot", "tentatek_splattershot");
    public static final RegistryObject<ShooterItem> wasabiSplattershot = ShooterItem.create(REGISTRY, "splattershot", "wasabi_splattershot");
    public static final RegistryObject<ShooterItem> ancientSplattershot = ShooterItem.create(REGISTRY, splattershot, "ancient_splattershot", true);
    public static final RegistryObject<ShooterItem> splattershotJr = ShooterItem.create(REGISTRY, "splattershot_jr", "splattershot_jr");
    public static final RegistryObject<ShooterItem> kensaSplattershotJr = ShooterItem.create(REGISTRY, "splattershot_jr", "kensa_splattershot_jr");
    public static final RegistryObject<ShooterItem> aerosprayMG = ShooterItem.create(REGISTRY,"aerospray", "aerospray_mg");
    public static final RegistryObject<ShooterItem> aerosprayRG = ShooterItem.create(REGISTRY, "aerospray", "aerospray_rg");
    public static final RegistryObject<ShooterItem> gal52 = ShooterItem.create(REGISTRY, "52_gal", "52_gal");
    public static final RegistryObject<ShooterItem> gal52Deco = ShooterItem.create(REGISTRY, "52_gal", "52_gal_deco");
    public static final RegistryObject<ShooterItem> kensaGal52 = ShooterItem.create(REGISTRY, "52_gal", "kensa_52_gal");
    public static final RegistryObject<ShooterItem> gal96 = ShooterItem.create(REGISTRY, "96_gal", "96_gal");
    public static final RegistryObject<ShooterItem> gal96Deco = ShooterItem.create(REGISTRY, "96_gal", "96_gal_deco");
    public static final RegistryObject<ShooterItem> nzap85 = ShooterItem.create(REGISTRY, "n-zap", "n-zap85");
    public static final RegistryObject<ShooterItem> nzap89 = ShooterItem.create(REGISTRY, "n-zap", "n-zap89");
    public static final RegistryObject<ShooterItem> jet_squelcher = ShooterItem.create(REGISTRY, "jet_squelcher", "jet_squelcher");

    //Blasters
    public static final RegistryObject<BlasterItem> blaster = BlasterItem.createBlaster(REGISTRY, "blaster", "blaster");
    public static final RegistryObject<BlasterItem> grimBlaster = BlasterItem.createBlaster(REGISTRY, "blaster", "grim_blaster");
    public static final RegistryObject<BlasterItem> rangeBlaster = BlasterItem.createBlaster(REGISTRY, "range_blaster", "range_blaster");
    public static final RegistryObject<BlasterItem> grimRangeBlaster = BlasterItem.createBlaster(REGISTRY, "range_blaster", "grim_range_blaster");
    public static final RegistryObject<BlasterItem> clashBlaster = BlasterItem.createBlaster(REGISTRY, "clash_blaster", "clash_blaster");
    public static final RegistryObject<BlasterItem> clashBlasterNeo = BlasterItem.createBlaster(REGISTRY, "clash_blaster", "clash_blaster_neo");
    public static final RegistryObject<BlasterItem> lunaBlaster = BlasterItem.createBlaster(REGISTRY, "luna_blaster", "luna_blaster");
    public static final RegistryObject<BlasterItem> rapidBlaster = BlasterItem.createBlaster(REGISTRY, "rapid_blaster", "rapid_blaster");
    public static final RegistryObject<BlasterItem> rapidBlasterPro = BlasterItem.createBlaster(REGISTRY, "rapid_blaster_pro", "rapid_blaster_pro");

    //Rollers
    public static final RegistryObject<RollerItem> splatRoller = RollerItem.create(REGISTRY, "splat_roller", "splat_roller");
    public static final RegistryObject<RollerItem> krakOnSplatRoller = RollerItem.create(REGISTRY, "splat_roller", "krak_on_splat_roller");
    public static final RegistryObject<RollerItem> coroCoroSplatRoller = RollerItem.create(REGISTRY, "splat_roller", "corocoro_splat_roller");
    public static final RegistryObject<RollerItem> carbonRoller = RollerItem.create(REGISTRY, "carbon_roller", "carbon_roller");
    public static final RegistryObject<RollerItem> dynamoRoller = RollerItem.create(REGISTRY, "dynamo_roller", "dynamo_roller");
    public static final RegistryObject<RollerItem> inkbrush = RollerItem.create(REGISTRY, "inkbrush", "inkbrush");
    public static final RegistryObject<RollerItem> octobrush = RollerItem.create(REGISTRY, "octobrush", "octobrush");
    public static final RegistryObject<RollerItem> kensaOctobrush = RollerItem.create(REGISTRY, "octobrush", "kensa_octobrush");

    //Chargers
    public static final RegistryObject<ChargerItem> splatCharger = ChargerItem.create(REGISTRY, "splat_charger", "splat_charger");
    public static final RegistryObject<ChargerItem> bentoSplatCharger = ChargerItem.create(REGISTRY, "splat_charger", "bento_splat_charger");
    public static final RegistryObject<ChargerItem> kelpSplatCharger = ChargerItem.create(REGISTRY, "splat_charger", "kelp_splat_charger");
    public static final RegistryObject<ChargerItem> eLiter4K = ChargerItem.create(REGISTRY, "e_liter", "e_liter_4k");
    public static final RegistryObject<ChargerItem> eliter3K = ChargerItem.create(REGISTRY, "e_liter", "e_liter_3k");
    public static final RegistryObject<ChargerItem> bamboozler14mk1 = ChargerItem.create(REGISTRY, "bamboozler_14", "bamboozler_14_mk1");
    public static final RegistryObject<ChargerItem> bamboozler14mk2 = ChargerItem.create(REGISTRY, "bamboozler_14", "bamboozler_14_mk2");
    public static final RegistryObject<ChargerItem> classicSquiffer = ChargerItem.create(REGISTRY, "squiffer", "classic_squiffer");


    //Dualies
    public static final RegistryObject<DualieItem> splatDualie = DualieItem.create(REGISTRY, "splat_dualies", "splat_dualies");
    public static final RegistryObject<DualieItem> enperrySplatDualie = DualieItem.create(REGISTRY, "splat_dualies", "enperry_splat_dualies");
    public static final RegistryObject<DualieItem> dualieSquelcher = DualieItem.create(REGISTRY, "dualie_squelchers", "dualie_squelchers");
    public static final RegistryObject<DualieItem> gloogaDualie = DualieItem.create(REGISTRY, "glooga_dualies", "glooga_dualies");
    public static final RegistryObject<DualieItem> gloogaDualieDeco = DualieItem.create(REGISTRY, "glooga_dualies", "glooga_dualies_deco");
    public static final RegistryObject<DualieItem> kensaGloogaDualie = DualieItem.create(REGISTRY, "glooga_dualies", "kensa_glooga_dualies");

    //Sloshers
    public static final RegistryObject<SlosherItem> slosher = SlosherItem.create(REGISTRY, "slosher", "slosher", SlosherItem.Type.DEFAULT);
    public static final RegistryObject<SlosherItem> classicSlosher = SlosherItem.create(REGISTRY, slosher, "classic_slosher");
    public static final RegistryObject<SlosherItem> sodaSlosher = SlosherItem.create(REGISTRY, slosher, "soda_slosher");
    public static final RegistryObject<SlosherItem> triSlosher = SlosherItem.create(REGISTRY, "tri_slosher", "tri_slosher", SlosherItem.Type.DEFAULT);
    public static final RegistryObject<SlosherItem> explosher = SlosherItem.create(REGISTRY, "explosher", "explosher", SlosherItem.Type.EXPLODING);

    //Splatlings
    public static final RegistryObject<SplatlingItem> miniSplatling = SplatlingItem.create(REGISTRY, "mini_splatling", "mini_splatling");
    public static final RegistryObject<SplatlingItem> heavySplatling = SplatlingItem.create(REGISTRY, "heavy_splatling", "heavy_splatling");
    public static final RegistryObject<SplatlingItem> heavySplatlingDeco = SplatlingItem.create(REGISTRY, "heavy_splatling", "heavy_splatling_deco");
    public static final RegistryObject<SplatlingItem> heavySplatlingRemix = SplatlingItem.create(REGISTRY, "heavy_splatling", "heavy_splatling_remix");
    public static final RegistryObject<SplatlingItem> classicHeavySplatling = SplatlingItem.create(REGISTRY, "heavy_splatling", "classic_heavy_splatling");

    //Ink Tanks
    public static final RegistryObject<InkTankItem> inkTank = REGISTRY.register("ink_tank", () -> new InkTankItem("ink_tank", 100));
    public static final RegistryObject<InkTankItem> classicInkTank = REGISTRY.register("classic_ink_tank", () -> new InkTankItem("classic_ink_tank", inkTank.get()));
    public static final RegistryObject<InkTankItem> inkTankJr = REGISTRY.register("ink_tank_jr", () -> new InkTankItem("ink_tank_jr", 110));
    public static final RegistryObject<InkTankItem> armoredInkTank = REGISTRY.register("armored_ink_tank", () -> new InkTankItem("armored_ink_tank", 85, ARMORED_INK_TANK));

    //Sub Weapons
    public static final RegistryObject<SubWeaponItem> splatBomb = REGISTRY.register("splat_bomb", () -> new SubWeaponItem(SplatcraftEntities.SPLAT_BOMB, "splat_bomb"));
    public static final RegistryObject<SubWeaponItem> splatBomb2 = REGISTRY.register("splat_bomb_2", () -> new SubWeaponItem(SplatcraftEntities.SPLAT_BOMB, "splat_bomb").setSecret(true));

    public static final RegistryObject<SubWeaponItem> burstBomb = REGISTRY.register("burst_bomb", () -> new SubWeaponItem(SplatcraftEntities.BURST_BOMB, "burst_bomb"));
    public static final RegistryObject<SubWeaponItem> suctionBomb = REGISTRY.register("suction_bomb", () -> new SubWeaponItem(SplatcraftEntities.SUCTION_BOMB, "suction_bomb"));
    public static final RegistryObject<SubWeaponItem> curlingBomb = REGISTRY.register("curling_bomb", () -> new CurlingSubWeaponItem(SplatcraftEntities.CURLING_BOMB, "curling_bomb", CurlingBombEntity::onItemUseTick));

    //Vanity
    public static final RegistryObject<Item> inkClothHelmet = REGISTRY.register("ink_cloth_helmet", () -> new ColoredArmorItem(INK_CLOTH, EquipmentSlot.HEAD));
    public static final RegistryObject<Item> inkClothChestplate = REGISTRY.register("ink_cloth_chestplate", () -> new ColoredArmorItem(INK_CLOTH, EquipmentSlot.CHEST));
    public static final RegistryObject<Item> inkClothLeggings = REGISTRY.register("ink_cloth_leggings", () -> new ColoredArmorItem(INK_CLOTH, EquipmentSlot.LEGS));
    public static final RegistryObject<Item> inkClothBoots = REGISTRY.register("ink_cloth_boots", () -> new ColoredArmorItem(INK_CLOTH, EquipmentSlot.FEET));

    //Materials
    public static final RegistryObject<Item> sardinium = REGISTRY.register("sardinium", () -> new Item(new Item.Properties().tab(SplatcraftItemGroups.GROUP_GENERAL)));
    public static final RegistryObject<Item> sardiniumBlock = REGISTRY.register("sardinium_block", () -> new BlockItem(SplatcraftBlocks.sardiniumBlock.get()));
    public static final RegistryObject<Item> rawSardinium = REGISTRY.register("raw_sardinium", () -> new Item(new Item.Properties().tab(SplatcraftItemGroups.GROUP_GENERAL)));
    public static final RegistryObject<Item> sardiniumOre = REGISTRY.register("sardinium_ore", () -> new BlockItem(SplatcraftBlocks.sardiniumOre.get()));
    public static final RegistryObject<Item> rawSardiniumBlock = REGISTRY.register("raw_sardinium_block", () -> new BlockItem(SplatcraftBlocks.rawSardiniumBlock.get()));
    public static final RegistryObject<Item> coralite = REGISTRY.register("coralite", () -> new ColoredBlockItem(SplatcraftBlocks.coralite.get()).setMatchColor(false).clearsToSelf());
    public static final RegistryObject<Item> coraliteSlab = REGISTRY.register("coralite_slab", () -> new ColoredBlockItem(SplatcraftBlocks.coraliteSlab.get()).setMatchColor(false).clearsToSelf());
    public static final RegistryObject<Item> coraliteStairs = REGISTRY.register("coralite_stairs", () -> new ColoredBlockItem(SplatcraftBlocks.coraliteStairs.get()).setMatchColor(false).clearsToSelf());
    public static final RegistryObject<Item> powerEgg = REGISTRY.register("power_egg", () -> new Item(new Item.Properties().tab(SplatcraftItemGroups.GROUP_GENERAL)));
    public static final RegistryObject<Item> powerEggCan = REGISTRY.register("power_egg_can", PowerEggCanItem::new);
    public static final RegistryObject<Item> powerEggBlock = REGISTRY.register("power_egg_block", () -> new BlockItem(SplatcraftBlocks.powerEggBlock.get()));
    public static final RegistryObject<Item> emptyInkwell = REGISTRY.register("empty_inkwell", () -> new BlockItem(SplatcraftBlocks.emptyInkwell.get()));
    public static final RegistryObject<Item> ammoKnightsScrap = REGISTRY.register("ammo_knights_scrap", () -> new Item(new Item.Properties().tab(SplatcraftItemGroups.GROUP_GENERAL)));
    public static final RegistryObject<Item> blueprint = REGISTRY.register("blueprint", BlueprintItem::new);
    public static final RegistryObject<Item> kensaPin = REGISTRY.register("toni_kensa_pin", () -> new Item(new Item.Properties().tab(SplatcraftItemGroups.GROUP_GENERAL).rarity(Rarity.UNCOMMON)));

    //Remotes
    public static final RegistryObject<RemoteItem> turfScanner = REGISTRY.register("turf_scanner", TurfScannerItem::new);
    public static final RegistryObject<RemoteItem> inkDisruptor = REGISTRY.register("ink_disruptor", InkDisruptorItem::new);
    public static final RegistryObject<RemoteItem> colorChanger = REGISTRY.register("color_changer", ColorChangerItem::new);

    //Filters
    public static final RegistryObject<FilterItem> emptyFilter = REGISTRY.register("filter", FilterItem::new);
    public static final RegistryObject<FilterItem> pastelFilter = REGISTRY.register("pastel_filter", FilterItem::new);
    public static final RegistryObject<FilterItem> organicFilter = REGISTRY.register("organic_filter", FilterItem::new);
    public static final RegistryObject<FilterItem> neonFilter = REGISTRY.register("neon_filter", FilterItem::new);
    public static final RegistryObject<FilterItem> enchantedFilter = REGISTRY.register("enchanted_filter", () -> new FilterItem(Rarity.UNCOMMON, true, false));
    public static final RegistryObject<FilterItem> overgrownFilter = REGISTRY.register("overgrown_filter", FilterItem::new);
    public static final RegistryObject<FilterItem> midnightFilter = REGISTRY.register("midnight_filter", FilterItem::new);
    public static final RegistryObject<FilterItem> creativeFilter = REGISTRY.register("creative_filter", () -> new FilterItem(Rarity.RARE, false, true));

    //Crafting Stations
    public static final RegistryObject<Item> inkVat = REGISTRY.register("ink_vat", () -> new BlockItem(SplatcraftBlocks.inkVat.get()));
    public static final RegistryObject<Item> weaponWorkbench = REGISTRY.register("ammo_knights_workbench", () -> new BlockItem(SplatcraftBlocks.weaponWorkbench.get()));

    //Map Items
    public static final RegistryObject<Item> inkwell = REGISTRY.register("inkwell", () -> new ColoredBlockItem(SplatcraftBlocks.inkwell.get(), 16, emptyInkwell.get()).addStarterColors());
    public static final RegistryObject<Item> spawnPad = REGISTRY.register("spawn_pad", () -> new ColoredBlockItem(SplatcraftBlocks.spawnPad.get(), 1));
    public static final RegistryObject<Item> grate = REGISTRY.register("grate", () -> new BlockItem(SplatcraftBlocks.grate.get()));
    public static final RegistryObject<Item> grateRamp = REGISTRY.register("grate_ramp", () -> new BlockItem(SplatcraftBlocks.grateRamp.get()));
    public static final RegistryObject<Item> barrierBar = REGISTRY.register("barrier_bar", () -> new BlockItem(SplatcraftBlocks.barrierBar.get()));
    public static final RegistryObject<Item> platedBarrierBar = REGISTRY.register("plated_barrier_bar", () -> new BlockItem(SplatcraftBlocks.platedBarrierBar.get()));
    public static final RegistryObject<Item> cautionBarrierBar = REGISTRY.register("caution_barrier_bar", () -> new BlockItem(SplatcraftBlocks.cautionBarrierBar.get()));
    public static final RegistryObject<Item> tarp = REGISTRY.register("tarp", () -> new BlockItem(SplatcraftBlocks.tarp.get()));
    public static final RegistryObject<Item> glassCover = REGISTRY.register("glass_cover", () -> new BlockItem(SplatcraftBlocks.glassCover.get()));
    public static final RegistryObject<Item> canvas = REGISTRY.register("canvas", () -> new ColoredBlockItem(SplatcraftBlocks.canvas.get()).setMatchColor(false));
    public static final RegistryObject<Item> squidBumper = REGISTRY.register("squid_bumper", SquidBumperItem::new);
    public static final RegistryObject<Item> sunkenCrate = REGISTRY.register("sunken_crate", () -> new BlockItem(SplatcraftBlocks.sunkenCrate.get()));
    public static final RegistryObject<Item> crate = REGISTRY.register("crate", () -> new BlockItem(SplatcraftBlocks.crate.get()));

    //Redstone Components
    public static final RegistryObject<Item> remotePedestal = REGISTRY.register("remote_pedestal", () -> new ColoredBlockItem(SplatcraftBlocks.remotePedestal.get()));
    public static final RegistryObject<Item> splatSwitch = REGISTRY.register("splat_switch", () -> new BlockItem(SplatcraftBlocks.splatSwitch.get()));

    //Ink Stained Blocks
    public static final RegistryObject<Item> inkedWool = REGISTRY.register("ink_stained_wool", () -> new ColoredBlockItem(SplatcraftBlocks.inkedWool.get(), new Item.Properties().tab(SplatcraftItemGroups.GROUP_GENERAL), Items.WHITE_WOOL));
    public static final RegistryObject<Item> inkedCarpet = REGISTRY.register("ink_stained_carpet", () -> new ColoredBlockItem(SplatcraftBlocks.inkedCarpet.get(), new Item.Properties().tab(SplatcraftItemGroups.GROUP_GENERAL), Items.WHITE_CARPET));
    public static final RegistryObject<Item> inkedGlass = REGISTRY.register("ink_stained_glass", () -> new ColoredBlockItem(SplatcraftBlocks.inkedGlass.get(), new Item.Properties().tab(SplatcraftItemGroups.GROUP_GENERAL), Items.GLASS));
    public static final RegistryObject<Item> inkedGlassPane = REGISTRY.register("ink_stained_glass_pane", () -> new ColoredBlockItem(SplatcraftBlocks.inkedGlassPane.get(), new Item.Properties().tab(SplatcraftItemGroups.GROUP_GENERAL), Items.GLASS_PANE));

    //Barriers
    public static final RegistryObject<Item> allowedColorBarrier = REGISTRY.register("allowed_color_barrier", () -> new ColoredBlockItem(SplatcraftBlocks.allowedColorBarrier.get()));
    public static final RegistryObject<Item> deniedColorBarrier = REGISTRY.register("denied_color_barrier", () -> new ColoredBlockItem(SplatcraftBlocks.deniedColorBarrier.get()));
    public static final RegistryObject<Item> stageBarrier = REGISTRY.register("stage_barrier", () -> new BlockItem(SplatcraftBlocks.stageBarrier.get()));
    public static final RegistryObject<Item> stageVoid = REGISTRY.register("stage_void", () -> new BlockItem(SplatcraftBlocks.stageVoid.get()));

    //Octarian Gear
    public static final RegistryObject<Item> splatfestBand = REGISTRY.register("splatfest_band", () -> new Item(new Item.Properties().stacksTo(1).tab(SplatcraftItemGroups.GROUP_GENERAL)));
    public static final RegistryObject<Item> clearBand = REGISTRY.register("clear_ink_band", () -> new Item(new Item.Properties().stacksTo(1).tab(SplatcraftItemGroups.GROUP_GENERAL)));
    public static final RegistryObject<Item> waxApplicator = REGISTRY.register("wax_applicator", InkWaxerItem::new);

    //Misc

    /*
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
        registry.register(spawnPad);
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
    }
    */

    public static void postRegister()
    {
        SplatcraftItemGroups.colorTabItems.addAll(new ArrayList<>(){{
            add(inkwell.get());
            add(spawnPad.get());
            add(squidBumper.get());
            add(canvas.get());
            add(coralite.get());
            add(coraliteSlab.get());
            add(coraliteStairs.get());
            add(inkedWool.get());
            add(inkedCarpet.get());
            add(inkedGlass.get());
            add(inkedGlassPane.get());
            add(inkedGlassPane.get());
            add(allowedColorBarrier.get());
            add(deniedColorBarrier.get());
        }});

        DispenserBlock.registerBehavior(SplatcraftItems.emptyInkwell.get(), new PlaceBlockDispenseBehavior());
        DispenserBlock.registerBehavior(SplatcraftItems.inkwell.get(), new PlaceBlockDispenseBehavior());
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerModelProperties() {
        ResourceLocation activeProperty = new ResourceLocation(Splatcraft.MODID, "active");
        ResourceLocation modeProperty = new ResourceLocation(Splatcraft.MODID, "mode");
        ResourceLocation inkProperty = new ResourceLocation(Splatcraft.MODID, "ink");
        ResourceLocation isLeftProperty = new ResourceLocation(Splatcraft.MODID, "is_left");
        ResourceLocation unfoldedProperty = new ResourceLocation(Splatcraft.MODID, "unfolded");

        for (RemoteItem remote : RemoteItem.remotes) {
            ItemProperties.register(remote, activeProperty, remote.getActiveProperty());
            ItemProperties.register(remote, modeProperty, remote.getModeProperty());
        }

        for (InkTankItem tank : InkTankItem.inkTanks) {
            ItemProperties.register(tank, inkProperty, (stack, level, entity, seed) -> InkTankItem.getInkAmount(stack) / tank.capacity);
        }

        for (DualieItem dualie : DualieItem.dualies) {
            ItemProperties.register(dualie, isLeftProperty, dualie.getIsLeft());
        }
        for (RollerItem roller : RollerItem.rollers) {
            ItemProperties.register(roller, unfoldedProperty, roller.getUnfolded());
        }

        ItemPropertyFunction coloredProperty = (stack, level, entity, seed) -> ColorUtils.getInkColor(stack) == -1 ? 0 : 1;
        ItemProperties.register(canvas.get(), new ResourceLocation(Splatcraft.MODID, "inked"), coloredProperty);
        ItemProperties.register(coralite.get(), new ResourceLocation(Splatcraft.MODID, "colored"), coloredProperty);
        ItemProperties.register(coraliteSlab.get(), new ResourceLocation(Splatcraft.MODID, "colored"), coloredProperty);
        ItemProperties.register(coraliteStairs.get(), new ResourceLocation(Splatcraft.MODID, "colored"), coloredProperty);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerArmorModels() {
        inkTank.get().setArmorModel(new InkTankModel(Minecraft.getInstance().getEntityModels().bakeLayer(InkTankModel.LAYER_LOCATION)));
        classicInkTank.get().setArmorModel(new ClassicInkTankModel(Minecraft.getInstance().getEntityModels().bakeLayer(ClassicInkTankModel.LAYER_LOCATION)));
        inkTankJr.get().setArmorModel(new InkTankJrModel(Minecraft.getInstance().getEntityModels().bakeLayer(InkTankJrModel.LAYER_LOCATION)));
        armoredInkTank.get().setArmorModel(new ArmoredInkTankModel(Minecraft.getInstance().getEntityModels().bakeLayer(ArmoredInkTankModel.LAYER_LOCATION)));
    }


    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class Missmaps {
        private static final HashMap<String, RegistryObject<? extends Item>> remaps = new HashMap<>() {{
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
                    item.remap(remaps.get(key).get());
            }
        }
    }
}
