package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.tileentities.TileEntityColor;
import com.cibernet.splatcraft.utils.InkColors;
import com.cibernet.splatcraft.tileentities.TileEntityInkedBlock;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import com.cibernet.splatcraft.world.save.SplatCraftGamerules;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockInked extends BlockInkColor implements IInked
{
	private boolean isGlittery;
	
	public BlockInked(String registryName, boolean isGlittery)
	{
		super(Material.CLAY);
		setUnlocalizedName("inkedBlock");
		setRegistryName(registryName);
		setTickRandomly(true);
		
		if(isGlittery)
			this.setLightLevel(0.4f);
		this.isGlittery = isGlittery;
		
	}
	
	public BlockInked()
	{
		this("inked_block", false);
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
	
	
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
	{
		if(worldIn.getTileEntity(pos) instanceof TileEntityInkedBlock)
		{
			TileEntityInkedBlock te = (TileEntityInkedBlock) worldIn.getTileEntity(pos);
			IBlockState savedState = te.getSavedState();
			if(savedState.getBlock() == this)
				return ItemStack.EMPTY;
			return savedState.getBlock().getItem(worldIn, pos, state);
		}
		return ItemStack.EMPTY;
	}
	
	@Override
	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
	{
		if(worldIn.getTileEntity(pos) instanceof TileEntityInkedBlock)
		{
			TileEntityInkedBlock te = (TileEntityInkedBlock) worldIn.getTileEntity(pos);
			IBlockState savedState = te.getSavedState();
			if(savedState.getBlock() == this)
				super.dropBlockAsItemWithChance(worldIn, pos, savedState, chance, fortune);
			savedState.getBlock().dropBlockAsItemWithChance(worldIn, pos, savedState, chance, fortune);
		}
		else super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
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
		try {return te.getSavedState().getBlock().getExplosionResistance(worldIn, pos, exploder, explosion); }
		catch(Exception e) { return 0; }
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack)
	{
		
		if(te instanceof TileEntityInkedBlock)
		{
			state = ((TileEntityInkedBlock) te).getSavedState();
			Block savedBlock = state.getBlock();
			
			player.addStat(StatList.getBlockStats(this));
			player.addExhaustion(0.005F);
			
			
			if(savedBlock.canSilkHarvest(worldIn, pos, state, player) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0)
			{
				java.util.List<ItemStack> items = new java.util.ArrayList<ItemStack>();
				ItemStack itemstack = SplatCraftUtils.getSilkTouchDropFromBlock(savedBlock, state);
				
				if(!itemstack.isEmpty())
				{
					items.add(itemstack);
				}
				
				net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos, state, 0, 1.0f, true, player);
				for(ItemStack item : items)
				{
					spawnAsEntity(worldIn, pos, item);
				}
			} else
			{
				harvesters.set(player);
				int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
				savedBlock.dropBlockAsItem(worldIn, pos, state, i);
				harvesters.set(null);
			}
		}
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
	public boolean clearInk(World worldIn, BlockPos pos)
	{
		if(worldIn.getTileEntity(pos) instanceof TileEntityInkedBlock)
		{
			TileEntityInkedBlock te = (TileEntityInkedBlock) worldIn.getTileEntity(pos);
			worldIn.setBlockState(pos, te.getSavedState(), 3);
			if(te.getSavedState().getBlock() instanceof BlockInkedWool)
				worldIn.setTileEntity(pos, ((TileEntityColor) te.getSavedState().getBlock().createTileEntity(worldIn, te.getSavedState())).setColor(te.getSavedColor()));
		}
		else worldIn.setBlockState(pos, Blocks.SAND.getDefaultState(), 3);
		
		return true;
	}
	
	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
	{
		//if (!this.tryTouchWater(worldIn, pos, state))
		{
			super.onBlockAdded(worldIn, pos, state);
		}
	}

	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}
	
	
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer()
	{
		return isGlittery ? BlockRenderLayer.TRANSLUCENT : BlockRenderLayer.SOLID;
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileEntityInkedBlock();
	}
}
