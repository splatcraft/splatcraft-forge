package net.splatcraft.forge.client.renderer.subs;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.splatcraft.forge.client.renderer.GeoNonLivingRenderer;
import net.splatcraft.forge.entities.subs.AbstractSubWeaponEntity;
import net.splatcraft.forge.entities.subs.SuctionBombEntity;
import net.splatcraft.forge.util.ColorUtils;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

import static software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer.getPackedOverlay;

public abstract class SubWeaponRenderer<T extends AbstractSubWeaponEntity> extends GeoNonLivingRenderer<T>
{
	private final int layers;

	public SubWeaponRenderer(EntityRendererProvider.Context renderManager, AnimatedGeoModel<T> modelProvider, int layers)
	{
		super(renderManager, modelProvider);
		this.layers = layers;
	}

	private int renderLayer = 0;

	@Override
	public void render(T animatable, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight)
	{
		for(renderLayer = 0; renderLayer < layers; renderLayer++)
			super.render(animatable, yaw, partialTick, poseStack, bufferSource, packedLight);
	}

	@Override
	public void render(GeoModel model, T animatable, float partialTick, RenderType type, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		super.render(model, animatable, partialTick, type, poseStack, bufferSource, buffer, packedLight, getPackedOverlay(animatable, getOverlayProgress(animatable, partialTick)), red, green, blue, alpha);
	}

	@Override
	public Color getRenderColor(T animatable, float partialTick, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight)
	{
		if (renderLayer == 1) {
			float[] rgb = ColorUtils.hexToRGB(animatable.getColor());
			return Color.ofRGB(rgb[0], rgb[1], rgb[2]);
		}
		return super.getRenderColor(animatable, partialTick, poseStack, bufferSource, buffer, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(T animatable)
	{
		ResourceLocation ret = super.getTextureLocation(animatable);
		if (renderLayer == 1) {
			return new ResourceLocation(ret.getNamespace(), ret.getPath().replace(".png", "_ink.png"));
		}
		return ret;
	}

	protected float getOverlayProgress(T animatable, float partialTick)
	{
		return 0;
	}

	protected int getRenderLayer()
	{
		return renderLayer;
	}
}
