package com.cibernet.splatcraft.data;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.items.InkTankItem;
import com.cibernet.splatcraft.util.InkColor;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeTagHandler;

import java.util.HashMap;

public class SplatcraftTags
{
    public static void register()
    {
        new Items();
        new Blocks();
        new InkColors();
    }

    public static class Items
    {
        public static final HashMap<InkTankItem, ITag.INamedTag<Item>> INK_TANK_WHITELIST = new HashMap<>();
        public static final HashMap<InkTankItem, ITag.INamedTag<Item>> INK_TANK_BLACKLIST = new HashMap<>();

        public static final ITag.INamedTag<Item> MATCH_ITEMS = createTag("match_items");
        public static final ITag.INamedTag<Item> REVEALS_BARRIERS = createTag("reveals_barriers");

        public static final ITag.INamedTag<Item> SHOOTERS = createTag("shooters");
        public static final ITag.INamedTag<Item> ROLLERS = createTag("rollers");
        public static final ITag.INamedTag<Item> CHARGERS = createTag("chargers");
        public static final ITag.INamedTag<Item> DUALIES = createTag("dualies");
        //public static final ITag.INamedTag<Item> SPLATLINGS = createTag("splatlings");
        //public static final ITag.INamedTag<Item> BRELLAS = createTag("brellas");

        public static final ITag.INamedTag<Item> MAIN_WEAPONS = createTag("main_weapons");
        //public static final ITag.INamedTag<Item> SUB_WEAPONS = createTag("sub_weapons");
        //public static final ITag.INamedTag<Item> SPECIAL_WEAPONS = createTag("special_weapons");
        public static final ITag.INamedTag<Item> INK_TANKS = createTag("ink_tanks");

        public static final ITag.INamedTag<Item> FILTERS = createTag("filters");
        public static final ITag.INamedTag<Item> REMOTES = createTag("remotes");

        public static void putInkTankTags(InkTankItem tank, String name)
        {
            if (!INK_TANK_WHITELIST.containsKey(tank))
            {
                INK_TANK_WHITELIST.put(tank, createTag(name + "_whitelist"));
            }
            if (!INK_TANK_BLACKLIST.containsKey(tank))
            {
                INK_TANK_BLACKLIST.put(tank, createTag(name + "_blacklist"));
            }
        }

        public static ITag<Item> getTag(ResourceLocation location)
        {
            return ItemTags.getCollection().get(location);
        }

        private static ITag.INamedTag<Item> createTag(String name)
        {
            return ItemTags.makeWrapperTag(new ResourceLocation(Splatcraft.MODID, name).toString());
        }
    }

    public static class Blocks
    {
        public static final ITag<Block> UNINKABLE_BLOCKS = createTag("inkproof");
        public static final ITag<Block> BLOCKS_INK = createTag("blocks_ink");
        public static final ITag<Block> INKABLE_BLOCKS = createTag("inkable_transparents");
        public static final ITag<Block> INK_CLEARING_BLOCKS = createTag("clears_ink");

        public static final ITag<Block> INKED_BLOCKS = createTag("inked_blocks");
        public static final ITag<Block> SCAN_TURF_IGNORED = createTag("scan_turf_ignored");
        public static final ITag<Block> SCAN_TURF_SCORED = createTag("scan_turf_scored");

        private static ITag<Block> createTag(String name)
        {
            return BlockTags.makeWrapperTag(new ResourceLocation(Splatcraft.MODID, name).toString());
        }
    }


    public static class InkColors
    {
        public static ITag<InkColor> STARTER_COLORS = createTag("starter_colors");

        private static ITag<InkColor> createTag(String name)
        {
            return ForgeTagHandler.makeWrapperTag(new ResourceLocation(Splatcraft.MODID, "ink_colors"), new ResourceLocation(Splatcraft.MODID, name));
        }


    }


}
