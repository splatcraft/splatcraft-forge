package com.cibernet.splatcraft.items.weapons;

import com.cibernet.splatcraft.entities.InkProjectileEntity;
import com.cibernet.splatcraft.registries.SplatcraftSounds;
import com.cibernet.splatcraft.util.InkBlockUtils;
import com.cibernet.splatcraft.util.PlayerCooldown;
import com.cibernet.splatcraft.util.WeaponStat;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.SoundCategory;
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
		
		
		addStat(new WeaponStat("range", (stack, world) -> (int) ((projectileSpeed/projectileLifespan)*100)));
		addStat(new WeaponStat("impact", (stack, world) -> (int) ((damage/20)*100)));
		addStat(new WeaponStat("fire_rate", (stack, world) -> (int) ((11-(cooldown*0.5f))*10)));
	}
	
	public BlasterItem(String name, BlasterItem parent)
	{
		this(name, parent.projectileSize, parent.projectileSpeed, parent.inaccuracy, parent.startupTicks, parent.firingSpeed, parent.damage, parent.splashDamage, parent.inkConsumption, parent.projLifespan);
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, LivingEntity entity, int timeLeft)
	{
		super.onPlayerStoppedUsing(stack, world, entity, timeLeft);
		if((getInkAmount(entity, stack) >= inkConsumption) && entity instanceof PlayerEntity)
		{
			CooldownTracker cooldownTracker = ((PlayerEntity)entity).getCooldownTracker();
			cooldownTracker.setCooldown(this, cooldown);
		}
	}
	
	@Override
	public void weaponUseTick(World world, LivingEntity entity, ItemStack stack, int timeLeft)
	{
		CooldownTracker cooldownTracker = ((PlayerEntity)entity).getCooldownTracker();
		//int cooldown = (getUseDuration(stack) - timeLeft < startupTicks) ? startupTicks : this.cooldown;
		if(getInkAmount(entity, stack) > inkConsumption && !cooldownTracker.hasCooldown(this))
		{
			if(entity instanceof PlayerEntity)
			{
				PlayerCooldown.setPlayerCooldown((PlayerEntity) entity, new PlayerCooldown(startupTicks, ((PlayerEntity) entity).inventory.currentItem, true, false, true));
				if(!world.isRemote)
					cooldownTracker.setCooldown(this, cooldown);
			}
		} else if(timeLeft % (cooldown) == 0) sendNoInkMessage(entity);
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
				world.playSound(null, player.getPosition(), SplatcraftSounds.blasterShot, SoundCategory.PLAYERS, 0.7F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.1F + 1.0F) * 0.95F);
				reduceInk(player, inkConsumption);
				
			}
		} else sendNoInkMessage(player, null);
	}
}
