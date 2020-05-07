package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.utils.TabSplatCraft;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockGrate extends BlockSquidPassable
{
	public static final PropertyEnum<BlockTrapDoor.DoorHalf> HALF = PropertyEnum.<BlockTrapDoor.DoorHalf>create("half", BlockTrapDoor.DoorHalf.class);
	
	protected static final AxisAlignedBB BOTTOM_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.1875D, 1.0D);
	protected static final AxisAlignedBB TOP_AABB = new AxisAlignedBB(0.0D, 0.8125D, 0.0D, 1.0D, 1.0D, 1.0D);
	
	public BlockGrate(String unlocName, String registryName)
	{
		super(unlocName, registryName, Material.IRON);
		setDefaultState(getDefaultState().withProperty(HALF, BlockTrapDoor.DoorHalf.BOTTOM));
		setCreativeTab(TabSplatCraft.main);
	}
	
	
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		IBlockState iblockstate = this.getDefaultState();
		
		if (facing.getAxis().isHorizontal())
		{
			iblockstate = iblockstate.withProperty(HALF, hitY > 0.5F ? BlockTrapDoor.DoorHalf.TOP : BlockTrapDoor.DoorHalf.BOTTOM);
		}
		else
		{
			iblockstate = iblockstate.withProperty(HALF, facing == EnumFacing.UP ? BlockTrapDoor.DoorHalf.BOTTOM : BlockTrapDoor.DoorHalf.TOP);
		}
		
		return iblockstate;
	}
	
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {HALF});
	}
	
	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(HALF) == BlockTrapDoor.DoorHalf.TOP ? 1 : 0;
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(HALF, meta % 2 == 1 ? BlockTrapDoor.DoorHalf.TOP : BlockTrapDoor.DoorHalf.BOTTOM);
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return state.getValue(HALF) == BlockTrapDoor.DoorHalf.TOP ? TOP_AABB : BOTTOM_AABB;
	}
	
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
	{
		return (face == EnumFacing.UP && state.getValue(HALF) == BlockTrapDoor.DoorHalf.TOP || face == EnumFacing.DOWN && state.getValue(HALF) == BlockTrapDoor.DoorHalf.BOTTOM) ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
	}
	
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.TRANSLUCENT;
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
	
}
