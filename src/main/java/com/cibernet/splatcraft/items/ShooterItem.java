package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.registries.SplatcraftItemGroups;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import net.minecraft.item.Item;

public class ShooterItem extends Item
{
	public ShooterItem()
	{
		super(new Properties().maxStackSize(1).group(SplatcraftItemGroups.GROUP_WEAPONS));
	}
}
