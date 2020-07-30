package com.cibernet.splatcraft.util;

import com.cibernet.splatcraft.Splatcraft;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;

public class SplatcraftArmorMaterial implements IArmorMaterial
{
	
	private int durability;
	private int damageReduction;
	private int enchantability;
	private SoundEvent soundEvent;
	private Ingredient repairMaterial;
	private String name;
	private float toughness;
	private float knockbackResistance;
	
	public SplatcraftArmorMaterial(String name, int durability, int damageReduction, float toughness, float knockbackResistance, int enchantability, SoundEvent equipSound, Ingredient repairMaterial)
	{
		this.name = Splatcraft.MODID + ":" + name;
		this.durability = durability;
		this.damageReduction = damageReduction;
		this.toughness = toughness;
		this.knockbackResistance = knockbackResistance;
		this.enchantability = enchantability;
		this.soundEvent = equipSound;
		this.repairMaterial = repairMaterial;
	}
	
	public SplatcraftArmorMaterial(String name, SoundEvent equipSound)
	{
		this(name, -1, 0, 0, 0, 0, equipSound, null);
	}
	
	@Override
	public int getDurability(EquipmentSlotType slotIn)
	{
		return durability;
	}
	
	@Override
	public int getDamageReductionAmount(EquipmentSlotType slotIn)
	{
		return damageReduction;
	}
	
	@Override
	public int getEnchantability()
	{
		return enchantability;
	}
	
	@Override
	public SoundEvent getSoundEvent()
	{
		return soundEvent;
	}
	
	@Override
	public Ingredient getRepairMaterial()
	{
		return repairMaterial;
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public float getToughness()
	{
		return toughness;
	}
	
	@Override
	public float getKnockbackResistance()
	{
		return knockbackResistance;
	}
}
