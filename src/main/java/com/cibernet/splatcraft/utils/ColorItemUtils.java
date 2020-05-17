package com.cibernet.splatcraft.utils;

import com.cibernet.splatcraft.utils.InkColors;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.management.AttributeList;
import java.util.ArrayList;

public class ColorItemUtils
{
	public static ArrayList<Item> inkColorItems = new ArrayList<>();
	
	public static int getInkColor(ItemStack stack)
	{
		if(!stack.hasTagCompound() || !stack.getTagCompound().hasKey("color"))
			return InkColors.DYE_WHITE.getColor();
		return stack.getTagCompound().getInteger("color");
	}
	public static boolean isColorLocked(ItemStack stack)
	{
		if(!stack.hasTagCompound() || !stack.getTagCompound().hasKey("colorLocked"))
			return false;
		return stack.getTagCompound().getBoolean("colorLocked");
	}
	
	public static NBTTagCompound checkTagCompound(ItemStack stack) {
		NBTTagCompound tagCompound = stack.getTagCompound();
		if (tagCompound == null) {
			tagCompound = new NBTTagCompound();
			stack.setTagCompound(tagCompound);
		}
		
		return tagCompound;
	}
	
	public static ItemStack setInkColor(ItemStack stack, int color)
	{
		checkTagCompound(stack).setInteger("color", color);
		return stack;
	}
	public static ItemStack setColorLocked(ItemStack stack, boolean colorLocked)
	{
		checkTagCompound(stack).setBoolean("colorLocked", colorLocked);
		return stack;
	}
	
	public static boolean hasInkColor(ItemStack stack)
	{
		return stack.getTagCompound() != null && stack.getTagCompound().hasKey("color");
	}
}
