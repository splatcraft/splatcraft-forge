package net.splatcraft.forge.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.splatcraft.forge.data.SplatcraftTags;
import net.splatcraft.forge.data.capabilities.worldink.WorldInk;
import net.splatcraft.forge.data.capabilities.worldink.WorldInkCapability;
import net.splatcraft.forge.handlers.WorldInkHandler;
import net.splatcraft.forge.registries.SplatcraftBlocks;
import net.splatcraft.forge.util.InkBlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

//TODO use RenderLevelStageEvent to render ink over blocks instead of overriding block rendering with mixins,
// this may have been a bad idea for compatibility
@OnlyIn(Dist.CLIENT)
public class BlockRenderMixin
{
	@Mixin(ModelBlockRenderer.class)
	public static class Renderer
	{
		@Inject(method = "putQuadData", cancellable = true, at = @At(value = "INVOKE", shift = At.Shift.BEFORE,
				target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;putBulkData(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lnet/minecraft/client/renderer/block/model/BakedQuad;[FFFF[IIZ)V"))
		public void getBlockPosFromQuad(BlockAndTintGetter level, BlockState blockState, BlockPos blockPos, VertexConsumer consumer, PoseStack.Pose pose, BakedQuad quad, float p_111030_, float p_111031_, float p_111032_, float p_111033_, int p_111034_, int p_111035_, int p_111036_, int p_111037_, int p_111038_, CallbackInfo ci)
		{
			if(level instanceof RenderChunkRegion region && WorldInkHandler.Render.splatcraft$renderInkedBlock(region, blockPos, consumer, pose, quad, new float[]{p_111030_, p_111031_, p_111032_, p_111033_}, new int[]{p_111034_, p_111035_, p_111036_, p_111037_}, p_111038_, true))
				ci.cancel();
		}
	}

	@Mixin(ChunkRenderDispatcher.RenderChunk.RebuildTask.class)
	public static class ChunkRenderDispatcherMixin
	{
		@Unique
		private BlockPos splatcraft$blockPos;
		@Unique
		private Level splatcraft$level;
		@Unique
		private static boolean splatcraft$renderAsCube;

		@Inject(method = "compile", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE",
				target = "Lnet/minecraft/client/renderer/chunk/RenderChunkRegion;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
		public void getBlockData(float p_112866_, float p_112867_, float p_112868_, ChunkRenderDispatcher.CompiledChunk p_112869_, ChunkBufferBuilderPack p_112870_, CallbackInfoReturnable<Set<BlockEntity>> cir, int i, BlockPos blockpos, BlockPos blockpos1, VisGraph visgraph, Set<BlockEntity> set, RenderChunkRegion renderchunkregion, PoseStack posestack, Random random, BlockRenderDispatcher blockrenderdispatcher, Iterator<BlockPos> var15, BlockPos blockpos2)
		{
			splatcraft$level = ((ChunkRegionAccessor)renderchunkregion).getLevel();
			splatcraft$blockPos = blockpos2;
			splatcraft$renderAsCube = InkBlockUtils.isInked(splatcraft$level, splatcraft$blockPos) && splatcraft$level.getBlockState(splatcraft$blockPos).is(SplatcraftTags.Blocks.RENDER_AS_CUBE);
		}

		@WrapOperation(method = "compile", at = @At(value = "INVOKE",
				target = "Lnet/minecraft/client/renderer/ItemBlockRenderTypes;canRenderInLayer(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/client/renderer/RenderType;)Z"))
		public boolean canRenderInLayer(BlockState state, RenderType type, Operation<Boolean> original)
		{
			if(WorldInkCapability.get(splatcraft$level, splatcraft$blockPos).isInked(splatcraft$blockPos))
			{
				WorldInk.Entry ink = WorldInkCapability.get(splatcraft$level, splatcraft$blockPos).getInk(splatcraft$blockPos);

				if(ink.type() == InkBlockUtils.InkType.GLOWING)
					return type == RenderType.translucent();
				else if(ink.type() == InkBlockUtils.InkType.NORMAL)
					return type == RenderType.solid();
			}
			return original.call(state, type);
		}

		@WrapOperation(method = "compile", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/RenderChunkRegion;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
		public BlockState getBlockState(RenderChunkRegion region, BlockPos pos, Operation<BlockState> original)
		{
			return splatcraft$renderAsCube ? SplatcraftBlocks.inkedBlock.get().defaultBlockState() : original.call(region, pos);
		}
	}

	@Mixin(RenderChunkRegion.class)
	public interface ChunkRegionAccessor
	{
		@Accessor("level")
		Level getLevel();
	}
}
