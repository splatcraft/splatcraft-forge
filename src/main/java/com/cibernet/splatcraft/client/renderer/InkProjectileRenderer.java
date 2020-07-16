package com.cibernet.splatcraft.client.renderer;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.client.model.InkProjectileModel;
import com.cibernet.splatcraft.entities.InkProjectileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class InkProjectileRenderer extends EntityRenderer<InkProjectileEntity> implements IEntityRenderer<InkProjectileEntity, InkProjectileModel>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(Splatcraft.MODID, "textures/entity/ink_projectile.png");
	private final InkProjectileModel MODEL = new InkProjectileModel();
	
	public InkProjectileRenderer(EntityRendererManager manager)
	{
		super(manager);
	}
	
	@Override
	public void render(InkProjectileEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn)
	{
		if (entityIn.ticksExisted >= 3 || this.renderManager.info.getRenderViewEntity().getDistanceSq(entityIn) >= 12.25D)
		{
			float scale = entityIn.getProjectileSize();
			int color = entityIn.getColor();
			float r = (float) (Math.floor(color / (256 * 256)) / 255f);
			float g = (float) ((Math.floor(color / 256) % 256) / 255f);
			float b = (color % 256) / 255f;
			
			matrixStackIn.push();
			matrixStackIn.scale(scale, scale, scale);
			getEntityModel().render(matrixStackIn, bufferIn.getBuffer(getEntityModel().getRenderType(TEXTURE)), packedLightIn, OverlayTexture.NO_OVERLAY, r, g, b, 1);
			matrixStackIn.pop();
			
			super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
		}
	}
	
	@Override
	public InkProjectileModel getEntityModel()
	{
		return MODEL;
	}
	
	@Override
	public ResourceLocation getEntityTexture(InkProjectileEntity entity)
	{
		return TEXTURE;
	}
}
