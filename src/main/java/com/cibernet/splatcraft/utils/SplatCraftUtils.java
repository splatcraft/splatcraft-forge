package com.cibernet.splatcraft.utils;

import com.cibernet.splatcraft.blocks.BlockInkColor;
import com.cibernet.splatcraft.blocks.BlockSquidPassable;
import com.cibernet.splatcraft.blocks.IInked;
import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.registries.SplatCraftStats;
import com.cibernet.splatcraft.tileentities.TileEntityColor;
import com.cibernet.splatcraft.tileentities.TileEntityInkedBlock;
import com.cibernet.splatcraft.tileentities.TileEntitySunkenCrate;
import com.cibernet.splatcraft.world.save.SplatCraftPlayerData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class SplatCraftUtils
{
	public static void setEntitySize(Entity entityIn, float width, float height)
	{
		if (width != entityIn.width || height != entityIn.height)
		{
			float f = entityIn.width;
			entityIn.width = width;
			entityIn.height = height;

			if (entityIn.width < f)
			{
				double d0 = (double)width / 2.0D;
				entityIn.setEntityBoundingBox(new AxisAlignedBB(entityIn.posX - d0, entityIn.posY, entityIn.posZ - d0, entityIn.posX + d0, entityIn.posY + (double)entityIn.height, entityIn.posZ + d0));
				return;
			}

			AxisAlignedBB axisalignedbb = entityIn.getEntityBoundingBox();
			entityIn.setEntityBoundingBox(new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + (double)entityIn.width, axisalignedbb.minY + (double)entityIn.height, axisalignedbb.minZ + (double)entityIn.width));


			if (entityIn.width > f  && !entityIn.world.isRemote)
			{
				entityIn.move(MoverType.SELF, (double)(f - entityIn.width), 0.0D, (double)(f - entityIn.width));
			}

		}
	}

	public static boolean canSquidHide(World worldIn, EntityPlayer playerIn)
	{
		BlockPos pos = new BlockPos(playerIn.posX, playerIn.posY-.1, playerIn.posZ);
		Block block = worldIn.getBlockState(pos).getBlock();
		
		if((!(block instanceof IInked) || (block instanceof IInked && ((IInked) block).canSwim())) && worldIn.getTileEntity(pos) instanceof TileEntityColor)
			return ((TileEntityColor)worldIn.getTileEntity(pos)).getColor() == SplatCraftPlayerData.getInkColor(playerIn) && !playerIn.isRiding();
		return SplatCraftUtils.canSquidClimb(worldIn, playerIn);
	}
	
	public static boolean canSquidClimb(World worldIn, EntityPlayer playerIn)
	{
		if(onEnemyInk(worldIn, playerIn))
			return false;
		for(int i = 0; i < 4; i++)
		{
			float xOff = (i < 2 ? .7f : 0) * (i % 2 == 0 ? 1 : -1), zOff = (i < 2 ? 0 : .7f) * (i % 2 == 0 ? 1 : -1);
			BlockPos pos = new BlockPos(playerIn.posX - xOff, playerIn.posY, playerIn.posZ - zOff);
			Block block = worldIn.getBlockState(pos).getBlock();
			
			if((!(block instanceof IInked) || (block instanceof IInked && ((IInked) block).canClimb())) && worldIn.getTileEntity(pos) instanceof TileEntityColor &&
					((TileEntityColor) worldIn.getTileEntity(pos)).getColor() == SplatCraftPlayerData.getInkColor(playerIn) && !playerIn.isRiding())
				return true;
		}
		return false;
	}
	
	public static boolean onEnemyInk(World worldIn, EntityPlayer playerIn)
	{
		BlockPos pos = new BlockPos(playerIn.posX, playerIn.posY-.1, playerIn.posZ);
		Block block = worldIn.getBlockState(pos).getBlock();
		
		if((!(block instanceof IInked) || (block instanceof IInked && ((IInked) block).canDamage())) && worldIn.getTileEntity(pos) instanceof TileEntityColor)
			return ((TileEntityColor)worldIn.getTileEntity(pos)).getColor() != SplatCraftPlayerData.getInkColor(playerIn) && !playerIn.isRiding();
		return false;
	}
	
	public static void createInkExplosion(World worldIn, Entity source, BlockPos pos, float radius, int color)
	{
		if (!worldIn.isRemote)
		{
			InkExplosion explosion = new InkExplosion(worldIn, source, pos.getX(), pos.getY(), pos.getZ(), radius, color, true);
			//explosion.explode();
			explosion.doExplosionA();
			explosion.doExplosionB(true);
			//boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(worldIn, this);
		}
	}

	public static void createInkExplosion(World worldIn, BlockPos pos, float radius, int color)
	{
		createInkExplosion(worldIn, null, pos, radius, color);
	}
	
	public static boolean playerInkBlock(EntityPlayer player, World worldIn, BlockPos pos, int color, float damage)
	{
		if(inkBlock(worldIn, pos, color, damage))
		{
			player.addStat(SplatCraftStats.BLOCKS_INKED);
			//TODO add points
			return true;
		}
		return false;
	}
	
	public static boolean inkBlock(World worldIn, BlockPos pos, int color, float damage)
	{

		IBlockState state = worldIn.getBlockState(pos);
		
		if(state.getBlockHardness(worldIn, pos) == -1 || IInked.touchingWater(worldIn, pos))
			return false;
	
		if(worldIn.getTileEntity(pos) instanceof TileEntitySunkenCrate)
		{
			TileEntitySunkenCrate te = (TileEntitySunkenCrate) worldIn.getTileEntity(pos);
			te.ink(color, (int) Math.floor(damage));
			worldIn.notifyBlockUpdate(pos, state, state, 3);
			return true;
		}
		
		if(worldIn.getTileEntity(pos) instanceof TileEntityColor)
		{
			if(state.getBlock() instanceof IInked)
				if(!((IInked) state.getBlock()).canInk())
					return false;
			
			TileEntityColor te = (TileEntityColor) worldIn.getTileEntity(pos);
			if(te.getColor() == color)
				return false;
			te.setColor(color);
			worldIn.notifyBlockUpdate(pos, state, state, 3);
			return true;
		}
		
		if(state.getBlock() instanceof BlockSlab && !((BlockSlab) state.getBlock()).isDouble())
		{
			worldIn.setBlockState(pos, SplatCraftBlocks.inkedSlab.getDefaultState().withProperty(BlockSlab.HALF, state.getValue(BlockSlab.HALF)));
			TileEntityInkedBlock te = (TileEntityInkedBlock) SplatCraftBlocks.inkedSlab.createTileEntity(worldIn, SplatCraftBlocks.inkedSlab.getDefaultState());
			
			worldIn.setTileEntity(pos, te);
			
			te.setColor(color);
			te.setSavedState(state);
			return true;
		}
		if(state.getBlock() instanceof BlockStairs)
		{
			worldIn.setBlockState(pos, SplatCraftBlocks.inkedStairs.getDefaultState().withProperty(BlockStairs.HALF, state.getValue(BlockStairs.HALF)).withProperty(BlockStairs.SHAPE, state.getValue(BlockStairs.SHAPE)).withProperty(BlockStairs.FACING, state.getValue(BlockStairs.FACING)));
			TileEntityInkedBlock te = (TileEntityInkedBlock) SplatCraftBlocks.inkedStairs.createTileEntity(worldIn, SplatCraftBlocks.inkedStairs.getDefaultState());
			
			worldIn.setTileEntity(pos, te);
			
			te.setColor(color);
			te.setSavedState(state);
			return true;
		}
		
		if(!state.isFullBlock() || (!state.isOpaqueCube() && !(state.getBlock() instanceof BlockLeaves)))
				return false;
		
		if(worldIn.getTileEntity(pos) != null)
				return false;

		worldIn.setBlockState(pos, SplatCraftBlocks.inkedBlock.getDefaultState());
		TileEntityInkedBlock te = (TileEntityInkedBlock) SplatCraftBlocks.inkedBlock.createTileEntity(worldIn, SplatCraftBlocks.inkedBlock.getDefaultState());

		worldIn.setTileEntity(pos, te);

		te.setColor(color);
		te.setSavedState(state);

		return true;
	}

	public static boolean canInkPassthrough(World worldIn, BlockPos pos)
	{
		IBlockState state = worldIn.getBlockState(pos);
		
		if(state.getBlock() == Blocks.AIR || state.getBlock() == Blocks.IRON_BARS || state.getBlock() instanceof BlockSquidPassable)
			return true;
		
		if(state.getBlock() instanceof BlockSlab || state.getBlock() instanceof BlockStairs)
			return false;
		if(state.isFullBlock() || state.getBlockHardness(worldIn, pos) != -1)
			return false;
		
		return true;
	}
	
	public static boolean canInk(World worldIn, BlockPos pos)
	{

		IBlockState state = worldIn.getBlockState(pos);
		
		if(state.getBlock() instanceof BlockSlab || state.getBlock() instanceof BlockStairs)
			return true;

		if(!state.isFullBlock() || (!state.isOpaqueCube() && !(state.getBlock() instanceof BlockLeaves)) || state.getBlockHardness(worldIn, pos) == -1 || IInked.touchingWater(worldIn, pos))
			return false;

		if(worldIn.getTileEntity(pos) instanceof TileEntitySunkenCrate)
		{
			return true;
		}

		if(worldIn.getTileEntity(pos) instanceof TileEntityColor)
		{
			if(state.getBlock() instanceof BlockInkColor)
				if(!((BlockInkColor) state.getBlock()).canInk)
					return false;
			return true;
		}

		if(worldIn.getTileEntity(pos) != null)
			return false;

		return true;
	}

	public static String getColorName(int color)
	{
		InkColors col = InkColors.getByColor(color);
		
		if(col == null)
		{
			String fallbackName = "color." + String.format("%06X", color).toLowerCase();
			String fallbackNameLocalized = I18n.format(fallbackName);
			
			return fallbackNameLocalized.equals(fallbackName) ? String.format("#%06X", color) : fallbackNameLocalized;
		}
		
		return I18n.format("color." + col.getName());
	}

	public static void dropItem(World worldIn, BlockPos pos, ItemStack stack, boolean useTileDrops)
	{
		boolean doTileDrops = useTileDrops ? worldIn.getGameRules().getBoolean("doTileDrops") : true;
		if (!worldIn.isRemote && !stack.isEmpty() && doTileDrops && !worldIn.restoringBlockSnapshots) // do not drop items while restoring blockstates, prevents item dupe
		{
			float f = 0.5F;
			double d0 = (double)(worldIn.rand.nextFloat() * 0.5F) + 0.25D;
			double d1 = (double)(worldIn.rand.nextFloat() * 0.5F) + 0.25D;
			double d2 = (double)(worldIn.rand.nextFloat() * 0.5F) + 0.25D;
			EntityItem entityitem = new EntityItem(worldIn, (double)pos.getX() + d0, (double)pos.getY() + d1, (double)pos.getZ() + d2, stack);
			entityitem.setDefaultPickupDelay();
			worldIn.spawnEntity(entityitem);
		}
	}
	
	public static void giveItem(EntityPlayer reciever, ItemStack stack)
	{
		if(!reciever.addItemStackToInventory(stack))
		{
			EntityItem entity = reciever.dropItem(stack, false);
			if(entity != null)
				entity.setNoPickupDelay();
		} else reciever.inventoryContainer.detectAndSendChanges();
	}
	
	public static ItemStack getSilkTouchDropFromBlock(Block block, IBlockState state)
	{
		Item item = Item.getItemFromBlock(block);
		int i = 0;
		
		if (item.getHasSubtypes())
		{
			i = block.getMetaFromState(state);
		}
		
		return new ItemStack(item, 1, i);
	}
	
	public static RayTraceResult rayTraceBlocks(World worldIn, Vec3d vecStart, Vec3d vecEnd)
	{
		boolean stopOnLiquid = true;
		boolean ignoreBlockWithoutBoundingBox = true;
		boolean returnLastUncollidableBlock = false;
		
		if (!Double.isNaN(vecStart.x) && !Double.isNaN(vecStart.y) && !Double.isNaN(vecStart.z))
		{
			if (!Double.isNaN(vecEnd.x) && !Double.isNaN(vecEnd.y) && !Double.isNaN(vecEnd.z))
			{
				int i = MathHelper.floor(vecEnd.x);
				int j = MathHelper.floor(vecEnd.y);
				int k = MathHelper.floor(vecEnd.z);
				int l = MathHelper.floor(vecStart.x);
				int i1 = MathHelper.floor(vecStart.y);
				int j1 = MathHelper.floor(vecStart.z);
				BlockPos blockpos = new BlockPos(l, i1, j1);
				IBlockState iblockstate = worldIn.getBlockState(blockpos);
				Block block = iblockstate.getBlock();
				
				if (!canInkPassthrough(worldIn, blockpos) && (!ignoreBlockWithoutBoundingBox || iblockstate.getCollisionBoundingBox(worldIn, blockpos) != Block.NULL_AABB) && block.canCollideCheck(iblockstate, stopOnLiquid))
				{
					RayTraceResult raytraceresult = iblockstate.collisionRayTrace(worldIn, blockpos, vecStart, vecEnd);
					
					if (raytraceresult != null)
					{
						return raytraceresult;
					}
				}
				
				RayTraceResult raytraceresult2 = null;
				int k1 = 200;
				
				while (k1-- >= 0)
				{
					if (Double.isNaN(vecStart.x) || Double.isNaN(vecStart.y) || Double.isNaN(vecStart.z))
					{
						return null;
					}
					
					if (l == i && i1 == j && j1 == k)
					{
						return returnLastUncollidableBlock ? raytraceresult2 : null;
					}
					
					boolean flag2 = true;
					boolean flag = true;
					boolean flag1 = true;
					double d0 = 999.0D;
					double d1 = 999.0D;
					double d2 = 999.0D;
					
					if (i > l)
					{
						d0 = (double)l + 1.0D;
					}
					else if (i < l)
					{
						d0 = (double)l + 0.0D;
					}
					else
					{
						flag2 = false;
					}
					
					if (j > i1)
					{
						d1 = (double)i1 + 1.0D;
					}
					else if (j < i1)
					{
						d1 = (double)i1 + 0.0D;
					}
					else
					{
						flag = false;
					}
					
					if (k > j1)
					{
						d2 = (double)j1 + 1.0D;
					}
					else if (k < j1)
					{
						d2 = (double)j1 + 0.0D;
					}
					else
					{
						flag1 = false;
					}
					
					double d3 = 999.0D;
					double d4 = 999.0D;
					double d5 = 999.0D;
					double d6 = vecEnd.x - vecStart.x;
					double d7 = vecEnd.y - vecStart.y;
					double d8 = vecEnd.z - vecStart.z;
					
					if (flag2)
					{
						d3 = (d0 - vecStart.x) / d6;
					}
					
					if (flag)
					{
						d4 = (d1 - vecStart.y) / d7;
					}
					
					if (flag1)
					{
						d5 = (d2 - vecStart.z) / d8;
					}
					
					if (d3 == -0.0D)
					{
						d3 = -1.0E-4D;
					}
					
					if (d4 == -0.0D)
					{
						d4 = -1.0E-4D;
					}
					
					if (d5 == -0.0D)
					{
						d5 = -1.0E-4D;
					}
					
					EnumFacing enumfacing;
					
					if (d3 < d4 && d3 < d5)
					{
						enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
						vecStart = new Vec3d(d0, vecStart.y + d7 * d3, vecStart.z + d8 * d3);
					}
					else if (d4 < d5)
					{
						enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
						vecStart = new Vec3d(vecStart.x + d6 * d4, d1, vecStart.z + d8 * d4);
					}
					else
					{
						enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
						vecStart = new Vec3d(vecStart.x + d6 * d5, vecStart.y + d7 * d5, d2);
					}
					
					l = MathHelper.floor(vecStart.x) - (enumfacing == EnumFacing.EAST ? 1 : 0);
					i1 = MathHelper.floor(vecStart.y) - (enumfacing == EnumFacing.UP ? 1 : 0);
					j1 = MathHelper.floor(vecStart.z) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
					blockpos = new BlockPos(l, i1, j1);
					IBlockState iblockstate1 = worldIn.getBlockState(blockpos);
					Block block1 = iblockstate1.getBlock();
					
					if (!ignoreBlockWithoutBoundingBox || iblockstate1.getMaterial() == Material.PORTAL || iblockstate1.getCollisionBoundingBox(worldIn, blockpos) != Block.NULL_AABB)
					{
						if (block1.canCollideCheck(iblockstate1, stopOnLiquid))
						{
							RayTraceResult raytraceresult1 = iblockstate1.collisionRayTrace(worldIn, blockpos, vecStart, vecEnd);
							
							if (raytraceresult1 != null)
							{
								return raytraceresult1;
							}
						}
						else
						{
							raytraceresult2 = new RayTraceResult(RayTraceResult.Type.MISS, vecStart, enumfacing, blockpos);
						}
					}
				}
				
				return returnLastUncollidableBlock ? raytraceresult2 : null;
			}
			else
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}
}
