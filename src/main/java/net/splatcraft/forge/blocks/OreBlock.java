package net.splatcraft.forge.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;

public class OreBlock extends Block
{
    public OreBlock()
    {
        super(Properties.of(Material.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops());
    }
}
