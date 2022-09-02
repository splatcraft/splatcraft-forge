package net.splatcraft.forge.registries;

import net.splatcraft.forge.Splatcraft;
import net.minecraft.stats.IStatFormatter;
import net.minecraft.stats.Stats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class SplatcraftStats
{
    public static final ResourceLocation TURF_WARS_WON = register("turf_wars_won", IStatFormatter.DEFAULT);

    public static final ResourceLocation BLOCKS_INKED = register("blocks_inked", IStatFormatter.DEFAULT);
    public static final ResourceLocation WEAPONS_CRAFTED = register("weapons_crafted", IStatFormatter.DEFAULT);
    public static final ResourceLocation INKWELLS_CRAFTED = register("inkwells_crafted", IStatFormatter.DEFAULT);
    public static final ResourceLocation SQUID_TIME = register("squid_time", IStatFormatter.TIME);

    public static void register()
    {
    }

    private static ResourceLocation register(String key, IStatFormatter formatter)
    {
        ResourceLocation resourcelocation = new ResourceLocation(Splatcraft.MODID, key);
        Registry.register(Registry.CUSTOM_STAT, new ResourceLocation(Splatcraft.MODID, key), resourcelocation);
        Stats.CUSTOM.get(resourcelocation, formatter);
        return resourcelocation;
    }
}
