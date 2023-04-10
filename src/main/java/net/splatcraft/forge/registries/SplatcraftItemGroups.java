package net.splatcraft.forge.registries;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.splatcraft.forge.util.ColorUtils;

import static net.splatcraft.forge.registries.SplatcraftItems.sardiniumBlock;
import static net.splatcraft.forge.registries.SplatcraftItems.splattershot;

public class SplatcraftItemGroups
{
    public static final CreativeModeTab GROUP_GENERAL = new CreativeModeTab("splatcraft_general")
    {
        @Override
        public ItemStack makeIcon()
        {
            return new ItemStack(sardiniumBlock.get());
        }
    };

    public static final CreativeModeTab GROUP_WEAPONS = new CreativeModeTab("splatcraft_weapons")
    {
        @Override
        public ItemStack makeIcon()
        {
            return ColorUtils.setInkColor(new ItemStack(splattershot.get()), ColorUtils.ORANGE);
        }
    };
}
