package com.cibernet.splatcraft.recipes;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.registries.SplatCraftItems;
import com.cibernet.splatcraft.utils.InkColors;
import net.minecraft.block.material.MapColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.EnumHelper;

public enum WeaponStationTabs
{
	TAB_SHOOTER("shooter"),
	TAB_ROLLER("roller"),
	TAB_CHARGER("charger"),
	TAB_INK_TANKS("tank");
	;
	
	String name;
	ResourceLocation icon;
	
	WeaponStationTabs(String displayName, ResourceLocation icon)
	{
		name = displayName;
		this.icon = icon;
	}
	
	WeaponStationTabs(String displayName) {this(displayName, new ResourceLocation(SplatCraft.MODID, "textures/gui/icons/"+displayName+".png"));}
	
	public String getUnlocalizedName()
	{
		return "stationTab." + name;
	}
	
	public ResourceLocation getIconLocation() { return icon; }
	
	public static WeaponStationTabs addTab(String name, String displayName, ResourceLocation iconLoc)
	{
		return EnumHelper.addEnum(WeaponStationTabs.class, name, new Class[] {String.class, ResourceLocation.class}, displayName, iconLoc);
	}
}
