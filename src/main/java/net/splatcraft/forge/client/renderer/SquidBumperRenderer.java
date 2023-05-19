package net.splatcraft.forge.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import net.splatcraft.forge.client.layer.EntityColorLayer;
import net.splatcraft.forge.client.models.SquidBumperModel;
import net.splatcraft.forge.entities.SquidBumperEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.example.client.renderer.entity.ExampleGeoRenderer;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class SquidBumperRenderer extends GeoEntityRenderer<SquidBumperEntity>
{
	public SquidBumperRenderer(EntityRendererProvider.Context renderManager)
	{
		super(renderManager, new SquidBumperModel());
		shadowRadius = .5f;

		addLayer(new EntityColorLayer<>(this, "squid_bumper"));
	}

	private float entityYaw;

	@Override
	public void render(SquidBumperEntity animatable, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {

		this.entityYaw = 0;
		super.render(animatable, 0, partialTick, poseStack, bufferSource, packedLight);
	}

	@Override
	public void render(GeoModel model, SquidBumperEntity animatable, float partialTick, RenderType type, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
	{
		//float entityYaw = Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot()) % 360;

		model.getBone("Base").get().setRotationY((float) Math.toRadians(entityYaw));

		super.render(model, animatable, partialTick, type, poseStack, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public boolean shouldShowName(SquidBumperEntity animatable)
	{
		return super.shouldShowName(animatable) || (!animatable.hasCustomName() && animatable.getInkHealth() < 20);
	}

	@Override
	protected void renderNameTag(SquidBumperEntity animatable, Component component, PoseStack poseStack, MultiBufferSource buffer, int partialTick)
	{
		if(!animatable.hasCustomName())
		{
			float health = 20 - animatable.getInkHealth();
			component = new TextComponent((health >= 20 ? ChatFormatting.DARK_RED : "") + String.format("%.1f", health));
		}
		super.renderNameTag(animatable, component, poseStack, buffer, partialTick);
	}
}
