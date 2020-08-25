package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import com.cibernet.splatcraft.registries.SplatcraftTileEntitites;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.tileentities.InkedBlockTileEntity;
import com.cibernet.splatcraft.util.InkBlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class CanvasBlock extends Block implements IColoredBlock
{
	
	public static final BooleanProperty INKED = BooleanProperty.create("inked");
	
	public CanvasBlock(String name)
	{
		super(Properties.create(Material.WOOL).hardnessAndResistance(0.8f).sound(SoundType.CLOTH));
		SplatcraftBlocks.inkColoredBlocks.add(this);
		setRegistryName(name);
		setDefaultState(getDefaultState().with(INKED, false));
	}
	
	
	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		InkColorTileEntity te = SplatcraftTileEntitites.colorTileEntity.create();
		te.setColor(-1);
		return te;
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(INKED);
	}
	
	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
	{
		int color = getColor((World) worldIn, currentPos);
		
		if(InkedBlock.isTouchingLiquid(worldIn, currentPos))
		{
			if(worldIn.getTileEntity(currentPos) instanceof InkColorTileEntity)
			{
				((InkColorTileEntity) worldIn.getTileEntity(currentPos)).setColor(-1);
			}
		}
		
		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos).with(INKED, color != -1);
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
	
	@Override
	public boolean inkBlock(World world, BlockPos pos, int color, float damage, InkBlockUtils.InkType inkType)
	{
		if(InkedBlock.isTouchingLiquid(world, pos))
			return false;
		
		if(color == getColor(world, pos))
			return false;
		
		if(world.getTileEntity(pos) instanceof InkColorTileEntity)
		{
			BlockState state = world.getBlockState(pos);
			((InkColorTileEntity) world.getTileEntity(pos)).setColor(color);
			world.notifyBlockUpdate(pos, state, state.with(INKED, true), 2);
			return true;
		}
		
		return false;
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
		return false;
	}
	
	@Override
	public boolean remoteColorChange(World world, BlockPos pos, int newColor)
	{
		BlockState state = world.getBlockState(pos);
		if(world.getTileEntity(pos) instanceof InkColorTileEntity && ((InkColorTileEntity) world.getTileEntity(pos)).getColor() != newColor)
		{
			((InkColorTileEntity) world.getTileEntity(pos)).setColor(newColor);
			world.setBlockState(pos, state.with(INKED, true), 2);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean remoteInkClear(World world, BlockPos pos)
	{
		return false;
	}
	
	@Override
	public boolean countsTowardsTurf(World world, BlockPos pos)
	{
		return false;
	}
}
