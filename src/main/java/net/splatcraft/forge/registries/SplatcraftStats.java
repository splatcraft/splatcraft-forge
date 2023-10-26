package net.splatcraft.forge.registries;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.criteriaTriggers.ChangeInkColorTrigger;
import net.splatcraft.forge.criteriaTriggers.CraftWeaponTrigger;
import net.splatcraft.forge.criteriaTriggers.FallIntoInkTrigger;
import net.splatcraft.forge.criteriaTriggers.ScanTurfTrigger;

public class SplatcraftStats
{
    public static final ResourceLocation TURF_WARS_WON = register("turf_wars_won", StatFormatter.DEFAULT);

    public static final ResourceLocation BLOCKS_INKED = register("blocks_inked", StatFormatter.DEFAULT);
    public static final ResourceLocation WEAPONS_CRAFTED = register("weapons_crafted", StatFormatter.DEFAULT);
    public static final ResourceLocation INKWELLS_CRAFTED = register("inkwells_crafted", StatFormatter.DEFAULT);
    public static final ResourceLocation SQUID_TIME = register("squid_time", StatFormatter.TIME);

    public static final CraftWeaponTrigger CRAFT_WEAPON_TRIGGER = CriteriaTriggers.register(new CraftWeaponTrigger());
    public static final ChangeInkColorTrigger CHANGE_INK_COLOR_TRIGGER = CriteriaTriggers.register(new ChangeInkColorTrigger());
    public static final ScanTurfTrigger SCAN_TURF_TRIGGER = CriteriaTriggers.register(new ScanTurfTrigger());
    public static final FallIntoInkTrigger FALL_INTO_INK_TRIGGER = CriteriaTriggers.register(new FallIntoInkTrigger());

    public static void register()
    {
    }

    private static ResourceLocation register(String key, StatFormatter formatter)
    {
        ResourceLocation resourcelocation = new ResourceLocation(Splatcraft.MODID, key);
        Registry.register(Registry.CUSTOM_STAT, new ResourceLocation(Splatcraft.MODID, key), resourcelocation);
        Stats.CUSTOM.get(resourcelocation, formatter);
        return resourcelocation;
    }
}
