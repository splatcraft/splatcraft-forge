package com.cibernet.splatcraft.client.model;// Made with Blockbench 3.5.4
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

public class InkSquidModel extends EntityModel<LivingEntity>
{
	private final ModelRenderer squid;
	private final ModelRenderer Body;
	private final ModelRenderer eyes;
	private final ModelRenderer tentacles;
	private final ModelRenderer LeftLimb;
	private final ModelRenderer RightLimb;

	public InkSquidModel() {
		textureWidth = 64;
		textureHeight = 64;

		squid = new ModelRenderer(this);
		squid.setRotationPoint(0.0F, 24.0F, 0.0F);
		

		Body = new ModelRenderer(this);
		Body.setRotationPoint(0.0F, 0.0F, 0.0F);
		squid.addChild(Body);
		Body.setTextureOffset(0, 0).addBox(-4.0F, -4.0F, -2.0F, 8.0F, 4.0F, 4.0F, 0.0F, false);
		Body.setTextureOffset(0, 9).addBox(-6.0F, -5.0F, -6.0F, 12.0F, 5.0F, 4.0F, 0.0F, false);
		Body.setTextureOffset(27, 0).addBox(-5.0F, -4.0F, -8.0F, 10.0F, 4.0F, 2.0F, 0.0F, false);
		Body.setTextureOffset(32, 6).addBox(-4.0F, -3.0F, -10.0F, 8.0F, 3.0F, 2.0F, 0.0F, false);
		Body.setTextureOffset(32, 12).addBox(-2.0F, -2.0F, -12.0F, 4.0F, 2.0F, 2.0F, 0.0F, false);

		eyes = new ModelRenderer(this);
		eyes.setRotationPoint(0.0F, 0.0F, 0.0F);
		Body.addChild(eyes);
		eyes.setTextureOffset(18, 19).addBox(-2.5F, -5.0F, -2.0F, 5.0F, 1.0F, 2.0F, 0.0F, false);
		eyes.setTextureOffset(0, 19).addBox(-3.0F, -4.5F, -2.25F, 6.0F, 1.0F, 3.0F, 0.0F, false);

		tentacles = new ModelRenderer(this);
		tentacles.setRotationPoint(4.0F, 0.0F, -2.25F);
		Body.addChild(tentacles);
		setRotationAngle(tentacles, 0.0F, -0.7854F, 0.0F);
		tentacles.setTextureOffset(56, 0).addBox(-2.6593F, -3.75F, 6.6593F, 2.0F, 1.0F, 2.0F, 0.0F, false);
		tentacles.setTextureOffset(56, 0).addBox(-1.495F, -3.75F, 5.495F, 2.0F, 1.0F, 2.0F, 0.0F, false);
		tentacles.setTextureOffset(56, 0).addBox(-0.1161F, -2.25F, 4.1161F, 2.0F, 1.0F, 2.0F, 0.0F, false);
		tentacles.setTextureOffset(56, 0).addBox(-1.495F, -2.25F, 5.495F, 2.0F, 1.0F, 2.0F, 0.0F, false);
		tentacles.setTextureOffset(56, 0).addBox(-0.1161F, -3.75F, 4.1161F, 2.0F, 1.0F, 2.0F, 0.0F, false);
		tentacles.setTextureOffset(56, 0).addBox(0.9875F, -3.75F, 2.9671F, 2.0F, 1.0F, 2.0F, 0.0F, false);

		LeftLimb = new ModelRenderer(this);
		LeftLimb.setRotationPoint(2.0F, 0.0F, 2.0F);
		squid.addChild(LeftLimb);
		LeftLimb.setTextureOffset(0, 23).addBox(0.0F, -3.0F, 0.0F, 2.0F, 3.0F, 3.0F, 0.0F, false);
		LeftLimb.setTextureOffset(0, 29).addBox(-1.0F, -3.0F, 3.0F, 3.0F, 3.0F, 4.0F, 0.0F, false);

		RightLimb = new ModelRenderer(this);
		RightLimb.setRotationPoint(-2.0F, 0.0F, 2.0F);
		squid.addChild(RightLimb);
		RightLimb.setTextureOffset(10, 23).addBox(-2.0F, -3.0F, 0.0F, 2.0F, 3.0F, 3.0F, 0.0F, true);
		RightLimb.setTextureOffset(14, 29).addBox(-2.0F, -3.0F, 3.0F, 3.0F, 3.0F, 4.0F, 0.0F, true);
	}

	@Override
	public void setRotationAngles(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
	{
		squid.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
	
	@Override
	public void setLivingAnimations(LivingEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime)
	{
		super.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTickTime);
		
		if(!entitylivingbaseIn.isPassenger())
		{
			float yDiff = (float) (entitylivingbaseIn.getPosY() - entitylivingbaseIn.prevPosY) * 1.1f;
			squid.rotateAngleX = (float) -Math.min(Math.PI / 2, Math.max(-Math.PI / 2, yDiff));
		}
		
		if(entitylivingbaseIn.isOnGround())
		{
			this.RightLimb.rotateAngleY = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / 1.5f;
			this.LeftLimb.rotateAngleY = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount / 1.5f;
		}
		else
		{
			if(Math.abs(Math.round(RightLimb.rotateAngleY*100)) != 0)
				this.RightLimb.rotateAngleY -= RightLimb.rotateAngleY/8f;
			if(Math.abs(Math.round(LeftLimb.rotateAngleY*100)) != 0)
				this.LeftLimb.rotateAngleY -= LeftLimb.rotateAngleY/8f;
		}
	}
}