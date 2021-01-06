package com.cibernet.splatcraft.items.weapons;

import com.cibernet.splatcraft.entities.InkProjectileEntity;
import com.cibernet.splatcraft.handlers.PlayerPosingHandler;
import com.cibernet.splatcraft.registries.SplatcraftSounds;
import com.cibernet.splatcraft.util.InkBlockUtils;
import com.cibernet.splatcraft.util.PlayerCooldown;
import com.cibernet.splatcraft.util.WeaponStat;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

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
		
		
		addStat(new WeaponStat("range", (stack, world) -> (int) (projectileSpeed*100)));
		addStat(new WeaponStat("damage", (stack, world) -> (int) ((damage/20)*100)));
		addStat(new WeaponStat("handling", (stack, world) -> (11-startupTicks)*10));
	}
	
	public SlosherItem(String name, SlosherItem parent)
	{
		this(name, parent.projectileSize, parent.projectileSpeed, parent.projectileCount, parent.diffAngle, parent.damage, parent.startupTicks, parent.inkConsumption);
	}
	
	@Override
	public void weaponUseTick(World world, LivingEntity entity, ItemStack stack, int timeLeft)
	{
		if(getInkAmount(entity, stack) >= inkConsumption)
		{
			if(entity instanceof PlayerEntity && getUseDuration(stack) - timeLeft < startupTicks)
				PlayerCooldown.setPlayerCooldown((PlayerEntity) entity, new PlayerCooldown(startupTicks, ((PlayerEntity) entity).inventory.currentItem, true, false, true));
		} else sendNoInkMessage(entity, null);
	}
	
	@Override
	public void onPlayerCooldownEnd(World world, PlayerEntity player, ItemStack stack)
	{
		if(getInkAmount(player, stack) >= inkConsumption)
		{
			if(!world.isRemote)
			{
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
				world.playSound(null, player.getPosition(), SplatcraftSounds.slosherShot, SoundCategory.PLAYERS, 0.7F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.1F + 1.0F) * 0.95F);
				reduceInk(player, inkConsumption);
			}
		} else sendNoInkMessage(player, null);
	}

	@Override
	public PlayerPosingHandler.WeaponPose getPose() {
		return PlayerPosingHandler.WeaponPose.BUCKET_SWING;
	}
}
