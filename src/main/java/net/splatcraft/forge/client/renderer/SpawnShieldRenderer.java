package net.splatcraft.forge.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.client.model.projectiles.InkProjectileModel;
import net.splatcraft.forge.entities.InkProjectileEntity;
import net.splatcraft.forge.entities.SpawnShieldEntity;
import net.splatcraft.forge.util.ColorUtils;

public class SpawnShieldRenderer extends EntityRenderer<SpawnShieldEntity>
{
	protected static final RenderState.TransparencyState TRANSLUCENT_TRANSPARENCY = new RenderState.TransparencyState("translucent_transparency", () -> {
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
	}, () -> {
		RenderSystem.disableBlend();
		RenderSystem.defaultBlendFunc();
	});
	private static final RenderType BARRIER_RENDER = RenderType.create("splatcraft:stage_barriers", DefaultVertexFormats.BLOCK, 7, 262144, false, true, RenderType.State.builder()
			.setShadeModelState(new RenderState.ShadeModelState(true)).setLightmapState(new RenderState.LightmapState(true)).setTextureState(new RenderState.TextureState(new ResourceLocation(Splatcraft.MODID, "textures/blocks/allowed_color_barrier_fancy.png"), false, true))
			.setAlphaState(new RenderState.AlphaState(0.003921569F)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).createCompositeState(true));


	public SpawnShieldRenderer(EntityRendererManager p_i46179_1_)
	{
		super(p_i46179_1_);
	}

	@Override
	public void render(SpawnShieldEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight)
	{
		//super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);

		float activeTime = entity.getActiveTime();
		float[] rgb = ColorUtils.hexToRGB(entity.getColor());
		float size = entity.getSize();
		float radius = size/2f;

		if (activeTime <= 0 /*|| ColorUtils.getPlayerColor(Minecraft.getInstance().player) == entity.getColor()*/)
		{
			return;
		}

		IVertexBuilder builder = buffer.getBuffer(BARRIER_RENDER);

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


	private void addVertex(IVertexBuilder builder, MatrixStack matrixStack, float x, float y, float z, float textureX, float textureY, float r, float g, float b, float a)
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
		return new ResourceLocation(Splatcraft.MODID, "textures/entity/default_projectile.png");
	}
}