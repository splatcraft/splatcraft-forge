package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.InkColors;
import com.cibernet.splatcraft.blocks.BlockInked;
import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.tileentities.TileEntityInkedBlock;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.state.IBlockState;
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

		inkBlock(worldIn, pos, getInkColor(stack));
		
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

	public static boolean inkBlock(World worldIn, BlockPos pos, int color)
	{

		IBlockState state = worldIn.getBlockState(pos);

		if(!state.isFullBlock() || state.isTranslucent() || state.getBlockHardness(worldIn, pos) == -1)
			return false;

		if(worldIn.getTileEntity(pos) instanceof TileEntityInkedBlock)
		{
			TileEntityInkedBlock te = (TileEntityInkedBlock) worldIn.getTileEntity(pos);
			te.setColor(color);
			return true;
		}

		if(!(worldIn.getTileEntity(pos) == null))
			return false;

		worldIn.setBlockState(pos, SplatCraftBlocks.inkedBlock.getDefaultState());
		TileEntityInkedBlock te = (TileEntityInkedBlock) SplatCraftBlocks.inkedBlock.createTileEntity(worldIn, SplatCraftBlocks.inkedBlock.getDefaultState());

		worldIn.setTileEntity(pos, te);

		te.setColor(color);
		te.setSavedState(state);

		return true;
	}
}
