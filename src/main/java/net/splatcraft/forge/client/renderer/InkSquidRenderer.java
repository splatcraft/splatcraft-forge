package net.splatcraft.forge.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.splatcraft.forge.client.layer.EntityColorLayer;
import net.splatcraft.forge.client.models.InkSquidModel;
import net.splatcraft.forge.entities.InkSquidEntity;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class InkSquidRenderer extends GeoEntityRenderer<InkSquidEntity>
{
	public InkSquidRenderer(EntityRendererProvider.Context renderManager)
	{
		super(renderManager, new InkSquidModel());
		shadowRadius = .5f;

		addLayer(new EntityColorLayer<>(this, "ink_squid"));
	}
}
