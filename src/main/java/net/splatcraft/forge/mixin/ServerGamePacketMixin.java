package net.splatcraft.forge.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.splatcraft.forge.commands.SuperJumpCommand;
import net.splatcraft.forge.util.PlayerCooldown;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketMixin
{

	//Hijacking move packet to prevent players from being kicked out for flying
	//Please forge make an event for this or something >_>

	@WrapOperation(method = "handleMovePlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isChangingDimension()Z"))
	public boolean isChangingDimOrSuperjumping(ServerPlayer player, Operation<Boolean> original)
	{
		return original.call(player) || PlayerCooldown.getPlayerCooldown(player) instanceof SuperJumpCommand.SuperJump;
	}

	@WrapOperation(method = "handleMovePlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isAutoSpinAttack()Z"))
	public boolean isSpinninggOrSuperJumping(ServerPlayer player, Operation<Boolean> original)
	{
		return original.call(player) || PlayerCooldown.getPlayerCooldown(player) instanceof SuperJumpCommand.SuperJump;
	}


}
