package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.entities.classes.EntityInkProjectile;
import com.cibernet.splatcraft.utils.InkColors;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
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
	
	public static int colIndex = 0;

	public ItemWeaponBase(String unlocName, String registryName)
	{
		setUnlocalizedName(unlocName);
		setRegistryName(registryName);
		setCreativeTab(CreativeTabs.COMBAT);
		weapons.add(this);
	}
	
	public static int getInkColor(ItemStack stack)
	{
		//return InkColors.values()[colIndex].getColor();
		
		
		if(!stack.hasTagCompound() || !stack.getTagCompound().hasKey("color"))
			return InkColors.ORANGE.getColor();
		
		
		return stack.getTagCompound().getInteger("color");
		
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
		NBTTagCompound nbt = new NBTTagCompound();
		checkTagCompound(stack).setInteger("color", color);
		return stack;
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(playerIn.isSneaking())
			return EnumActionResult.FAIL;
		
		ItemStack stack = playerIn.getHeldItem(hand);

		SplatCraftUtils.inkBlock(worldIn, pos, getInkColor(stack));
		
		return EnumActionResult.SUCCESS;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		
		if(worldIn.isRemote || !playerIn.isSneaking()) return super.onItemRightClick(worldIn, playerIn, handIn);
		
		colIndex++;
		if(colIndex >= InkColors.values().length)
			colIndex = 0;
		
		ItemStack stack = playerIn.getHeldItem(handIn);
		
		setInkColor(stack, InkColors.values()[colIndex].getColor());
		
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	public int getMaxItemUseDuration(ItemStack stack)
	{
		return 72000;
	}

	public void onItemTickUse(World worldIn, EntityPlayer playerIn, ItemStack stack, int useTime)
	{
	}

	public float getUseWalkSpeed()
	{
		return 0.5f;
	}

	public void onItemLeftClick(World world, EntityPlayer player, ItemStack stack)
	{
	}
}
