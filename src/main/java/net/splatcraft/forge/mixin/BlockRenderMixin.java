package net.splatcraft.forge.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;
import net.splatcraft.forge.data.capabilities.worldink.WorldInk;
import net.splatcraft.forge.data.capabilities.worldink.WorldInkCapability;
import net.splatcraft.forge.util.InkBlockUtils;
import net.splatcraft.forge.util.MixinDataHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class BlockRenderMixin
{
	@Mixin(ModelBlockRenderer.class)
	public static class Renderer
	{
		@Unique
		BlockPos splatcraft$pos;
		@Unique
		BlockAndTintGetter splatcraft$level;

		@Inject(method = "putQuadData", at = @At(value = "INVOKE", shift = At.Shift.BEFORE,
				target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;putBulkData(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lnet/minecraft/client/renderer/block/model/BakedQuad;[FFFF[IIZ)V"))
		public void getBlockPosFromQuad(BlockAndTintGetter level, BlockState p_111025_, BlockPos pos, VertexConsumer p_111027_, PoseStack.Pose p_111028_, BakedQuad p_111029_, float p_111030_, float p_111031_, float p_111032_, float p_111033_, int p_111034_, int p_111035_, int p_111036_, int p_111037_, int p_111038_, CallbackInfo ci)
		{
			this.splatcraft$pos = pos;
			this.splatcraft$level = level;
		}


		@Redirect(method = "putQuadData", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;putBulkData(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lnet/minecraft/client/renderer/block/model/BakedQuad;[FFFF[IIZ)V"))
		public void redirectBulkData(VertexConsumer consumer, PoseStack.Pose pose, BakedQuad quad, float[] f0, float r, float g, float b, int[] f1, int f2, boolean f3)
		{
			if(splatcraft$level instanceof RenderChunkRegion region && !MixinDataHolder.BlockRenderer.splatcraft$renderInkedBlock(region, splatcraft$pos, consumer, pose, quad, f0, f1, f2, f3))
				consumer.putBulkData(pose, quad, f0, r, g, b, f1, f2, f3);
		}
	}

	@Mixin(ChunkRenderDispatcher.RenderChunk.RebuildTask.class)
	public static class ChunkRenderDispatcherMixin
	{
		@Unique
		private BlockPos splatcraft$blockPos;
		@Unique
		private Level splatcraft$level;

		@Inject(method = "compile", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE",
				target = "Lnet/minecraft/client/renderer/ItemBlockRenderTypes;canRenderInLayer(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/client/renderer/RenderType;)Z"))
		public void getBlockData(float p_112866_, float p_112867_, float p_112868_, ChunkRenderDispatcher.CompiledChunk p_112869_, ChunkBufferBuilderPack p_112870_, CallbackInfoReturnable<Set<BlockEntity>> cir, int i, BlockPos blockpos, BlockPos blockpos1, VisGraph visgraph, Set set, RenderChunkRegion renderchunkregion, PoseStack posestack, Random random, BlockRenderDispatcher blockrenderdispatcher, Iterator var15, BlockPos blockpos2, BlockState blockstate, BlockState blockstate1, FluidState fluidstate, IModelData modelData, Iterator var21, RenderType rendertype)
		{
			splatcraft$level = ((ChunkRegionAccessor)renderchunkregion).getLevel();

			splatcraft$blockPos = blockpos2;
		}

		@Redirect(method = "compile", at = @At(value = "INVOKE",
				target = "Lnet/minecraft/client/renderer/ItemBlockRenderTypes;canRenderInLayer(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/client/renderer/RenderType;)Z"))
		public boolean canRenderInLayer(BlockState state, RenderType type)
		{
			if(WorldInkCapability.get(splatcraft$level, splatcraft$blockPos).isInked(splatcraft$blockPos))
			{
				WorldInk.Entry ink = WorldInkCapability.get(splatcraft$level, splatcraft$blockPos).getInk(splatcraft$blockPos);

				if(ink.type() == InkBlockUtils.InkType.GLOWING)
					return type == RenderType.translucent();
				else if(ink.type() == InkBlockUtils.InkType.NORMAL)
					return type == RenderType.solid();
			}
			return ItemBlockRenderTypes.canRenderInLayer(state, type);
		}
	}

	@Mixin(ModelBlockRenderer.AmbientOcclusionFace.class)
	public interface AOFace
	{
		@Accessor
		int[] getLightmap();
		@Accessor
		float[] getBrightness();
	}

	@Mixin(ModelBlockRenderer.class)
	public interface ModelRenderer
	{
		@Invoker("calculateShape")
		void invokeCalculateShape(BlockAndTintGetter world, BlockState state, BlockPos pos, int[] vertices, Direction direction, @Nullable float[] afloat, BitSet bitset);
	}

	@Mixin(RenderChunkRegion.class)
	public interface ChunkRegionAccessor
	{
		@Accessor("level")
		Level getLevel();
	}
}
