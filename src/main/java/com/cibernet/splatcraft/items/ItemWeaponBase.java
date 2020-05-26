package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.entities.models.ModelPlayerOverride;
import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.tileentities.TileEntityColor;
import com.cibernet.splatcraft.utils.*;
import com.cibernet.splatcraft.world.save.SplatCraftGamerules;
import com.cibernet.splatcraft.world.save.SplatCraftPlayerData;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.cibernet.splatcraft.utils.ColorItemUtils.*;

public class ItemWeaponBase extends Item implements IBattleItem
{
	
	public static List<ItemWeaponBase> weapons = new ArrayList<>();
	public float inkConsumption;
	
	public ItemWeaponBase(String unlocName, String registryName, float inkConsumption)
	{
		setUnlocalizedName(unlocName);
		setRegistryName(registryName);
		setCreativeTab(TabSplatCraft.main);
		setMaxStackSize(1);
		ColorItemUtils.inkColorItems.add(this);
		weapons.add(this);
		
		this.inkConsumption = inkConsumption;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced)
	{
		if(isColorLocked(stack))
		{
			int color = getInkColor(stack);
			tooltip.add(SplatCraftUtils.getColorName(color));
		}

		super.addInformation(stack, player, tooltip, advanced);
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		return EnumActionResult.PASS;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		ItemStack stack = playerIn.getHeldItem(handIn);
		playerIn.setActiveHand(handIn);
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}
	
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		if(isColorLocked(stack) || !(entityIn instanceof EntityPlayer))
			return;
		
		setInkColor(stack, SplatCraftPlayerData.getInkColor((EntityPlayer) entityIn));
		
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft)
	{
		if(entityLiving.getActiveItemStack().equals(stack))
			entityLiving.resetActiveHand();
		super.onPlayerStoppedUsing(stack, worldIn, entityLiving, timeLeft);
	}
	
	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack)
	{
		if(!entityLiving.world.isRemote)
		{
			boolean isSquid = false;
			if(entityLiving instanceof EntityPlayer)
				isSquid = SplatCraftPlayerData.getIsSquid((EntityPlayer) entityLiving);
			if(!isSquid)
				onItemLeftClick(entityLiving.world, (EntityPlayer) entityLiving, stack);
		}
		return super.onEntitySwing(entityLiving, stack);
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
				
				if(getInkColor(stack) != te.getColor() || !isColorLocked(stack))
				{
					setInkColor(stack, te.getColor());
					setColorLocked(stack, true);
				}
			}
		}
		else if (entityItem.world.getBlockState(pos.up()).getMaterial().equals(Material.WATER) && isColorLocked(stack))
		{
			setInkColor(stack, InkColors.DYE_WHITE.getColor());
			setColorLocked(stack, false);
		}
		
		return super.onEntityItemUpdate(entityItem);
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{
		try
		{
			return ClientUtils.getDurabilityForDisplay(stack);
		}catch(NoClassDefFoundError e)
		{
			return 1;
		}
	}
	
	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack)
	{
		return ColorItemUtils.getInkColor(stack);
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		try
		{
			return ClientUtils.showDurabilityBar(stack);
		}catch(NoClassDefFoundError e)
		{
			return false;
		}
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack)
	{
		return 72000;
	}

	public void onItemTickUse(World worldIn, EntityPlayer playerIn, ItemStack stack, int useTime)
	{
	}

	public AttributeModifier getSpeedModifier() {return null;}

	public void onItemLeftClick(World world, EntityPlayer player, ItemStack stack)
	{
	}
	
	public ModelPlayerOverride.EnumAnimType getAnimType() {return ModelPlayerOverride.EnumAnimType.NONE;}
	
	public boolean hasInk(EntityPlayer player, ItemStack weapon)
	{
		return hasInk(player, weapon, inkConsumption);
	}
	
	public static boolean hasInk(EntityPlayer player, ItemStack weapon, float offset)
	{
		int color = ColorItemUtils.getInkColor(weapon);
		
		if(!SplatCraftGamerules.getGameruleValue("requireInkTank"))
			return true;
		ItemStack chespiece = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		
		return chespiece.getItem() instanceof ItemInkTank && ColorItemUtils.getInkColor(chespiece) == color && ItemInkTank.getInkAmountStatic(chespiece, weapon)-offset > 0;
	}
	
	public static void reduceInk(EntityPlayer player, float amount)
	{
		if(!SplatCraftGamerules.getGameruleValue("requireInkTank"))
			return;
		
		ItemStack chespiece = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		if(chespiece.getItem() instanceof ItemInkTank)
		{
			ItemInkTank.setInkAmount(chespiece, ItemInkTank.getInkAmount(chespiece)-amount);
		}
	}
	
	public void reduceInk(EntityPlayer player)
	{
		reduceInk(player, inkConsumption);
	}
	
	public AttributeModifier getNoInkSpeed()
	{
		return null;
	}
}
