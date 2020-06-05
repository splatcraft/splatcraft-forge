package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.entities.models.ModelArmoredInkTank;
import com.cibernet.splatcraft.entities.models.ModelClassicInkTank;
import com.cibernet.splatcraft.entities.models.ModelInkTank;
import com.cibernet.splatcraft.entities.models.ModelInkTankJr;
import com.cibernet.splatcraft.items.*;
import com.cibernet.splatcraft.utils.TabSplatCraft;
import net.minecraft.block.Block;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;

public class SplatCraftItems
{
    
    public static final ItemArmor.ArmorMaterial INK_CLOTH_MATERIAL = EnumHelper.addArmorMaterial("inkCloth", SplatCraft.MODID+":ink_cloth", -1, new int[] {0,0,0,0}, 0, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0);
    
    public static ArrayList<Block> itemBlocks = new ArrayList<>();

    public static final Item powerEgg = new Item().setUnlocalizedName("powerEgg").setRegistryName("power_egg").setCreativeTab(TabSplatCraft.main);
    public static final Item powerEggCan = new ItemPowerEggCan();
    public static final Item sardinium = new Item().setUnlocalizedName("sardinium").setRegistryName("sardinium").setCreativeTab(TabSplatCraft.main);

    public static final Item splattershot = new ItemShooterBase("splattershot", "splattershot", 1.05f, 0.65f, 12f, 4, 8f, 0.9f);
    public static final Item tentatekSplattershot = new ItemShooterBase("tentatekSplattershot", "tentatek_splattershot", splattershot);
    public static final Item wasabiSplattershot = new ItemShooterBase("wasabiSplattershot", "wasabi_splattershot", splattershot);
    public static final Item splatRoller = new ItemRollerBase("splatRoller", "splat_roller", -3d, 0.4f, 4f, 0.8f, 9f,1.15d, 3, 20, 0.25f, false); //TODO revert ic to 0.1 when the roller overhaul hits
    public static final Item krakOnSplatRoller = new ItemRollerBase("krakOnSplatRoller", "krak_on_splat_roller", splatRoller);
    public static final Item coroCoroSplatRoller = new ItemRollerBase("coroCoroSplatRoller", "corocoro_splat_roller", splatRoller);
    public static final Item splatCharger = new ItemChargerBase("splatCharger", "splat_charger", 0.75f, 9, 20, 40, 32f, 2.25f, 18f, 0.4);
    public static final Item bentoSplatCharger = new ItemChargerBase("bentoSplatCharger", "bento_splat_charger", splatCharger);
    public static final Item splattershotJr = new ItemShooterBase("splattershotJr", "splattershot_jr", 1f, 0.35f, 13.5f, 4, 6.5f, 0.5f);
    public static final Item inkbrush = new ItemRollerBase("inkbrush", "inkbrush", 8D, 0.35f, 6f, 0.85f, 2f, 1.3d,1, 5, 0.135f,true);
    public static final Item aerosprayMG = new ItemShooterBase("aerosprayMG", "aerospray_mg", 1.3f, 0.35f, 26f, 2, 4.8f, 0.5f);
    public static final Item aerosprayRG = new ItemShooterBase("aerosprayRG", "aerospray_rg", aerosprayMG);
    //public static final Item clashBlaster = new ItemShooterBase("clashBlaster", "clash_blaster", 2f, 0.95f, 10f, 12, 12, false);
    public static final Item clashBlaster = new ItemBlasterBase("clashBlaster", "clash_blaster", 2f, 1.2f, 5f, 1, 10, 12f, 6f, 4, 5);
    public static final Item clashBlasterNeo = new ItemBlasterBase("clashBlasterNeo", "clash_blaster_neo", clashBlaster);
    public static final Item octobrush = new ItemRollerBase("octobrush", "octobrush", -0.1D, 0.5f, 8f, 0.95f, 3.2f, 1.2d, 2, 4, 0.18f, true);
    public static final Item eLiter4K = new ItemChargerBase("eLiter4K", "e_liter_4k", 0.85f, 14, 35, 40, 36f, 2.25f, 25f, 0.15);
    public static final Item blaster = new ItemBlasterBase("blaster", "blaster", 3f, 1.1f, 5f, 3, 18, 25f, 10f, 10f, 10);
    public static final Item grimBlaster = new ItemBlasterBase("grimBlaster", "grim_blaster", blaster);
    public static final Item splatDualie = new ItemDualieBase("splatDualies", "splat_dualies", 1f, 0.55f, 10, 8, 6, 0.75f, 1, 0.7f, 9, 8, 30);
    public static final Item enperrySplatDualie = new ItemDualieBase("enperrySplatDualies", "enperry_splat_dualies", splatDualie);
    public static final Item carbonRoller = new ItemRollerBase("carbonRoller", "carbon_roller", -1.5d, 0.38f, 4f, 0.7f, 3.5f, 1.25d, 2, 14, 0.1f, false);
    public static final Item gal52 = new ItemShooterBase("52Gal", "52_gal", 1.2f, 0.68f, 16f, 9, 10.4f, 1.3f);
    public static final Item gal96 = new ItemShooterBase("96Gal", "96_gal", 1.3f, 0.75f, 12.5f, 11, 12.4f, 2.5f);
    public static final Item dualieSquelcher = new ItemDualieBase("dualieSquelchers", "dualie_squelchers", 0.9f, 0.64f, 11.5f, 12, 4.4f, 1.2f, 1, 1f, 5, 6, 12);
    
    
    public static final ItemInkTank inkTank = new ItemInkTank("inkTank", "ink_tank", 100);
    public static final ItemInkTank classicInkTank = new ItemInkTank("classicInkTank", "ink_tank_classic", inkTank);
    public static final ItemInkTank inkTankJr = new ItemInkTank("inkTankJr", "ink_tank_jr", 110).addAllowedWeapons(splattershotJr);
    public static final ItemInkTank armoredInkTank = new ItemInkTank("armoredInkTank", "ink_tank_armored", 85, 3);
    
    public static final Item squidBumper = new ItemSquidBumper("squidBumper", "squid_bumper");
    
    public static final Item inkClothHat = new ItemInkColoredArmor("inkClothHelmet", "ink_cloth_helmet", INK_CLOTH_MATERIAL, 0, EntityEquipmentSlot.HEAD);
    public static final Item inkClothShirt = new ItemInkColoredArmor("inkClothChestplate", "ink_cloth_chestplate", INK_CLOTH_MATERIAL, 0, EntityEquipmentSlot.CHEST);
    public static final Item inkClothPants = new ItemInkColoredArmor("inkClothLeggings", "ink_cloth_leggings", INK_CLOTH_MATERIAL, 0, EntityEquipmentSlot.LEGS);
    public static final Item inkClothShoes = new ItemInkColoredArmor("inkClothBoots", "ink_cloth_boots", INK_CLOTH_MATERIAL, 0, EntityEquipmentSlot.FEET);
    
    public static final ItemFilter filterEmpty = new ItemFilter("filterEmpty", "filter_empty", false);
    public static final ItemFilter filterNeon = new ItemFilter("filterNeon", "filter_neon", false);
    public static final ItemFilter filterDye = new ItemFilter("filterDye", "filter_dye", false);
    public static final ItemFilter filterPastel = new ItemFilter("filterPastel", "filter_pastel", false);
    public static final ItemFilter filterEnchanted = new ItemFilter("filterEnchanted", "filter_enchanted", true);
    public static final ItemFilter filterCreative = new ItemFilter("filterCreative", "filter_creative", true);
    
    public static final Item inkDisruptor = new ItemInkDisruptor();
    public static final Item turfScanner = new ItemTurfScanner();
    public static final Item colorChanger = new ItemColorChanger();
    
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> registry = event.getRegistry();

        registerItem(registry, powerEgg);
        registerItem(registry, powerEggCan);
        registerItem(registry, sardinium);

        registerItem(registry, splattershot);
        registerItem(registry, tentatekSplattershot);
        registerItem(registry, wasabiSplattershot);
        registerItem(registry, splatRoller);
        registerItem(registry, krakOnSplatRoller);
        registerItem(registry, coroCoroSplatRoller);
        registerItem(registry, splatCharger);
        registerItem(registry, bentoSplatCharger);
        registerItem(registry, splattershotJr);
        registerItem(registry, inkbrush);
        registerItem(registry, aerosprayMG);
        registerItem(registry, aerosprayRG);
        registerItem(registry, clashBlaster);
        registerItem(registry, clashBlasterNeo);
        registerItem(registry, octobrush);
        registerItem(registry, eLiter4K);
        registerItem(registry, blaster);
        registerItem(registry, grimBlaster);
        registerItem(registry, splatDualie);
        registerItem(registry, enperrySplatDualie);
        registerItem(registry, carbonRoller);
        registerItem(registry, gal52);
        registerItem(registry, gal96);
        registerItem(registry, dualieSquelcher);
        
        registerItem(registry, inkTank);
        registerItem(registry, classicInkTank);
        registerItem(registry, armoredInkTank);
        registerItem(registry, inkTankJr);
    
        registerItem(registry, squidBumper);
        
        registerItem(registry, inkDisruptor);
        registerItem(registry, turfScanner);
        registerItem(registry, colorChanger );
    
        registerItem(registry, inkClothHat);
        registerItem(registry, inkClothShirt);
        registerItem(registry, inkClothPants);
        registerItem(registry, inkClothShoes);
        
        registerItem(registry, filterEmpty);
        registerItem(registry, filterNeon);
        registerItem(registry, filterDye);
        registerItem(registry, filterPastel);
        registerItem(registry, filterEnchanted);
        registerItem(registry, filterCreative);
        
        registerItemBlocks(registry);
    }

    @SideOnly(Side.CLIENT)
    public static void registerArmorModels()
    {
        inkTank.setArmorModel(new ModelInkTank());
        inkTankJr.setArmorModel(new ModelInkTankJr());
        armoredInkTank.setArmorModel(new ModelArmoredInkTank());
        classicInkTank.setArmorModel(new ModelClassicInkTank());
    }
    
    private static Item registerItem(IForgeRegistry<Item> registry, Item item)
    {
        registry.register(item);
        SplatCraftModelManager.items.add(item);
        return item;
    }

    public static void registerItemBlocks(IForgeRegistry<Item> registry)
    {
        for(Block block : itemBlocks)
        {
            ItemBlock item = new ItemBlock(block);
            registerItem(registry, item.setRegistryName(item.getBlock().getRegistryName()));
        }

        registerItem(registry, new ItemBlockInkColor(SplatCraftBlocks.inkwell).setMaxStackSize(16));
        registerItem(registry, new ItemBlockInkColor(SplatCraftBlocks.inkedWool));
    }
}
