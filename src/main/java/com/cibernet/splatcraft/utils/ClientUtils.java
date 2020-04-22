package com.cibernet.splatcraft.utils;


import com.cibernet.splatcraft.items.ItemInkTank;
import com.cibernet.splatcraft.items.ItemWeaponBase;
import com.cibernet.splatcraft.world.save.SplatCraftPlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientUtils
{
	@SideOnly(Side.CLIENT)
	public static double getDurabilityForDisplay(ItemStack stack)
	{
			EntityPlayerSP player = Minecraft.getMinecraft().player;
			ItemStack chestpiece = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
			if(chestpiece.getItem() instanceof ItemInkTank && ItemWeaponBase.hasInk(player, ColorItemUtils.getInkColor(stack), 0))
				return 1- ItemInkTank.getInkAmount(chestpiece) / ((ItemInkTank) chestpiece.getItem()).capacity;
			return 1;
	}
	
	@SideOnly(Side.CLIENT)
	public static boolean showDurabilityBar(ItemStack stack)
	{
		return Minecraft.getMinecraft().player.getHeldItemMainhand().equals(stack);
	}
}
