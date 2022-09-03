package net.splatcraft.forge.items;

import com.google.common.collect.Lists;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.splatcraft.forge.registries.SplatcraftItemGroups;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FilterItem extends Item
{
    public static final ArrayList<FilterItem> filters = Lists.newArrayList();
    protected final boolean isGlowing;
    protected final boolean isOmni;

    public FilterItem(String name, boolean isGlowing, boolean isOmni)
    {
        super(new Properties().tab(SplatcraftItemGroups.GROUP_GENERAL).stacksTo(1));
        setRegistryName(name);

        this.isGlowing = isGlowing;
        this.isOmni = isOmni;

        filters.add(this);
    }

    public FilterItem(String name)
    {
        this(name, false, false);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World level, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag isAdvanced)
    {
        super.appendHoverText(stack, level, tooltip, isAdvanced);
        tooltip.add(new TranslationTextComponent("item.splatcraft.filter.tooltip").withStyle(TextFormatting.GRAY));
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack)
    {
        return isGlowing;
    }

    public boolean isOmni()
    {
        return isOmni;
    }
}
