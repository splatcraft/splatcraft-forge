package net.splatcraft.forge.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.splatcraft.forge.registries.SplatcraftEntities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public class PlayerMixin
{
	@ModifyReturnValue(method = "createAttributes", at = @At("RETURN"))
	private static AttributeSupplier.Builder createAttributes(AttributeSupplier.Builder original)
	{
		return SplatcraftEntities.injectPlayerAttributes(original);
	}
}
