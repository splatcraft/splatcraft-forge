package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.handlers.client.ColorHandler;
import com.cibernet.splatcraft.registries.SplatcraftTileEntitites;
import com.cibernet.splatcraft.tileentities.InkedBlockTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

public class InkedBlock extends Block
{
	public InkedBlock()
	{
		super(Properties.create(Material.CLAY).tickRandomly());
		ColorHandler.inkColoredBlocks.add(this);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
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
}
