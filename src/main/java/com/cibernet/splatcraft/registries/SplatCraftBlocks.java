package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.blocks.*;
import com.cibernet.splatcraft.utils.TabSplatCraft;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class SplatCraftBlocks
{

    public static Block inkedBlock = new BlockInked();

    public static Block emptyInkwell = new BlockGlass(Material.GLASS, true).setHardness(0.5f).setCreativeTab(TabSplatCraft.main).setUnlocalizedName("inkwellEmpty").setRegistryName("inkwell_empty");
    public static Block inkwell = new BlockInkwell();

    public static Block sunkenCrate = new BlockSunkenCrate();
    public static Block oreSardinium = new BlockOre(1, "oreSardinium", "sardinium_ore");

    public static Block PowerEggBlock = new BlockPowerEggStorage();
    
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        IForgeRegistry<Block> registry = event.getRegistry();
        
        registerBlock(registry, inkedBlock);
        registerBlock(registry, emptyInkwell, true);
        registerBlock(registry, inkwell);
        registerBlock(registry, sunkenCrate, true);
        registerBlock(registry, oreSardinium, true);
        registerBlock(registry, PowerEggBlock, true);

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
