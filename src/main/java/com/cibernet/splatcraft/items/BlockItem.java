package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.registries.SplatcraftItemGroups;
import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;

public class BlockItem extends net.minecraft.item.BlockItem
{
	public BlockItem(Block block)
	{
		super(block, new Properties().group(SplatcraftItemGroups.GROUP_GENERAL));
	}
	
	public BlockItem(RegistryObject<Block> blockObj)
	{
		this(blockObj.get());
	}
}
