package com.cibernet.splatcraft.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class OreBlock extends Block
{
    public OreBlock(int harvestLevel)
    {
        super(Properties.of(Material.STONE).strength(3.0F, 3.0F).harvestLevel(harvestLevel).harvestTool(ToolType.PICKAXE));
    }
}
