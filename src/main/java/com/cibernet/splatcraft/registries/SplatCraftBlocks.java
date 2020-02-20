package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.blocks.BlockInkColor;
import com.cibernet.splatcraft.blocks.BlockInked;
import com.cibernet.splatcraft.blocks.BlockInkwell;
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

    public static Block sunkenCrate = new Block(Material.WOOD).setCreativeTab(TabSplatCraft.main).setUnlocalizedName("sunkenCrate").setRegistryName("sunken_crate"); //TODO
    public static Block oreSardinium = new Block(Material.ROCK).setCreativeTab(TabSplatCraft.main).setUnlocalizedName("oreSardinium").setRegistryName("sardinium_ore");
    
    public static Block PowerEggBlock = new Block(Material.CLAY, MapColor.getBlockColor(EnumDyeColor.ORANGE)).setHardness(0.2f).setRegistryName("power_egg_block").setUnlocalizedName("powerEggBlock").setCreativeTab(TabSplatCraft.main);
    
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
