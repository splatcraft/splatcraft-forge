package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.tileentities.TileEntityColor;
import com.cibernet.splatcraft.utils.ColorItemUtils;
import com.cibernet.splatcraft.utils.InkColors;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import com.cibernet.splatcraft.utils.TabSplatCraft;
import com.cibernet.splatcraft.world.save.SplatCraftPlayerData;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

import static com.cibernet.splatcraft.utils.ColorItemUtils.*;
import static com.cibernet.splatcraft.utils.ColorItemUtils.setColorLocked;

public class ItemInkColoredArmor extends ItemArmor implements IBattleItem
{
	public ItemInkColoredArmor(String unlocName, String registryName, ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn)
	{
		super(materialIn, renderIndexIn, equipmentSlotIn);
		setUnlocalizedName(unlocName);
		setRegistryName(registryName);
		
		setCreativeTab(TabSplatCraft.main);
		ColorItemUtils.inkColorItems.add(this);
	}
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		super.addInformation(stack, worldIn, tooltip, flagIn);
		
		tooltip.add(I18n.format("item.inkCloth.tooltip"));
		
		if(isColorLocked(stack))
		{
			int color = getInkColor(stack);
			tooltip.add(SplatCraftUtils.getColorName(color));
		}
	}
	
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		if(isColorLocked(stack) || !(entityIn instanceof EntityPlayer))
			return;
		
		
		setInkColor(stack, SplatCraftPlayerData.getInkColor((EntityPlayer) entityIn));
		
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
	}
	
	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem)
	{
		BlockPos pos = new BlockPos(entityItem.posX, entityItem.posY-1, entityItem.posZ);
		
		ItemStack stack = entityItem.getItem();
		if(entityItem.world.getBlockState(pos).getBlock().equals(SplatCraftBlocks.inkwell))
		{
			if(entityItem.world.getTileEntity(pos) instanceof TileEntityColor)
			{
				TileEntityColor te = (TileEntityColor) entityItem.world.getTileEntity(pos);
				
				if(getInkColor(stack) != te.getColor() || !isColorLocked(stack))
				{
					setInkColor(stack, te.getColor());
					setColorLocked(stack, true);
				}
			}
		}
		else if (entityItem.world.getBlockState(pos.up()).getMaterial().equals(Material.WATER) && isColorLocked(stack))
		{
			setInkColor(stack, InkColors.DYE_WHITE.getColor());
			setColorLocked(stack, false);
		}
		
		return super.onEntityItemUpdate(entityItem);
	}
	
	@Override
	public boolean hasColor(ItemStack stack)
	{
		return true;
	}
	
	@Override
	public int getColor(ItemStack stack)
	{
		return ColorItemUtils.getInkColor(stack);
	}
	
}
