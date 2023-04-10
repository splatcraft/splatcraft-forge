package net.splatcraft.forge.data;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.tags.ITag;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.items.InkTankItem;
import net.splatcraft.forge.util.InkColor;

import java.util.HashMap;

public class SplatcraftTags
{
    public static void register()
    {
        new Items();
        new Blocks();
        new InkColors();
        new EntityTypes();
    }

    public static class Items
    {
        public static final HashMap<InkTankItem, TagKey<Item>> INK_TANK_WHITELIST = new HashMap<>();
        public static final HashMap<InkTankItem, TagKey<Item>> INK_TANK_BLACKLIST = new HashMap<>();

        public static final TagKey<Item> MATCH_ITEMS = createTag("match_items");
        public static final TagKey<Item> REVEALS_BARRIERS = createTag("reveals_barriers");

        public static final TagKey<Item> SHOOTERS = createTag("shooters");
        public static final TagKey<Item> ROLLERS = createTag("rollers");
        public static final TagKey<Item> CHARGERS = createTag("chargers");
        public static final TagKey<Item> DUALIES = createTag("dualies");
        //public static final TagKey<Item> SPLATLINGS = createTag("splatlings");
        //public static final TagKey<Item> BRELLAS = createTag("brellas");

        public static final TagKey<Item> MAIN_WEAPONS = createTag("main_weapons");
        public static final TagKey<Item> SUB_WEAPONS = createTag("sub_weapons");
        //public static final TagKey<Item> SPECIAL_WEAPONS = createTag("special_weapons");
        public static final TagKey<Item> INK_TANKS = createTag("ink_tanks");
        public static final TagKey<Item> INK_BANDS = createTag("ink_bands");

        public static final TagKey<Item> FILTERS = createTag("filters");
        public static final TagKey<Item> REMOTES = createTag("remotes");

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

        private static TagKey<Item> createTag(String name)
        {
            return ItemTags.create(new ResourceLocation(Splatcraft.MODID, name));
        }
    }

    public static class Blocks
    {
        public static final TagKey<Block> UNINKABLE_BLOCKS = createTag("inkproof");
        public static final TagKey<Block> BLOCKS_INK = createTag("deters_ink");
        public static final TagKey<Block> INK_CLEARING_BLOCKS = createTag("clears_ink");

        public static final TagKey<Block> INK_PASSTHROUGH = createTag("ink_passthrough");
        public static final TagKey<Block> SQUID_PASSTHROUGH = createTag("squid_passthrough");

        public static final TagKey<Block> INKED_BLOCKS = createTag("inked_blocks");
        public static final TagKey<Block> SCAN_TURF_IGNORED = createTag("scan_turf_ignored");
        public static final TagKey<Block> SCAN_TURF_SCORED = createTag("scan_turf_scored");

        private static TagKey<Block> createTag(String name)
        {
            return BlockTags.create(new ResourceLocation(Splatcraft.MODID, name));
        }
    }


    public static class EntityTypes
    {
        public static final TagKey<EntityType<?>> BYPASSES_SPAWN_SHIELD = createTag("bypasses_spawn_shield");
        public static final TagKey<EntityType<?>> SUB_WEAPONS = createTag("sub_weapons");

        private static TagKey<EntityType<?>> createTag(String name)
        {
            return TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(Splatcraft.MODID, name));
        }
    }


    public static class InkColors
    {
        public static ITag<InkColor> STARTER_COLORS = createTag("starter_colors");

        private static ITag<InkColor> createTag(String name)
        {
            return null;
            //return ForgeTagHandler.makeWrapperTag(new ResourceLocation(Splatcraft.MODID, "ink_colors"), new ResourceLocation(Splatcraft.MODID, name)); TODO
        }


    }


}
