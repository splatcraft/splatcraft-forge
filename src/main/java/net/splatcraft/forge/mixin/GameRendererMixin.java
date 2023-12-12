package net.splatcraft.forge.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.splatcraft.forge.SplatcraftConfig;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.util.InkBlockUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin
{

    /**
     * Disables view bobbing if configured.
     */
    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void onBobView(PoseStack matrices, float f, CallbackInfo ci) {
        if (Minecraft.getInstance().player != null) {
            if (PlayerInfoCapability.isSquid(Minecraft.getInstance().player)) {
                SplatcraftConfig.PreventBobView value = SplatcraftConfig.Client.preventBobView.get();
                if (value.equals(SplatcraftConfig.PreventBobView.ALWAYS) || value.equals(SplatcraftConfig.PreventBobView.SUBMERGED) && PlayerInfoCapability.isSquid(Minecraft.getInstance().player) && InkBlockUtils.canSquidHide(Minecraft.getInstance().player))
                    ci.cancel();
            }
        }
    }
}