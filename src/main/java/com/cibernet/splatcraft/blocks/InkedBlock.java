package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.crafting.InkColor;
import com.cibernet.splatcraft.crafting.InkColorManager;
import com.cibernet.splatcraft.handlers.client.ColorHandler;
import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import com.cibernet.splatcraft.registries.SplatcraftTileEntitites;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.tileentities.InkedBlockTileEntity;
import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.Random;

public class InkedBlock extends Block implements IColoredBlock
{
	public InkedBlock()
	{
		super(Properties.create(Material.CLAY).tickRandomly());
		SplatcraftBlocks.inkColoredBlocks.add(this);
	}
	
	@Override
	public float getPlayerRelativeBlockHardness(BlockState state, PlayerEntity player, IBlockReader worldIn, BlockPos pos)
	{
		if(!(worldIn.getTileEntity(pos) instanceof InkedBlockTileEntity))
			return super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
		InkedBlockTileEntity te = (InkedBlockTileEntity) worldIn.getTileEntity(pos);
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
		if(world.getTileEntity(pos) instanceof InkedBlockTileEntity)
		{
			InkedBlockTileEntity te = (InkedBlockTileEntity) world.getTileEntity(pos);
			if(te.hasSavedState())
				world.setBlockState(pos, te.getSavedState(), 3);
		}
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
}
