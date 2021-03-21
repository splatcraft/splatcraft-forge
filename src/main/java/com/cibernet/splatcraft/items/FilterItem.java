package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.registries.SplatcraftItemGroups;
import com.google.common.collect.Lists;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FilterItem extends Item {
    protected final boolean isGlowing;
    protected final boolean isOmni;

    public static final ArrayList<FilterItem> filters = Lists.newArrayList();

    public FilterItem(String name, boolean isGlowing, boolean isOmni) {
        super(new Properties().group(SplatcraftItemGroups.GROUP_GENERAL).maxStackSize(1));
        setRegistryName(name);

        this.isGlowing = isGlowing;
        this.isOmni = isOmni;

        filters.add(this);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag isAdvanced) {
        super.addInformation(stack, world, tooltip, isAdvanced);
        tooltip.add(new TranslationTextComponent("item.splatcraft.filter.tooltip").mergeStyle(TextFormatting.GRAY));
    }

    public FilterItem(String name) {
        this(name, false, false);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return isGlowing;
    }

    public boolean isOmni() {
        return isOmni;
    }
}
