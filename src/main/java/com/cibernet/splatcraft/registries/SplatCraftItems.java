package com.cibernet.splatcraft.registries;

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

    public static final Item splattershot = new ItemWeaponBase("splattershot", "splattershot");
    public static final Item splatRoller = new ItemWeaponBase("splatRoller", "splat_roller");

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> registry = event.getRegistry();

        registerItem(registry, splattershot);
        registerItem(registry, splatRoller);

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
