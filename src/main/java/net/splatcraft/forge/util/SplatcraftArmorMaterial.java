package net.splatcraft.forge.util;

import net.splatcraft.forge.Splatcraft;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class SplatcraftArmorMaterial implements IArmorMaterial
{

    private final int durability;
    private final int damageReduction;
    private final int enchantability;
    private final SoundEvent soundEvent;
    private final Ingredient repairMaterial;
    private final String name;
    private final float toughness;
    private final float knockbackResistance;

    public SplatcraftArmorMaterial(String name, int durability, int damageReduction, float toughness, float knockbackResistance, int enchantability, SoundEvent equipSound, Ingredient repairMaterial)
    {
        this.name = new ResourceLocation(Splatcraft.MODID, name).toString();
        this.durability = durability;
        this.damageReduction = damageReduction;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.enchantability = enchantability;
        this.soundEvent = equipSound;
        this.repairMaterial = repairMaterial;
    }

    public SplatcraftArmorMaterial(String name, SoundEvent equipSound, int armor, float toughness, float knockbackResistance)
    {
        this(name, -1, armor, toughness, knockbackResistance, 0, equipSound, null);
    }

    public SplatcraftArmorMaterial(String name, SplatcraftArmorMaterial parent)
    {
        this(name, parent.soundEvent, parent.damageReduction, parent.toughness, parent.knockbackResistance);
    }

    @Override
    public int getDurabilityForSlot(EquipmentSlotType slotIn)
    {
        return durability;
    }

    @Override
    public int getDefenseForSlot(EquipmentSlotType slotIn)
    {
        return damageReduction;
    }

    @Override
    public int getEnchantmentValue()
    {
        return enchantability;
    }

    @Override
    public SoundEvent getEquipSound()
    {
        return soundEvent;
    }

    @Override
    public Ingredient getRepairIngredient()
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
