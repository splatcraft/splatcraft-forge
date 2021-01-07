package com.cibernet.splatcraft.items.weapons;

import com.cibernet.splatcraft.entities.InkProjectileEntity;
import com.cibernet.splatcraft.handlers.PlayerPosingHandler;
import com.cibernet.splatcraft.registries.SplatcraftSounds;
import com.cibernet.splatcraft.util.InkBlockUtils;
import com.cibernet.splatcraft.util.WeaponStat;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ShooterItem extends WeaponBaseItem
{
	public float projectileSize;
	public float projectileSpeed;
	public float inaccuracy;
	public int firingSpeed;
	public float damage;
	public float inkConsumption;
	
	public ShooterItem(String name, float projectileSize, float projectileSpeed, float inaccuracy, int firingSpeed, float damage, float inkConsumption)
	{
		super();
		setRegistryName(name);
		
		this.projectileSize = projectileSize;
		this.projectileSpeed = projectileSpeed;
		this.inaccuracy = inaccuracy;
		this.firingSpeed = firingSpeed;
		this.damage = damage;
		this.inkConsumption = inkConsumption;
		
		if(!(this instanceof BlasterItem))
		{
			addStat(new WeaponStat("range", (stack, world) -> (int) ((projectileSpeed/1.2f)*100)));
			addStat(new WeaponStat("damage", (stack, world) -> (int) ((damage/20)*100)));
			addStat(new WeaponStat("fire_rate", (stack, world) -> (int) ((11-(firingSpeed))*10)));
		}
	}
	
	public ShooterItem(String name, ShooterItem parent)
	{
		this(name, parent.projectileSize, parent.projectileSpeed, parent.inaccuracy, parent.firingSpeed, parent.damage, parent.inkConsumption);
	}
	
	@Override
	public void weaponUseTick(World world, LivingEntity entity, ItemStack stack, int timeLeft)
	{
		if(!world.isRemote && (getUseDuration(stack) - timeLeft - 1) % firingSpeed == 0)
		{
			if(getInkAmount(entity, stack) >= inkConsumption)
			{
				InkProjectileEntity proj = new InkProjectileEntity(world, entity, stack, InkBlockUtils.getInkType(entity), projectileSize, damage).setShooterTrail();
				proj.shoot(entity, entity.rotationPitch, entity.rotationYaw, 0.0f, projectileSpeed, inaccuracy);
				world.addEntity(proj);
				world.playSound(null, entity.getPosX(), entity.getPosY(), entity.getPosZ(), SplatcraftSounds.shooterShot, SoundCategory.PLAYERS, 0.7F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.1F + 1.0F) * 0.95F);
				reduceInk(entity, inkConsumption);
			}
			else sendNoInkMessage(entity);
		}
	}

	@Override
	public PlayerPosingHandler.WeaponPose getPose() {
		return PlayerPosingHandler.WeaponPose.FIRE;
	}
}
