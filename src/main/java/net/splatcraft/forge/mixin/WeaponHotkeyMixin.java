package net.splatcraft.forge.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.splatcraft.forge.client.handlers.SplatcraftKeyHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class WeaponHotkeyMixin
{

	@Mixin(MultiPlayerGameMode.class)
	public static abstract class PlayerControllerMix
	{
		@Inject(method = "releaseUsingItem", at = @At("HEAD"), cancellable = true)
		private void releaseUsingItem(Player player, CallbackInfo callbackInfo)
		{
			if (SplatcraftKeyHandler.isSubWeaponHotkeyDown() && player.getUsedItemHand() == InteractionHand.OFF_HAND)
				callbackInfo.cancel();
		}
	}

	@Mixin(Minecraft.class)
	public static abstract class MinecraftInstance
	{
		@Inject(method = "startUseItem", at = @At("HEAD"), cancellable = true)
		private void startUseItem(CallbackInfo ci)
		{
			if (SplatcraftKeyHandler.isSubWeaponHotkeyDown())
			{
				SplatcraftKeyHandler.startUsingItemInHand(InteractionHand.OFF_HAND);
				ci.cancel();
			}
		}
	}
}
