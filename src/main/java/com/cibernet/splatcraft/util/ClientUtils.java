package com.cibernet.splatcraft.util;

import com.cibernet.splatcraft.SplatcraftConfig;
import com.cibernet.splatcraft.items.InkTankItem;
import com.cibernet.splatcraft.items.weapons.WeaponBaseItem;
import com.cibernet.splatcraft.registries.SplatcraftGameRules;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class ClientUtils
{
	public static PlayerEntity getClientPlayer()
	{
		return Minecraft.getInstance().player;
	}
	
	public static boolean showDurabilityBar(ItemStack stack)
	{
		return(SplatcraftConfig.Client.inkIndicator.get().equals(SplatcraftConfig.InkIndicator.BOTH) || SplatcraftConfig.Client.inkIndicator.get().equals(SplatcraftConfig.InkIndicator.DURABILITY)) &&
				getClientPlayer().getHeldItem(Hand.MAIN_HAND).equals(stack) && getDurabilityForDisplay(stack) > 0;
	}
	
	public static double getDurabilityForDisplay(ItemStack stack)
	{
		PlayerEntity player = getClientPlayer();
		
		if(!SplatcraftGameRules.getBooleanRuleValue(player.world, SplatcraftGameRules.REQUIRE_INK_TANK))
			return 0;
			
		ItemStack chestpiece = player.getItemStackFromSlot(EquipmentSlotType.CHEST.CHEST);
		if(chestpiece.getItem() instanceof InkTankItem)
			return 1- WeaponBaseItem.getInkAmount(player, stack) / ((InkTankItem) chestpiece.getItem()).capacity;
		return 1;
	}
	
	public static void playClientSound(BlockPos pos, SoundEvent soundIn, SoundCategory category, float volume, float pitch)
	{
		Minecraft.getInstance().getSoundHandler().play(new ClientPlayerSound(soundIn, category, volume, pitch));
	}
	
	public static boolean canPerformRoll(PlayerEntity entity)
	{
		MovementInput input = ((ClientPlayerEntity)entity).movementInput;
		
		return !PlayerCooldown.hasPlayerCooldown(entity) && input.jump && (input.moveStrafe != 0 || input.moveForward != 0);
	}
	
	public static Vector3d getDodgeRollVector(PlayerEntity entity)
	{
		MovementInput input = ((ClientPlayerEntity)entity).movementInput;
		return new Vector3d(input.moveStrafe, -0.4f, input.moveForward);
	}
}
