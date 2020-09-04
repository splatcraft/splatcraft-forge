package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.entities.InkProjectileEntity;
import com.cibernet.splatcraft.registries.SplatcraftItemGroups;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import com.cibernet.splatcraft.registries.SplatcraftSounds;
import com.cibernet.splatcraft.util.ColorUtils;
import com.cibernet.splatcraft.util.InkBlockUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
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
	}
	
	public ShooterItem(String name, ShooterItem parent)
	{
		this(name, parent.projectileSize, parent.projectileSpeed, parent.inaccuracy, parent.firingSpeed, parent.damage, parent.inkConsumption);
	}
	
	@Override
	public void weaponUseTick(World world, LivingEntity entity, ItemStack stack, int timeLeft)
	{
		if(getInkAmount(entity, stack) >= inkConsumption)
		{
			if(!world.isRemote && (getUseDuration(stack) - timeLeft - 1) % firingSpeed == 0)
			{
				InkProjectileEntity proj = new InkProjectileEntity(world, entity, stack, InkBlockUtils.getInkType(entity), projectileSize, damage).setShooterTrail();
				proj.shoot(entity, entity.rotationPitch, entity.rotationYaw, 0.0f, projectileSpeed, inaccuracy);
				world.addEntity(proj);
				world.playSound(null, entity.getPosition(), SplatcraftSounds.shooterShot, SoundCategory.PLAYERS, 0.7F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.1F + 1.0F) * 0.95F);
				reduceInk(entity, inkConsumption);
			}
		}
		else sendNoInkMessage(entity);
	}
}
