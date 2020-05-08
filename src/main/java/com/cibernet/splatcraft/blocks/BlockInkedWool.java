package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.utils.ColorItemUtils;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
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
		
		canInk = true;
	}
	
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced)
	{
		tooltip.add(SplatCraftUtils.getColorName(BlockInkwell.getInkColor(stack)));
		super.addInformation(stack, player, tooltip, advanced);
	}
	
}
