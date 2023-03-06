package net.splatcraft.forge.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.management.PlayerList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.splatcraft.forge.tileentities.SpawnPadTileEntity;
import net.splatcraft.forge.util.ColorUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(PlayerList.class)
public abstract class ServerPlayerMixin
{

	private boolean canRespawn = true;

	//@Inject(method = "getRespawnPosition", at = @At(value = "TAIL"), cancellable = true)
	@Redirect(method = "respawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ServerPlayerEntity;getRespawnPosition()Lnet/minecraft/util/math/BlockPos;"))
	public BlockPos getRespawnPosition(ServerPlayerEntity instance)
	{
		BlockPos res = instance.getRespawnPosition();

		if(res == null)
			return res;

		TileEntity te = instance.getLevel().getBlockEntity(res);

		canRespawn = !(te instanceof SpawnPadTileEntity) || ColorUtils.colorEquals(instance, te);

		return res;
	}

	@Redirect(method = "respawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;findRespawnPositionAndUseSpawnBlock(Lnet/minecraft/world/server/ServerWorld;Lnet/minecraft/util/math/BlockPos;FZZ)Ljava/util/Optional;"))
	public Optional<Vector3d> respawn(ServerWorld optional, BlockPos flag, float flag1, boolean p_242374_0_, boolean p_242374_1_)
	{
		if(!canRespawn)
			return Optional.empty();
		return PlayerEntity.findRespawnPositionAndUseSpawnBlock(optional, flag, flag1, p_242374_0_, p_242374_1_);
	}
}
