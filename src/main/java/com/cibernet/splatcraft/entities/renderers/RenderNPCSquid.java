package com.cibernet.splatcraft.entities.renderers;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.entities.classes.EntityNPCSquid;
import com.cibernet.splatcraft.entities.models.ModelInklingSquid;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderNPCSquid extends RenderLivingBase<EntityNPCSquid>
{
	
	private static final ResourceLocation TEXTURE = new ResourceLocation(SplatCraft.MODID, "textures/mobs/inkling_squid_eyes.png");
	
	public RenderNPCSquid(RenderManager renderManagerIn)
	{
		super(renderManagerIn, new ModelInklingSquid(), 0.6f);
		this.addLayer(new LayerNPCSquidColor(this));
	}
	
	@Override
	public void doRender(EntityNPCSquid entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		GlStateManager.pushMatrix();
		if(entity.isRiding())
			GlStateManager.translate(0,0.3f, 0);
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
		GlStateManager.popMatrix();
	}
	
	protected boolean canRenderName(EntityNPCSquid entity)
	{
		return super.canRenderName(entity) && (entity.getAlwaysRenderNameTagForRender() || entity.hasCustomName() && entity == this.renderManager.pointedEntity);
	}
	
	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntityNPCSquid entity)
	{
		return TEXTURE;
	}
}
