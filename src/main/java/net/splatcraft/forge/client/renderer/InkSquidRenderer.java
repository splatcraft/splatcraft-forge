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
	private static EntityRendererProvider.Context renderManager;

	public InkSquidRenderer(EntityRendererProvider.Context context)
	{
		super(context, new InkSquidModel());
		shadowRadius = .5f;

		addLayer(new EntityColorLayer<>(this, "ink_squid"));
		renderManager = context;
	}

	@Override
	public void render(GeoModel model, InkSquidEntity animatable, float partialTick, RenderType type, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
	{
		SquidFormRenderer.animate(model, animatable, partialTick);
		super.render(model, animatable, partialTick, type, poseStack, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	//this is really dumb
	public static EntityRendererProvider.Context getRenderManager() {
		return renderManager;
	}
}
