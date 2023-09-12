package net.splatcraft.forge.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.client.layer.InkSquidColorLayer;
import net.splatcraft.forge.client.models.InkSquidModel;

public class InkSquidRenderer extends LivingEntityRenderer<LivingEntity, InkSquidModel> implements RenderLayerParent<LivingEntity, InkSquidModel>
{

	private static EntityRendererProvider.Context squidFormContext;
	private static final ResourceLocation TEXTURE = new ResourceLocation(Splatcraft.MODID, "textures/entity/ink_squid_overlay.png");

	public InkSquidRenderer(EntityRendererProvider.Context context)
	{
		super(context, new InkSquidModel(context.bakeLayer(InkSquidModel.LAYER_LOCATION)), 0.5f);
		addLayer(new InkSquidColorLayer(this, context.getModelSet()));

		if(squidFormContext == null)
			squidFormContext = context;
	}

	public static EntityRendererProvider.Context getContext()
	{
		return squidFormContext;
	}

	@Override
	protected boolean shouldShowName(LivingEntity entity)
	{
		return super.shouldShowName(entity) && (entity.shouldShowName() || entity.hasCustomName() && entity == this.entityRenderDispatcher.crosshairPickEntity);
	}

	@Override
	public ResourceLocation getTextureLocation(LivingEntity entity)
	{
		return TEXTURE;
	}
}
