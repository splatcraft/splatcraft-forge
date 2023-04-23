package net.splatcraft.forge.client.models;

import net.minecraft.resources.ResourceLocation;
import net.splatcraft.forge.entities.subs.AbstractSubWeaponEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class SubWeaponModel<T extends AbstractSubWeaponEntity> extends AnimatedGeoModel<T>
{
	@Override
	public ResourceLocation getModelLocation(T object)
	{
		ResourceLocation id = object.getType().getRegistryName();
		assert id != null;
		return new ResourceLocation(id.getNamespace(), "geo/" + id.getPath() + ".geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(T object)
	{
		ResourceLocation id = object.getType().getRegistryName();
		assert id != null;
		return new ResourceLocation(id.getNamespace(), "textures/weapons/sub/" + id.getPath() + ".png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(T animatable)
	{
		ResourceLocation id = animatable.getType().getRegistryName();
		assert id != null;
		return new ResourceLocation(id.getNamespace(), "animations/" + id.getPath() + ".animation.json");
	}
}
