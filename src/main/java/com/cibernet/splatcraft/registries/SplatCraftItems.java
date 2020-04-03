package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.items.*;
import com.cibernet.splatcraft.utils.TabSplatCraft;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

public class SplatCraftItems
{
    public static ArrayList<Block> itemBlocks = new ArrayList<>();

    public static final Item powerEgg = new Item().setUnlocalizedName("powerEgg").setRegistryName("power_egg").setCreativeTab(TabSplatCraft.main);
    public static final Item powerEggCan = new ItemPowerEggCan();
    public static final Item sardinium = new Item().setUnlocalizedName("sardinium").setRegistryName("sardinium").setCreativeTab(TabSplatCraft.main);

    public static final Item splattershot = new ItemShooterBase("splattershot", "splattershot", 1f, 0.7f, 10f, 5, 7f);
    public static final Item splatRoller = new ItemRollerBase("splatRoller", "splat_roller", -3d, 0.6f, 4f,1.25d, 3, 20, false);
    public static final Item splatCharger = new ItemChargerBase("splatCharger", "splat_charger", 0.65f, 3f, 0.9f, 90, 60, 32f, 0.4);
    public static final Item splattershotJr = new ItemShooterBase("splattershotJr", "splattershot_jr", 1f, 0.55f, 3.5f, 4, 5.5f);
    public static final Item inkbrush = new ItemRollerBase("inkbrush", "inkbrush", 8D, 0.4f, 6f, 1.3d,1, 5,true);
    public static final Item aerosprayMG = new ItemShooterBase("aerosprayMG", "aerospray_mg", 1.2f, 0.5f, 20f, 2, 4.8f);
    //public static final Item clashBlaster = new ItemShooterBase("clashBlaster", "clash_blaster", 2f, 0.95f, 10f, 12, 12, false); //TODO
    public static final Item clashBlaster = new ItemBlasterBase("clashBlaster", "clash_blaster", 2f, 1.2f, 5f, 5, 10, 12, 5);
    public static final Item octobrush = new ItemRollerBase("octobrush", "octobrush", 4D, 0.5f, 8f, 1.1d, 2, 4, true);
    public static final Item eLiter4K = new ItemChargerBase("eLiter4K", "e_liter_4k", 0.75f, 3.7f, 0.85f, 110, 60, 36f, 0.15);
    public static final Item blaster = new ItemBlasterBase("blaster", "blaster", 3f, 1.1f, 5f, 7, 16, 25, 10);

    public static final ItemFilter filterEmpty = new ItemFilter("filterEmpty", "filter_empty", false);
    public static final ItemFilter filterNeon = new ItemFilter("filterNeon", "filter_neon", false);
    public static final ItemFilter filterDye = new ItemFilter("filterDye", "filter_dye", false);
    public static final ItemFilter filterPastel = new ItemFilter("filterPastel", "filter_pastel", false);
    public static final ItemFilter filterEnchanted = new ItemFilter("filterEnchanted", "filter_enchanted", true);
    public static final ItemFilter filterCreative = new ItemFilter("filterCreative", "filter_creative", true);
    
    public static final Item inkDisruptor = new ItemInkDisruptor();
    public static final Item turfScanner = new ItemTurfScanner();
    
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> registry = event.getRegistry();

        registerItem(registry, powerEgg);
        registerItem(registry, powerEggCan);
        registerItem(registry, sardinium);

        registerItem(registry, splattershot);
        registerItem(registry, splatRoller);
        registerItem(registry, splatCharger);
        registerItem(registry, splattershotJr);
        registerItem(registry, inkbrush);
        registerItem(registry, aerosprayMG);
        registerItem(registry, clashBlaster);
        registerItem(registry, octobrush);
        registerItem(registry, eLiter4K);
        registerItem(registry, blaster);
        
        registerItem(registry, filterEmpty);
        registerItem(registry, filterNeon);
        registerItem(registry, filterDye);
        registerItem(registry, filterPastel);
        registerItem(registry, filterEnchanted);
        registerItem(registry, filterCreative);
        
        registerItem(registry, inkDisruptor);
        registerItem(registry, turfScanner);

        registerItemBlocks(registry);
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

        registerItem(registry, new ItemInkwell());
    }
}
