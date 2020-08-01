package com.cibernet.splatcraft.data.tags;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.crafting.InkColor;
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
		public static final HashMap<InkTankItem, ITag<Item>> INK_TANK_WHITELIST = new HashMap<>();
		public static final HashMap<InkTankItem, ITag<Item>> INK_TANK_BLACKLIST = new HashMap<>();
		
		public static final ITag<Item> SHOOTERS = createTag("shooters");
		public static final ITag<Item> ROLLERS = createTag("rollers");
		public static final ITag<Item> CHARGERS = createTag("chargers");
		public static final ITag<Item> DUALIES = createTag("dualies");
		public static final ITag<Item> SPLATLINGS = createTag("splatlings");
		public static final ITag<Item> BRELLAS = createTag("brellas");
		
		public static final ITag<Item> MAIN_WEAPONS = createTag("main_weapons");
		public static final ITag<Item> SUB_WEAPONS = createTag("sub_weapons");
		public static final ITag<Item> SPECIAL_WEAPONS = createTag("special_weapons");
		public static final ITag<Item> INK_TANKS = createTag("ink_tanks");
		
		public static final ITag<Item> FILTERS = createTag("filters");
		public static final ITag<Item> REMOTES = createTag("remotes");
		
		public static void putInkTankTags(InkTankItem tank, String name)
		{
			if(!INK_TANK_WHITELIST.containsKey(tank))
				INK_TANK_WHITELIST.put(tank, createTag(name+"whitelist"));
			if(!INK_TANK_BLACKLIST.containsKey(tank))
				INK_TANK_BLACKLIST.put(tank, createTag(name+"_blacklist"));
		}
		
		private static ITag<Item> createTag(String name)
		{
			return ItemTags.getCollection().getOrCreate(new ResourceLocation(Splatcraft.MODID, name));
			//return ItemTags.makeWrapperTag(Splatcraft.MODID+":"+name);
		}
	}
	
	public static class Blocks
	{
		public static final ITag<Block> UNINKABLE_BLOCKS = createTag("uninkable_blocks");
		public static final ITag<Block> INKABLE_BLOCKS = createTag("inkable_blocks");
		
		private static ITag<Block> createTag(String name)
		{
			return BlockTags.getCollection().getOrCreate(new ResourceLocation(Splatcraft.MODID, name));
		}
	}
	
	
	public static class InkColors
	{
		private static final TagRegistry<InkColor> collection = new TagRegistry();
		
		private static ITag<InkColor> createTag(String name)
		{
			return collection.func_232939_b_().getOrCreate(new ResourceLocation(Splatcraft.MODID, name));
		}
		
		
	}
	
	
}
