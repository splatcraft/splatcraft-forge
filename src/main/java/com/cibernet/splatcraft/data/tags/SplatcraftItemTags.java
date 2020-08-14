package com.cibernet.splatcraft.data.tags;

import com.cibernet.splatcraft.registries.SplatcraftItems;
import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.data.TagsProvider;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;

public class SplatcraftItemTags extends ItemTagsProvider
{
	
	public static final ITag.INamedTag<Item> MUSIC_DISCS = ItemTags.makeWrapperTag("music_discs");
	
	public SplatcraftItemTags(DataGenerator generator, BlockTagsProvider blockTagsProvider)
	{
		super(generator, blockTagsProvider);
	}
	
	@Override
	protected void registerTags()
	{
	}
	
	protected TagsProvider.Builder<Item> addToTag(ITag.INamedTag<Item> tag, Item... entries)
	{
		return this.getOrCreateBuilder(tag).add(entries);
	}
}
