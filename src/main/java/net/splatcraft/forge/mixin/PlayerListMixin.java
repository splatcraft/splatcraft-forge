package net.splatcraft.forge.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.phys.Vec3;
import net.splatcraft.forge.tileentities.SpawnPadTileEntity;
import net.splatcraft.forge.util.ColorUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin
{
	@Unique
	private boolean splatcraft$canRespawn = true;

	@Inject(method = "respawn", at = @At(value = "HEAD"))
	public void getRespawnPosition(ServerPlayer instance, boolean p_11238_, CallbackInfoReturnable<ServerPlayer> cir)
	{
		BlockPos res = instance.getRespawnPosition();

		if(res != null)
		{
			if(instance.server.getLevel(instance.getRespawnDimension()).getBlockEntity(res) instanceof SpawnPadTileEntity te)
			{
				instance.reviveCaps();
				splatcraft$canRespawn = ColorUtils.colorEquals(instance, te);
				instance.invalidateCaps();
				return;
			}
		}
		splatcraft$canRespawn = true;
	}

	@WrapOperation(method = "respawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;findRespawnPositionAndUseSpawnBlock(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;FZZ)Ljava/util/Optional;"))
	public Optional<Vec3> respawn(ServerLevel level, BlockPos flag, float flag1, boolean p_242374_0_, boolean p_242374_1_, Operation<Optional<Vec3>> original)
	{
		if (!splatcraft$canRespawn)
			return Optional.empty();
		return original.call(level, flag, flag1, p_242374_0_, p_242374_1_);
	}
}
