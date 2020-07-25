package com.cibernet.splatcraft.util;

import com.cibernet.splatcraft.blocks.IColoredBlock;
import com.cibernet.splatcraft.blocks.InkedBlock;
import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.tileentities.InkedBlockTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
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
			world.notifyBlockUpdate(pos, state, state, 2);
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
	
	
	
	public static boolean canSquidHide(LivingEntity entity)
	{
		return canSquidSwim(entity) || canSquidClimb(entity);
	}
	
	public static boolean canSquidSwim(LivingEntity entity)
	{
		boolean canSwim = false;
		
		if(entity.world.getBlockState(entity.getPosition().down()).getBlock() instanceof IColoredBlock)
			canSwim = ((IColoredBlock) entity.world.getBlockState(entity.getPosition().down()).getBlock()).canSwim();
		
		if(canSwim)
			return ColorUtils.colorEquals(entity, entity.world.getTileEntity(entity.getPosition().down()));
		return false;
	}
	
	public static boolean onEnemyInk(LivingEntity entity)
	{
		boolean canDamage = false;
		
		if(entity.world.getBlockState(entity.getPosition().down()).getBlock() instanceof IColoredBlock)
			canDamage = ((IColoredBlock) entity.world.getBlockState(entity.getPosition().down()).getBlock()).canDamage();
		
		return canDamage && ColorUtils.getInkColor(entity.world.getTileEntity(entity.getPosition().down())) != -1 && !canSquidSwim(entity);
	}
	
	public static boolean canSquidClimb(LivingEntity entity)
	{
		if(onEnemyInk(entity))
			return false;
		for(int i = 0; i < 4; i++)
		{
			float xOff = (i < 2 ? .32f : 0) * (i % 2 == 0 ? 1 : -1), zOff = (i < 2 ? 0 : .32f) * (i % 2 == 0 ? 1 : -1);
			BlockPos pos = new BlockPos(entity.getPosX() - xOff, entity.getPosY(), entity.getPosZ() - zOff);
			Block block = entity.world.getBlockState(pos).getBlock();
			
			if((!(block instanceof IColoredBlock) || (block instanceof IColoredBlock && ((IColoredBlock) block).canClimb())) && entity.world.getTileEntity(pos) instanceof InkColorTileEntity &&
					(((InkColorTileEntity) entity.world.getTileEntity(pos)).getColor() == ColorUtils.getEntityColor(entity)) && !entity.isPassenger())
				return true;
		}
		return false;
	}
	
	public static InkBlockUtils.InkType getInkType(LivingEntity entity) //TODO
	{
		return InkBlockUtils.InkType.NORMAL;
	}
	
	public enum InkType
	{
		NORMAL,
		GLOWING
	}
}
