package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.capabilities.PlayerInfoCapability;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public abstract class AbstractSquidPassthroughBlock extends Block
{
	
	public AbstractSquidPassthroughBlock(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
		boolean isSquid = false;
		
		if(context.getEntity() instanceof LivingEntity)
			isSquid = PlayerInfoCapability.isSquid((LivingEntity) context.getEntity());
		
		return isSquid ? VoxelShapes.empty() : super.getCollisionShape(state, worldIn, pos, context);
	}
}
