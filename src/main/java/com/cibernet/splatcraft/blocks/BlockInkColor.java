package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.items.ItemWeaponBase;
import com.cibernet.splatcraft.tileentities.TileEntityColor;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockInkColor extends Block
{
	
	public static List<BlockInkColor> blocks = new ArrayList<>();
	
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
		return new TileEntityColor();
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
}
