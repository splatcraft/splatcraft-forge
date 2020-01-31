package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.blocks.BlockInked;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class SplatCraftBlocks
{

    public static Block inkedBlock = new BlockInked();
    
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        IForgeRegistry<Block> registry = event.getRegistry();
        
        registerBlock(registry, inkedBlock);
        
    }


    private static Block registerBlock(IForgeRegistry<Block> registry, Block block, boolean... hasItem)
    {
        registry.register(block);
        SplatCraftModelManager.blocks.add(block);

        if(hasItem.length > 0)
            SplatCraftItems.itemBlocks.add(block);

        return block;
    }
}
