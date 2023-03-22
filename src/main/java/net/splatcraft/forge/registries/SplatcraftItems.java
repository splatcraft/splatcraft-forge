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
import net.splatcraft.forge.entities.subs.CurlingBombEntity;
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
import net.splatcraft.forge.items.weapons.settings.RollerWeaponSettings;
import net.splatcraft.forge.items.weapons.settings.WeaponSettings;
import net.splatcraft.forge.util.SplatcraftArmorMaterial;

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
            .setProjectileSize(1).setProjectileSpeed(0.75f)
            .setFiringSpeed(3)
            .setGroundInaccuracy(6).setAirInaccuracy(12)
            .setInkConsumption(0.9f).setInkRecoveryCooldown(7)
            .setBaseDamage(8).setMinDamage(4).setDamageDecayStartTick(3).setDamageDecayPerTick(0.34f));
    public static final ShooterItem tentatekSplattershot = new ShooterItem(splattershot.settings.setName("tentatek_splattershot"));
    public static final ShooterItem wasabiSplattershot = new ShooterItem(splattershot.settings.setName("wasabi_splattershot"));
    public static final ShooterItem ancientSplattershot = (ShooterItem) new ShooterItem(splattershot.settings.setName("ancient_splattershot")).setSecret();
    public static final ShooterItem splattershotJr = new ShooterItem(new WeaponSettings("splattershot_jr")
            .setProjectileSize(0.95f).setProjectileSpeed(0.55f)
            .setFiringSpeed(3)
            .setGroundInaccuracy(12).setAirInaccuracy(15)
            .setInkConsumption(0.5f).setInkRecoveryCooldown(5)
            .setBaseDamage(6.5f).setMinDamage(3.3f).setDamageDecayStartTick(3).setDamageDecayPerTick(0.53f));
    public static final ShooterItem kensaSplattershotJr = new ShooterItem(splattershotJr.settings.setName("kensa_splattershot_jr"));
    public static final ShooterItem aerosprayMG = new ShooterItem(new WeaponSettings("aerospray_mg")
            .setProjectileSize(1.2f).setProjectileSpeed(0.45f)
            .setFiringSpeed(3)
            .setGroundInaccuracy(13).setAirInaccuracy(16)
            .setInkConsumption(0.5f).setInkRecoveryCooldown(5)
            .setBaseDamage(5).setMinDamage(2.5f).setDamageDecayStartTick(3).setDamageDecayPerTick(0.45f));
    public static final ShooterItem aerosprayRG = new ShooterItem(aerosprayMG.settings.setName("aerospray_rg"));
    public static final ShooterItem gal52 = new ShooterItem(new WeaponSettings("52_gal")
            .setProjectileSize(1.1f).setProjectileSpeed(0.78f)
            .setFiringSpeed(6)
            .setGroundInaccuracy(6).setAirInaccuracy(12)
            .setInkConsumption(1.3f).setInkRecoveryCooldown(7)
            .setBaseDamage(10.4f).setMinDamage(6).setDamageDecayStartTick(4).setDamageDecayPerTick(0.83f));
    public static final ShooterItem gal52Deco = new ShooterItem(gal52.settings.setName("52_gal_deco"));
    public static final ShooterItem kensaGal52 = new ShooterItem(gal52.settings.setName("kensa_52_gal"));
    public static final ShooterItem gal96 = new ShooterItem(new WeaponSettings("96_gal")
            .setProjectileSize(1.2f).setProjectileSpeed(0.88f)
            .setFiringSpeed(8)
            .setGroundInaccuracy(4).setAirInaccuracy(11)
            .setInkConsumption(2.5f).setInkRecoveryCooldown(7)
            .setBaseDamage(12.4f).setMinDamage(7).setDamageDecayStartTick(3).setDamageDecayPerTick(1));
    public static final ShooterItem gal96Deco = new ShooterItem(gal96.settings.setName("96_gal_deco"));
    public static final ShooterItem nzap85 = new ShooterItem(new WeaponSettings("n-zap85")
            .setProjectileSize(1).setProjectileSpeed(0.75f)
            .setFiringSpeed(2)
            .setGroundInaccuracy(6).setAirInaccuracy(12)
            .setInkConsumption(0.8f).setInkRecoveryCooldown(7)
            .setBaseDamage(5.9f).setMinDamage(2.8f).setDamageDecayStartTick(3).setDamageDecayPerTick(0.53f));
    public static final ShooterItem nzap89 = new ShooterItem(nzap85.settings.setName("n-zap89"));

    //Blasters
    public static final BlasterItem blaster = new BlasterItem(new WeaponSettings("blaster")
            .setProjectileSize(2.25f).setProjectileLifespan(5).setProjectileSpeed(1.05f)
            .setFiringSpeed(20).setStartupTicks(4)
            .setGroundInaccuracy(0).setAirInaccuracy(10)
            .setInkConsumption(10).setInkRecoveryCooldown(20)
            .setBaseDamage(25).setMinDamage(10));
    public static final BlasterItem grimBlaster = new BlasterItem(blaster.settings.setName("grim_blaster"));
    public static final BlasterItem clashBlaster = new BlasterItem(new WeaponSettings("clash_blaster")
            .setProjectileSize(1.7f).setProjectileLifespan(4).setProjectileSpeed(1.1f)
            .setFiringSpeed(10).setStartupTicks(1)
            .setGroundInaccuracy(0).setAirInaccuracy(8)
            .setInkConsumption(4).setInkRecoveryCooldown(13).setBaseDamage(12).setMinDamage(6));
    public static final BlasterItem clashBlasterNeo = new BlasterItem(clashBlaster.settings.setName("clash_blaster_neo"));

    //Rollers
    public static final RollerItem splatRoller = new RollerItem(new RollerWeaponSettings("splat_roller").setBrush(false)
            .setRollSize(3).setRollConsumption(0.06f).setRollInkRecoveryCooldown(7).setRollDamage(25).setRollMobility(1.08f)
            .setDashMobility(1.32f).setDashConsumption(0.3f).setDashTime(30)
            .setSwingMobility(0.48f).setSwingConsumption(9).setSwingInkRecoveryCooldown(15).setSwingProjectileSpeed(0.55f).setSwingTime(6)
            .setSwingBaseDamage(30).setSwingMinDamage(7).setSwingDamageDecayStartTick(8).setSwingDamageDecayPerTick(3.45f));
    public static final RollerItem krakOnSplatRoller = new RollerItem(splatRoller.settings.setName("krak_on_splat_roller"));
    public static final RollerItem coroCoroSplatRoller = new RollerItem(splatRoller.settings.setName("corocoro_splat_roller"));
    public static final RollerItem carbonRoller = new RollerItem(new RollerWeaponSettings("carbon_roller").setBrush(false)
            .setRollSize(2).setRollConsumption(0.06f).setRollInkRecoveryCooldown(7).setRollDamage(14).setRollMobility(1.28f)
            .setDashMobility(1.52f).setDashConsumption(0.3f).setDashTime(10)
            .setSwingMobility(0.6f).setSwingConsumption(4).setSwingInkRecoveryCooldown(13).setSwingProjectileSpeed(0.45f).setSwingTime(3)
            .setSwingBaseDamage(20).setSwingMinDamage(5).setSwingDamageDecayStartTick(8).setSwingDamageDecayPerTick(2.25f)
            .setFlingInkRecoveryCooldown(15).setFlingProjectileSpeed(0.58f).setFlingTime(4)
            .setFlingBaseDamage(24).setFlingMinDamage(7).setFlingDamageDecayStartTick(10).setFlingDamageDecayPerTick(3.4f));
    public static final RollerItem inkbrush = new RollerItem(new RollerWeaponSettings("inkbrush").setBrush(true)
            .setRollSize(1).setRollConsumption(0.4f).setRollInkRecoveryCooldown(7).setRollDamage(4).setRollMobility(1.92f)
            .setSwingMobility(0.24f).setSwingConsumption(2).setSwingInkRecoveryCooldown(10).setSwingProjectileSpeed(0.6f).setSwingTime(2)
            .setSwingBaseDamage(6).setSwingMinDamage(3).setSwingDamageDecayStartTick(7).setSwingDamageDecayPerTick(0.45f));
    public static final RollerItem octobrush = new RollerItem(new RollerWeaponSettings("octobrush").setBrush(true)
            .setRollSize(2).setRollConsumption(0.54f).setRollInkRecoveryCooldown(7).setRollDamage(5).setRollMobility(1.92f)
            .setSwingMobility(0.24f).setSwingConsumption(3.2f).setSwingInkRecoveryCooldown(10).setSwingProjectileSpeed(0.65f).setSwingTime(3)
            .setSwingBaseDamage(8).setSwingMinDamage(4).setSwingDamageDecayStartTick(8).setSwingDamageDecayPerTick(0.57f));
    public static final RollerItem kensaOctobrush = new RollerItem(octobrush.settings.setName("kensa_octobrush"));

    //Chargers
    public static final ChargerItem splatCharger = new ChargerItem(new WeaponSettings("splat_charger")
            .setProjectileSize(0.7f).setProjectileLifespan(13).setProjectileSpeed(1.8f)
            .setStartupTicks(20).setDischargeTicks(20)
            .setMinInkConsumption(2.25f).setInkConsumption(18).setInkRecoveryCooldown(7)
            .setBaseDamage(32)
            .setChargerMobility(0.4f)
            .setFastMidAirCharge(false)
            .setChargerPiercesAt(1.1f));
    public static final ChargerItem bentoSplatCharger = new ChargerItem(splatCharger.settings.setName("bento_splat_charger"));
    public static final ChargerItem kelpSplatCharger = new ChargerItem(splatCharger.settings.setName("kelp_splat_charger"));
    public static final ChargerItem eLiter4K = new ChargerItem(new WeaponSettings("e_liter_4k")
            .setProjectileSize(0.85f).setProjectileLifespan(16).setProjectileSpeed(2.4f)
            .setStartupTicks(35).setDischargeTicks(40)
            .setMinInkConsumption(2.25f).setInkConsumption(25).setInkRecoveryCooldown(7)
            .setBaseDamage(36)
            .setChargerMobility(0.15f)
            .setFastMidAirCharge(false)
            .setChargerPiercesAt(1.0f));
    public static final ChargerItem bamboozler14mk1 = new ChargerItem(new WeaponSettings("bamboozler_14_mk1")
            .setProjectileSize(0.75f).setProjectileLifespan(8).setProjectileSpeed(1.9f)
            .setStartupTicks(4).setDischargeTicks(0) // no charge storage
            .setMinInkConsumption(2.8f).setInkConsumption(7).setInkRecoveryCooldown(7)
            .setBaseDamage(16) // bamboo without MPU :trollface:
            .setChargerMobility(0.8f)
            .setFastMidAirCharge(false)
            .setChargerPiercesAt(1.1f));
    public static final ChargerItem bamboozler14mk2 = new ChargerItem(bamboozler14mk1.settings.setName("bamboozler_14_mk2"));
    public static final ChargerItem classicSquiffer = new ChargerItem(new WeaponSettings("classic_squiffer")
            .setProjectileSize(0.7f).setProjectileLifespan(12).setProjectileSpeed(1.85f)
            .setStartupTicks(15).setDischargeTicks(25)
            .setMinInkConsumption(1.87f).setInkConsumption(10.5f).setInkRecoveryCooldown(7)
            .setBaseDamage(28f)
            .setChargerMobility(0.3f)
            .setFastMidAirCharge(true)
            .setChargerPiercesAt(1.0f));

    //Dualies
    public static final DualieItem splatDualie = new DualieItem(new WeaponSettings("splat_dualies")
            .setProjectileSize(0.9f).setProjectileSpeed(0.65f)
            .setFiringSpeed(8)
            .setGroundInaccuracy(2).setAirInaccuracy(7.5f).setInkConsumption(0.75f).setInkRecoveryCooldown(7)
            .setBaseDamage(6).setMinDamage(3).setDamageDecayStartTick(2).setDamageDecayPerTick(1.13f)
            .setRollCount(1)
            .setRollSpeed(0.9f)
            .setRollInaccuracy(0).setRollInkConsumption(9).setRollInkRecoveryCooldown(23)
            .setRollCooldown(8).setLastRollCooldown(30));
    public static final DualieItem enperrySplatDualie = new DualieItem(splatDualie.settings.setName("enperry_splat_dualies"));
    public static final DualieItem dualieSquelcher = new DualieItem(new WeaponSettings("dualie_squelchers")
            .setProjectileSize(0.85f).setProjectileSpeed(0.74f)
            .setFiringSpeed(10)
            .setGroundInaccuracy(4).setAirInaccuracy(8)
            .setInkConsumption(1.2f)
            .setInkRecoveryCooldown(7)
            .setBaseDamage(5.6f).setMinDamage(2.8f).setDamageDecayStartTick(3).setDamageDecayPerTick(0.53f)
            .setRollCount(1)
            .setRollSpeed(0.7f)
            .setRollInaccuracy(2)
            .setRollInkConsumption(5).setRollInkRecoveryCooldown(20)
            .setRollCooldown(6).setLastRollCooldown(14));
    public static final DualieItem gloogaDualie = new DualieItem(new WeaponSettings("glooga_dualies")
            .setProjectileSize(0.8f).setProjectileSpeed(0.72f)
            .setFiringSpeed(7)
            .setGroundInaccuracy(4).setAirInaccuracy(8)
            .setInkConsumption(1.4f).setInkRecoveryCooldown(7)
            .setBaseDamage(7.3f).setMinDamage(3.6f).setDamageDecayStartTick(2).setDamageDecayPerTick(1.35f)
            .setRollCount(1)
            .setRollSpeed(0.7f)
            .setRollBaseDamage(10.6f).setRollMinDamage(5.26f).setRollDamageDecayPerTick(2)
            .setRollInaccuracy(3)
            .setRollInkConsumption(8).setRollInkRecoveryCooldown(23)
            .setRollCooldown(9).setLastRollCooldown(24));
    public static final DualieItem gloogaDualieDeco = new DualieItem(gloogaDualie.settings.setName("glooga_dualies_deco"));
    public static final DualieItem kensaGloogaDualie = new DualieItem(gloogaDualie.settings.setName("kensa_glooga_dualies"));

    //Sloshers
    public static final SlosherItem slosher = new SlosherItem(new WeaponSettings("slosher")
            .setProjectileSize(1.6f).setProjectileSpeed(0.4f).setProjectileCount(2)
            .setStartupTicks(7)
            .setGroundInaccuracy(8)
            .setInkConsumption(7f).setInkRecoveryCooldown(13)
            .setBaseDamage(14));
    public static final SlosherItem classicSlosher = new SlosherItem(slosher.settings.setName("classic_slosher"));
    public static final SlosherItem sodaSlosher = new SlosherItem(slosher.settings.setName("soda_slosher"));
    public static final SlosherItem triSlosher = new SlosherItem(new WeaponSettings("tri_slosher")
            .setProjectileSize(1.55f).setProjectileSpeed(0.444f).setProjectileCount(3)
            .setStartupTicks(4)
            .setGroundInaccuracy(20)
            .setInkConsumption(6f).setInkRecoveryCooldown(12)
            .setBaseDamage(12.4f));
    public static final SlosherItem explosher = new SlosherItem(new WeaponSettings("explosher")
            .setProjectileSize(2f).setProjectileSpeed(0.75f).setProjectileCount(1)
            .setFiringSpeed(20).setStartupTicks(5)
            .setGroundInaccuracy(0)
            .setInkConsumption(11.7f).setInkRecoveryCooldown(23)
            .setBaseDamage(11f).setMinDamage(7)).setSlosherType(SlosherItem.Type.EXPLODING);

    //Ink Tanks
    public static final InkTankItem inkTank = new InkTankItem("ink_tank", 100);
    public static final InkTankItem classicInkTank = new InkTankItem("classic_ink_tank", inkTank);
    public static final InkTankItem inkTankJr = new InkTankItem("ink_tank_jr", 110);
    public static final InkTankItem armoredInkTank = new InkTankItem("armored_ink_tank", 85, ARMORED_INK_TANK);

    //Sub Weapons
    public static final SubWeaponItem splatBomb = new SubWeaponItem("splat_bomb", SplatcraftEntities.SPLAT_BOMB, SplatBombEntity.DIRECT_DAMAGE, SplatBombEntity.EXPLOSION_SIZE, 70, 20);
    public static final SubWeaponItem splatBomb2 = (SubWeaponItem) new SubWeaponItem("splat_bomb_2", SplatcraftEntities.SPLAT_BOMB, SplatBombEntity.DIRECT_DAMAGE, SplatBombEntity.EXPLOSION_SIZE, 70, 20).setSecret();
    public static final SubWeaponItem burstBomb = new SubWeaponItem("burst_bomb", SplatcraftEntities.BURST_BOMB, BurstBombEntity.DIRECT_DAMAGE, BurstBombEntity.EXPLOSION_SIZE, 40, 15);
    public static final SubWeaponItem suctionBomb = new SubWeaponItem("suction_bomb", SplatcraftEntities.SUCTION_BOMB, SuctionBombEntity.DIRECT_DAMAGE, SuctionBombEntity.EXPLOSION_SIZE, 70, 30);
    public static final SubWeaponItem curlingBomb = new SubWeaponItem("curling_bomb", SplatcraftEntities.CURLING_BOMB, CurlingBombEntity.DIRECT_DAMAGE, CurlingBombEntity.EXPLOSION_SIZE, 70, 30, 20, CurlingBombEntity::onItemUseTick);

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
    public static final Item spawnPad = new ColoredBlockItem(SplatcraftBlocks.spawnPad, "spawn_pad", 1, null);
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
