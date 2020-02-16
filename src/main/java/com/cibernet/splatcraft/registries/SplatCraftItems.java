package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.items.ItemRollerBase;
import com.cibernet.splatcraft.items.ItemShooterBase;
import com.cibernet.splatcraft.items.ItemWeaponBase;
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

    public static final Item splattershot = new ItemShooterBase("splattershot", "splattershot", 1f, 0.5f, 10f, 5);
    public static final Item splatRoller = new ItemRollerBase("splatRoller", "splat_roller", -3d, 0.4f);
    public static final Item splatCharger = new ItemShooterBase("splatCharger", "splat_charger", 0.5f, 5f, 2f, 50, false);
    public static final Item splattershotJr = new ItemShooterBase("splattershotJr", "splattershot_jr", 1f, 0.3f, 3.5f, 4);
    public static final Item inkbrush = new ItemRollerBase("inkbrush", "inkbrush", 8D, 0.2f);
    
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> registry = event.getRegistry();

        registerItem(registry, splattershot);
        registerItem(registry, splatRoller);
        registerItem(registry, splatCharger);
        registerItem(registry, splattershotJr);
        registerItem(registry, inkbrush);
    
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
    }
}
