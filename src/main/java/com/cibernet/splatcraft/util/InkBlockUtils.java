package com.cibernet.splatcraft.util;

import com.cibernet.splatcraft.blocks.InkedBlock;
import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import com.cibernet.splatcraft.tileentities.InkedBlockTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InkBlockUtils
{
	
	public static boolean inkBlock(World world, BlockPos pos, int color, InkType inkType)
	{
		BlockState state = world.getBlockState(pos);
		TileEntity te = world.getTileEntity(pos);
		
		if(te instanceof InkedBlockTileEntity)
		{
			if(((InkedBlockTileEntity) te).getColor() == color)
				return false;
			((InkedBlockTileEntity) te).setColor(color);
			return true;
		}
		else if(te != null) return false;
		
		world.setBlockState(pos, SplatcraftBlocks.inkedBlock.getDefaultState(), 3);
		world.setTileEntity(pos, SplatcraftBlocks.inkedBlock.createTileEntity(SplatcraftBlocks.inkedBlock.getDefaultState(), world));
		InkedBlockTileEntity inkte = (InkedBlockTileEntity) world.getTileEntity(pos);
		inkte.setColor(color);
		inkte.setSavedState(state);
		return true;
	}
	
	public enum InkType
	{
		NORMAL,
		GLOWING
	}
}
