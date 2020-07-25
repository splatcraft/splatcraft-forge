package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.handlers.client.ColorHandler;
import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import com.cibernet.splatcraft.registries.SplatcraftItemGroups;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class InkwellItem extends BlockItem
{
	public InkwellItem()
	{
		super(SplatcraftBlocks.inkwell, new Properties().maxStackSize(16).group(SplatcraftItemGroups.GROUP_GENERAL));
		SplatcraftItems.inkColoredItems.add(this);
	}
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag)
	{
		super.addInformation(stack, world, tooltip, flag);
		tooltip.add(new StringTextComponent(TextFormatting.GRAY + ColorUtils.getColorName(ColorUtils.getInkColor(stack))));
		
	}
	
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items)
	{
		if(isInGroup(group))
			for(int color : ColorUtils.STARTER_COLORS)
				items.add(ColorUtils.setInkColor(new ItemStack(this), color));
	}
	
}
