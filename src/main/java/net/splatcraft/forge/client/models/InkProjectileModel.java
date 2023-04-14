package net.splatcraft.forge.client.models;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.entities.InkProjectileEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.resource.GeckoLibCache;

public class InkProjectileModel extends AnimatedGeoModel<InkProjectileEntity>
{
	@Override
	public ResourceLocation getModelLocation(InkProjectileEntity object)
	{
		ResourceLocation loc = new ResourceLocation(Splatcraft.MODID, "geo/ink_projectile_"+ object.getProjectileType() +".geo.json");
		if(GeckoLibCache.getInstance().getGeoModels().containsKey(loc))
			return loc;
		return new ResourceLocation(Splatcraft.MODID, "geo/ink_projectile_default.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(InkProjectileEntity object)
	{
		ResourceLocation loc = new ResourceLocation(Splatcraft.MODID, "textures/entity/ink_projectile_"+ object.getProjectileType() + ".png");
		if(Minecraft.getInstance().getResourceManager().hasResource(loc))
			return loc;
		return new ResourceLocation(Splatcraft.MODID, "textures/entity/ink_projectile_default.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(InkProjectileEntity object)
	{
		ResourceLocation loc = new ResourceLocation(Splatcraft.MODID, "animations/ink_projectile_"+ object.getProjectileType() +".animation.json");
		if(GeckoLibCache.getInstance().getAnimations().containsKey(loc))
			return loc;
		return new ResourceLocation(Splatcraft.MODID, "animations/ink_projectile_default.animation.json");
	}
}
