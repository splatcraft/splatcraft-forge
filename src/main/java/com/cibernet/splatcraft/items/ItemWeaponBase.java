package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.entities.classes.EntityInkProjectile;
import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.tileentities.TileEntityColor;
import com.cibernet.splatcraft.utils.InkColors;
import com.cibernet.splatcraft.utils.SplatCraftPlayerData;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import com.cibernet.splatcraft.utils.TabSplatCraft;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ItemWeaponBase extends Item
{
	public static List<ItemWeaponBase> weapons = new ArrayList<>();
	
	public ItemWeaponBase(String unlocName, String registryName)
	{
		setUnlocalizedName(unlocName);
		setRegistryName(registryName);
		setCreativeTab(TabSplatCraft.main);
		setMaxStackSize(1);
		weapons.add(this);
	}
	
	public static int getInkColor(ItemStack stack)
	{
		if(!stack.hasTagCompound() || !stack.getTagCompound().hasKey("color"))
			return 0xFAFAFA;
		return stack.getTagCompound().getInteger("color");
	}
	public static boolean isColorLocked(ItemStack stack)
	{
		if(!stack.hasTagCompound() || !stack.getTagCompound().hasKey("colorLocked"))
			return false;
		return stack.getTagCompound().getBoolean("colorLocked");
	}
	
	private static NBTTagCompound checkTagCompound(ItemStack stack) {
		NBTTagCompound tagCompound = stack.getTagCompound();
		if (tagCompound == null) {
			tagCompound = new NBTTagCompound();
			stack.setTagCompound(tagCompound);
		}
		
		return tagCompound;
	}
	
	public static ItemStack setInkColor(ItemStack stack, int color)
	{
		checkTagCompound(stack).setInteger("color", color);
		return stack;
	}
	public static ItemStack setColorLocked(ItemStack stack, boolean colorLocked)
	{
		checkTagCompound(stack).setBoolean("colorLocked", colorLocked);
		return stack;
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		ItemStack stack = playerIn.getHeldItem(hand);

		SplatCraftUtils.inkBlock(worldIn, pos, getInkColor(stack));
		
		return EnumActionResult.SUCCESS;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		ItemStack stack = playerIn.getHeldItem(handIn);
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
			setInkColor(stack, 0xFAFAFA);
			setColorLocked(stack, false);
		}
		
		return super.onEntityItemUpdate(entityItem);
	}
	
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
}
