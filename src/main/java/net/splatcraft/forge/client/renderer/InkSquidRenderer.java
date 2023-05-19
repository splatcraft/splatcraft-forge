package net.splatcraft.forge.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.splatcraft.forge.client.layer.EntityColorLayer;
import net.splatcraft.forge.client.models.InkSquidModel;
import net.splatcraft.forge.entities.InkSquidEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class InkSquidRenderer extends GeoEntityRenderer<InkSquidEntity>
{
	public InkSquidRenderer(EntityRendererProvider.Context renderManager)
	{
		super(renderManager, new InkSquidModel());
		shadowRadius = .5f;

		addLayer(new EntityColorLayer<>(this, "ink_squid"));
	}

	@Override
	public void render(InkSquidEntity animatable, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight)
	{
		super.render(animatable, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}
}
