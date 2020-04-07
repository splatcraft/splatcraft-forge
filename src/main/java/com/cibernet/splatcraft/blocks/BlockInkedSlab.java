package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.tileentities.TileEntityInkedBlock;
import com.cibernet.splatcraft.utils.InkColors;
import com.cibernet.splatcraft.world.save.SplatCraftGamerules;
import com.cibernet.splatcraft.world.save.SplatCraftPlayerData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPurpurSlab;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockInkedSlab extends BlockSlab implements IInked
{
	public static final PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);
	
	public BlockInkedSlab()
	{
		super(Material.CLAY);
		setRegistryName("inked_slab");
		setUnlocalizedName("inkedSlab");
		setTickRandomly(true);
		BlockInkColor.blocks.add(this);
	}
	
	@Override
	public boolean isDouble()
	{
		return false;
	}
	
	/**
	 * Returns the slab block name with the type associated with it
	 */
	public String getUnlocalizedName(int meta)
	{
		return super.getUnlocalizedName();
	}
	
	public IProperty<?> getVariantProperty()
	{
		return VARIANT;
	}
	
	public Comparable<?> getTypeForItem(ItemStack stack)
	{
		return BlockPurpurSlab.Variant.DEFAULT;
	}
	
	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	public IBlockState getStateFromMeta(int meta)
	{
		IBlockState iblockstate = this.getDefaultState().withProperty(VARIANT, Variant.DEFAULT);
		
		if (!this.isDouble())
		{
			iblockstate = iblockstate.withProperty(HALF, (meta & 8) == 0 ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP);
		}
		
		return iblockstate;
	}
	
	/**
	 * Convert the BlockState into the correct metadata value
	 */
	public int getMetaFromState(IBlockState state)
	{
		int i = 0;
		
		if (!this.isDouble() && state.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP)
		{
			i |= 8;
		}
		
		return i;
	}
	
	protected BlockStateContainer createBlockState()
	{
		return this.isDouble() ? new BlockStateContainer(this, new IProperty[] {VARIANT}) : new BlockStateContainer(this, new IProperty[] {HALF, VARIANT});
	}
	
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
	{
		return ItemStack.EMPTY;
	}
	
	@Override
	public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		if(!(worldIn.getTileEntity(pos) instanceof TileEntityInkedBlock))
			return super.getMapColor(state, worldIn, pos);
		
		TileEntityInkedBlock te = (TileEntityInkedBlock) worldIn.getTileEntity(pos);
		InkColors color = InkColors.getByColor(te.getColor());
		if(color == null)
			return super.getMapColor(state, worldIn, pos);
		else return color.getMapColor();
		
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
	{
		if(world.getTileEntity(pos) instanceof TileEntityInkedBlock)
		{
			TileEntityInkedBlock te = (TileEntityInkedBlock) world.getTileEntity(pos);
			IBlockState savedState = te.getSavedState();
			if(savedState.getBlock() == this)
				return super.getPickBlock(state, target, world, pos, player);
			return savedState.getBlock().getPickBlock(savedState, target, world, pos, player);
		}
		return super.getPickBlock(state, target, world, pos, player);
	}
	
	@Override
	public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos)
	{
		if(!(worldIn.getTileEntity(pos) instanceof TileEntityInkedBlock))
			return super.getBlockHardness(blockState, worldIn, pos);
		
		TileEntityInkedBlock te = (TileEntityInkedBlock) worldIn.getTileEntity(pos);
		
		if(te.getSavedState().getBlock() instanceof BlockInkedSlab)
			return blockHardness;
		
		return te.getSavedState().getBlock().getBlockHardness(te.getSavedState(), worldIn, pos);
	}
	
	@Override
	public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World worldIn, BlockPos pos)
	{
		if(!(worldIn.getTileEntity(pos) instanceof TileEntityInkedBlock))
			return super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
		TileEntityInkedBlock te = (TileEntityInkedBlock) worldIn.getTileEntity(pos);
		return te.getSavedState().getBlock().getPlayerRelativeBlockHardness(te.getSavedState(), player, worldIn, pos);
	}
	
	@Override
	public float getExplosionResistance(World worldIn, BlockPos pos, @Nullable Entity exploder, Explosion explosion)
	{
		
		if(!(worldIn.getTileEntity(pos) instanceof TileEntityInkedBlock))
			return super.getExplosionResistance(worldIn, pos, exploder, explosion);
		TileEntityInkedBlock te = (TileEntityInkedBlock) worldIn.getTileEntity(pos);
		return te.getSavedState().getBlock().getExplosionResistance(worldIn, pos, exploder, explosion);
	}
	
	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack)
	{
		if(!(worldIn.getTileEntity(pos) instanceof TileEntityInkedBlock))
			super.harvestBlock(worldIn, player, pos, state, te, stack);
		else ((TileEntityInkedBlock)te).getSavedState().getBlock().harvestBlock(worldIn, player, pos, state, te, stack);
	}
	
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		if(SplatCraftGamerules.getGameruleValue("inkDecay"))
			clearInk(worldIn, pos);
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
	{
		if(worldIn.getBlockState(fromPos).getMaterial().equals(Material.WATER))
			clearInk(worldIn, pos);
	}
	
	@Override
	public void clearInk(World worldIn, BlockPos pos)
	{
		IBlockState state = worldIn.getBlockState(pos);
		if(worldIn.getTileEntity(pos) instanceof TileEntityInkedBlock)
		{
			TileEntityInkedBlock te = (TileEntityInkedBlock) worldIn.getTileEntity(pos);
			worldIn.setBlockState(pos, te.getSavedState().withProperty(BlockSlab.HALF, state.getValue(BlockSlab.HALF)), 3);
		} else worldIn.setBlockState(pos, Blocks.SAND.getDefaultState(), 3);
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileEntityInkedBlock();
	}
	
	@Override
	public boolean canInk()
	{
		return true;
	}
	
	@Override
	public boolean canDamage()
	{
		return true;
	}
	
	@Override
	public boolean canSwim()
	{
		return true;
	}
	
	@Override
	public boolean canClimb()
	{
		return true;
	}
	
	@Override
	public boolean countsTowardsScore()
	{
		return true;
	}
	
	public static enum Variant implements IStringSerializable
	{
		DEFAULT;
		
		public String getName()
		{
			return "default";
		}
	}
}
