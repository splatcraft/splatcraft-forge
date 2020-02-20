package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.utils.TabSplatCraft;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumDyeColor;

public class BlockPowerEggStorage extends Block
{
    public BlockPowerEggStorage()
    {

        super(Material.CLAY, MapColor.getBlockColor(EnumDyeColor.ORANGE));
        setRegistryName("power_egg_block");
        setUnlocalizedName("powerEggBlock");
        setCreativeTab(TabSplatCraft.main);
        setHardness(0.2f);
        setSoundType(SoundType.SLIME);
        setLightLevel(9f);
    }
}
