package com.cibernet.splatcraft.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface ICharge
{
	float getDischargeSpeed();
	float getChargeSpeed();
	void onRelease(World worldIn, EntityPlayer playerIn, ItemStack stack);
}
