package com.cibernet.splatcraft.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;

public class MetalBlock extends Block
{
    public MetalBlock(Material material, MaterialColor color)
    {
        super(Properties.create(material, color).hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL));
    }
}
