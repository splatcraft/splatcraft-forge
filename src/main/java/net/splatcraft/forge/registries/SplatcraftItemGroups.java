package net.splatcraft.forge.registries;

import net.splatcraft.forge.util.ColorUtils;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import static net.splatcraft.forge.registries.SplatcraftItems.sardiniumBlock;
import static net.splatcraft.forge.registries.SplatcraftItems.splattershot;

public class SplatcraftItemGroups
{
    public static final ItemGroup GROUP_GENERAL = new ItemGroup("splatcraft_general")
    {
        @Override
        public ItemStack makeIcon()
        {
            return new ItemStack(sardiniumBlock);
        }
    };

    public static final ItemGroup GROUP_WEAPONS = new ItemGroup("splatcraft_weapons")
    {
        @Override
        public ItemStack makeIcon()
        {
            return ColorUtils.setInkColor(new ItemStack(splattershot), ColorUtils.ORANGE);
        }
    };
}
