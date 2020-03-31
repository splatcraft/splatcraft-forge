package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.gui.SplatCraftGuiHandler;
import com.cibernet.splatcraft.utils.TabSplatCraft;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockWeaponStation extends BlockHorizontal
{
	public BlockWeaponStation()
	{
		super(Material.ROCK);
		setUnlocalizedName("weaponStation");
		setRegistryName("weapon_station");
		setCreativeTab(TabSplatCraft.main);
		setHarvestLevel("pickaxe", 0);
		setHardness(3.0F);
		
	}
	
	
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	@Override
	public boolean isTopSolid(IBlockState state)
	{
		return true;
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(!worldIn.isRemote)
			playerIn.openGui(SplatCraft.instance, SplatCraftGuiHandler.WEAPON_STATION_GUI, worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}
	
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, FACING);
	}
	
	
	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(FACING).getHorizontalIndex();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta % 4));
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		return super.getActualState(state, worldIn, pos);
	}
	
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getStateFromMeta(meta).withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}
	
}
