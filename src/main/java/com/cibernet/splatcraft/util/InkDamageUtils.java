package com.cibernet.splatcraft.util;

import com.cibernet.splatcraft.entities.InkSquidEntity;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class InkDamageUtils
{
	
	public static boolean doSplatDamage(World world, LivingEntity target, float damage, int color, Entity source, ItemStack sourceItem, boolean damageMobs, InkBlockUtils.InkType inkType)
	{
		return doDamage(world, target, damage, color, source, sourceItem, damageMobs, inkType, "splat");
	}
	
	public static boolean doRollDamage(World world, LivingEntity target, float damage, int color, Entity source, ItemStack sourceItem, boolean damageMobs, InkBlockUtils.InkType inkType)
	{
		return doDamage(world, target, damage, color, source, sourceItem, damageMobs, inkType, "roll");
	}
	
	public static boolean doDamage(World world, LivingEntity target, float damage, int color, Entity source, ItemStack sourceItem, boolean damageMobs, InkBlockUtils.InkType inkType, String name)
	{
		//if(target instanceof InkSquidEntity)
			//return false;
		
		if(damage == 0)
			return false;
		
		boolean doDamage = false;
		int targetColor = ColorUtils.getEntityColor(target);
		
		if(targetColor > -1)
			doDamage = (targetColor != color);
		
		if(doDamage)
			target.attackEntityFrom(new InkDamageSource(name, source, source, sourceItem), damage);
		
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
			return !weapon.isEmpty() && weapon.hasDisplayName() ? new TranslationTextComponent(s1, entityLivingBaseIn.getDisplayName(), itextcomponent, weapon.getTextComponent()) : new TranslationTextComponent(s, entityLivingBaseIn.getDisplayName(), itextcomponent);
		}
	}
}
