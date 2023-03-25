package net.splatcraft.forge.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.splatcraft.forge.client.handlers.SplatcraftKeyHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class WeaponHotkeyMixin
{

	@Mixin(PlayerController.class)
	public static abstract class PlayerControllerMix
	{
		@Inject(method = "releaseUsingItem", at = @At("HEAD"), cancellable = true)
		private void releaseUsingItem(PlayerEntity player, CallbackInfo callbackInfo)
		{
			if(SplatcraftKeyHandler.subWeaponHotkey.isDown() && player.getUsedItemHand() == Hand.OFF_HAND)
				callbackInfo.cancel();
		}
	}

	@Mixin(Minecraft.class)
	public static abstract class MinecraftInstance
	{
		@Inject(method = "startUseItem", at = @At("HEAD"), cancellable = true)
		private void startUseItem(CallbackInfo ci)
		{
			if(SplatcraftKeyHandler.subWeaponHotkey.isDown())
			{
				SplatcraftKeyHandler.startUsingItemInHand(Hand.OFF_HAND);
				ci.cancel();
			}
		}
	}
}
