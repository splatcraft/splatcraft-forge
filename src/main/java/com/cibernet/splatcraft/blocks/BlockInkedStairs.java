package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.tileentities.TileEntityInkedBlock;
import com.cibernet.splatcraft.utils.InkColors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockInkedStairs extends BlockStairs implements IInked
{
	public BlockInkedStairs()
	{
		super(SplatCraftBlocks.inkedBlock.getDefaultState());
		setUnlocalizedName("inkedStairs");
		setRegistryName("inked_stairs");
		setTickRandomly(true);
		BlockInkColor.blocks.add(this);
	}
	
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
	{
		return ItemStack.EMPTY;
	}
	
	@Override
	public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		if(!(worldIn.getTileEntity(pos) instanceof TileEntityInkedBlock))
			return super.getMapColor(state, worldIn, pos);
		
		TileEntityInkedBlock te = (TileEntityInkedBlock) worldIn.getTileEntity(pos);
		InkColors color = InkColors.getByColor(te.getColor());
		if(color == null)
			return super.getMapColor(state, worldIn, pos);
		else return color.getMapColor();
		
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
	{
		if(world.getTileEntity(pos) instanceof TileEntityInkedBlock)
		{
			TileEntityInkedBlock te = (TileEntityInkedBlock) world.getTileEntity(pos);
			IBlockState savedState = te.getSavedState();
			if(savedState.getBlock() == this)
				return super.getPickBlock(state, target, world, pos, player);
			return savedState.getBlock().getPickBlock(savedState, target, world, pos, player);
		}
		return super.getPickBlock(state, target, world, pos, player);
	}
	
	@Override
	public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos)
	{
		if(!(worldIn.getTileEntity(pos) instanceof TileEntityInkedBlock))
			return super.getBlockHardness(blockState, worldIn, pos);
		
		TileEntityInkedBlock te = (TileEntityInkedBlock) worldIn.getTileEntity(pos);
		
		if(te.getSavedState().getBlock() instanceof BlockInkedSlab)
			return blockHardness;
		
		return te.getSavedState().getBlock().getBlockHardness(te.getSavedState(), worldIn, pos);
	}
	
	@Override
	public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World worldIn, BlockPos pos)
	{
		if(!(worldIn.getTileEntity(pos) instanceof TileEntityInkedBlock))
			return super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
		TileEntityInkedBlock te = (TileEntityInkedBlock) worldIn.getTileEntity(pos);
		return te.getSavedState().getBlock().getPlayerRelativeBlockHardness(te.getSavedState(), player, worldIn, pos);
	}
	
	@Override
	public float getExplosionResistance(World worldIn, BlockPos pos, @Nullable Entity exploder, Explosion explosion)
	{
		
		if(!(worldIn.getTileEntity(pos) instanceof TileEntityInkedBlock))
			return super.getExplosionResistance(worldIn, pos, exploder, explosion);
		TileEntityInkedBlock te = (TileEntityInkedBlock) worldIn.getTileEntity(pos);
		return te.getSavedState().getBlock().getExplosionResistance(worldIn, pos, exploder, explosion);
	}
	
	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack)
	{
		if(!(worldIn.getTileEntity(pos) instanceof TileEntityInkedBlock))
			super.harvestBlock(worldIn, player, pos, state, te, stack);
		else ((TileEntityInkedBlock)te).getSavedState().getBlock().harvestBlock(worldIn, player, pos, state, te, stack);
	}
	
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		clearInk(worldIn, pos);
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
	{
		if(worldIn.getBlockState(fromPos).getMaterial().equals(Material.WATER))
			clearInk(worldIn, pos);
	}
	
	@Override
	public void clearInk(World worldIn, BlockPos pos)
	{
		IBlockState state = worldIn.getBlockState(pos);
		if(worldIn.getTileEntity(pos) instanceof TileEntityInkedBlock)
		{
			TileEntityInkedBlock te = (TileEntityInkedBlock) worldIn.getTileEntity(pos);
			worldIn.setBlockState(pos, te.getSavedState().withProperty(HALF, state.getValue(HALF)).withProperty(SHAPE, state.getValue(SHAPE)).withProperty(FACING, state.getValue(FACING)), 3);
		} else worldIn.setBlockState(pos, Blocks.SAND.getDefaultState(), 3);
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileEntityInkedBlock();
	}
	
	@Override
	public boolean canInk()
	{
		return true;
	}
	
	@Override
	public boolean canDamage()
	{
		return true;
	}
	
	@Override
	public boolean canSwim()
	{
		return true;
	}
	
	@Override
	public boolean canClimb()
	{
		return true;
	}
	
	@Override
	public boolean countsTowardsScore()
	{
		return true;
	}
}
