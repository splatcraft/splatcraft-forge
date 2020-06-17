package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.utils.SplatCraftUtils;
import com.cibernet.splatcraft.utils.TabSplatCraft;
import com.cibernet.splatcraft.world.save.SplatCraftPlayerData;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockObserver;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class BlockBarrierBar extends BlockHorizontal
{
	public static final PropertyEnum<BarShape> SHAPE = PropertyEnum.create("shape", BarShape.class);
	
	protected static final AxisAlignedBB STRAIGHT_AABB = new AxisAlignedBB(0,13/16f,13/16f,1,1,1);
	protected static final AxisAlignedBB EDGE_AABB = new AxisAlignedBB(0,13/16f,13/16f,3/16f,1,1);
	protected static final AxisAlignedBB ROTATED_STRAIGHT_AABB = modifyAABBForDirection(EnumFacing.EAST, STRAIGHT_AABB);
	protected static final AxisAlignedBB TOP_AABB = new AxisAlignedBB(0,13/16f,0,1,1,1);
	
	public BlockBarrierBar(String unlocName, String registryName)
	{
		super(Material.IRON, MapColor.AIR);
		setUnlocalizedName(unlocName);
		setRegistryName(registryName);
		setHardness(3.0f);
		setCreativeTab(TabSplatCraft.main);
		setDefaultState(getDefaultState().withProperty(SHAPE, BarShape.STRAIGHT));
	}
	
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, SHAPE, FACING);
	}
	
	@Override
	public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side)
	{
		return side.ordinal() > 1;
	}
	
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState)
	{
		if(entityIn instanceof EntityPlayer && SplatCraftPlayerData.getIsSquid((EntityPlayer) entityIn) && SplatCraftUtils.canSquidClimb(worldIn, (EntityPlayer) entityIn))
		{
			addCollisionBoxToList(pos, entityBox, collidingBoxes, TOP_AABB);
			return;
		}
		
		if (!isActualState)
		{
			state = this.getActualState(state, worldIn, pos);
		}
		
		for (AxisAlignedBB axisalignedbb : getCollisionBoxList(state))
		{
			addCollisionBoxToList(pos, entityBox, collidingBoxes, axisalignedbb);
		}
	}
	
	private static List<AxisAlignedBB> getCollisionBoxList(IBlockState state)
	{
		List<AxisAlignedBB> list = Lists.<AxisAlignedBB>newArrayList();
		EnumFacing facing = state.getValue(FACING);
		
		BarShape shape = state.getValue(SHAPE);
		
		if(shape != BarShape.INNER_LEFT && shape != BarShape.INNER_RIGHT)
			list.add(modifyAABBForDirection(facing, STRAIGHT_AABB));
		switch(shape)
		{
			case INNER_LEFT:
				list.add(modifyAABBForDirection(facing.rotateYCCW(), EDGE_AABB));
				break;
			case INNER_RIGHT:
				list.add(modifyAABBForDirection(facing, EDGE_AABB));
				break;
			case OUTER_LEFT:
				list.add(modifyAABBForDirection(facing.getOpposite(), ROTATED_STRAIGHT_AABB));
				break;
			case OUTER_RIGHT:
				list.add(modifyAABBForDirection(facing, ROTATED_STRAIGHT_AABB));
				break;
			
		}
		
		return list;
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		EnumFacing facing = state.getValue(FACING);
		
		BarShape shape = getActualState(state, source, pos).getValue(SHAPE);
		
		switch(shape)
		{
			case STRAIGHT:
				return modifyAABBForDirection(facing, STRAIGHT_AABB);
			case INNER_LEFT:
				return modifyAABBForDirection(facing.rotateYCCW(), EDGE_AABB);
			case INNER_RIGHT:
				return modifyAABBForDirection(facing, EDGE_AABB);
			case OUTER_LEFT:
				return TOP_AABB;
			case OUTER_RIGHT:
				return TOP_AABB;
			
		}
		
		return super.getBoundingBox(state, source, pos);
	}
	
	public static AxisAlignedBB modifyAABBForDirection(EnumFacing facing, AxisAlignedBB bb)
	{
		switch(facing)
		{
			case EAST:
				return new AxisAlignedBB(1 - bb.maxZ, bb.minY, bb.minX, 1 - bb.minZ, bb.maxY, bb.maxX);
			case SOUTH:
				return new AxisAlignedBB(1 - bb.maxX, bb.minY, 1- bb.maxZ, 1 - bb.minX, bb.maxY, 1 - bb.minZ);
			case WEST:
				return new AxisAlignedBB(bb.minZ, bb.minY, 1 - bb.maxX, bb.maxZ, bb.maxY, 1 - bb.minX);
		}
		return bb;
	}
	
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		IBlockState iblockstate = super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
		iblockstate = iblockstate.withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(SHAPE, BarShape.STRAIGHT);
		return iblockstate;
	}
	
	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	public IBlockState getStateFromMeta(int meta)
	{
		IBlockState iblockstate = this.getDefaultState();
		iblockstate = iblockstate.withProperty(FACING, EnumFacing.getFront(5 - (meta & 3)));
		return iblockstate;
	}
	
	/**
	 * Convert the BlockState into the correct metadata value
	 */
	public int getMetaFromState(IBlockState state)
	{
		int i = 0;
		
		/*
		if (state.getValue(HALF) == BlockStairs.EnumHalf.TOP)
		{
			i |= 4;
		}
		*/
		
		i = i | 5 - ((EnumFacing)state.getValue(FACING)).getIndex();
		return i;
	}
	
	/**
	 * Get the actual Block state of this Block at the given position. This applies properties not visible in the
	 * metadata, such as fence connections.
	 */
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		return state.withProperty(SHAPE, getStairsShape(state, worldIn, pos));
	}
	
	private static BarShape getStairsShape(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
		IBlockState iblockstate = world.getBlockState(pos.offset(enumfacing));
		
		if (isBlockStairs(iblockstate))
		{
			EnumFacing enumfacing1 = (EnumFacing)iblockstate.getValue(FACING);
			
			if (enumfacing1.getAxis() != ((EnumFacing)state.getValue(FACING)).getAxis() && isDifferentStairs(state, world, pos, enumfacing1.getOpposite()))
			{
				if (enumfacing1 == enumfacing.rotateYCCW())
				{
					return BarShape.OUTER_LEFT;
				}
				
				return BarShape.OUTER_RIGHT;
			}
		}
		
		IBlockState iblockstate1 = world.getBlockState(pos.offset(enumfacing.getOpposite()));
		
		if (isBlockStairs(iblockstate1))
		{
			EnumFacing enumfacing2 = (EnumFacing)iblockstate1.getValue(FACING);
			
			if (enumfacing2.getAxis() != ((EnumFacing)state.getValue(FACING)).getAxis() && isDifferentStairs(state, world, pos, enumfacing2))
			{
				if (enumfacing2 == enumfacing.rotateYCCW())
				{
					return BarShape.INNER_LEFT;
				}
				
				return BarShape.INNER_RIGHT;
			}
		}
		
		return BarShape.STRAIGHT;
	}
	
	private static boolean isDifferentStairs(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing facing)
	{
		IBlockState iblockstate = world.getBlockState(pos.offset(facing));
		return !isBlockStairs(iblockstate) || iblockstate.getValue(FACING) != state.getValue(FACING);
	}
	
	public static boolean isBlockStairs(IBlockState state)
	{
		return state.getBlock() instanceof BlockBarrierBar;
	}
	
	
	public static enum BarShape implements IStringSerializable
	{
		STRAIGHT("straight"),
		INNER_LEFT("inner_left"),
		INNER_RIGHT("inner_right"),
		OUTER_LEFT("outer_left"),
		OUTER_RIGHT("outer_right");
		
		private final String name;
		
		private BarShape(String name) {this.name = name;}
		
		@Override
		public String getName()
		{
			return name;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
	
	
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}
	@Override
	public boolean isTranslucent(IBlockState state) { return true; }
	
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
	{
		return BlockFaceShape.UNDEFINED;
	}
}
