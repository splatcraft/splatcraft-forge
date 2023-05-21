package net.splatcraft.forge.client.models;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.data.capabilities.inkoverlay.InkOverlayCapability;
import net.splatcraft.forge.data.capabilities.inkoverlay.InkOverlayInfo;
import net.splatcraft.forge.entities.InkSquidEntity;
import net.splatcraft.forge.entities.SquidFormPlayer;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.resource.GeckoLibCache;

public class SquidFormModel extends AnimatedGeoModel
{
	@Override
	public ResourceLocation getModelLocation(Object object) {
		return new ResourceLocation(Splatcraft.MODID, "geo/ink_squid.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(Object object) {
		return new ResourceLocation(Splatcraft.MODID, "textures/entity/ink_squid_overlay.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(Object animatable) {
		return new ResourceLocation(Splatcraft.MODID, "animations/ink_squid.animation.json");
	}

	@Override
	public void setCustomAnimations(Object animatable, int instanceId, AnimationEvent animationEvent)
	{
		GeckoLibCache.getInstance().parser.setValue("limb_swing", animationEvent::getLimbSwing);
		GeckoLibCache.getInstance().parser.setValue("limb_swing_amount", animationEvent::getLimbSwingAmount);
		super.setCustomAnimations((IAnimatable) animatable, instanceId, animationEvent);
	}

	@Override
	public void codeAnimations(IAnimatable animatable, Integer uniqueID, AnimationEvent customPredicate)
	{
		super.codeAnimations(animatable, uniqueID, customPredicate);



		//TODO find a way to use json animations by 4.0
		if(animatable instanceof LivingEntity entity)
		{
			float limbSwingAmount = 0;
			float limbSwing = 0;
			boolean shouldSit = entity.isPassenger() && (entity.getVehicle() != null && entity.getVehicle().shouldRiderSit());

			if (!shouldSit && entity.isAlive()) {
				limbSwingAmount = Math.min(1, Mth.lerp(customPredicate.getPartialTick(), entity.animationSpeedOld, entity.animationSpeed));
				limbSwing = entity.animationPosition - entity.animationSpeed * (1 - customPredicate.getPartialTick());

				if (entity.isBaby())
					limbSwing *= 3.0F;
			}

			GeoModel model = getModel(getModelLocation(animatable));

			GeoBone RightLimb = model.getBone("RightLimb").get();
			GeoBone LeftLimb = model.getBone("LeftLimb").get();
			GeoBone squid = model.getBone("squid").get();

			boolean isSwimming = entity.isSwimming();

			if (!entity.isPassenger())
			{
				InkOverlayInfo info = InkOverlayCapability.get(entity);

				double angle = isSwimming ? -(entity.getXRot() * Math.PI / 180F) : Mth.lerp(customPredicate.getPartialTick(), info.getSquidRotO(), info.getSquidRot()) * 1.1f;
				squid.setRotationX((float) -Math.min(Math.PI / 2, Math.max(-Math.PI / 2, angle)));
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
}
