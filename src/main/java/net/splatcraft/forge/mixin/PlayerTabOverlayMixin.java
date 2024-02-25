package net.splatcraft.forge.mixin;

import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.splatcraft.forge.SplatcraftConfig;
import net.splatcraft.forge.util.ClientUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerTabOverlay.class)
public class PlayerTabOverlayMixin
{

	@Inject(method = "decorateName", at = @At("HEAD"))
	public void decorateName(PlayerInfo playerInfo, MutableComponent component, CallbackInfoReturnable<Component> cir)
	{
		if(SplatcraftConfig.Client.coloredPlayerNames.get())
			component.setStyle(component.getStyle().withColor(ClientUtils.getClientPlayerColor(playerInfo.getProfile().getId())));
	}

}
