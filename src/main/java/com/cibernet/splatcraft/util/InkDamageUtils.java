package com.cibernet.splatcraft.util;

import com.cibernet.splatcraft.data.capabilities.inkoverlay.IInkOverlayInfo;
import com.cibernet.splatcraft.data.capabilities.inkoverlay.InkOverlayCapability;
import com.cibernet.splatcraft.entities.IColoredEntity;
import com.cibernet.splatcraft.entities.SquidBumperEntity;
import com.cibernet.splatcraft.network.SplatcraftPacketHandler;
import com.cibernet.splatcraft.network.UpdateInkOverlayPacket;
import com.cibernet.splatcraft.registries.SplatcraftGameRules;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class InkDamageUtils
{
	
	public static final DamageSource VOID_DAMAGE = (new DamageSource("outOfWorld")).setDamageBypassesArmor();;
	public static final DamageSource ENEMY_INK = new DamageSource("enemyInk");
	public static final DamageSource WATER = new DamageSource("water");
	
	public static boolean doSplatDamage(World world, LivingEntity target, float damage, int color, Entity source, ItemStack sourceItem, boolean damageMobs, InkBlockUtils.InkType inkType)
	{
		return doDamage(world, target, damage, color, source, sourceItem, damageMobs, inkType, "splat", false);
	}
	
	public static boolean doRollDamage(World world, LivingEntity target, float damage, int color, Entity source, ItemStack sourceItem, boolean damageMobs, InkBlockUtils.InkType inkType)
	{
		return doDamage(world, target, damage, color, source, sourceItem, damageMobs, inkType, "roll", true);
	}
	
	public static boolean doDamage(World world, LivingEntity target, float damage, int color, Entity source, ItemStack sourceItem, boolean damageMobs, InkBlockUtils.InkType inkType, String name, boolean applyHurtCooldown)
	{
		
		if(damage == 0)
			return false;

		float mobDmgPctg = SplatcraftGameRules.getIntRuleValue(world, SplatcraftGameRules.INK_MOB_DAMAGE_PERCENTAGE)*0.01f;
		boolean doDamage = damageMobs || mobDmgPctg > 0;
		boolean applyInkCoverage = true;
		int targetColor = ColorUtils.getEntityColor(target);
		
		if(targetColor > -1)
		{
			doDamage = (targetColor != color || SplatcraftGameRules.getBooleanRuleValue(world, SplatcraftGameRules.INK_FRIENDLY_FIRE));
			applyInkCoverage = doDamage;
		}
		
		InkDamageSource damageSource = new InkDamageSource(name, source, source, sourceItem);
		
		if(target instanceof IColoredEntity)
		{
			doDamage = ((IColoredEntity) target).onEntityInked(damageSource, damage, color);
			applyInkCoverage = doDamage;
		}

		if(target instanceof SheepEntity)
		{
			if(!((SheepEntity) target).getSheared())
			{
				doDamage = false;
				applyInkCoverage = false;
			}
		}

		if(doDamage)
			target.attackEntityFrom(damageSource, damage * ((target instanceof IColoredEntity || damageMobs) ? 1 : mobDmgPctg));

		if(applyInkCoverage && !target.isInWater())
		{
			if(InkOverlayCapability.hasCapability(target))
			{
				IInkOverlayInfo info = InkOverlayCapability.get(target);

				if(info.getAmount() < (target instanceof SquidBumperEntity ? SquidBumperEntity.maxInkHealth : target.getMaxHealth())*1.5)
					info.addAmount(damage * ((target instanceof IColoredEntity || damageMobs) ? 1 : Math.max(0.5f,mobDmgPctg)));
				info.setColor(color);
				SplatcraftPacketHandler.sendToAll(new UpdateInkOverlayPacket(target, info));
			}
		}

		if(!applyHurtCooldown && !SplatcraftGameRules.getBooleanRuleValue(world, SplatcraftGameRules.INK_DAMAGE_COOLDOWN))
			target.hurtResistantTime = 0;

		return doDamage;
	}
	
	public static class InkDamageSource extends IndirectEntityDamageSource
	{
		private final ItemStack weapon;
		
		public InkDamageSource(String damageTypeIn, Entity source, @Nullable Entity indirectEntityIn, ItemStack weapon)
		{
			super(damageTypeIn, source, indirectEntityIn);
			this.weapon = weapon;
		}
		
		@Override
		public ITextComponent getDeathMessage(LivingEntity entityLivingBaseIn)
		{
			ITextComponent itextcomponent = this.getTrueSource() == null ? this.damageSourceEntity.getDisplayName() : this.getTrueSource().getDisplayName();
			String s = "death.attack." + this.damageType;
			String s1 = s + ".item";
			return !weapon.isEmpty() ? new TranslationTextComponent(s1, entityLivingBaseIn.getDisplayName(), itextcomponent, weapon.getTextComponent()) : new TranslationTextComponent(s, entityLivingBaseIn.getDisplayName(), itextcomponent);
		}
	}
}
