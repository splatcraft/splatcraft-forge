package com.cibernet.splatcraft.crafting;

import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;

public class InkColor
{
	private final int hexCode;
	private final String name;
	private final Item filter;
	private final MaterialColor materialColor;
	
	public InkColor(String registryName, int color, int mapColor, Item filter)
	{
		hexCode = color;
		name = registryName;
		materialColor = MaterialColor.COLORS[mapColor];
		this.filter = filter;
	}
	
	public String getLocalizedName()
	{
		return I18n.format(getUnlocalizedName());
	}
	
	public String getUnlocalizedName() { return "ink_color."+name;}
	
	public String getHexCode() {return String.format("%06X", hexCode);}
	
	public int getColor() {return hexCode;}
	public MaterialColor getMaterialColor() {return materialColor;}
	public Item getFilter() {return filter;}
	public String getName() {return name;}
	
	@Override
	public String toString()
	{
		return name + ": #" + getHexCode().toUpperCase();
	}
}
