package com.cibernet.splatcraft.entities;

import com.cibernet.splatcraft.util.InkDamageUtils;

public interface IColoredEntity
{
    int getColor();

    void setColor(int color);

    default boolean onEntityInked(InkDamageUtils.InkDamageSource source, float damage, int color)
    {
        return false;
    }
}
