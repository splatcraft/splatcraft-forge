package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.entities.classes.EntityChargerProjectile;
import com.cibernet.splatcraft.entities.classes.EntityInkProjectile;
import com.cibernet.splatcraft.entities.models.ModelPlayerOverride;
import com.cibernet.splatcraft.utils.SplatCraftPlayerData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemChargerBase extends ItemWeaponBase implements ICharge
{
	private final AttributeModifier SPEED_MODIFIER;
	
	public float projectileSize;
	public float inaccuracy;
	public float projectileSpeed;
	public float chargeSpeed;
	public float dischargeSpeed;
	public float damage;
	
	public ItemChargerBase(String unlocName, String registryName, float projectileSize, float projectileSpeed, float inaccuracy, int chargeTime, int dischargeTime , float damage, double mobility)
	{
		super(unlocName, registryName);
		
		this.projectileSize = projectileSize;
		this.inaccuracy = inaccuracy;
		this.projectileSpeed = projectileSpeed;
		this.chargeSpeed = 1f/chargeTime;
		this.dischargeSpeed = 1f/dischargeTime;
		this.damage = damage;
		SPEED_MODIFIER = new AttributeModifier("Charger Mobility", mobility-1, 2).setSaved(false);
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
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count)
	{
		super.onUsingTick(stack, player, count);
	}
	
	@Override
	public void onItemTickUse(World worldIn, EntityPlayer playerIn, ItemStack stack, int useTime)
	{
		if(!SplatCraftPlayerData.getIsSquid(playerIn))
			SplatCraftPlayerData.addWeaponCharge(playerIn, stack, chargeSpeed);
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft)
	{
		EntityPlayer playerIn = (EntityPlayer) entityLiving;
		float charge = SplatCraftPlayerData.getWeaponCharge(playerIn, stack);
		if(charge > 0.05f && !worldIn.isRemote && !SplatCraftPlayerData.getIsSquid(playerIn))
		{
			EntityInkProjectile proj = new EntityChargerProjectile(worldIn, playerIn, getInkColor(stack), charge > 0.95f ? damage : damage*charge/4f + damage/4f);
			proj.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, projectileSpeed*charge, inaccuracy);
			proj.setProjectileSize(projectileSize);
			worldIn.spawnEntity(proj);
			SplatCraftPlayerData.setWeaponCharge(playerIn, stack, 0f);
			playerIn.getCooldownTracker().setCooldown(this, 10);
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
	
	@Override
	public AttributeModifier getSpeedModifier()
	{
		return SPEED_MODIFIER;
	}
	
	@Override
	public ModelPlayerOverride.EnumAnimType getAnimType()
	{
		return ModelPlayerOverride.EnumAnimType.CHARGER;
	}
}
