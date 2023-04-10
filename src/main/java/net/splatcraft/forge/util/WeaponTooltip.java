package net.splatcraft.forge.util;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class WeaponTooltip {
    private final String name;
    private final IStatValueGetter valueGetter;

    public WeaponTooltip(String name, IStatValueGetter valueGetter) {
        this.name = name;
        this.valueGetter = valueGetter;
    }

    public int getStatValue(ItemStack stack, @Nullable Level level) {
        return valueGetter.get(stack, level);
    }

    public MutableComponent getTextComponent(ItemStack stack, Level level)
    {
        return new TranslatableComponent("weaponStat." + name, getStatValue(stack, level));
    }

    public interface IStatValueGetter
    {
        int get(ItemStack stack, @Nullable Level level);
    }
}
