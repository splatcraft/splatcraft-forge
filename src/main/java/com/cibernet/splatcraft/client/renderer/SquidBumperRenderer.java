package com.cibernet.splatcraft.client.renderer;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.client.layer.InkSquidColorLayer;
import com.cibernet.splatcraft.client.layer.SquidBumperColorLayer;
import com.cibernet.splatcraft.client.model.InkSquidModel;
import com.cibernet.splatcraft.client.model.SquidBumperModel;
import com.cibernet.splatcraft.entities.SquidBumperEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class SquidBumperRenderer extends LivingRenderer<SquidBumperEntity, SquidBumperModel> //implements IEntityRenderer<LivingEntity, InkSquidModel>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(Splatcraft.MODID, "textures/mobs/squid_bumper_overlay.png");
	
	public SquidBumperRenderer(EntityRendererManager manager)
	{
		super(manager, new SquidBumperModel(), 0.5f);
		addLayer(new SquidBumperColorLayer(this));
	}
	
	@Override
	protected boolean canRenderName(SquidBumperEntity entity)
	{
		return (entity.hasCustomName() || entity.getInkHealth() >= 20) ? super.canRenderName(entity) && (entity.getAlwaysRenderNameTagForRender() || entity == this.renderManager.pointedEntity) : true;
	}
	
	/*
	@Override
	public void render(SquidBumperEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn)
	{
		getEntityModel().render(entityIn, matrixStackIn, bufferIn.getBuffer(getEntityModel().getRenderType(TEXTURE)), packedLightIn);
	}
	*/
	
	@Override
	public void render(SquidBumperEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn)
	{
		if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Pre(entityIn, this, partialTicks, matrixStackIn, bufferIn, packedLightIn))) return;
		matrixStackIn.push();
		this.entityModel.swingProgress = this.getSwingProgress(entityIn, partialTicks);
		
		boolean shouldSit = entityIn.isPassenger() && (entityIn.getRidingEntity() != null && entityIn.getRidingEntity().shouldRiderSit());
		this.entityModel.isSitting = shouldSit;
		this.entityModel.isChild = entityIn.isChild();
		float f = MathHelper.interpolateAngle(partialTicks, entityIn.prevRenderYawOffset, entityIn.renderYawOffset);
		float f1 = MathHelper.interpolateAngle(partialTicks, entityIn.prevRotationYawHead, entityIn.rotationYawHead);
		float f2 = f1 - f;
		if (shouldSit && entityIn.getRidingEntity() instanceof LivingEntity) {
			LivingEntity livingentity = (LivingEntity)entityIn.getRidingEntity();
			f = MathHelper.interpolateAngle(partialTicks, livingentity.prevRenderYawOffset, livingentity.renderYawOffset);
			f2 = f1 - f;
			float f3 = MathHelper.wrapDegrees(f2);
			if (f3 < -85.0F) {
				f3 = -85.0F;
			}
			
			if (f3 >= 85.0F) {
				f3 = 85.0F;
			}
			
			f = f1 - f3;
			if (f3 * f3 > 2500.0F) {
				f += f3 * 0.2F;
			}
			
			f2 = f1 - f;
		}
		
		float f6 = MathHelper.lerp(partialTicks, entityIn.prevRotationPitch, entityIn.rotationPitch);
		if (entityIn.getPose() == Pose.SLEEPING) {
			Direction direction = entityIn.getBedDirection();
			if (direction != null) {
				float f4 = entityIn.getEyeHeight(Pose.STANDING) - 0.1F;
				matrixStackIn.translate((double)((float)(-direction.getXOffset()) * f4), 0.0D, (double)((float)(-direction.getZOffset()) * f4));
			}
		}
		
		float f7 = this.handleRotationFloat(entityIn, partialTicks);
		this.applyRotations(entityIn, matrixStackIn, f7, f, partialTicks);
		matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
		this.preRenderCallback(entityIn, matrixStackIn, partialTicks);
		matrixStackIn.translate(0.0D, (double)-1.501F, 0.0D);
		float f8 = 0.0F;
		float f5 = 0.0F;
		if (!shouldSit && entityIn.isAlive()) {
			f8 = MathHelper.lerp(partialTicks, entityIn.prevLimbSwingAmount, entityIn.limbSwingAmount);
			f5 = entityIn.limbSwing - entityIn.limbSwingAmount * (1.0F - partialTicks);
			if (entityIn.isChild()) {
				f5 *= 3.0F;
			}
			
			if (f8 > 1.0F) {
				f8 = 1.0F;
			}
		}
		
		this.entityModel.setLivingAnimations(entityIn, f5, f8, partialTicks);
		this.entityModel.setRotationAngles(entityIn, f5, f8, f7, f2, f6);
		Minecraft minecraft = Minecraft.getInstance();
		boolean flag = this.isVisible(entityIn);
		boolean flag1 = !flag && !entityIn.isInvisibleToPlayer(minecraft.player);
		boolean flag2 = minecraft.func_238206_b_(entityIn);
		RenderType rendertype = this.func_230496_a_(entityIn, flag, flag1, flag2);
		if (rendertype != null) {
			IVertexBuilder ivertexbuilder = bufferIn.getBuffer(rendertype);
			int i = getPackedOverlay(entityIn, this.getOverlayProgress(entityIn, partialTicks));
			
			
			this.entityModel.renderBase(matrixStackIn, ivertexbuilder, packedLightIn, i, 1.0F, 1.0F, 1.0F, flag1 ? 0.15F : 1.0F);
			
			float scale = entityIn.getInkHealth() <= 0 ? (10 - Math.min(entityIn.getRespawnTime(), 10))/10f : 1;
			matrixStackIn.push();
			matrixStackIn.scale(scale, scale, scale);
			this.entityModel.renderBumper(matrixStackIn, ivertexbuilder, packedLightIn, i, 1.0F, 1.0F, 1.0F, flag1 ? 0.15F : 1.0F);
			matrixStackIn.pop();
			
		}
		
		if (!entityIn.isSpectator()) {
			for(LayerRenderer<SquidBumperEntity, SquidBumperModel> layerrenderer : this.layerRenderers) {
				layerrenderer.render(matrixStackIn, bufferIn, packedLightIn, entityIn, f5, f8, partialTicks, f7, f2, f6);
			}
		}
		
		matrixStackIn.pop();
		net.minecraftforge.client.event.RenderNameplateEvent renderNameplateEvent = new net.minecraftforge.client.event.RenderNameplateEvent(entityIn, entityIn.getDisplayName(), this, matrixStackIn, bufferIn, packedLightIn);
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(renderNameplateEvent);
		if (renderNameplateEvent.getResult() != net.minecraftforge.eventbus.api.Event.Result.DENY && (renderNameplateEvent.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW || this.canRenderName(entityIn))) {
			this.renderName(entityIn, renderNameplateEvent.getContent(), matrixStackIn, bufferIn, packedLightIn);
		}
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Post(entityIn, this, partialTicks, matrixStackIn, bufferIn, packedLightIn));
	}
	
	@Override
	protected void renderName(SquidBumperEntity entityIn, ITextComponent displayNameIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn)
	{
		if(entityIn.hasCustomName())
			super.renderName(entityIn, displayNameIn, matrixStackIn, bufferIn, packedLightIn);
		else
		{
			float health = 20-entityIn.getInkHealth();
			super.renderName(entityIn, new StringTextComponent((health >= 20 ? TextFormatting.DARK_RED : "") + String.format("%.1f",health)), matrixStackIn, bufferIn, packedLightIn);
			
		}
	}
	
	@Override
	protected void applyRotations(SquidBumperEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks)
	{
		matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F - rotationYaw));
		float punchTime = (float)(entityLiving.world.getGameTime() - entityLiving.punchCooldown) + partialTicks;
		float hurtTime = (float)(entityLiving.world.getGameTime() - entityLiving.hurtCooldown) + partialTicks;
		
		if (punchTime < 5.0F)
			matrixStackIn.rotate(Vector3f.YP.rotationDegrees(MathHelper.sin(punchTime / 1.5F * (float)Math.PI) * 3.0F));
		if (hurtTime < 5.0F)
			matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(MathHelper.sin(hurtTime / 1.5F * (float)Math.PI) * 3.0F));
	}
	
	@Override
	public ResourceLocation getEntityTexture(SquidBumperEntity entity)
	{
		return TEXTURE;
	}
}
