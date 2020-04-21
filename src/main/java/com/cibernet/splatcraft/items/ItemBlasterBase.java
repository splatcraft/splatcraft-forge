package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.entities.classes.EntityBlasterProjectile;
import com.cibernet.splatcraft.entities.classes.EntityInkProjectile;
import com.cibernet.splatcraft.utils.ColorItemUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.CooldownTracker;
import net.minecraft.world.World;

public class ItemBlasterBase extends ItemShooterBase
{
	
	public int projLifespan;
	public int startupTicks;
	public int cooldown;
	
	public ItemBlasterBase(String unlocName, String registryName, float projectileSize, float projectileSpeed, float inaccuracy, int startupTicks, int cooldown, float damage, int projectileLifespan)
	{
		super(unlocName, registryName, projectileSize, projectileSpeed, inaccuracy, cooldown, damage, true);
		this.projLifespan = projectileLifespan;
		this.startupTicks = startupTicks;
		this.cooldown = cooldown;
	}
	
	@Override
	public void onItemTickUse(World worldIn, EntityPlayer playerIn, ItemStack stack, int useTime)
	{
		CooldownTracker cooldownTracker = playerIn.getCooldownTracker();
		if(!worldIn.isRemote && !cooldownTracker.hasCooldown(this))
		{
			if(getMaxItemUseDuration(stack) - useTime < startupTicks)
			{
				cooldownTracker.setCooldown(this, startupTicks);
			}
			else
			{
				EntityBlasterProjectile proj = new EntityBlasterProjectile(worldIn, playerIn, ColorItemUtils.getInkColor(stack), damage, projLifespan);
				proj.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, projectileSpeed, inaccuracy);
				proj.setProjectileSize(projectileSize);
				worldIn.spawnEntity(proj);
				cooldownTracker.setCooldown(this, cooldown);
			}
		}
	}
}
