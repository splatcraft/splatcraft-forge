package com.cibernet.splatcraft.util;

import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.TreeMap;

public class InkColor extends ForgeRegistryEntry<InkColor> implements Comparable<InkColor>
{
	private final int hexCode;
	private final String name;
	private final DyeColor dyeColor;
	private final int ID;
	
	private static int idIndex = 0;
	
	private static final TreeMap<Integer, InkColor> colorMap = new TreeMap<>();
	
	public InkColor(String name, int color, @Nullable DyeColor dyeColor)
	{
		hexCode = color;
		this.name = name;
		this.dyeColor = dyeColor;
		
		ID = idIndex++;
		colorMap.put(color, this);
		setRegistryName(name);
	}
	
	public static InkColor getByHex(int hexCode)
	{
		return colorMap.get(hexCode);
	}
	
	public InkColor(String name, int color)
	{
		this(name, color, null);
	}
	
	public String getLocalizedName()
	{
		return (I18n.format(getUnlocalizedName()));
	}
	
	public String getUnlocalizedName() { return "ink_color." + getRegistryName().getNamespace() + "." + getRegistryName().getPath();}
	
	public String getHexCode() {return String.format("%06X", hexCode);}
	
	public int getColor() {return hexCode;}
	public @Nullable DyeColor getDyeColor() {return dyeColor;}
	public String getName() {return name;}
	
	@Override
	public String toString()
	{
		return name + ": #" + getHexCode().toUpperCase();
	}
	
	@Override
	public int compareTo(InkColor other)
	{
		return ID - other.ID;
	}
	
	public static class DummyType extends InkColor
	{
		public DummyType()
		{
			super("dummy", ColorUtils.DEFAULT);
		}
	}
}
