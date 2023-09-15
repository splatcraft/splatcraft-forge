package net.splatcraft.forge.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.client.renderer.tileentity.StageBarrierTileEntityRenderer;
import net.splatcraft.forge.entities.SpawnShieldEntity;
import net.splatcraft.forge.util.ColorUtils;

import java.util.function.Function;

public class SpawnShieldRenderer extends EntityRenderer<SpawnShieldEntity>
{
	private static final Function<ResourceLocation, RenderType> ENTITY_TRANSLUCENT_CULL = Util.memoize((p_173198_) -> {
		RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder().setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEntityTranslucentShader)).setTextureState(new RenderStateShard.TextureStateShard(p_173198_, false, false)).setTransparencyState(new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		}, () -> {
			RenderSystem.disableBlend();
			RenderSystem.defaultBlendFunc();
		})).setLightmapState(new RenderStateShard.LightmapStateShard(true)).setOverlayState(new RenderStateShard.OverlayStateShard(true)).createCompositeState(true);
		return RenderType.create("entity_translucent_cull", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, rendertype$compositestate);
	});

	public SpawnShieldRenderer(EntityRendererProvider.Context context)
	{
		super(context);
	}

	@Override
	public void render(SpawnShieldEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight)
	{
		float activeTime = entity.getActiveTime();
		float[] rgb = ColorUtils.hexToRGB(entity.getColor());
		float size = entity.getSize();
		float radius = size/2f;

		if (activeTime <= 0)
		{
			return;
		}


		VertexConsumer builder = buffer.getBuffer(ENTITY_TRANSLUCENT_CULL.apply(getTextureLocation(entity)));// buffer.getBuffer(RenderType.entityTranslucentCull(getTextureLocation(entity)));//buffer.getBuffer(Minecraft.useShaderTransparency() ? RenderType.translucentMovingBlock() : RenderType.translucentNoCrumbling());

		float alpha = activeTime / entity.MAX_ACTIVE_TIME;

		addVertex(builder, matrixStack, -radius, size, -radius, 0, size, rgb[0], rgb[1], rgb[2], alpha);
		addVertex(builder, matrixStack, radius, size, -radius, size, size, rgb[0], rgb[1], rgb[2], alpha);
		addVertex(builder, matrixStack, radius, 0, -radius, size, 0, rgb[0], rgb[1], rgb[2], alpha);
		addVertex(builder, matrixStack, -radius, 0, -radius, 0, 0, rgb[0], rgb[1], rgb[2], alpha);


		addVertex(builder, matrixStack, -radius, 0, radius, 0, 0, rgb[0], rgb[1], rgb[2], alpha);
		addVertex(builder, matrixStack, radius, 0, radius, size, 0, rgb[0], rgb[1], rgb[2], alpha);
		addVertex(builder, matrixStack, radius, size, radius, size, size, rgb[0], rgb[1], rgb[2], alpha);
		addVertex(builder, matrixStack, -radius, size, radius, 0, size, rgb[0], rgb[1], rgb[2], alpha);

		addVertex(builder, matrixStack, -radius, 0, -radius, 0, 0, rgb[0], rgb[1], rgb[2], alpha);
		addVertex(builder, matrixStack, -radius, 0, radius, 0, size, rgb[0], rgb[1], rgb[2], alpha);
		addVertex(builder, matrixStack, -radius, size, radius, size, size, rgb[0], rgb[1], rgb[2], alpha);
		addVertex(builder, matrixStack, -radius, size, -radius, size, 0, rgb[0], rgb[1], rgb[2], alpha);


		addVertex(builder, matrixStack, radius, 0, -radius, 0, 0, rgb[0], rgb[1], rgb[2], alpha);
		addVertex(builder, matrixStack, radius, size, -radius, size, 0, rgb[0], rgb[1], rgb[2], alpha);
		addVertex(builder, matrixStack, radius, size, radius, size, size, rgb[0], rgb[1], rgb[2], alpha);
		addVertex(builder, matrixStack, radius, 0, radius, 0, size, rgb[0], rgb[1], rgb[2], alpha);


		addVertex(builder, matrixStack, -radius, 0, -radius, 0, 0, rgb[0], rgb[1], rgb[2], alpha);
		addVertex(builder, matrixStack, radius, 0, -radius, size, 0, rgb[0], rgb[1], rgb[2], alpha);
		addVertex(builder, matrixStack, radius, 0, radius, size, size, rgb[0], rgb[1], rgb[2], alpha);
		addVertex(builder, matrixStack, -radius, 0, radius, 0, size, rgb[0], rgb[1], rgb[2], alpha);


		addVertex(builder, matrixStack, -radius, size, radius, 0, size, rgb[0], rgb[1], rgb[2], alpha);
		addVertex(builder, matrixStack, radius, size, radius, size, size, rgb[0], rgb[1], rgb[2], alpha);
		addVertex(builder, matrixStack, radius, size, -radius, size, 0, rgb[0], rgb[1], rgb[2], alpha);
		addVertex(builder, matrixStack, -radius, size, -radius, 0, 0, rgb[0], rgb[1], rgb[2], alpha);

	}


	private void addVertex(VertexConsumer builder, PoseStack matrixStack, float x, float y, float z, float textureX, float textureY, float r, float g, float b, float a)
	{
		builder.vertex(matrixStack.last().pose(), x, y, z)
				.color(r, g, b, a)
				.uv(textureX, textureY)
				.overlayCoords(OverlayTexture.NO_OVERLAY)
				.uv2(0, 240)
				.normal(0.0F, 1.0F, 0.0F)
				.endVertex();
	}

	@Override
	public ResourceLocation getTextureLocation(SpawnShieldEntity p_110775_1_)
	{
		return new ResourceLocation(Splatcraft.MODID, "textures/blocks/allowed_color_barrier_fancy.png");
	}
}