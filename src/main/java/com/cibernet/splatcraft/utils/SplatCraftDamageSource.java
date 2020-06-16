package com.cibernet.splatcraft.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;

import javax.annotation.Nullable;

public class SplatCraftDamageSource extends EntityDamageSourceIndirect
{
	
	public static final DamageSource VOID_DAMAGE = (new DamageSource("outOfWorld")).setDamageBypassesArmor();
	
	public SplatCraftDamageSource(String damageTypeIn, Entity source, @Nullable Entity indirectEntityIn)
	{
		super(damageTypeIn, source, indirectEntityIn);
	}
	
	@Override
	public ITextComponent getDeathMessage(EntityLivingBase entityLivingBaseIn)
	{
		ITextComponent itextcomponent = this.getTrueSource() == null ? this.damageSourceEntity.getDisplayName() : this.getTrueSource().getDisplayName();
		ItemStack itemstack = this.getTrueSource() instanceof EntityLivingBase ? ((EntityLivingBase)this.getTrueSource()).getHeldItemMainhand() : ItemStack.EMPTY;
		String s = "death.attack." + this.damageType;
		String s1 = s + ".item";
		return !itemstack.isEmpty() && I18n.canTranslate(s1) ? new TextComponentTranslation(s1, new Object[] {entityLivingBaseIn.getDisplayName(), itextcomponent, itemstack.getTextComponent()}) :
				new TextComponentTranslation(s, new Object[] {entityLivingBaseIn.getDisplayName(), itextcomponent});
	}
}
