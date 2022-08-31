package net.splatcraft.forge.mixin;

import net.splatcraft.forge.SplatcraftConfig;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.util.InkBlockUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    /**
     * Disables view bobbing if configured.
     */
    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void onBobView(MatrixStack matrices, float f, CallbackInfo ci) {
        if (this.minecraft.player != null) {
            if (PlayerInfoCapability.isSquid(this.minecraft.player)) {
                SplatcraftConfig.PreventBobView value = SplatcraftConfig.Client.preventBobView.get();
                if (value.equals(SplatcraftConfig.PreventBobView.ALWAYS) || value.equals(SplatcraftConfig.PreventBobView.SUBMERGED) && PlayerInfoCapability.isSquid(this.minecraft.player) && InkBlockUtils.canSquidHide(this.minecraft.player))
                    ci.cancel();
            }
        }
    }
}