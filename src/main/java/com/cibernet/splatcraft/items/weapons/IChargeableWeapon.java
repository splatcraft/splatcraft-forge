package com.cibernet.splatcraft.items.weapons;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IChargeableWeapon
{
	float getDischargeSpeed();
	float getChargeSpeed();
	void onRelease(World worldIn, PlayerEntity playerIn, ItemStack stack, float charge);
}
