package com.cibernet.splatcraft.utils;

import net.minecraft.block.material.MapColor;
import net.minecraft.item.EnumDyeColor;

public enum InkColors
{
	ORANGE(0xe85407, MapColor.getBlockColor(EnumDyeColor.ORANGE), "orange"),
	BLUE(0x2e0cb5, MapColor.getBlockColor(EnumDyeColor.BLUE), "blue"),
	PINK(0xc83d79, MapColor.getBlockColor(EnumDyeColor.PINK), "pink"),
	GREEN(0x409d3b, MapColor.getBlockColor(EnumDyeColor.GREEN), "green"),

	MOJANG(0xDF242F, MapColor.RED_STAINED_HARDENED_CLAY, "mojangRed", false),
	INK_BLACK(0x1F1F2D, MapColor.CYAN_STAINED_HARDENED_CLAY, "dyeBlack", false),
	;

	InkColors(int color, MapColor mapColor, String displayName, boolean obtainable)
	{
		this.color = color;
		this.mapColor = mapColor;
		this.name = displayName;
		this.obtainable = obtainable;
	}

	InkColors(int color, MapColor mapColor, String name)
	{
		this(color, mapColor, name, true);
	}

	private final int color;
	private final MapColor mapColor;

	private final String name;
	private final boolean obtainable;

	public int getColor()
	{
		return color;
	}
	public boolean isObtainable() {return obtainable;}

	public static InkColors getByColor(int color)
	{
		for(int i = 0; i < InkColors.values().length; i++)
			if(InkColors.values()[i].getColor() == color)
				return InkColors.values()[i];

		return null;
	}

	public MapColor getMapColor()
	{
		return mapColor;
	}
	public String getName() {return name;}
}
