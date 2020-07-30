package com.cibernet.splatcraft.blocks;

import net.minecraft.block.AbstractGlassBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.GlassBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class EmptyInkwellBlock extends AbstractGlassBlock
{
	private static final VoxelShape SHAPE = VoxelShapes.or(
			makeCuboidShape(0, 0, 0, 16, 12, 16),
			makeCuboidShape(1, 12, 1, 14/16f, 13, 14),
			makeCuboidShape(0, 13, 0, 16, 16, 16));
	
	
	public EmptyInkwellBlock(Properties properties)
	{
		super(properties.notSolid());
	}
	
	@Override
	public boolean isTransparent(BlockState state)
	{
		return true;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
		return SHAPE;
	}
}
