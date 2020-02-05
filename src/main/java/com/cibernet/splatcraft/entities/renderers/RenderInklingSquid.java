package com.cibernet.splatcraft.entities.renderers;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelSquid;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderInklingSquid extends RenderLivingBase<EntityPlayer>
{
	
	private static final ResourceLocation SQUID_TEXTURES = new ResourceLocation("textures/entity/squid.png");
	
	public RenderInklingSquid(RenderManager renderManagerIn)
	{
		super(renderManagerIn, new ModelSquid(), 0.5f);
	}
	
	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntityPlayer entity)
	{
		return SQUID_TEXTURES;
	}
}
