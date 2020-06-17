package com.cibernet.splatcraft.utils;


import com.cibernet.splatcraft.entities.models.ModelAbstractTank;
import com.cibernet.splatcraft.entities.models.ModelInkTank;
import com.cibernet.splatcraft.items.ItemInkTank;
import com.cibernet.splatcraft.items.ItemWeaponBase;
import com.cibernet.splatcraft.world.save.SplatCraftGamerules;
import com.cibernet.splatcraft.world.save.SplatCraftPlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ClientUtils
{
	public static ModelBiped getInkTankModel(ModelAbstractTank model, EntityLivingBase entity, ItemStack stack, EntityEquipmentSlot slot, ModelBiped _default)
	{
		if(!(stack.getItem() instanceof ItemInkTank))
			return null;
		
		if(model == null)
			return null;
		
		//model.setInk(ItemInkTank.getInkAmount(stack)/ ((ItemInkTank) stack.getItem()).capacity);
		
		if(!stack.isEmpty())
		{
			if(stack.getItem() instanceof ItemInkTank)
			{
				model.bipedRightLeg.showModel = slot == EntityEquipmentSlot.LEGS || slot == EntityEquipmentSlot.FEET;
				model.bipedLeftLeg.showModel = slot == EntityEquipmentSlot.LEGS || slot == EntityEquipmentSlot.FEET;
				
				model.bipedBody.showModel = slot == EntityEquipmentSlot.CHEST;
				model.bipedLeftArm.showModel = slot == EntityEquipmentSlot.CHEST;
				model.bipedRightArm.showModel = slot == EntityEquipmentSlot.CHEST;
				
				model.bipedHead.showModel = slot == EntityEquipmentSlot.HEAD;
				model.bipedHeadwear.showModel = slot == EntityEquipmentSlot.HEAD;
				
				
				model.isSneak = _default.isSneak;
				model.isRiding = _default.isRiding;
				model.isChild = _default.isChild;
				
				model.rightArmPose = _default.rightArmPose;
				model.leftArmPose = _default.leftArmPose;
				
				return model;
			}
		}
		
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	public static double getDurabilityForDisplay(ItemStack stack)
	{
			EntityPlayerSP player = Minecraft.getMinecraft().player;
			ItemStack chestpiece = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
			if(chestpiece.getItem() instanceof ItemInkTank && ItemWeaponBase.hasInk(player, stack, 0))
				return 1- ItemInkTank.getInkAmount(chestpiece) / ((ItemInkTank) chestpiece.getItem()).capacity;
			return 1;
	}
	
	@SideOnly(Side.CLIENT)
	public static boolean showDurabilityBar(ItemStack stack)
	{
		return SplatCraftGamerules.getGameruleValue("requireInkTank") && (Minecraft.getMinecraft().player.getHeldItemMainhand().equals(stack) || Minecraft.getMinecraft().player.getHeldItemOffhand().equals(stack));
	}
	
	@SideOnly(Side.CLIENT)
	public static EntityPlayer getClientPlayer()
	{
		return Minecraft.getMinecraft().player;
	}
	
	@SideOnly(Side.CLIENT)
	public static boolean getFancyGraphics()
	{
		return Minecraft.getMinecraft().gameSettings.fancyGraphics;
	}
}
