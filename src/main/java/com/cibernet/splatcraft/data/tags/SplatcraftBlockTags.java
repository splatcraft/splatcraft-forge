package com.cibernet.splatcraft.data.tags;

import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.Tag;
import net.minecraftforge.common.Tags;

public class SplatcraftBlockTags extends BlockTagsProvider
{

	public SplatcraftBlockTags(DataGenerator generator)
	{
		super(generator);
	}
	
	@Override
	protected void registerTags()
	{
		addToTag(BlockTags.field_232875_ap_, SplatcraftBlocks.sardiniumBlock.getBlock());
		addToTag(Tags.Blocks.ORES, SplatcraftBlocks.sardiniumOre.getBlock());
		addToTag(Tags.Blocks.STORAGE_BLOCKS, SplatcraftBlocks.sardiniumBlock.getBlock(), SplatcraftBlocks.powerEggBlock.getBlock());
	}
	
	protected TagsProvider.Builder<Block> addToTag(ITag.INamedTag<Block> tag, Block... entries)
	{
		return this.func_240522_a_(tag).func_240534_a_(entries);
	}
	
}
