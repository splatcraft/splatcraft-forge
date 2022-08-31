package net.splatcraft.forge.entities;

import net.splatcraft.forge.util.InkDamageUtils;

public interface IColoredEntity
{
    int getColor();

    void setColor(int color);

    default boolean onEntityInked(InkDamageUtils.InkDamageSource source, float damage, int color)
    {
        return false;
    }
}
