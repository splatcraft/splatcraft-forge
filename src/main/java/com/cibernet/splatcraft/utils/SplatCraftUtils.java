package com.cibernet.splatcraft.utils;

import com.cibernet.splatcraft.blocks.BlockInkColor;
import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.tileentities.TileEntityColor;
import com.cibernet.splatcraft.tileentities.TileEntityInkedBlock;
import com.cibernet.splatcraft.tileentities.TileEntitySunkenCrate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTableList;

import java.util.List;

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
		BlockPos pos = new BlockPos(playerIn.posX, playerIn.posY-1, playerIn.posZ);
		
		if(worldIn.getTileEntity(pos) instanceof TileEntityColor)
			return ((TileEntityColor)worldIn.getTileEntity(pos)).getColor() == SplatCraftPlayerData.getInkColor(playerIn);
		return false;
	}
	
	/**
	 * Creates an explosion as determined by this creeper's power and explosion radius.
	 */
	public static void createInkExplosion(World worldIn, Entity source, BlockPos pos, float radius, int color)
	{
		if (!worldIn.isRemote)
		{
			InkExplosion explosion = new InkExplosion(worldIn, source, pos.getX(), pos.getY(), pos.getZ(), (int) radius, color, true);
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

	public static boolean inkBlock(World worldIn, BlockPos pos, int color)
	{

			IBlockState state = worldIn.getBlockState(pos);

			if(!state.isFullBlock() || !state.isOpaqueCube() || state.getBlockHardness(worldIn, pos) == -1)
					return false;

			if(worldIn.getTileEntity(pos) instanceof TileEntitySunkenCrate)
			{
				TileEntitySunkenCrate te = (TileEntitySunkenCrate) worldIn.getTileEntity(pos);
				te.ink(color);
				worldIn.notifyBlockUpdate(pos, state, state, 3);
				return true;
			}

			if(worldIn.getTileEntity(pos) instanceof TileEntityColor)
			{
				if(state.getBlock() instanceof BlockInkColor)
					if(!((BlockInkColor) state.getBlock()).canInk)
						return false;

				TileEntityColor te = (TileEntityColor) worldIn.getTileEntity(pos);
				te.setColor(color);
				worldIn.notifyBlockUpdate(pos, state, state, 3);
				return true;
			}

			if(worldIn.getTileEntity(pos) != null)
					return false;

			worldIn.setBlockState(pos, SplatCraftBlocks.inkedBlock.getDefaultState());
			TileEntityInkedBlock te = (TileEntityInkedBlock) SplatCraftBlocks.inkedBlock.createTileEntity(worldIn, SplatCraftBlocks.inkedBlock.getDefaultState());

			worldIn.setTileEntity(pos, te);

			te.setColor(color);
			te.setSavedState(state);

			return true;
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
}
