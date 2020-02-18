package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.items.ItemWeaponBase;
import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.tileentities.TileEntityColor;
import com.cibernet.splatcraft.utils.InkColors;
import com.cibernet.splatcraft.utils.TabSplatCraft;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Random;

public class BlockInkwell extends BlockInkColor
{
	public BlockInkwell()
	{
		super(Material.GLASS);
		setUnlocalizedName("inkwell");
		setRegistryName("inkwell");
		setCreativeTab(TabSplatCraft.main);
		setHardness(0.5f);
	}


	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items)
	{
		for(InkColors color : InkColors.values())
		{
			ItemStack stack = new ItemStack(this);
			ItemWeaponBase.setInkColor(stack, color.getColor());
			items.add(stack);
		}
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack)
	{
		player.addStat(StatList.getBlockStats(this));
		player.addExhaustion(0.005F);

		java.util.List<ItemStack> items = new java.util.ArrayList<ItemStack>();
		ItemStack itemstack = new ItemStack(this);

			if(te instanceof TileEntityColor)
				ItemWeaponBase.setInkColor(itemstack, ((TileEntityColor)te).getColor());

		net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos, state, 0, 1.0f, true, player);
		spawnAsEntity(worldIn, pos, itemstack);

	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
	{
		int color = 0x000fff;
		if(world.getTileEntity(pos) instanceof TileEntityColor)
			color = ((TileEntityColor)world.getTileEntity(pos)).getColor();
		return ItemWeaponBase.setInkColor(super.getPickBlock(state, target, world, pos, player), color);
	}

	@Override
	public boolean isTranslucent(IBlockState state)
	{
		return true;
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	public static int getInkColor(ItemStack stack)
	{
		if(!stack.hasTagCompound() || !stack.getTagCompound().hasKey("color"))
			return 0x000FFF;
		return stack.getTagCompound().getInteger("color");
	}
}
