package com.cibernet.splatcraft.data.tags;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
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
}
