package net.splatcraft.forge.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class MetalBlock extends Block
{
    public MetalBlock(Material material, MaterialColor color)
    {
        super(BlockBehaviour.Properties.of(material, color).strength(5.0F, 6.0F).sound(SoundType.METAL));
    }
}
