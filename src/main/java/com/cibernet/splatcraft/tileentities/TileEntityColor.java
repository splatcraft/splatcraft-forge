package com.cibernet.splatcraft.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityColor extends TileEntity
{
	
	private int color = 0x000FFF;
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		
		if(compound.hasKey("color"))
			color = compound.getInteger("color");
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		compound.setInteger("color", color);
		return super.writeToNBT(compound);
	}
	
	
	public TileEntityColor setColor(int color)
	{
		this.color = color;
		return this;
	}
	
	public int getColor() {return color;}
}
