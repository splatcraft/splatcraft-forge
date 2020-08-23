package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.entities.InkProjectileEntity;
import com.cibernet.splatcraft.registries.SplatcraftItemGroups;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import com.cibernet.splatcraft.util.ColorUtils;
import com.cibernet.splatcraft.util.InkBlockUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
		if(hasInk(entity, stack))
		{
			if(!world.isRemote && (getUseDuration(stack) - timeLeft - 1) % firingSpeed == 0)
			{
				InkProjectileEntity proj = new InkProjectileEntity(world, entity, stack, InkBlockUtils.getInkType(entity), projectileSize, damage);
				proj.shoot(entity, entity.rotationPitch, entity.rotationYaw, 0.0f, projectileSpeed, inaccuracy);
				world.addEntity(proj);
				reduceInk(entity, inkConsumption);
			}
		}
		else if(entity instanceof PlayerEntity)
			((PlayerEntity) entity).sendStatusMessage(new TranslationTextComponent("status.no_ink").mergeStyle(TextFormatting.RED), true);
	}
}
