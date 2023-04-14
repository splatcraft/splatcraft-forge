package net.splatcraft.forge.client.models;

import net.minecraft.resources.ResourceLocation;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.entities.SquidBumperEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class SquidBumperModel extends AnimatedGeoModel<SquidBumperEntity>
{

	@Override
	public ResourceLocation getModelLocation(SquidBumperEntity object) {
		return new ResourceLocation(Splatcraft.MODID, "geo/squid_bumper.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(SquidBumperEntity object) {
		return new ResourceLocation(Splatcraft.MODID, "textures/entity/squid_bumper_overlay.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(SquidBumperEntity animatable) {
		return new ResourceLocation(Splatcraft.MODID, "animations/squid_bumper.animation.json");
	}

	@Override
	public void setCustomAnimations(SquidBumperEntity animatable, int instanceId)
	{
		super.setCustomAnimations(animatable, instanceId);

		//IBone head = this.getAnimationProcessor().getBone("head");
	}
}
