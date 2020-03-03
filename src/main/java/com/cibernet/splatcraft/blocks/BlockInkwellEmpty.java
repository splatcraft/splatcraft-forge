package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.utils.TabSplatCraft;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockInkwellEmpty extends BlockGlass {
    public BlockInkwellEmpty()
    {
        super(Material.GLASS, true);
        setHardness(0.5f);
        setCreativeTab(TabSplatCraft.main);
        setUnlocalizedName("inkwellEmpty");
        setRegistryName("inkwell_empty");
        setSoundType(SoundType.GLASS);
    }
}
