package net.splatcraft.forge.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.util.InkBlockUtils;
import net.splatcraft.forge.util.PlayerCooldown;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin
{
	@Inject(method = "isInvisible", at = @At("TAIL"), cancellable = true)
	public void isInvisible(CallbackInfoReturnable<Boolean> cir)
	{
		Entity entity = (Entity) (Object) this;
		if (!(entity instanceof Player player) || !PlayerInfoCapability.hasCapability(player)) {
			return;
		}

		if (InkBlockUtils.canSquidHide(player) && PlayerInfoCapability.get(player).isSquid())
			cir.setReturnValue(true);
	}

	@Inject(method = "setSprinting", at = @At("HEAD"), cancellable = true)
	public void setSprinting(boolean p_70031_1_, CallbackInfo ci)
	{
		Entity entity = (Entity) (Object) this;
		if (!(entity instanceof Player player) || !PlayerInfoCapability.hasCapability(player)) {
			return;
		}
		if (p_70031_1_ && PlayerCooldown.hasPlayerCooldown(player))
		{
			player.setSprinting(false);
			ci.cancel();
		}
	}
}
