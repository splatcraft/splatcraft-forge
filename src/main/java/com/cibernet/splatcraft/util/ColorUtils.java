package com.cibernet.splatcraft.util;

import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ColorUtils
{
	public static int getInkColor(ItemStack stack)
	{
		CompoundNBT nbt = stack.getTag();
		
		if(nbt == null || !nbt.contains("Color"))
			return -1;
		
		return nbt.getInt("Color");
	}
	
	public static ItemStack setInkColor(ItemStack stack, int color)
	{
		stack.getOrCreateTag().putInt("Color", color);
		return stack;
	}
	
	public static int getInkColor(TileEntity te)
	{
		if(!(te instanceof InkColorTileEntity))
			return -1;
		
		return ((InkColorTileEntity) te).getColor();
	}
	
	public static boolean setInkColor(TileEntity te, int color)
	{
		if(!(te instanceof InkColorTileEntity))
			return false;
		
		((InkColorTileEntity) te).setColor(color);
		return true;
	}
}
