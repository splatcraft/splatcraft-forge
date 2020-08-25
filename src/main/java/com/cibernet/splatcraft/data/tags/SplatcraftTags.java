package com.cibernet.splatcraft.data.tags;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.util.InkColor;
import com.cibernet.splatcraft.items.InkTankItem;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagRegistry;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

public class SplatcraftTags
{
	public static void register()
	{
	}
	
	public static class Items
	{
		public static final HashMap<InkTankItem, ResourceLocation> INK_TANK_WHITELIST = new HashMap<>();
		public static final HashMap<InkTankItem, ResourceLocation> INK_TANK_BLACKLIST = new HashMap<>();
		
		public static final ResourceLocation SHOOTERS = createTag("shooters");
		public static final ResourceLocation ROLLERS = createTag("rollers");
		public static final ResourceLocation CHARGERS = createTag("chargers");
		public static final ResourceLocation DUALIES = createTag("dualies");
		public static final ResourceLocation SPLATLINGS = createTag("splatlings");
		public static final ResourceLocation BRELLAS = createTag("brellas");
		
		public static final ResourceLocation MAIN_WEAPONS = createTag("main_weapons");
		public static final ResourceLocation SUB_WEAPONS = createTag("sub_weapons");
		public static final ResourceLocation SPECIAL_WEAPONS = createTag("special_weapons");
		public static final ResourceLocation INK_TANKS = createTag("ink_tanks");
		
		public static final ResourceLocation FILTERS = createTag("filters");
		public static final ResourceLocation REMOTES = createTag("remotes");
		
		public static void putInkTankTags(InkTankItem tank, String name)
		{
			if(!INK_TANK_WHITELIST.containsKey(tank))
				INK_TANK_WHITELIST.put(tank, createTag(name+"_whitelist"));
			if(!INK_TANK_BLACKLIST.containsKey(tank))
				INK_TANK_BLACKLIST.put(tank, createTag(name+"_blacklist"));
		}
		
		public static ITag<Item> getTag(ResourceLocation location)
		{
			return ItemTags.getCollection().getOrCreate(location);
		}
		
		private static ResourceLocation createTag(String name)
		{
			return new ResourceLocation(Splatcraft.MODID, name);
		}
	}
	
	public static class Blocks
	{
		public static final ITag<Block> UNINKABLE_BLOCKS = createTag("uninkable_blocks");
		public static final ITag<Block> INKABLE_BLOCKS = createTag("inkable_blocks");
		
		public static final ITag<Block> INKED_BLOCKS = createTag("inked_blocks");
		public static final ITag<Block> BLOCKS_TURF = createTag("blocks_turf");
		
		private static ITag<Block> createTag(String name)
		{
			return BlockTags.getCollection().getOrCreate(new ResourceLocation(Splatcraft.MODID, name));
		}
	}
	
	
	public static class InkColors
	{
		private static final TagRegistry<InkColor> collection = new TagRegistry();
		
		public static final ITag<InkColor> STARTER_COLORS = createTag("starter_colors");
		
		private static ITag<InkColor> createTag(String name)
		{
			return collection.func_232939_b_().getOrCreate(new ResourceLocation(Splatcraft.MODID, name));
		}
		
		
	}
	
	
}
