package com.cibernet.splatcraft.utils;

import com.cibernet.splatcraft.items.ItemWeaponBase;
import com.cibernet.splatcraft.registries.SplatCraftItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class TabSplatCraft extends CreativeTabs
{
    public static final CreativeTabs main = new TabSplatCraft("tabSplatcraft");

    public TabSplatCraft(String label) {
        super(label);
    }

    @Override
    public ItemStack getTabIconItem() {
        return ColorItemUtils.setInkColor(new ItemStack(SplatCraftItems.splattershot), InkColors.ORANGE.getColor());
    }
}
