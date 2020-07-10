package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import static com.cibernet.splatcraft.registries.SplatcraftItems.*;

public class SplatcraftItemGroups
{
	public static final ItemGroup GROUP_GENERAL = new ItemGroup("splatcraftGeneral")
	{
		@Override
		public ItemStack createIcon()
		{
			return new ItemStack(sardiniumBlock.get());
		}
	};
	
	public static final ItemGroup GROUP_WEAPONS = new ItemGroup("splatcraftWeapons")
	{
		@Override
		public ItemStack createIcon()
		{
			return ColorUtils.setInkColor(new ItemStack(splattershot.get()), ColorUtils.ORANGE);
		}
	};
}
