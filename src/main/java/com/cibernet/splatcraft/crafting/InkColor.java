package com.cibernet.splatcraft.crafting;

import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;

import javax.annotation.Nullable;

public class InkColor
{
	private final int hexCode;
	private final String name;
	private final String displayName;
	private final DyeColor dyeColor;
	
	public InkColor(String registryName, String displayName, int color, @Nullable DyeColor dyeColor)
	{
		hexCode = color;
		name = registryName;
		this.dyeColor = dyeColor;
		this.displayName = displayName;
	}
	
	public String getLocalizedName()
	{
		return (!I18n.hasKey(getUnlocalizedName()) && !displayName.isEmpty()) ? displayName : I18n.format(getUnlocalizedName());
	}
	
	public String getUnlocalizedName() { return "ink_color."+name;}
	
	public String getHexCode() {return String.format("%06X", hexCode);}
	
	public int getColor() {return hexCode;}
	public @Nullable DyeColor getDyeColor() {return dyeColor;}
	public String getName() {return name;}
	
	@Override
	public String toString()
	{
		return name + ": #" + getHexCode().toUpperCase();
	}
}
