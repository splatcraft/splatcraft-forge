package com.cibernet.splatcraft.client.layer;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.SplatcraftConfig;
import com.cibernet.splatcraft.client.model.InkSquidModel;
import com.cibernet.splatcraft.client.model.SquidBumperModel;
import com.cibernet.splatcraft.entities.SquidBumperEntity;
import com.cibernet.splatcraft.util.ColorUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

public class SquidBumperColorLayer extends LayerRenderer<SquidBumperEntity, SquidBumperModel>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(Splatcraft.MODID, "textures/entity/squid_bumper.png");
	private final SquidBumperModel MODEL = new SquidBumperModel();
	
	public SquidBumperColorLayer(IEntityRenderer<SquidBumperEntity, SquidBumperModel> renderer)
	{
		super(renderer);
	}
	
	
	@Override
	public void render(MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int packedLightIn, SquidBumperEntity entity, float v, float v1, float v2, float v3, float v4, float v5)
	{
		int color = ColorUtils.getEntityColor(entity);
		if(SplatcraftConfig.Client.getColorLock())
			color = ColorUtils.getLockedColor(color);
		float r = ((color & 16711680) >> 16) / 255.0f;
		float g = ((color & '\uff00') >> 8) / 255.0f;
		float b = ((color & 255) >> 0) / 255.0f;
		
		renderCopyCutoutModel(getEntityModel(), MODEL, TEXTURE, matrixStack, bufferIn, packedLightIn, entity, v, v1, v2, v3, v4, v5, r, g, b);
	}
	
	protected static <T extends SquidBumperEntity> void renderCopyCutoutModel(SquidBumperModel modelParentIn, SquidBumperModel modelIn, ResourceLocation textureLocationIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float partialTicks, float red, float green, float blue)
	{
		if (!entityIn.isInvisible())
		{
			modelParentIn.copyModelAttributesTo(modelIn);
			modelIn.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTicks);
			modelIn.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
			renderCutoutModel(modelIn, textureLocationIn, matrixStackIn, bufferIn, packedLightIn, entityIn, red, green, blue);
		}
		
	}
	
	protected static <T extends LivingEntity> void renderCutoutModel(SquidBumperModel modelIn, ResourceLocation textureLocationIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, SquidBumperEntity entityIn, float red, float green, float blue)
	{
		IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(textureLocationIn));
		modelIn.renderBase(matrixStackIn, ivertexbuilder, packedLightIn, LivingRenderer.getPackedOverlay(entityIn, 0.0F), red, green, blue, 1.0F);
		
		float scale = entityIn.getInkHealth() <= 0 ? (10 - Math.min(entityIn.getRespawnTime(), 10))/10f : 1;
		matrixStackIn.push();
		matrixStackIn.scale(scale, scale, scale);
		modelIn.renderBumper(matrixStackIn, ivertexbuilder, packedLightIn, LivingRenderer.getPackedOverlay(entityIn, 0.0F), red, green, blue, 1.0F);
		matrixStackIn.pop();
		
		
	}
	
}
