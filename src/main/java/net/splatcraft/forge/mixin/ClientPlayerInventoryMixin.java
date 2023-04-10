package net.splatcraft.forge.mixin;

import net.minecraft.world.entity.player.Inventory;
import net.splatcraft.forge.client.handlers.SplatcraftKeyHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Inventory.class)
public class ClientPlayerInventoryMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    public void resetCanUseHotkeys(CallbackInfo ci) {
        SplatcraftKeyHandler.canUseHotkeys = true;
    }
}
