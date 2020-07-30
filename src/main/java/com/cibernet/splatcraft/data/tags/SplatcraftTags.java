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

import java.util.HashMap;

public class SplatcraftTags
{
	public static class Items
	{
		public static final HashMap<InkTankItem, ITag.INamedTag<Item>> INK_TANK_WHITELIST = new HashMap<>();
		public static final HashMap<InkTankItem, ITag.INamedTag<Item>> INK_TANK_BLACKLIST = new HashMap<>();
		
		public static final ITag.INamedTag<Item> SHOOTERS = createTag("shooters");
		public static final ITag.INamedTag<Item> ROLLERS = createTag("rollers");
		public static final ITag.INamedTag<Item> CHARGERS = createTag("chargers");
		public static final ITag.INamedTag<Item> DUALIES = createTag("dualies");
		public static final ITag.INamedTag<Item> SPLATLINGS = createTag("splatlings");
		public static final ITag.INamedTag<Item> BRELLAS = createTag("brellas");
		
		public static final ITag.INamedTag<Item> MAIN_WEAPONS = createTag("main_weapons");
		public static final ITag.INamedTag<Item> SUB_WEAPONS = createTag("sub_weapons");
		public static final ITag.INamedTag<Item> SPECIAL_WEAPONS = createTag("special_weapons");
		public static final ITag.INamedTag<Item> INK_TANKS = createTag("ink_tanks");
		
		public static final ITag.INamedTag<Item> FILTERS = createTag("filters");
		public static final ITag.INamedTag<Item> REMOTES = createTag("remotes");
		
		public static void putInkTankTags(InkTankItem tank, String name)
		{
			if(!INK_TANK_WHITELIST.containsKey(tank))
				INK_TANK_WHITELIST.put(tank, createTag(name+"whitelist"));
			if(!INK_TANK_BLACKLIST.containsKey(tank))
				INK_TANK_BLACKLIST.put(tank, createTag(name+"_blacklist"));
		}
		
		private static ITag.INamedTag<Item> createTag(String name)
		{
			return ItemTags.makeWrapperTag(Splatcraft.MODID+":"+name);
		}
	}
	
	public static class Blocks
	{
		
		
		private static ITag.INamedTag<Block> createTag(String name)
		{
			return BlockTags.makeWrapperTag(Splatcraft.MODID+":"+name);
		}
	}
	
	
	public static class InkColors
	{
		private static final TagRegistry<InkColor> collection = new TagRegistry();
		
		private static ITag.INamedTag<InkColor> createTag(String name)
		{
			return collection.func_232937_a_(Splatcraft.MODID+":"+name);
		}
		
		
	}
	
	
}
