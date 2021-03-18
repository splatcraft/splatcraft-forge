package com.cibernet.splatcraft.items.weapons;

import com.cibernet.splatcraft.data.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.entities.InkProjectileEntity;
import com.cibernet.splatcraft.handlers.PlayerPosingHandler;
import com.cibernet.splatcraft.network.ChargeableReleasePacket;
import com.cibernet.splatcraft.network.SplatcraftPacketHandler;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import com.cibernet.splatcraft.registries.SplatcraftSounds;
import com.cibernet.splatcraft.util.InkBlockUtils;
import com.cibernet.splatcraft.util.PlayerCharge;
import com.cibernet.splatcraft.util.PlayerCooldown;
import com.cibernet.splatcraft.util.WeaponStat;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ChargerItem extends WeaponBaseItem implements IChargeableWeapon
{
	private final AttributeModifier SPEED_MODIFIER;
	
	public float projectileSize;
	public float projectileSpeed;
	public int projectileLifespan;
	public float chargeSpeed;
	public float dischargeSpeed;
	public float damage;
	public float pierceCharge;

	public float minConsumption;
	public float maxConsumption;

	public boolean airCharge;

	private final double mobility;
	
	public ChargerItem(String name, float projectileSize, float projectileSpeed, int projectileLifespan, int chargeTime, int dischargeTime , float damage, float minConsumption, float maxConsumption, double mobility, boolean canAirCharge, float pierceCharge)
	{
		setRegistryName(name);
		
		this.projectileSize = projectileSize;
		this.projectileLifespan = projectileLifespan;
		this.chargeSpeed = 1f/(float)chargeTime;
		this.dischargeSpeed = 1f/(float)dischargeTime;
		this.damage = damage;
		this.projectileSpeed = projectileSpeed;

		this.airCharge = canAirCharge;
		this.pierceCharge = pierceCharge;

		this.mobility = mobility;
		
		SPEED_MODIFIER = new AttributeModifier(SplatcraftItems.SPEED_MOD_UUID, "Charger Mobility", mobility-1, AttributeModifier.Operation.MULTIPLY_TOTAL);
		
		this.maxConsumption = maxConsumption;
		this.minConsumption = minConsumption;
		
		addStat(new WeaponStat("range", (stack, world) -> (int) ((projectileSpeed/projectileLifespan)*100)));
		addStat(new WeaponStat("charge_speed", ((stack, world) -> (int) ((40-chargeTime)*100/40f))));
		addStat(new WeaponStat("mobility", ((stack, world) -> (int) (mobility*100))));
	}
	
	public ChargerItem(String name, ChargerItem parent)
	{
		this(name, parent.projectileSize, parent.projectileSpeed, parent.projectileLifespan, (int) (1f/parent.chargeSpeed), (int) (1f/parent.dischargeSpeed), parent.damage, parent.minConsumption, parent.maxConsumption, parent.mobility, parent.airCharge, parent.pierceCharge);
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
	public void onRelease(World world, PlayerEntity player, ItemStack stack, float charge)
	{
		InkProjectileEntity proj = new InkProjectileEntity(world, player, stack, InkBlockUtils.getInkType(player), projectileSize, charge > 0.95f ? damage : damage*charge/4f + damage/4f);
		proj.setChargerStats((int) (projectileLifespan*charge), charge >= pierceCharge);
		proj.shoot(player, player.rotationPitch, player.rotationYaw, 0.0f, projectileSpeed, 0.1f);
		world.addEntity(proj);
		world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SplatcraftSounds.chargerShot, SoundCategory.PLAYERS, 0.7F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.1F + 1.0F) * 0.95F);
		reduceInk(player, getInkConsumption(charge));
		PlayerCooldown.setPlayerCooldown(player, new PlayerCooldown(10, player.inventory.currentItem, true, false, false));
	}


	@Override
	public void weaponUseTick(World world, LivingEntity entity, ItemStack stack, int timeLeft)
	{
		if(entity instanceof PlayerEntity && getInkAmount(entity, stack) >= getInkConsumption(PlayerCharge.getChargeValue((PlayerEntity) entity, stack)))
		{
			if(world.isRemote)
			{
				PlayerCharge.addChargeValue((PlayerEntity) entity, stack, chargeSpeed * (entity.isAirBorne && !airCharge ? 0.5f : 1));
			}
		}
		else sendNoInkMessage(entity, null);
	}


	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, LivingEntity entity, int timeLeft)
	{
		super.onPlayerStoppedUsing(stack, world, entity, timeLeft);
		
		if(world.isRemote && !PlayerInfoCapability.isSquid(entity) && entity instanceof PlayerEntity)
		{
			float charge = PlayerCharge.getChargeValue((PlayerEntity) entity, stack);
			if(charge > 0.05f)
			{
				PlayerCharge.reset((PlayerEntity) entity);
				PlayerCooldown.setPlayerCooldown((PlayerEntity) entity, new PlayerCooldown(10, ((PlayerEntity)entity).inventory.currentItem, true, false, false));
				SplatcraftPacketHandler.sendToServer(new ChargeableReleasePacket(charge, stack));
			}
			PlayerCharge.setCanDischarge((PlayerEntity) entity, true);
		}
	}


	public float getInkConsumption(float charge)
	{
		return minConsumption + (maxConsumption-minConsumption)*charge;
	}

	@Override
	public AttributeModifier getSpeedModifier() {
		return SPEED_MODIFIER;
	}

	@Override
	public PlayerPosingHandler.WeaponPose getPose() {
		return PlayerPosingHandler.WeaponPose.BOW_CHARGE;
	}
}
