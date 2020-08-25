package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.registries.SplatcraftItemGroups;
import com.google.common.collect.Lists;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class FilterItem extends Item
{
	protected final boolean isGlowing;
	protected final boolean isOmni;
	
	public static final ArrayList<FilterItem> filters = Lists.newArrayList();
	
	public FilterItem(String name, boolean isGlowing, boolean isOmni)
	{
		super(new Properties().group(SplatcraftItemGroups.GROUP_GENERAL).maxStackSize(1));
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
	public boolean hasEffect(ItemStack stack)
	{
		return isGlowing;
	}
	
	public boolean isOmni()
	{
		return isOmni;
	}
}
