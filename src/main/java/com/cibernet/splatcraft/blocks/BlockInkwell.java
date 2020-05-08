package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.tileentities.TileEntityColor;
import com.cibernet.splatcraft.utils.ColorItemUtils;
import com.cibernet.splatcraft.utils.InkColors;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import com.cibernet.splatcraft.utils.TabSplatCraft;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
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
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
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
		canInk = false;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced)
	{
		tooltip.add(SplatCraftUtils.getColorName(BlockInkwell.getInkColor(stack)));
		super.addInformation(stack, player, tooltip, advanced);
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items)
	{
		for(InkColors color : InkColors.creativeTabColors)
		{
			ItemStack stack = new ItemStack(this);
			ColorItemUtils.setInkColor(stack, color.getColor());
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
				ColorItemUtils.setInkColor(itemstack, ((TileEntityColor)te).getColor());

		net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos, state, 0, 1.0f, true, player);
		spawnAsEntity(worldIn, pos, itemstack);

	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
	{
		int color = SplatCraft.DEFAULT_INK;
		if(world.getTileEntity(pos) instanceof TileEntityColor)
			color = ((TileEntityColor)world.getTileEntity(pos)).getColor();
		return ColorItemUtils.setInkColor(super.getPickBlock(state, target, world, pos, player), color);
	}
	
	@Override
	public boolean isTranslucent(IBlockState state)
	{
		return true;
	}
	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
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
			return SplatCraft.DEFAULT_INK;
		return stack.getTagCompound().getInteger("color");
	}
	
	@Override
	public boolean canSwim()
	{
		return true;
	}
}
