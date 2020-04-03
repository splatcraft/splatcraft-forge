package com.cibernet.splatcraft.utils;

import net.minecraft.block.material.MapColor;
import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.common.util.EnumHelper;

import java.util.ArrayList;
import java.util.List;

public enum InkColors
{
	ORANGE(0xe85407, MapColor.getBlockColor(EnumDyeColor.ORANGE), "orange"),
	BLUE(0x2e0cb5, MapColor.getBlockColor(EnumDyeColor.BLUE), "blue"),
	PINK(0xc83d79, MapColor.getBlockColor(EnumDyeColor.PINK), "pink"),
	GREEN(0x409d3b, MapColor.getBlockColor(EnumDyeColor.GREEN), "green"),
	LIGHT_BLUE(0x228cff, MapColor.getBlockColor(EnumDyeColor.LIGHT_BLUE), "lightBlue"),
	TURQUOISE(0x048188, MapColor.getBlockColor(EnumDyeColor.CYAN), "turquoise"),
	YELLOW(0xe1a307, MapColor.getBlockColor(EnumDyeColor.YELLOW), "yellow"),
	LILAC(0x4d24a3, MapColor.getBlockColor(EnumDyeColor.PURPLE), "lilac"),
	LEMON(0x91b00b, MapColor.FOLIAGE, "lemon"),
	PLUM(0x830b9c, MapColor.MAGENTA, "plum"),

	CYAN(0x4ACBCB, MapColor.DIAMOND, "cyan"),
	TANGERINE(0xEA8546, MapColor.ORANGE_STAINED_HARDENED_CLAY, "peach"),
	MINT(0x08B672, MapColor.LIGHT_BLUE, "mint"),
	CHERRY(0xE24F65, MapColor.PINK_STAINED_HARDENED_CLAY, "cherry"),

	NEON_PINK(0xcf0466, MapColor.getBlockColor(EnumDyeColor.PINK), "neonPink"),
	NEON_GREEN(0x17a80d, MapColor.getBlockColor(EnumDyeColor.GREEN), "neonGreen"),

	MOJANG(0xDF242F, MapColor.RED_STAINED_HARDENED_CLAY, "mojangRed"),
	COBALT(0x005682, MapColor.getBlockColor(EnumDyeColor.CYAN), "cobalt"),
	ICEARSTORM(0x88ffc1, MapColor.LIGHT_BLUE, "icearstorm"),
	INK_BLACK(0x1F1F2D, MapColor.CYAN_STAINED_HARDENED_CLAY, "default"),
	
	DYE_WHITE(0xFAFAFA, MapColor.getBlockColor(EnumDyeColor.WHITE), "dyeWhite"),
	DYE_ORANGE(16351261, MapColor.getBlockColor(EnumDyeColor.ORANGE), "dyeOrange"),
	DYE_MAGENTA(13061821, MapColor.getBlockColor(EnumDyeColor.MAGENTA), "dyeMagenta"),
	DYE_LIGHT_BLUE(3847130, MapColor.getBlockColor(EnumDyeColor.LIGHT_BLUE), "dyeLightBlue"),
	DYE_YELLOW(16701501, MapColor.getBlockColor(EnumDyeColor.YELLOW), "dyeYellow"),
	DYE_LIME(8439583, MapColor.getBlockColor(EnumDyeColor.LIME), "dyeLime"),
	DYE_PINK(15961002, MapColor.getBlockColor(EnumDyeColor.PINK), "dyePink"),
	DYE_GRAY(4673362, MapColor.getBlockColor(EnumDyeColor.GRAY), "dyeGray"),
	DYE_SILVER(10329495, MapColor.getBlockColor(EnumDyeColor.SILVER), "dyeSilver"),
	DYE_CYAN(1481884, MapColor.getBlockColor(EnumDyeColor.CYAN), "dyeCyan"),
	DYE_PURPLE(8991416, MapColor.getBlockColor(EnumDyeColor.PURPLE), "dyePurple"),
	DYE_BLUE(3949738, MapColor.getBlockColor(EnumDyeColor.BLUE), "dyeBlue"),
	DYE_BROWN(8606770, MapColor.getBlockColor(EnumDyeColor.BROWN), "dyeBrown"),
	DYE_GREEN(6192150, MapColor.getBlockColor(EnumDyeColor.GREEN), "dyeGreen"),
	DYE_RED(11546150, MapColor.getBlockColor(EnumDyeColor.RED), "dyeRed"),
	DYE_BLACK(1908001, MapColor.getBlockColor(EnumDyeColor.BLACK), "dyeBlack"),
	;

	InkColors(Integer color, MapColor mapColor, String displayName)
	{
		this.color = color;
		this.mapColor = mapColor;
		this.name = displayName;
	}

	public static InkColors addColor(String name, int color, MapColor mapColor, String unlocalizedName)
	{
		return EnumHelper.addEnum(InkColors.class, name, new Class[] {Integer.class, MapColor.class, String.class}, color, mapColor, unlocalizedName);
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
