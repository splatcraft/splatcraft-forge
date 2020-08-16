package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import com.cibernet.splatcraft.registries.SplatcraftGameRules;
import com.cibernet.splatcraft.registries.SplatcraftTileEntitites;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.tileentities.InkedBlockTileEntity;
import com.cibernet.splatcraft.util.InkBlockUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.Random;

public class InkedSlabBlock extends SlabBlock implements IColoredBlock
{
	public InkedSlabBlock(String name)
	{
		super( Properties.create(Material.CLAY, MaterialColor.BLACK_TERRACOTTA).tickRandomly().harvestTool(ToolType.PICKAXE).setRequiresTool());
		SplatcraftBlocks.inkColoredBlocks.add(this);
		setRegistryName(name);
	}
	
	@Override
	public float getPlayerRelativeBlockHardness(BlockState state, PlayerEntity player, IBlockReader worldIn, BlockPos pos)
	{
		if(!(worldIn.getTileEntity(pos) instanceof InkedBlockTileEntity))
			return super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
		InkedBlockTileEntity te = (InkedBlockTileEntity) worldIn.getTileEntity(pos);
		
		if(te.getSavedState().getBlock() instanceof InkedBlock)
			return super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
		
		
		return te.getSavedState().getBlock().getPlayerRelativeBlockHardness(te.getSavedState(), player, worldIn, pos);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}
	
	@Override
	public float getJumpFactor()
	{
		
		return super.getJumpFactor();
	}
	
	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random rand)
	{
		if(world.getGameRules().getBoolean(SplatcraftGameRules.INK_DECAY) && world.getTileEntity(pos) instanceof InkedBlockTileEntity)
		{
			clearInk(world, pos);
		}
	}
	
	
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
	{
		if(InkedBlock.isTouchingLiquid(worldIn, currentPos))
		{
			if(worldIn.getTileEntity(currentPos) instanceof InkedBlockTileEntity)
				return clearInk(worldIn, currentPos);
		}
		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}
	
	private static BlockState clearInk(IWorld world, BlockPos pos)
	{
		InkedBlockTileEntity te = (InkedBlockTileEntity) world.getTileEntity(pos);
		if(te.hasSavedState())
		{
			world.setBlockState(pos, te.getSavedState(), 3);
			
			if(te.hasSavedColor() && te.getSavedState().getBlock() instanceof IColoredBlock)
			{
				((World)world).setTileEntity(pos, te.getSavedState().getBlock().createTileEntity(te.getSavedState(), world));
				if(world.getTileEntity(pos) instanceof InkColorTileEntity)
				{
					InkColorTileEntity newte = (InkColorTileEntity) world.getTileEntity(pos);
					newte.setColor(te.getSavedColor());
				}
			}
			
			return te.getSavedState();
		}
		
		return world.getBlockState(pos);
	}
	
	
	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return SplatcraftTileEntitites.inkedTileEntity.get().create();
	}
	
	@Override
	public boolean canClimb()
	{
		return true;
	}
	
	@Override
	public boolean canSwim()
	{
		return true;
	}
	
	@Override
	public boolean canDamage()
	{
		return true;
	}
	
	@Override
	public int getColor(World world, BlockPos pos)
	{
		if(world.getTileEntity(pos) instanceof InkColorTileEntity)
			return ((InkColorTileEntity) world.getTileEntity(pos)).getColor();
		return -1;
	}
	
	@Override
	public boolean remoteColorChange(World world, BlockPos pos, int newColor)
	{
		
		return false;
	}
	
	@Override
	public boolean remoteInkClear(World world, BlockPos pos)
	{
		BlockState oldState = world.getBlockState(pos);
		if(world.getTileEntity(pos) instanceof InkedBlockTileEntity)
			return !clearInk(world, pos).equals(oldState);
		return false;
	}
	
	@Override
	public boolean countsTowardsTurf(World world, BlockPos pos)
	{
		return true;
	}
	
	@Override
	public boolean inkBlock(World world, BlockPos pos, int color, float damage, InkBlockUtils.InkType inkType)
	{
		if(!(world.getTileEntity(pos) instanceof InkedBlockTileEntity))
			return false;
		
		InkedBlockTileEntity te = (InkedBlockTileEntity) world.getTileEntity(pos);
		BlockState state = world.getBlockState(pos);
		
		if(te.getColor() == color)
			return false;
		te.setColor(color);
		world.notifyBlockUpdate(pos, state, state, 2);
		return true;
	}
}
