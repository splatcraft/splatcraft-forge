package com.cibernet.splatcraft.entities;

import com.cibernet.splatcraft.util.InkDamageUtils;
import net.minecraft.util.DamageSource;

public interface IColoredEntity
{
	int getColor();
	void setColor(int color);
	
	default boolean onEntityInked(InkDamageUtils.InkDamageSource source, float damage)
	{
		return false;
	}
}
