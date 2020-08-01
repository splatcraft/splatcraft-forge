package com.cibernet.splatcraft.client.renderer;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.client.layer.InkSquidColorLayer;
import com.cibernet.splatcraft.client.model.InkSquidModel;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

public class InkSquidRenderer extends LivingRenderer<LivingEntity, InkSquidModel> //implements IEntityRenderer<LivingEntity, InkSquidModel>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(Splatcraft.MODID, "textures/mobs/ink_squid_overlay.png");
	
	public InkSquidRenderer(EntityRendererManager manager)
	{
		super(manager, new InkSquidModel(), 0.5f);
		addLayer(new InkSquidColorLayer(this));
	}
	
	@Override
	protected boolean canRenderName(LivingEntity entity)
	{
		return super.canRenderName(entity) && (entity.getAlwaysRenderNameTagForRender() || entity.hasCustomName() && entity == this.renderManager.pointedEntity);
	}
	
	@Override
	public ResourceLocation getEntityTexture(LivingEntity entity)
	{
		return TEXTURE;
	}
}
