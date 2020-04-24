package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.tileentities.TileEntityColor;
import com.cibernet.splatcraft.tileentities.TileEntityInkedBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IInked
{
	boolean canInk();
	boolean canDamage();
	boolean canSwim();
	boolean canClimb();
	boolean countsTowardsScore();
	
	default boolean clearInk(World worldIn, BlockPos pos) {return false;}
	
	default int getColor(World worldIn, BlockPos pos)
	{
		TileEntity te = worldIn.getTileEntity(pos);
		return (te instanceof TileEntityColor) ? ((TileEntityColor) te).getColor() : -1;
	}
	
	
	static boolean tryTouchWater(World worldIn, BlockPos pos, IBlockState state)
	{
		boolean touchingWater = touchingWater(worldIn, pos);
		if (touchingWater && worldIn.getBlockState(pos).getBlock() instanceof IInked)
		{
			((IInked) worldIn.getBlockState(pos).getBlock()).clearInk(worldIn, pos);
		}
		
		return touchingWater;
	}
	
	static boolean touchingWater(World worldIn, BlockPos pos)
	{
		boolean touchingWater = false;
		
		for (EnumFacing enumfacing : EnumFacing.values())
		{
			if (enumfacing != EnumFacing.DOWN)
			{
				BlockPos blockpos = pos.offset(enumfacing);
				
				if (worldIn.getBlockState(blockpos).getMaterial() == Material.WATER)
				{
					touchingWater = true;
					break;
				}
			}
		}
		return touchingWater;
	}
}
