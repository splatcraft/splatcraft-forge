package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.tileentities.TileEntityColor;
import com.cibernet.splatcraft.tileentities.TileEntityInkedBlock;
import com.cibernet.splatcraft.utils.ColorItemUtils;
import com.cibernet.splatcraft.utils.InkColors;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BlockInkedWool extends BlockInkColor
{
	public BlockInkedWool(String unlocName, String registryName)
	{
		super(Material.CLOTH);
		setUnlocalizedName(unlocName);
		setRegistryName(registryName);
		setSoundType(SoundType.CLOTH);
		setHardness(0.8f);
		
		canInk = true;
		dropColored = true;
	}
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced)
	{
		tooltip.add(SplatCraftUtils.getColorName(BlockInkwell.getInkColor(stack)));
		super.addInformation(stack, player, tooltip, advanced);
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
	public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		if(!(worldIn.getTileEntity(pos) instanceof TileEntityInkedBlock))
			return super.getMapColor(state, worldIn, pos);
		
		TileEntityInkedBlock te = (TileEntityInkedBlock) worldIn.getTileEntity(pos);
		InkColors color = InkColors.getByColor(te.getColor());
		if(color == null)
			return super.getMapColor(state, worldIn, pos);
		else return color.getMapColor();
		
	}
	
	@Override
	public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World worldIn, BlockPos pos)
	{
		if(player.getHeldItemMainhand().getItem() instanceof ItemShears)
			return 0.95f;
		return super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
	}
}
