package net.splatcraft.forge.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.SplatcraftConfig;
import net.splatcraft.forge.util.ColorUtils;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class EntityColorLayer<E extends Entity & IAnimatable> extends GeoLayerRenderer<E>
{
	private final String textureId;

	public EntityColorLayer(IGeoRenderer<E> entityRendererIn, String textureId)
	{
		super(entityRendererIn);
		this.textureId = textureId;
	}

	@Override
	public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, E entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
	{
		int color = ColorUtils.getEntityColor(entity);
		if (SplatcraftConfig.Client.getColorLock())
		{
			color = ColorUtils.getLockedColor(color);
		}
		float r = ((color & 16711680) >> 16) / 255.0f;
		float g = ((color & '\uff00') >> 8) / 255.0f;
		float b = (color & 255) / 255.0f;

		renderModel(entityRenderer.getGeoModelProvider(), getEntityTexture(entity), matrixStackIn, bufferIn, packedLightIn, entity, partialTicks, r, g, b);
	}

	@Override
	public ResourceLocation getEntityTexture(E object) {
		return new ResourceLocation(Splatcraft.MODID, "textures/entity/"+textureId+".png");

	}
}
