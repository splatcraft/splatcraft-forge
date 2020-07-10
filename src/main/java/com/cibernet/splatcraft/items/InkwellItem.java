package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.handlers.client.ColorHandler;
import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import com.cibernet.splatcraft.registries.SplatcraftItemGroups;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class InkwellItem extends BlockItem
{
	public InkwellItem()
	{
		super(SplatcraftBlocks.inkwell, new Properties().maxStackSize(16).group(SplatcraftItemGroups.GROUP_GENERAL));
		ColorHandler.inkColoredItems.add(this);
	}
	
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items)
	{
		if(isInGroup(group))
			for(int color : ColorUtils.STARTER_COLORS)
				items.add(ColorUtils.setInkColor(new ItemStack(this), color));
	}
	
}
