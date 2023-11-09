package net.splatcraft.forge.mixin.compat;


import me.jellysquid.mods.sodium.client.model.IndexBufferBuilder;
import me.jellysquid.mods.sodium.client.model.light.data.QuadLightData;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import me.jellysquid.mods.sodium.client.model.quad.blender.ColorSampler;
import me.jellysquid.mods.sodium.client.model.quad.properties.ModelQuadOrientation;
import me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import me.jellysquid.mods.sodium.client.render.chunk.format.ModelVertexSink;
import me.jellysquid.mods.sodium.client.render.pipeline.BlockRenderer;
import me.jellysquid.mods.sodium.client.util.color.ColorABGR;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.splatcraft.forge.handlers.WorldInkHandler;
import net.splatcraft.forge.registries.SplatcraftInkColors;
import net.splatcraft.forge.util.ColorUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BlockRenderer.class)
public class RubidiumMixin
{
	@Unique
	private BlockPos splatcraft$pos;
	@Unique
	private Direction.Axis splatcraft$axis;
	@Unique
	private QuadLightData splatcraft$lightData;
	@Unique
	private int splatcraft$vertexIndex;

	@Unique
	private float splatcraft$x;
	@Unique
	private float splatcraft$y;
	@Unique
	private float splatcraft$z;

	@Inject(method = "renderQuad", remap = false, locals = LocalCapture.CAPTURE_FAILHARD,at = @At(value = "INVOKE", shift = At.Shift.BEFORE,
			target = "Lme/jellysquid/mods/sodium/client/render/chunk/format/ModelVertexSink;writeVertex(Lnet/minecraft/core/Vec3i;FFFIFFII)V"))
	public void getBlockPosFromQuad(BlockAndTintGetter world, BlockState state, BlockPos pos, BlockPos origin, ModelVertexSink vertices, IndexBufferBuilder indices, Vec3 blockOffset, ColorSampler<BlockState> colorSampler, BakedQuad bakedQuad, QuadLightData light, ChunkModelBuilder model, CallbackInfo ci, ModelQuadView src, ModelQuadOrientation orientation, int[] colors, int vertexStart, int i, int j, float x, float y, float z, int color, float u, float v, int lm)
	{
		this.splatcraft$pos = pos;
		this.splatcraft$axis = bakedQuad.getDirection().getAxis();
		this.splatcraft$lightData = light;
		this.splatcraft$vertexIndex = j;
		this.splatcraft$x = x;
		this.splatcraft$y = y;
		this.splatcraft$z = z;
	}


	@ModifyArg(method = "renderQuad", remap = false, index = 4, at = @At(value = "INVOKE",
			target = "Lme/jellysquid/mods/sodium/client/render/chunk/format/ModelVertexSink;writeVertex(Lnet/minecraft/core/Vec3i;FFFIFFII)V"))
	public int modifyVertexColor(int originalColor)
	{
		float[] rgb = ColorUtils.hexToRGB(SplatcraftInkColors.cobalt.getColor());



		return ColorABGR.mul(ColorABGR.pack(rgb[0] * 255, rgb[1] * 255, rgb[2] * 255), splatcraft$lightData.br[splatcraft$vertexIndex]);
	}

	@ModifyArg(method = "renderQuad", remap = false, index = 5, at = @At(value = "INVOKE",
			target = "Lme/jellysquid/mods/sodium/client/render/chunk/format/ModelVertexSink;writeVertex(Lnet/minecraft/core/Vec3i;FFFIFFII)V"))
	public float modifyVertexU(float posX)
	{
		TextureAtlasSprite sprite = WorldInkHandler.Render.INKED_BLOCK_SPRITE;
		return sprite.getU0() + (splatcraft$axis.equals(Direction.Axis.X) ? splatcraft$z : splatcraft$x)*(sprite.getU1()-sprite.getU0());
	}

	@ModifyArg(method = "renderQuad", remap = false, index = 6, at = @At(value = "INVOKE",
			target = "Lme/jellysquid/mods/sodium/client/render/chunk/format/ModelVertexSink;writeVertex(Lnet/minecraft/core/Vec3i;FFFIFFII)V"))
	public float modifyVertexV(float posY)
	{
		TextureAtlasSprite sprite = WorldInkHandler.Render.INKED_BLOCK_SPRITE;
		return sprite.getV0() + (splatcraft$axis.equals(Direction.Axis.Y) ? splatcraft$z : splatcraft$y)*(sprite.getV1()-sprite.getV0());
	}

	/* java.lang.NoClassDefFoundError: org/spongepowered/asm/synthetic/args/Args$1 -._-.
	@ModifyArgs(method = "renderQuad", remap = false, at = @At(value = "INVOKE",
	target = "Lme/jellysquid/mods/sodium/client/render/chunk/format/ModelVertexSink;writeVertex(Lnet/minecraft/core/Vec3i;FFFIFFII)V"))
	public void writeVertex(Args args)
	{
		///color = 4
		//u = 5
		//v = 6

		int color = ColorABGR.mul(SplatcraftInkColors.orange.getColor(), splatcraft$lightData.br[splatcraft$vertexIndex]);
		float x = args.get(0);
		float y = args.get(1);
		float z = args.get(2);

		TextureAtlasSprite sprite = WorldInkHandler.Render.SPRITE;

		float texU = sprite.getU0() + (splatcraft$axis.equals(Direction.Axis.X) ? z : x)*(sprite.getU1()-sprite.getU0());
		float texV = sprite.getV0() + (splatcraft$axis.equals(Direction.Axis.Y) ? z : y)*(sprite.getV1()-sprite.getV0());

		args.set(4, color);
		args.set(5, texU);
		args.set(6, texV);
	}
	*/
}
