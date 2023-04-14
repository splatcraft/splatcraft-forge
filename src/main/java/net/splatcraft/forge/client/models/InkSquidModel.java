package net.splatcraft.forge.client.models;

import net.minecraft.resources.ResourceLocation;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.entities.InkSquidEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

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
}
