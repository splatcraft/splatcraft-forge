package com.cibernet.splatcraft.utils;

import net.minecraft.block.material.MapColor;
import net.minecraft.item.EnumDyeColor;

import java.util.ArrayList;
import java.util.List;

public enum InkColors
{
	ORANGE(0xe85407, MapColor.getBlockColor(EnumDyeColor.ORANGE), "orange"),
	BLUE(0x2e0cb5, MapColor.getBlockColor(EnumDyeColor.BLUE), "blue"),
	PINK(0xc83d79, MapColor.getBlockColor(EnumDyeColor.PINK), "pink"),
	GREEN(0x409d3b, MapColor.getBlockColor(EnumDyeColor.GREEN), "green"),

	MOJANG(0xDF242F, MapColor.RED_STAINED_HARDENED_CLAY, "mojangRed"),
	INK_BLACK(0x1F1F2D, MapColor.CYAN_STAINED_HARDENED_CLAY, "dyeBlack"),
	DEFAULT_WHITE(0xFAFAFA, MapColor.getBlockColor(EnumDyeColor.WHITE), "defaultWhite"),
	COBALT(0x005682, MapColor.getBlockColor(EnumDyeColor.CYAN), "cobalt"),
	;

	InkColors(int color, MapColor mapColor, String displayName)
	{
		this.color = color;
		this.mapColor = mapColor;
		this.name = displayName;
	}

	public static final List<InkColors> creativeTabColors = new ArrayList<InkColors>() {{add(ORANGE); add(BLUE); add(GREEN); add(PINK); }};
	public static final List<InkColors> mainSelectionColors = new ArrayList<InkColors>() {{add(ORANGE); add(BLUE); add(GREEN); add(PINK); }};

	private final int color;
	private final MapColor mapColor;
	private final String name;

	public int getColor()
	{
		return color;
	}

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
