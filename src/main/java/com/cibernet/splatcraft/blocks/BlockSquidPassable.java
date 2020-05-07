package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.world.save.SplatCraftPlayerData;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BlockSquidPassable extends Block
{
	public BlockSquidPassable(String unlocName, String registryName, Material blockMaterialIn)
	{
		super(blockMaterialIn);
		setUnlocalizedName(unlocName);
		setRegistryName(registryName);
	}
	
	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState)
	{
		if(entityIn != null && entityIn instanceof EntityPlayer && entityIn.getRecursivePassengersByType(EntityPlayer.class).isEmpty())
		{
			if(SplatCraftPlayerData.getIsSquid((EntityPlayer) entityIn))
				return;
		}
		
		super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
	}
}
