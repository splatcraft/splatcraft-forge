package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.entities.InkProjectileEntity;
import com.cibernet.splatcraft.util.InkBlockUtils;
import com.cibernet.splatcraft.util.PlayerCooldown;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class SlosherItem extends WeaponBaseItem
{
	public float projectileSize;
	public float projectileSpeed;
	public float damage;
	public int startupTicks;
	public int projectileCount;
	public float diffAngle;
	public float inkConsumption;
	
	public SlosherItem(String name, float projectileSize, float projectileSpeed, int projectileCount, float offsetBetweenProj, float damage, int startupTicks, float inkConsumption)
	{
		super();
		setRegistryName(name);
		
		this.projectileSize = projectileSize;
		this.projectileSpeed = projectileSpeed;
		this.damage = damage;
		this.startupTicks = startupTicks;
		this.projectileCount = projectileCount;
		this.diffAngle = offsetBetweenProj;
		this.inkConsumption = inkConsumption;
	}
	
	public SlosherItem(String name, SlosherItem parent)
	{
		this(name, parent.projectileSize, parent.projectileSpeed, parent.projectileCount, parent.diffAngle, parent.damage, parent.startupTicks, parent.inkConsumption);
	}
	
	public SlosherItem(String name, Item parent)
	{
		this(name, (SlosherItem) parent);
	}
	
	@Override
	public void weaponUseTick(World world, LivingEntity entity, ItemStack stack, int timeLeft)
	{
		if(getInkAmount(entity, stack) >= inkConsumption)
		{
			if(entity instanceof PlayerEntity && getUseDuration(stack) - timeLeft < startupTicks)
				PlayerCooldown.setPlayerCooldown((PlayerEntity) entity, new PlayerCooldown(startupTicks, ((PlayerEntity) entity).inventory.currentItem, true, false, true));
		} else sendNoInkMessage(entity);
	}
	
	@Override
	public void onPlayerCooldownEnd(World world, PlayerEntity player, ItemStack stack)
	{
		if(getInkAmount(player, stack) >= inkConsumption)
		{
			if(!world.isRemote)
			{
				reduceInk(player, inkConsumption);
				for(int i = 0; i < projectileCount; i++)
				{
					boolean hasTrail = i == Math.floor((projectileCount - 1) / 2f) || i == Math.ceil((projectileCount - 1) / 2f);
					float angle = (diffAngle * i) - (diffAngle * (projectileCount - 1)/2);
					
					InkProjectileEntity proj = new InkProjectileEntity(world, player, stack, InkBlockUtils.getInkType(player), projectileSize * (hasTrail ? 1 : 0.8f), damage);
					if(hasTrail);
					proj.setShooterTrail();
					proj.shoot(player, player.rotationPitch, player.rotationYaw + angle, 0.0f, projectileSpeed, 2);
					world.addEntity(proj);
				}
			}
		} else sendNoInkMessage(player);
	}
}
