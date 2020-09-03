package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.entities.InkProjectileEntity;
import com.cibernet.splatcraft.util.InkBlockUtils;
import com.cibernet.splatcraft.util.PlayerCooldown;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.CooldownTracker;
import net.minecraft.world.World;

public class BlasterItem extends ShooterItem
{
	public int projLifespan;
	public int startupTicks;
	public int cooldown;
	public float splashDamage;
	
	public BlasterItem(String name, float projectileSize, float projectileSpeed, float inaccuracy, int startupTicks, int cooldown, float damage, float splashDamage, float inkConsumption, int projectileLifespan)
	{
		super(name, projectileSize, projectileSpeed, inaccuracy, cooldown, damage, inkConsumption);
		this.projLifespan = projectileLifespan;
		this.startupTicks = startupTicks;
		this.cooldown = cooldown;
		this.splashDamage = splashDamage;
		
	}
	
	public BlasterItem(String name, BlasterItem parent)
	{
		this(name, parent.projectileSize, parent.projectileSpeed, parent.inaccuracy, parent.startupTicks, parent.firingSpeed, parent.damage, parent.splashDamage, parent.inkConsumption, parent.projLifespan);
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, LivingEntity entity, int timeLeft)
	{
		super.onPlayerStoppedUsing(stack, world, entity, timeLeft);
		if(entity instanceof PlayerEntity)
		{
			CooldownTracker cooldownTracker = ((PlayerEntity)entity).getCooldownTracker();
			cooldownTracker.setCooldown(this, cooldown);
		}
	}
	
	@Override
	public void weaponUseTick(World world, LivingEntity entity, ItemStack stack, int timeLeft)
	{
		if(getInkAmount(entity, stack) > inkConsumption)
		{
			if(entity instanceof PlayerEntity)
				PlayerCooldown.setPlayerCooldown((PlayerEntity) entity, new PlayerCooldown((getUseDuration(stack) - timeLeft < startupTicks) ? startupTicks : cooldown, ((PlayerEntity) entity).inventory.currentItem, true, false, true));
		} else sendNoInkMessage(entity);
	}
	
	@Override
	public void onPlayerCooldownEnd(World world, PlayerEntity player, ItemStack stack)
	{
		if(getInkAmount(player, stack) >= inkConsumption)
		{
			if(!world.isRemote)
			{
				InkProjectileEntity proj = new InkProjectileEntity(world, player, stack, InkBlockUtils.getInkType(player), projectileSize, damage).setShooterTrail();
				proj.setBlasterStats(projLifespan, splashDamage);
				proj.shoot(player, player.rotationPitch, player.rotationYaw, 0.0f, projectileSpeed, inaccuracy);
				world.addEntity(proj);
				reduceInk(player, inkConsumption);
				
			}
		} else sendNoInkMessage(player);
	}
}
