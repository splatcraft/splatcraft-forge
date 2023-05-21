package net.splatcraft.forge.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.splatcraft.forge.client.layer.EntityColorLayer;
import net.splatcraft.forge.client.models.InkSquidModel;
import net.splatcraft.forge.client.models.SquidFormModel;
import net.splatcraft.forge.data.capabilities.inkoverlay.InkOverlayCapability;
import net.splatcraft.forge.data.capabilities.inkoverlay.InkOverlayInfo;
import net.splatcraft.forge.entities.InkSquidEntity;
import net.splatcraft.forge.entities.SquidFormPlayer;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.GeoReplacedEntityRenderer;
import software.bernie.shadowed.eliotlash.mclib.utils.MathHelper;

public class SquidFormRenderer extends GeoReplacedEntityRenderer<SquidFormPlayer>
{
	public SquidFormRenderer(EntityRendererProvider.Context renderManager)
	{
		super(renderManager, new SquidFormModel(), new SquidFormPlayer());
		shadowRadius = .5f;

		addLayer(new EntityColorLayer<>(this, "ink_squid"));
	}

	@Override
	public void render(GeoModel model, Object animatable, float partialTick, RenderType type, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
	{
		if(animatable instanceof LivingEntity entity)
			animate(model, entity, partialTick);
		super.render(model, animatable, partialTick, type, poseStack, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	//TODO find a way to use json animations by 4.0
	public static void animate(GeoModel model, LivingEntity entity, float partialTick)
	{
		float limbSwingAmount = 0;
		float limbSwing = 0;
		boolean shouldSit = entity.isPassenger() && (entity.getVehicle() != null && entity.getVehicle().shouldRiderSit());

		if (!shouldSit && entity.isAlive()) {
			limbSwingAmount = Math.min(1, Mth.lerp(partialTick, entity.animationSpeedOld, entity.animationSpeed));
			limbSwing = entity.animationPosition - entity.animationSpeed * (1 - partialTick);

			if (entity.isBaby())
				limbSwing *= 3.0F;
		}

		GeoBone RightLimb = model.getBone("RightLimb").get();
		GeoBone LeftLimb = model.getBone("LeftLimb").get();
		GeoBone squid = model.getBone("squid").get();

		boolean isSwimming = entity.isSwimming();

		if (!entity.isPassenger())
		{
			InkOverlayInfo info = InkOverlayCapability.get(entity);

			double angle = isSwimming ? -(entity.getXRot() * Math.PI / 180F) : Mth.lerp(partialTick, info.getSquidRotO(), info.getSquidRot()) * 1.1f;
			squid.setRotationX((float) Math.min(Math.PI / 2, Math.max(-Math.PI / 2, angle)));
		}

		if (entity.isOnGround() || isSwimming)
		{
			RightLimb.setRotationY(Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / (isSwimming ? 2.2f : 1.5f));
			LeftLimb.setRotationY(Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount / (isSwimming ? 2.2f : 1.5f));
		} else
		{
			if (Math.abs(Math.round(RightLimb.getRotationY() * 100)) != 0)
			{
				RightLimb.setRotationY(RightLimb.getPositionY() - RightLimb.getPositionY() / 8f);
			}
			if (Math.abs(Math.round(LeftLimb.getRotationY() * 100)) != 0)
			{
				LeftLimb.setRotationY(LeftLimb.getRotationY() - LeftLimb.getRotationY() / 8f);
			}
		}
	}
}
