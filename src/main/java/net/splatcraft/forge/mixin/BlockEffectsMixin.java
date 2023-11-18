package net.splatcraft.forge.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.InkBlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

public class BlockEffectsMixin
{
	@Mixin(LivingEntity.class)
	public static class LivingEntityMixin
	{
		@WrapOperation(method = "checkFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;addLandingEffects(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/LivingEntity;I)Z"))
		public boolean addLandingEffects(BlockState state, ServerLevel level, BlockPos pos, BlockState blockState, LivingEntity entity, int i, Operation<Boolean> original)
		{
			if(InkBlockUtils.isInked(level, pos))
			{
				ColorUtils.addInkSplashParticle(level, InkBlockUtils.getInk(level, pos).color(), entity.getX(), entity.getY(level.getRandom().nextFloat() * 0.3f), entity.getZ(), (float) (Math.sqrt(i) * 0.3f));
				return true;
			}
			return original.call(state, level, pos, blockState, entity, i);
		}

		@WrapOperation(method = "playBlockFallSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getSoundType(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/level/block/SoundType;"))
		public SoundType getFallSound(BlockState state, LevelReader levelReader, BlockPos pos, Entity entity, Operation<SoundType> original)
		{
			if(levelReader instanceof Level level && InkBlockUtils.isInked(level, pos))
			{
				return SplatcraftSounds.SOUND_TYPE_INK;
			}
			return original.call(state, levelReader, pos, entity);
		}
	}

	@Mixin(Entity.class)
	public static class EntityMixin
	{
		@WrapOperation(method = "spawnSprintParticle", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;addRunningEffects(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)Z"))
		public boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity, Operation<Boolean> original)
		{

			if(InkBlockUtils.isInked(level, pos))
			{
				ColorUtils.addInkSplashParticle(level, InkBlockUtils.getInk(level, pos).color(), entity.getX() + level.getRandom().nextFloat() * entity.getBbWidth() - entity.getBbWidth() * 0.5,
						entity.getY(level.getRandom().nextFloat() * 0.3f), entity.getZ() + level.getRandom().nextFloat() * entity.getBbWidth() - entity.getBbWidth() * 0.5, level.random.nextFloat(0.3f, 0.7f));
				return true;
			}

			return original.call(state, level, pos, entity);
		}

		@WrapOperation(method = "playStepSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getSoundType(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/level/block/SoundType;"))
		public SoundType getRunningSound(BlockState state, LevelReader levelReader, BlockPos pos, Entity entity, Operation<SoundType> original)
		{
			if(levelReader instanceof Level level && InkBlockUtils.isInked(level, pos))
			{
				return entity instanceof LivingEntity player && PlayerInfoCapability.isSquid(player) && InkBlockUtils.canSquidSwim(player) ?
						SplatcraftSounds.SOUND_TYPE_SWIMMING : SplatcraftSounds.SOUND_TYPE_INK;
			}
			return original.call(state, levelReader, pos, entity);
		}
	}
}
