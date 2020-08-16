package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.util.InkBlockUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IColoredBlock
{
	boolean canClimb();
	boolean canSwim();
	boolean canDamage();
	default int getColor(World world, BlockPos pos)
	{
		if(world.getTileEntity(pos) instanceof InkColorTileEntity)
			return ((InkColorTileEntity) world.getTileEntity(pos)).getColor();
		return -1;
	}
	
	boolean remoteColorChange(World world, BlockPos pos, int newColor);
	boolean remoteInkClear(World world, BlockPos pos);
	boolean countsTowardsTurf(World world, BlockPos pos);
	
	default boolean inkBlock(World world, BlockPos pos, int color, float damage, InkBlockUtils.InkType inkType)
	{
		return false;
	}
}
