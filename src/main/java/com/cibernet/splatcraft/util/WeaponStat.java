package com.cibernet.splatcraft.util;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class WeaponStat
{
    private final String name;
    private final IStatValueGetter valueGetter;

    public WeaponStat(String name, IStatValueGetter valueGetter)
    {
        this.name = name;
        this.valueGetter = valueGetter;
    }

    public int getStatValue(ItemStack stack, @Nullable World level)
    {
        return valueGetter.get(stack, level);
    }

    public TextComponent getTextComponent(ItemStack stack, World level)
    {
        return new TranslationTextComponent("weaponStat." + name, getStatValue(stack, level));
    }

    public interface IStatValueGetter
    {
        int get(ItemStack stack, @Nullable World level);
    }
}
