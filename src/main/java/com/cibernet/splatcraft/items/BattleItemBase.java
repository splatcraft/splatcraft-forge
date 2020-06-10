package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.utils.TabSplatCraft;
import net.minecraft.item.Item;

public class BattleItemBase extends Item implements IBattleItem
{
	public BattleItemBase(String unlocName, String registryName, boolean displayInCreativeTab)
	{
		setUnlocalizedName(unlocName);
		setRegistryName(registryName);
		if(displayInCreativeTab)
			setCreativeTab(TabSplatCraft.main);
	}
}
