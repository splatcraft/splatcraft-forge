package net.splatcraft.forge.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.splatcraft.forge.data.capabilities.playerinfo.IPlayerInfo;
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
		if(!(((Object)this) instanceof LivingEntity))
			return;

		LivingEntity entity = ((LivingEntity)(Object)this);

		IPlayerInfo info = PlayerInfoCapability.get(entity);

		if(info != null && InkBlockUtils.canSquidHide(entity) && info.isSquid())
			cir.setReturnValue(true);
	}

	@Inject(method = "setSprinting", at = @At("HEAD"), cancellable = true)
	public void setSprinting(boolean p_70031_1_, CallbackInfo ci)
	{
		if(!(((Object)this) instanceof PlayerEntity))
			return;
		if(p_70031_1_ && PlayerCooldown.hasPlayerCooldown(((PlayerEntity)(Object)this)))
		{
			ci.cancel();
			((PlayerEntity)(Object)this).setSprinting(false);
		}

	}
}
