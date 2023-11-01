package net.splatcraft.forge.registries;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.splatcraft.forge.items.ColoredBlockItem;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.InkColor;

import java.util.ArrayList;

import static net.splatcraft.forge.registries.SplatcraftItems.*;

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

    public static final ArrayList<Item> colorTabItems = new ArrayList<>();

    public static final CreativeModeTab GROUP_COLORS = new CreativeModeTab("splatcraft_colors")
    {

        @Override
        public boolean hasSearchBar() {
            return true;
        }

        @Override
        public ItemStack makeIcon() {
            return ColorUtils.setInkColor(new ItemStack(inkwell.get()), ColorUtils.ORANGE);
        }

        @Override
        public void fillItemList(NonNullList<ItemStack> list)
        {
            for(Item item : colorTabItems)
            {
                for(InkColor color : SplatcraftInkColors.REGISTRY.get().getValues().stream().sorted().toList())
                    list.add(ColorUtils.setColorLocked(ColorUtils.setInkColor(new ItemStack(item), color.getColor()), true));
                if(!(item instanceof ColoredBlockItem coloredBlockItem) || coloredBlockItem.matchesColor())
                    list.add(ColorUtils.setInverted(new ItemStack(item), true));
            }
        }
    }.setBackgroundImage(new ResourceLocation("textures/gui/container/creative_inventory/tab_item_search.png"));
}
