package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.SplatCraft;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class SplatCraftModelManager
{
    public static final List<Item> items = new ArrayList<>();
    public static final List<Block> blocks = new ArrayList<>();

    @SubscribeEvent
    public static void handleModelRegistry(ModelRegistryEvent event)
    {
        ItemModels();
        ItemBlockModels();
    }

    private static void ItemModels()
    {
        for(Item item : items)
            register(item);
    }
    private static void ItemBlockModels()
    {
        for(Block block : blocks)
            register(block);
    }


    private static void register(Item item)
    {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Item.REGISTRY.getNameForObject(item), "inventory"));
    }

    private static void register(Item item, int meta, String modelResource)
    {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(SplatCraft.MODID+":"+modelResource, "inventory"));
    }

    private static void register(Block block)
    {
        register(Item.getItemFromBlock(block));
    }

    private static void register(Block block, int meta, String modelResource)
    {
        register(Item.getItemFromBlock(block), meta, modelResource);
    }
}
