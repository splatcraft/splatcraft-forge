package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.entities.classes.EntityInkProjectile;
import com.cibernet.splatcraft.utils.SplatCraftPlayerData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemChargerBase extends ItemWeaponBase implements ICharge
{
	public float projectileSize;
	public float inaccuracy;
	public float projectileSpeed;
	public float chargeSpeed;
	public float dischargeSpeed;
	public float damage;
	
	public ItemChargerBase(String unlocName, String registryName, float projectileSize, float projectileSpeed, float inaccuracy, float chargeSpeed, float dischargeSpeed , float damage)
	{
		super(unlocName, registryName);
		
		this.projectileSize = projectileSize;
		this.inaccuracy = inaccuracy;
		this.projectileSpeed = projectileSpeed;
		this.chargeSpeed = chargeSpeed;
		this.dischargeSpeed = dischargeSpeed;
		this.damage = damage;
		
	}
	
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
		if((entityIn instanceof EntityPlayer))
		{
			EntityPlayer player = (EntityPlayer) entityIn;
			
			//if(!player.getActiveItemStack().equals(stack))
			//	SplatCraftPlayerData.addWeaponCharge(player, stack, -dischargeSpeed);
		}
	}
	
	@Override
	public void onItemTickUse(World worldIn, EntityPlayer playerIn, ItemStack stack, int useTime)
	{
		System.out.println(SplatCraftPlayerData.getWeaponCharge(playerIn, stack));
		SplatCraftPlayerData.addWeaponCharge(playerIn, stack, 0.01f);
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft)
	{
		EntityPlayer playerIn = (EntityPlayer) entityLiving;
		if(SplatCraftPlayerData.getWeaponCharge(playerIn, stack) >= 1 && !worldIn.isRemote)
		{
			EntityInkProjectile proj = new EntityInkProjectile(worldIn, playerIn, getInkColor(stack), damage);
			proj.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, projectileSpeed, inaccuracy);
			proj.setProjectileSize(projectileSize);
			worldIn.spawnEntity(proj);
		}
	}
	
	@Override
	public float getDischargeSpeed()
	{
		return dischargeSpeed;
	}
	
	@Override
	public float getChargeSpeed()
	{
		return chargeSpeed;
	}
}
