package net.splatcraft.forge.registries;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.entities.subs.BurstBombEntity;
import net.splatcraft.forge.entities.subs.CurlingBombEntity;
import net.splatcraft.forge.entities.subs.SplatBombEntity;
import net.splatcraft.forge.entities.subs.SuctionBombEntity;
import net.splatcraft.forge.items.*;
import net.splatcraft.forge.items.remotes.ColorChangerItem;
import net.splatcraft.forge.items.remotes.InkDisruptorItem;
import net.splatcraft.forge.items.remotes.RemoteItem;
import net.splatcraft.forge.items.remotes.TurfScannerItem;
import net.splatcraft.forge.items.weapons.*;
import net.splatcraft.forge.items.weapons.settings.RollerWeaponSettings;
import net.splatcraft.forge.items.weapons.settings.WeaponSettings;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.SplatcraftArmorMaterial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static net.splatcraft.forge.Splatcraft.MODID;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = Splatcraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SplatcraftItems 
{
    protected static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    
    public static final List<Item> weapons = new ArrayList<>();
    public static final ArrayList<Item> inkColoredItems = new ArrayList<>();
    public static final UUID SPEED_MOD_UUID = UUID.fromString("dc65cedb-19d2-4731-a492-ee930c8234df");

    //Attributes
    public static final Attribute INK_SWIM_SPEED = createAttribute("ink_swim_speed", new RangedAttribute("attribute.splatcraft.ink_swim_speed", 0.7F, 0.0D, 1024.0D).setSyncable(true));

    //Armor Materials
    public static final ArmorMaterial INK_CLOTH = new SplatcraftArmorMaterial("ink_cloth", SoundEvents.ARMOR_EQUIP_LEATHER, 0, 0, 0);
    public static final ArmorMaterial ARMORED_INK_TANK = new SplatcraftArmorMaterial("armored_ink_tank", SoundEvents.ARMOR_EQUIP_IRON, 3, 0, 0.05f);

    //Shooters
    public static final RegistryObject<ShooterItem> splattershot = ShooterItem.create(REGISTRY, new WeaponSettings("splattershot")
            .setProjectileSize(1).setProjectileSpeed(0.75f)
            .setFiringSpeed(3)
            .setGroundInaccuracy(6).setAirInaccuracy(12)
            .setInkConsumption(0.9f).setInkRecoveryCooldown(7)
            .setBaseDamage(8).setMinDamage(4).setDamageDecayStartTick(3).setDamageDecayPerTick(0.34f));
    public static final RegistryObject<ShooterItem> tentatekSplattershot = ShooterItem.create(REGISTRY, splattershot, "tentatek_splattershot");
    public static final RegistryObject<ShooterItem> wasabiSplattershot = ShooterItem.create(REGISTRY, splattershot, "wasabi_splattershot");
    public static final RegistryObject<ShooterItem> ancientSplattershot = ShooterItem.create(REGISTRY, splattershot, "ancient_splattershot");
    public static final RegistryObject<ShooterItem> splattershotJr = ShooterItem.create(REGISTRY, new WeaponSettings("splattershot_jr")
            .setProjectileSize(0.95f).setProjectileSpeed(0.55f)
            .setFiringSpeed(3)
            .setGroundInaccuracy(12).setAirInaccuracy(15)
            .setInkConsumption(0.5f).setInkRecoveryCooldown(5)
            .setBaseDamage(6.5f).setMinDamage(3.3f).setDamageDecayStartTick(3).setDamageDecayPerTick(0.53f));
    public static final RegistryObject<ShooterItem> kensaSplattershotJr = ShooterItem.create(REGISTRY, splattershotJr, "kensa_splattershot_jr");
    public static final RegistryObject<ShooterItem> aerosprayMG = ShooterItem.create(REGISTRY, new WeaponSettings("aerospray_mg")
            .setProjectileSize(1.2f).setProjectileSpeed(0.45f)
            .setFiringSpeed(3)
            .setGroundInaccuracy(13).setAirInaccuracy(16)
            .setInkConsumption(0.5f).setInkRecoveryCooldown(5)
            .setBaseDamage(5).setMinDamage(2.5f).setDamageDecayStartTick(3).setDamageDecayPerTick(0.45f));
    public static final RegistryObject<ShooterItem> aerosprayRG = ShooterItem.create(REGISTRY, aerosprayMG, "aerospray_rg");
    public static final RegistryObject<ShooterItem> gal52 = ShooterItem.create(REGISTRY, new WeaponSettings("52_gal")
            .setProjectileSize(1.1f).setProjectileSpeed(0.78f)
            .setFiringSpeed(6)
            .setGroundInaccuracy(6).setAirInaccuracy(12)
            .setInkConsumption(1.3f).setInkRecoveryCooldown(7)
            .setBaseDamage(10.4f).setMinDamage(6).setDamageDecayStartTick(4).setDamageDecayPerTick(0.83f));
    public static final RegistryObject<ShooterItem> gal52Deco = ShooterItem.create(REGISTRY, gal52, "52_gal_deco");
    public static final RegistryObject<ShooterItem> kensaGal52 = ShooterItem.create(REGISTRY, gal52, "kensa_52_gal");
    public static final RegistryObject<ShooterItem> gal96 = ShooterItem.create(REGISTRY, new WeaponSettings("96_gal")
            .setProjectileSize(1.2f).setProjectileSpeed(0.88f)
            .setFiringSpeed(8)
            .setGroundInaccuracy(4).setAirInaccuracy(11)
            .setInkConsumption(2.5f).setInkRecoveryCooldown(7)
            .setBaseDamage(12.4f).setMinDamage(7).setDamageDecayStartTick(3).setDamageDecayPerTick(1));
    public static final RegistryObject<ShooterItem> gal96Deco = ShooterItem.create(REGISTRY, gal96, "96_gal_deco");
    public static final RegistryObject<ShooterItem> nzap85 = ShooterItem.create(REGISTRY, new WeaponSettings("n-zap85")
            .setProjectileSize(1).setProjectileSpeed(0.75f)
            .setFiringSpeed(2)
            .setGroundInaccuracy(6).setAirInaccuracy(12)
            .setInkConsumption(0.8f).setInkRecoveryCooldown(7)
            .setBaseDamage(5.9f).setMinDamage(2.8f).setDamageDecayStartTick(3).setDamageDecayPerTick(0.53f));
    public static final RegistryObject<ShooterItem> nzap89 = ShooterItem.create(REGISTRY, nzap85, "n-zap89");

    //Blasters
    public static final RegistryObject<BlasterItem> blaster = BlasterItem.createBlaster(REGISTRY, (new WeaponSettings("blaster")
            .setProjectileSize(2.25f).setProjectileLifespan(5).setProjectileSpeed(1.05f)
            .setFiringSpeed(20).setStartupTicks(4)
            .setGroundInaccuracy(0).setAirInaccuracy(10)
            .setInkConsumption(10).setInkRecoveryCooldown(20)
            .setBaseDamage(25).setMinDamage(10)));
    public static final RegistryObject<BlasterItem> grimBlaster = BlasterItem.createBlaster(REGISTRY, blaster, "grim_blaster");
    public static final RegistryObject<BlasterItem> clashBlaster = BlasterItem.createBlaster(REGISTRY, new WeaponSettings("clash_blaster")
            .setProjectileSize(1.7f).setProjectileLifespan(4).setProjectileSpeed(1.1f)
            .setFiringSpeed(10).setStartupTicks(1)
            .setGroundInaccuracy(0).setAirInaccuracy(8)
            .setInkConsumption(4).setInkRecoveryCooldown(13).setBaseDamage(12).setMinDamage(6));
    public static final RegistryObject<BlasterItem> clashBlasterNeo = BlasterItem.createBlaster(REGISTRY, clashBlaster, "clash_blaster_neo");

    //Rollers
    public static final RegistryObject<RollerItem> splatRoller = RollerItem.create(REGISTRY, new RollerWeaponSettings("splat_roller").setBrush(false)
            .setRollSize(3).setRollConsumption(0.06f).setRollInkRecoveryCooldown(7).setRollDamage(25).setRollMobility(1.08f)
            .setDashMobility(1.32f).setDashConsumption(0.3f).setDashTime(30)
            .setSwingMobility(0.48f).setSwingConsumption(9).setSwingInkRecoveryCooldown(15).setSwingProjectileSpeed(0.55f).setSwingTime(6)
            .setSwingBaseDamage(30).setSwingMinDamage(7).setSwingDamageDecayStartTick(8).setSwingDamageDecayPerTick(3.45f));
    public static final RegistryObject<RollerItem> krakOnSplatRoller = RollerItem.create(REGISTRY, splatRoller, "krak_on_splat_roller");
    public static final RegistryObject<RollerItem> coroCoroSplatRoller = RollerItem.create(REGISTRY, splatRoller, "corocoro_splat_roller");
    public static final RegistryObject<RollerItem> carbonRoller = RollerItem.create(REGISTRY, new RollerWeaponSettings("carbon_roller").setBrush(false)
            .setRollSize(2).setRollConsumption(0.06f).setRollInkRecoveryCooldown(7).setRollDamage(14).setRollMobility(1.28f)
            .setDashMobility(1.52f).setDashConsumption(0.3f).setDashTime(10)
            .setSwingMobility(0.6f).setSwingConsumption(4).setSwingInkRecoveryCooldown(13).setSwingProjectileSpeed(0.45f).setSwingTime(3)
            .setSwingBaseDamage(20).setSwingMinDamage(5).setSwingDamageDecayStartTick(8).setSwingDamageDecayPerTick(2.25f)
            .setFlingInkRecoveryCooldown(15).setFlingProjectileSpeed(0.58f).setFlingTime(4)
            .setFlingBaseDamage(24).setFlingMinDamage(7).setFlingDamageDecayStartTick(10).setFlingDamageDecayPerTick(3.4f));
    public static final RegistryObject<RollerItem> dynamoRoller = RollerItem.create(REGISTRY, new RollerWeaponSettings("dynamo_roller").setBrush(false)
            .setRollSize(4).setRollConsumption(0.012f).setRollInkRecoveryCooldown(7).setRollDamage(32).setRollMobility(0.88f)
            .setDashMobility(1.08f).setDashConsumption(0.06f).setDashTime(20)
            .setSwingMobility(0.24f).setSwingConsumption(18).setSwingInkRecoveryCooldown(22).setSwingProjectileSpeed(0.85f).setSwingTime(12)
            .setSwingBaseDamage(25).setSwingMinDamage(5).setSwingDamageDecayStartTick(18).setSwingDamageDecayPerTick(1.125f)
            .setFlingInkRecoveryCooldown(26).setFlingProjectileSpeed(0.98f).setFlingTime(18)
            .setFlingBaseDamage(36).setFlingMinDamage(7).setFlingDamageDecayStartTick(18).setFlingDamageDecayPerTick(1.6f));
    public static final RegistryObject<RollerItem> inkbrush = RollerItem.create(REGISTRY, new RollerWeaponSettings("inkbrush").setBrush(true)
            .setRollSize(1).setRollConsumption(0.4f).setRollInkRecoveryCooldown(7).setRollDamage(4).setRollMobility(1.92f)
            .setSwingMobility(0.24f).setSwingConsumption(2).setSwingInkRecoveryCooldown(10).setSwingProjectileSpeed(0.6f).setSwingTime(2)
            .setSwingBaseDamage(6).setSwingMinDamage(3).setSwingDamageDecayStartTick(7).setSwingDamageDecayPerTick(0.45f));
    public static final RegistryObject<RollerItem> octobrush = RollerItem.create(REGISTRY, new RollerWeaponSettings("octobrush").setBrush(true)
            .setRollSize(2).setRollConsumption(0.54f).setRollInkRecoveryCooldown(7).setRollDamage(5).setRollMobility(1.92f)
            .setSwingMobility(0.24f).setSwingConsumption(3.2f).setSwingInkRecoveryCooldown(10).setSwingProjectileSpeed(0.65f).setSwingTime(3)
            .setSwingBaseDamage(8).setSwingMinDamage(4).setSwingDamageDecayStartTick(8).setSwingDamageDecayPerTick(0.57f));
    public static final RegistryObject<RollerItem> kensaOctobrush = RollerItem.create(REGISTRY, octobrush, "kensa_octobrush");

    //Chargers
    public static final RegistryObject<ChargerItem> splatCharger = ChargerItem.create(REGISTRY, new WeaponSettings("splat_charger")
            .setProjectileSize(0.7f).setProjectileLifespan(13).setProjectileSpeed(1.8f)
            .setStartupTicks(20).setDischargeTicks(20)
            .setMinInkConsumption(2.25f).setInkConsumption(18).setInkRecoveryCooldown(7)
            .setBaseDamage(32)
            .setChargerMobility(0.4f)
            .setFastMidAirCharge(false)
            .setChargerPiercesAt(1.1f));
    public static final RegistryObject<ChargerItem> bentoSplatCharger = ChargerItem.create(REGISTRY, splatCharger, "bento_splat_charger");
    public static final RegistryObject<ChargerItem> kelpSplatCharger = ChargerItem.create(REGISTRY, splatCharger, "kelp_splat_charger");
    public static final RegistryObject<ChargerItem> eLiter4K = ChargerItem.create(REGISTRY, new WeaponSettings("e_liter_4k")
            .setProjectileSize(0.85f).setProjectileLifespan(16).setProjectileSpeed(2.4f)
            .setStartupTicks(35).setDischargeTicks(40)
            .setMinInkConsumption(2.25f).setInkConsumption(25).setInkRecoveryCooldown(7)
            .setBaseDamage(36)
            .setChargerMobility(0.15f)
            .setFastMidAirCharge(false)
            .setChargerPiercesAt(1.0f));
    public static final RegistryObject<ChargerItem> bamboozler14mk1 = ChargerItem.create(REGISTRY, new WeaponSettings("bamboozler_14_mk1")
            .setProjectileSize(0.75f).setProjectileLifespan(8).setProjectileSpeed(1.9f)
            .setStartupTicks(4).setDischargeTicks(0) // no charge storage
            .setMinInkConsumption(2.8f).setInkConsumption(7).setInkRecoveryCooldown(7)
            .setBaseDamage(16) // bamboo without MPU :trollface:
            .setChargerMobility(0.8f)
            .setFastMidAirCharge(false)
            .setChargerPiercesAt(1.1f));
    public static final RegistryObject<ChargerItem> bamboozler14mk2 = ChargerItem.create(REGISTRY, bamboozler14mk1, "bamboozler_14_mk2");
    public static final RegistryObject<ChargerItem> classicSquiffer = ChargerItem.create(REGISTRY, new WeaponSettings("classic_squiffer")
            .setProjectileSize(0.7f).setProjectileLifespan(12).setProjectileSpeed(1.85f)
            .setStartupTicks(15).setDischargeTicks(25)
            .setMinInkConsumption(1.87f).setInkConsumption(10.5f).setInkRecoveryCooldown(7)
            .setBaseDamage(28f)
            .setChargerMobility(0.3f)
            .setFastMidAirCharge(true)
            .setChargerPiercesAt(1.0f));


    //Dualies
    public static final RegistryObject<DualieItem> splatDualie = DualieItem.create(REGISTRY, new WeaponSettings("splat_dualies")
            .setProjectileSize(0.9f).setProjectileSpeed(0.65f)
            .setFiringSpeed(7)
            .setGroundInaccuracy(2).setAirInaccuracy(7.5f).setInkConsumption(0.75f).setInkRecoveryCooldown(7)
            .setBaseDamage(6).setMinDamage(4).setDamageDecayStartTick(2).setDamageDecayPerTick(1.13f)
            .setRollCount(1)
            .setRollSpeed(0.9f)
            .setRollInaccuracy(0).setRollInkConsumption(9).setRollInkRecoveryCooldown(23)
            .setRollCooldown(8).setLastRollCooldown(30));
    public static final RegistryObject<DualieItem> enperrySplatDualie = DualieItem.create(REGISTRY, splatDualie, "enperry_splat_dualies");
    public static final RegistryObject<DualieItem> dualieSquelcher = DualieItem.create(REGISTRY, new WeaponSettings("dualie_squelchers")
            .setProjectileSize(0.85f).setProjectileSpeed(0.74f)
            .setFiringSpeed(8)
            .setGroundInaccuracy(4).setAirInaccuracy(8)
            .setInkConsumption(1.2f)
            .setInkRecoveryCooldown(7)
            .setBaseDamage(5.6f).setMinDamage(2.8f).setDamageDecayStartTick(3).setDamageDecayPerTick(0.53f)
            .setRollCount(1)
            .setRollSpeed(0.7f)
            .setRollInaccuracy(2)
            .setRollInkConsumption(5).setRollInkRecoveryCooldown(20)
            .setRollCooldown(6).setLastRollCooldown(14));
    public static final RegistryObject<DualieItem> gloogaDualie = DualieItem.create(REGISTRY, new WeaponSettings("glooga_dualies")
            .setProjectileSize(0.8f).setProjectileSpeed(0.72f)
            .setFiringSpeed(10)
            .setGroundInaccuracy(4).setAirInaccuracy(8)
            .setInkConsumption(1.4f).setInkRecoveryCooldown(7)
            .setBaseDamage(7.3f).setMinDamage(3.6f).setDamageDecayStartTick(2).setDamageDecayPerTick(1.35f)
            .setRollCount(1)
            .setRollSpeed(0.7f)
            .setRollBaseDamage(10.6f).setRollMinDamage(5.26f).setRollDamageDecayPerTick(2)
            .setRollInaccuracy(3)
            .setRollInkConsumption(8).setRollInkRecoveryCooldown(23)
            .setRollCooldown(9).setLastRollCooldown(24));
    public static final RegistryObject<DualieItem> gloogaDualieDeco = DualieItem.create(REGISTRY, gloogaDualie, "glooga_dualies_deco");
    public static final RegistryObject<DualieItem> kensaGloogaDualie = DualieItem.create(REGISTRY, gloogaDualie, "kensa_glooga_dualies");

    //Sloshers
    public static final RegistryObject<SlosherItem> slosher = SlosherItem.create(REGISTRY, new WeaponSettings("slosher")
            .setProjectileSize(1.6f).setProjectileSpeed(0.4f).setProjectileCount(2)
            .setStartupTicks(7)
            .setGroundInaccuracy(8)
            .setInkConsumption(7f).setInkRecoveryCooldown(13)
            .setBaseDamage(14), SlosherItem.Type.DEFAULT);
    public static final RegistryObject<SlosherItem> classicSlosher = SlosherItem.create(REGISTRY, slosher, "classic_slosher");
    public static final RegistryObject<SlosherItem> sodaSlosher = SlosherItem.create(REGISTRY, slosher, "soda_slosher");
    public static final RegistryObject<SlosherItem> triSlosher = SlosherItem.create(REGISTRY, new WeaponSettings("tri_slosher")
            .setProjectileSize(1.55f).setProjectileSpeed(0.444f).setProjectileCount(3)
            .setStartupTicks(4)
            .setGroundInaccuracy(20)
            .setInkConsumption(6f).setInkRecoveryCooldown(12)
            .setBaseDamage(12.4f), SlosherItem.Type.DEFAULT);
    public static final RegistryObject<SlosherItem> explosher = SlosherItem.create(REGISTRY, new WeaponSettings("explosher")
            .setProjectileSize(2f).setProjectileSpeed(0.75f).setProjectileCount(1)
            .setFiringSpeed(20).setStartupTicks(5)
            .setGroundInaccuracy(0)
            .setInkConsumption(11.7f).setInkRecoveryCooldown(23)
            .setBaseDamage(11f).setMinDamage(7), SlosherItem.Type.EXPLODING);

    //Ink Tanks
    public static final RegistryObject<InkTankItem> inkTank = REGISTRY.register("ink_tank", () -> new InkTankItem("ink_tank", 100));
    public static final RegistryObject<InkTankItem> classicInkTank = REGISTRY.register("classic_ink_tank", () -> new InkTankItem("classic_ink_tank", inkTank.get()));
    public static final RegistryObject<InkTankItem> inkTankJr = REGISTRY.register("ink_tank_jr", () -> new InkTankItem("ink_tank_jr", 110));
    public static final RegistryObject<InkTankItem> armoredInkTank = REGISTRY.register("armored_ink_tank", () -> new InkTankItem("armored_ink_tank", 85, ARMORED_INK_TANK));

    //Sub Weapons
    public static final RegistryObject<SubWeaponItem> splatBomb = REGISTRY.register("splat_bomb", () -> new SubWeaponItem(SplatcraftEntities.SPLAT_BOMB, SplatBombEntity.DIRECT_DAMAGE, SplatBombEntity.EXPLOSION_SIZE, 70, 20));
    public static final RegistryObject<SubWeaponItem> splatBomb2 = REGISTRY.register("splat_bomb_2", () -> (SubWeaponItem) new SubWeaponItem(SplatcraftEntities.SPLAT_BOMB, SplatBombEntity.DIRECT_DAMAGE, SplatBombEntity.EXPLOSION_SIZE, 70, 20).setSecret());
    public static final RegistryObject<SubWeaponItem> burstBomb = REGISTRY.register("burst_bomb", () -> new SubWeaponItem(SplatcraftEntities.BURST_BOMB, BurstBombEntity.DIRECT_DAMAGE, BurstBombEntity.EXPLOSION_SIZE, 40, 15));
    public static final RegistryObject<SubWeaponItem> suctionBomb = REGISTRY.register("suction_bomb", () -> new SubWeaponItem(SplatcraftEntities.SUCTION_BOMB, SuctionBombEntity.DIRECT_DAMAGE, SuctionBombEntity.EXPLOSION_SIZE, 70, 30));
    public static final RegistryObject<SubWeaponItem> curlingBomb = REGISTRY.register("curling_bomb", () -> new SubWeaponItem(SplatcraftEntities.CURLING_BOMB, CurlingBombEntity.DIRECT_DAMAGE, CurlingBombEntity.EXPLOSION_SIZE, 70, 30, CurlingBombEntity.MAX_COOK_TIME, CurlingBombEntity::onItemUseTick));

    //Vanity
    public static final RegistryObject<Item> inkClothHelmet = REGISTRY.register("ink_cloth_helmet", () -> new ColoredArmorItem(INK_CLOTH, EquipmentSlot.HEAD));
    public static final RegistryObject<Item> inkClothChestplate = REGISTRY.register("ink_cloth_chestplate", () -> new ColoredArmorItem(INK_CLOTH, EquipmentSlot.CHEST));
    public static final RegistryObject<Item> inkClothLeggings = REGISTRY.register("ink_cloth_leggings", () -> new ColoredArmorItem(INK_CLOTH, EquipmentSlot.LEGS));
    public static final RegistryObject<Item> inkClothBoots = REGISTRY.register("ink_cloth_boots", () -> new ColoredArmorItem(INK_CLOTH, EquipmentSlot.FEET));

    //Materials
    public static final RegistryObject<Item> sardinium = REGISTRY.register("sardinium", () -> new Item(new Item.Properties().tab(SplatcraftItemGroups.GROUP_GENERAL)));
    public static final RegistryObject<Item> sardiniumBlock = REGISTRY.register("sardinium_block", () -> new BlockItem(SplatcraftBlocks.sardiniumBlock.get()));
    public static final RegistryObject<Item> sardiniumOre = REGISTRY.register("sardinium_ore", () -> new BlockItem(SplatcraftBlocks.sardiniumOre.get()));
    public static final RegistryObject<Item> powerEgg = REGISTRY.register("power_egg", () -> new Item(new Item.Properties().tab(SplatcraftItemGroups.GROUP_GENERAL)));
    public static final RegistryObject<Item> powerEggCan = REGISTRY.register("power_egg_can", PowerEggCanItem::new);
    public static final RegistryObject<Item> powerEggBlock = REGISTRY.register("power_egg_block", () -> new BlockItem(SplatcraftBlocks.powerEggBlock.get()));
    public static final RegistryObject<Item> emptyInkwell = REGISTRY.register("empty_inkwell", () -> new BlockItem(SplatcraftBlocks.emptyInkwell.get()));
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
    public static final RegistryObject<FilterItem> enchantedFilter = REGISTRY.register("enchanted_filter", () -> new FilterItem(true, false));
    public static final RegistryObject<FilterItem> overgrownFilter = REGISTRY.register("overgrown_filter", FilterItem::new);
    public static final RegistryObject<FilterItem> midnightFilter = REGISTRY.register("midnight_filter", FilterItem::new);
    public static final RegistryObject<FilterItem> creativeFilter = REGISTRY.register("creative_filter", () -> new FilterItem(true, true));

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
    public static final RegistryObject<Item> stageBarrier = REGISTRY.register("stage_barrier", () -> new BlockItem(SplatcraftBlocks.stageBarrier.get()));
    public static final RegistryObject<Item> stageVoid = REGISTRY.register("stage_void", () -> new BlockItem(SplatcraftBlocks.stageVoid.get()));
    public static final RegistryObject<Item> allowedColorBarrier = REGISTRY.register("allowed_color_barrier", () -> new ColoredBlockItem(SplatcraftBlocks.allowedColorBarrier.get()).addStarters(false));
    public static final RegistryObject<Item> deniedColorBarrier = REGISTRY.register("denied_color_barrier", () -> new ColoredBlockItem(SplatcraftBlocks.deniedColorBarrier.get()).addStarters(false));

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

        ItemProperties.register(canvas.get(), new ResourceLocation(Splatcraft.MODID, "inked"), (stack, level, entity, seed) -> ColorUtils.getInkColor(stack) == -1 ? 0 : 1);
    }

    /*
    @OnlyIn(Dist.CLIENT)
    public static void registerArmorModels() {
        inkTank.get().setArmorModel(new InkTankModel());
        classicInkTank.get().setArmorModel(new ClassicInkTankModel());
        inkTankJr.get().setArmorModel(new InkTankJrModel());
        armoredInkTank.get().setArmorModel(new ArmoredInkTankModel());
    }
    */

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
