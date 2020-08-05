package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.registries.SplatcraftItemGroups;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ColoredBlockItem extends BlockItem
{
	public ColoredBlockItem(Block block, String name, Properties properties)
	{
		super(block, properties);
		SplatcraftItems.inkColoredItems.add(this);
		setRegistryName(name);
	}
	
	public ColoredBlockItem(Block block, String name, int stackSize)
	{
		this(block, name, new Properties().maxStackSize(stackSize).group(SplatcraftItemGroups.GROUP_GENERAL));
	}
	
	public ColoredBlockItem(Block block, String name)
	{
		this(block, name, 64);
	}
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag)
	{
		super.addInformation(stack, world, tooltip, flag);
		tooltip.add(ColorUtils.getFormatedColorName(ColorUtils.getInkColor(stack), true));
		
	}
	@Override
	protected boolean onBlockPlaced(BlockPos pos, World worldIn, @Nullable PlayerEntity player, ItemStack stack, BlockState state)
	{
		MinecraftServer server = worldIn.getServer();
		if (server == null)
			return false;
		
		int color = ColorUtils.getInkColor(stack);
		
		if(color != -1 && worldIn.getTileEntity(pos) instanceof InkColorTileEntity)
			((InkColorTileEntity) worldIn.getTileEntity(pos)).setColor(color);
		
		return super.onBlockPlaced(pos, worldIn, player, stack, state);
	}
	
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items)
	{
		if(isInGroup(group))
			for(int color : ColorUtils.STARTER_COLORS)
				items.add(ColorUtils.setInkColor(new ItemStack(this), color));
	}
	
	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
		
		if(ColorUtils.getInkColor(stack) == -1)
			ColorUtils.setInkColor(stack, ColorUtils.DEFAULT);
	}
}
