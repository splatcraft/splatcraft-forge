package com.cibernet.splatcraft.util;

import com.cibernet.splatcraft.blocks.AbstractSquidPassthroughBlock;
import com.cibernet.splatcraft.blocks.IColoredBlock;
import com.cibernet.splatcraft.blocks.InkedBlock;
import com.cibernet.splatcraft.data.tags.SplatcraftTags;
import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.tileentities.InkedBlockTileEntity;
import net.minecraft.block.*;
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
		
		if(state.getBlock() instanceof IColoredBlock)
			return ((IColoredBlock) state.getBlock()).inkBlock(world, pos, color, inkType);
			
		if(!canInk(world, pos))
			return false;
		
		world.setBlockState(pos, SplatcraftBlocks.inkedBlock.getDefaultState(), 3);
		world.setTileEntity(pos, SplatcraftBlocks.inkedBlock.createTileEntity(SplatcraftBlocks.inkedBlock.getDefaultState(), world));
		InkedBlockTileEntity inkte = (InkedBlockTileEntity) world.getTileEntity(pos);
		if(inkte == null)
			return false;
		inkte.setColor(color);
		inkte.setSavedState(state);
		return true;
	}
	
	public static boolean canInk(World world, BlockPos pos)
	{
		Block block = world.getBlockState(pos).getBlock();
		
		if(SplatcraftTags.Blocks.UNINKABLE_BLOCKS.contains(block))
			return false;
		
		if(block instanceof StairsBlock || block instanceof SlabBlock || block instanceof BarrierBlock)
			return true;
		
		if(world.getTileEntity(pos) != null)
			return false;
		
		
		if(SplatcraftTags.Blocks.INKABLE_BLOCKS.contains(block))
			return true;
		
		if(canInkPassthrough(world, pos))
			return false;
		
		if(!world.getBlockState(pos).isOpaqueCube(world, pos))
			return false;
		
		if(block.isTransparent(world.getBlockState(pos)))
			return false;
			
		return true;
	}
	
	public static boolean canInkPassthrough(World world, BlockPos pos)
	{
		BlockState state = world.getBlockState(pos);
		
		if(state.getBlock() instanceof AbstractSquidPassthroughBlock)
			return true;
		
		if(state.getCollisionShape(world, pos).isEmpty())
			return true;
		
		return false;
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