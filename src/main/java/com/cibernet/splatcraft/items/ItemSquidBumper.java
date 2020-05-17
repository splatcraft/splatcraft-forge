package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.entities.classes.EntitySquidBumper;
import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.tileentities.TileEntityColor;
import com.cibernet.splatcraft.utils.ColorItemUtils;
import com.cibernet.splatcraft.utils.InkColors;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import com.cibernet.splatcraft.utils.TabSplatCraft;
import com.cibernet.splatcraft.world.save.SplatCraftPlayerData;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

import static com.cibernet.splatcraft.utils.ColorItemUtils.*;
import static com.cibernet.splatcraft.utils.ColorItemUtils.setColorLocked;

public class ItemSquidBumper extends Item
{
	public ItemSquidBumper(String unlocName, String registryName)
	{
		setUnlocalizedName(unlocName);
		setRegistryName(registryName);
		setCreativeTab(TabSplatCraft.main);
		
		inkColorItems.add(this);
	}
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		super.addInformation(stack, worldIn, tooltip, flagIn);
		if(hasInkColor(stack))
		{
			int color = getInkColor(stack);
			tooltip.add(SplatCraftUtils.getColorName(color));
		}
	}
	
	/**
	 * Called when a Block is right-clicked with this Item
	 */
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if (facing == EnumFacing.DOWN)
		{
			return EnumActionResult.FAIL;
		}
		else
		{
			boolean flag = worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos);
			BlockPos blockpos = flag ? pos : pos.offset(facing);
			ItemStack itemstack = player.getHeldItem(hand);
			
			if (!player.canPlayerEdit(blockpos, facing, itemstack))
			{
				return EnumActionResult.FAIL;
			}
			else
			{
				BlockPos blockpos1 = blockpos.up();
				boolean flag1 = !worldIn.isAirBlock(blockpos) && !worldIn.getBlockState(blockpos).getBlock().isReplaceable(worldIn, blockpos);
				flag1 = flag1 | (!worldIn.isAirBlock(blockpos1) && !worldIn.getBlockState(blockpos1).getBlock().isReplaceable(worldIn, blockpos1));
				
				if (flag1)
				{
					return EnumActionResult.FAIL;
				}
				else
				{
					double d0 = (double)blockpos.getX();
					double d1 = (double)blockpos.getY();
					double d2 = (double)blockpos.getZ();
					List<Entity> list = worldIn.getEntitiesWithinAABBExcludingEntity((Entity)null, new AxisAlignedBB(d0, d1, d2, d0 + 1.0D, d1 + 2.0D, d2 + 1.0D));
					
					if (!list.isEmpty())
					{
						return EnumActionResult.FAIL;
					}
					else
					{
						if (!worldIn.isRemote)
						{
							worldIn.setBlockToAir(blockpos);
							worldIn.setBlockToAir(blockpos1);
							EntitySquidBumper bumperEntity = new EntitySquidBumper(worldIn, d0 + 0.5D, d1, d2 + 0.5D, ColorItemUtils.getInkColor(itemstack));
							float f = (float)MathHelper.floor((MathHelper.wrapDegrees(player.rotationYaw - 180.0F) + 22.5F) / 45.0F) * 45.0F;
							bumperEntity.setLocationAndAngles(d0 + 0.5D, d1, d2 + 0.5D, f, 0.0F);
							//this.applyRandomRotations(bumperEntity, worldIn.rand);
							ItemMonsterPlacer.applyItemEntityDataToEntity(worldIn, player, itemstack, bumperEntity);
							worldIn.spawnEntity(bumperEntity);
							worldIn.playSound(null, bumperEntity.posX, bumperEntity.posY, bumperEntity.posZ, SoundEvents.ENTITY_ARMORSTAND_PLACE, SoundCategory.BLOCKS, 0.75F, 0.8F);
						}
						
						itemstack.shrink(1);
						return EnumActionResult.SUCCESS;
					}
				}
			}
		}
	}
	
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		if(hasInkColor(stack) || !(entityIn instanceof EntityPlayer))
			return;
		
		setInkColor(stack, SplatCraftPlayerData.getInkColor((EntityPlayer) entityIn));
		
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
	}
	
	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem)
	{
		BlockPos pos = new BlockPos(entityItem.posX, entityItem.posY-1, entityItem.posZ);
		
		ItemStack stack = entityItem.getItem();
		if(entityItem.world.getBlockState(pos).getBlock().equals(SplatCraftBlocks.inkwell))
		{
			if(entityItem.world.getTileEntity(pos) instanceof TileEntityColor)
			{
				TileEntityColor te = (TileEntityColor) entityItem.world.getTileEntity(pos);
				
				if(getInkColor(stack) != te.getColor())
					setInkColor(stack, te.getColor());
			}
		}
		
		return super.onEntityItemUpdate(entityItem);
	}
	
}
