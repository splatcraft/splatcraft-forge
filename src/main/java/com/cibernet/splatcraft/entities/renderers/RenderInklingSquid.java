package com.cibernet.splatcraft.entities.renderers;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.entities.models.ModelInklingSquid;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelSquid;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderInklingSquid extends RenderLivingBase<EntityPlayer>
{
	
	private static final ResourceLocation TEXTURE = new ResourceLocation(SplatCraft.MODID, "textures/mobs/inkling_squid_eyes.png");
	
	public RenderInklingSquid(RenderManager renderManagerIn)
	{
		super(renderManagerIn, new ModelInklingSquid(), 0.6f);
		this.addLayer(new LayerSquidColor(this));
	}
	
	@Override
	public void doRender(EntityPlayer entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		if(entity.isRiding())
			GlStateManager.translate(0,0.65f, 0);
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}
	
	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntityPlayer entity)
	{
		return TEXTURE;
	}
}
