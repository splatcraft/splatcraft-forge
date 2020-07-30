package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.data.tags.SplatcraftTags;
import com.cibernet.splatcraft.registries.SplatcraftItemGroups;
import com.cibernet.splatcraft.util.SplatcraftArmorMaterial;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

import java.util.ArrayList;
import java.util.List;

public class InkTankItem extends ColoredArmorItem
{
	private final List<Item> weaponWhitelist = new ArrayList<>();
	private final List<Item> weaponBlacklist = new ArrayList<>();
	
	public final float capacity;
	public final Properties properties;
	
	public InkTankItem(String name, float capacity, IArmorMaterial material, Properties properties)
	{
		super(name, material, EquipmentSlotType.CHEST, properties);
		this.capacity = capacity;
		this.properties = properties;
		
		SplatcraftTags.Items.putInkTankTags(this, name);
		
		//todo texture overrides
	}
	
	public InkTankItem(String name, float capacity, IArmorMaterial material)
	{
		this(name, capacity, material, new Properties().group(SplatcraftItemGroups.GROUP_WEAPONS).maxStackSize(1));
		
	}
	
	public InkTankItem(String name, InkTankItem parent)
	{
		this(name, parent.capacity, parent.material, parent.properties);
	}
	
	public InkTankItem(String name, float capacity)
	{
		this(name, capacity, new SplatcraftArmorMaterial(name, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN));
	}
	
	public boolean canUse(Item item)
	{
		boolean hasWhitelist = SplatcraftTags.Items.INK_TANK_WHITELIST.get(this).getAllElements().size() > 0;
		boolean inWhitelist = SplatcraftTags.Items.INK_TANK_WHITELIST.get(this).contains(item);
		boolean inBlacklist = SplatcraftTags.Items.INK_TANK_BLACKLIST.get(this).contains(item);
		
		return !inBlacklist && ((hasWhitelist && inWhitelist) || !hasWhitelist);
	}
	
}
