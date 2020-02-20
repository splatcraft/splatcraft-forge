package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.utils.TabSplatCraft;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class BlockOre extends Block
{

    public BlockOre(int harvestLevel, String unlocName, String regName) {
        super(Material.ROCK);
        setSoundType(SoundType.STONE);
        setHardness(3f);
        setResistance(5f);
        setHarvestLevel("pickaxe", harvestLevel);
        setUnlocalizedName(unlocName);
        setRegistryName(regName);
        setCreativeTab(TabSplatCraft.main);

    }
}
