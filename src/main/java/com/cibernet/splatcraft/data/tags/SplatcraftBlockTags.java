package com.cibernet.splatcraft.data.tags;

import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
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
		addToTag(BlockTags.BEACON_BASE_BLOCKS, SplatcraftBlocks.sardiniumBlock);
		addToTag(Tags.Blocks.ORES, SplatcraftBlocks.sardiniumOre);
		addToTag(Tags.Blocks.STORAGE_BLOCKS, SplatcraftBlocks.sardiniumBlock, SplatcraftBlocks.powerEggBlock);
		
	}
	
	protected TagsProvider.Builder<Block> addToTag(ITag.INamedTag<Block> tag, Block... entries)
	{
		return this.getOrCreateBuilder(tag).add(entries);
	}
	
}
