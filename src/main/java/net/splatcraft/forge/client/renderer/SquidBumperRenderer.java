package net.splatcraft.forge.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.splatcraft.forge.client.layer.EntityColorLayer;
import net.splatcraft.forge.client.models.SquidBumperModel;
import net.splatcraft.forge.entities.SquidBumperEntity;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class SquidBumperRenderer extends GeoEntityRenderer<SquidBumperEntity>
{
	public SquidBumperRenderer(EntityRendererProvider.Context renderManager)
	{
		super(renderManager, new SquidBumperModel());
		shadowRadius = .5f;

		addLayer(new EntityColorLayer<>(this, "squid_bumper"));
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
