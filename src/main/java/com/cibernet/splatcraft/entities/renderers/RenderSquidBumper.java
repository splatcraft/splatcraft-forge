package com.cibernet.splatcraft.entities.renderers;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.entities.classes.EntityNPCSquid;
import com.cibernet.splatcraft.entities.classes.EntitySquidBumper;
import com.cibernet.splatcraft.entities.models.ModelSquidBumper;
import net.minecraft.client.model.ModelArmorStand;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;

public class RenderSquidBumper extends RenderLivingBase<EntitySquidBumper>
{
	
	public static final ResourceLocation TEXTURE = new ResourceLocation(SplatCraft.MODID, "textures/mobs/squid_bumper_overlay.png");
	
	public RenderSquidBumper(RenderManager renderManagerIn)
	{
		super(renderManagerIn, new ModelSquidBumper(), 0.6f);
		addLayer(new LayerSquidBumperColor(this));
	}
	
	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntitySquidBumper entity)
	{
		return TEXTURE;
	}
	
	@Override
	protected void renderEntityName(EntitySquidBumper entityIn, double x, double y, double z, String name, double distanceSq)
	{
		if(entityIn.hasCustomName())
			super.renderEntityName(entityIn, x, y, z, name, distanceSq);
		else
		{
			float health = 20-entityIn.getInkHealth();
			this.renderLivingLabel(entityIn, (health >= 20 ? TextFormatting.DARK_RED : "") + String.valueOf(health), x, y, z, 64);
		}
	}
	
	@Override
	protected boolean canRenderName(EntitySquidBumper entity)
	{
		return (entity.hasCustomName() || entity.getInkHealth() >= 20) ? super.canRenderName(entity) && (entity.getAlwaysRenderNameTagForRender() || entity == this.renderManager.pointedEntity) : true;
	}
	
	@Override
	protected void applyRotations(EntitySquidBumper entityLiving, float p_77043_2_, float rotationYaw, float partialTicks)
	{
		GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
		float punchTime = (float)(entityLiving.world.getTotalWorldTime() - entityLiving.punchCooldown) + partialTicks;
		float hurtTime = (float)(entityLiving.world.getTotalWorldTime() - entityLiving.hurtCooldown) + partialTicks;
		
		if (punchTime < 5.0F)
			GlStateManager.rotate(MathHelper.sin(punchTime / 1.5F * (float)Math.PI) * 3.0F, 0.0F, 1.0F, 0.0F);
		if (hurtTime < 5.0F)
			GlStateManager.rotate(MathHelper.sin(hurtTime / 1.5F * (float)Math.PI) * 3.0F, 1.0F, 0.0F, 1.0F);
		
	}
}
