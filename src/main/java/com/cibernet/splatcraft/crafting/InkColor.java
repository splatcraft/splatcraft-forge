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
	private final MaterialColor materialColor;
	private final DyeColor dyeColor;
	
	public InkColor(String registryName, int color, int mapColor, @Nullable DyeColor dyeColor)
	{
		hexCode = color;
		name = registryName;
		materialColor = MaterialColor.COLORS[mapColor];
		this.dyeColor = dyeColor;
	}
	
	public String getLocalizedName()
	{
		return I18n.format(getUnlocalizedName());
	}
	
	public String getUnlocalizedName() { return "ink_color."+name;}
	
	public String getHexCode() {return String.format("%06X", hexCode);}
	
	public int getColor() {return hexCode;}
	public MaterialColor getMaterialColor() {return materialColor;}
	public @Nullable DyeColor getDyeColor() {return dyeColor;}
	public String getName() {return name;}
	
	@Override
	public String toString()
	{
		return name + ": #" + getHexCode().toUpperCase();
	}
}
