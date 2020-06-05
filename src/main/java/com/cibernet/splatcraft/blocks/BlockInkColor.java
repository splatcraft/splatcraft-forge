package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.items.ItemWeaponBase;
import com.cibernet.splatcraft.tileentities.TileEntityColor;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockInkColor extends Block implements IInked
{
	
	public static List<Block> blocks = new ArrayList<>();
	public boolean canInk = true;
	
	public BlockInkColor(Material materialIn)
	{
		super(materialIn);
		blocks.add(this);
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
		return new TileEntityColor(getDefaultColor());
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
	
	@Override
	public boolean canInk()
	{
		return canInk;
	}
	
	@Override
	public boolean canDamage()
	{
		return false;
	}
	
	@Override
	public boolean canSwim()
	{
		return false;
	}
	
	@Override
	public boolean countsTowardsScore()
	{
		return false;
	}
}
