package net.splatcraft.forge.client.models;

import net.minecraft.resources.ResourceLocation;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.entities.InkSquidEntity;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.resource.GeckoLibCache;

public class InkSquidModel extends AnimatedGeoModel<InkSquidEntity>
{
	@Override
	public ResourceLocation getModelLocation(InkSquidEntity object) {
		return new ResourceLocation(Splatcraft.MODID, "geo/ink_squid.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(InkSquidEntity object) {
		return new ResourceLocation(Splatcraft.MODID, "textures/entity/ink_squid_overlay.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(InkSquidEntity animatable) {
		return new ResourceLocation(Splatcraft.MODID, "animations/ink_squid.animation.json");
	}

	@Override
	public void setCustomAnimations(InkSquidEntity animatable, int instanceId, AnimationEvent animationEvent)
	{
		GeckoLibCache.getInstance().parser.setValue("limb_swing", animationEvent::getLimbSwing);
		GeckoLibCache.getInstance().parser.setValue("limb_swing_amount", animationEvent::getLimbSwingAmount);
		super.setCustomAnimations(animatable, instanceId, animationEvent);
	}
}
