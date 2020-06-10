package com.cibernet.splatcraft.entities.models;
//Made with Blockbench

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class ModelInklingSquid extends ModelBase {
	private final ModelRenderer squid;
	private final ModelRenderer Body;
	private final ModelRenderer eyes;
	private final ModelRenderer tentacles;
	private final ModelRenderer LeftLimb;
	private final ModelRenderer RightLimb;
	
	public ModelInklingSquid() {
		textureWidth = 64;
		textureHeight = 64;
		
		squid = new ModelRenderer(this);
		squid.setRotationPoint(0.0F, 24.0F, 0.0F);
		
		Body = new ModelRenderer(this);
		Body.setRotationPoint(0.0F, 0.0F, 0.0F);
		squid.addChild(Body);
		Body.cubeList.add(new ModelBox(Body, 0, 0, -4.0F, -4.0F, -2.0F, 8, 4, 4, 0.0F, false));
		Body.cubeList.add(new ModelBox(Body, 0, 9, -6.0F, -5.0F, -6.0F, 12, 5, 4, 0.0F, false));
		Body.cubeList.add(new ModelBox(Body, 27, 0, -5.0F, -4.0F, -8.0F, 10, 4, 2, 0.0F, false));
		Body.cubeList.add(new ModelBox(Body, 32, 6, -4.0F, -3.0F, -10.0F, 8, 3, 2, 0.0F, false));
		Body.cubeList.add(new ModelBox(Body, 32, 12, -2.0F, -2.0F, -12.0F, 4, 2, 2, 0.0F, false));
		
		eyes = new ModelRenderer(this);
		eyes.setRotationPoint(0.0F, 0.0F, 0.0F);
		Body.addChild(eyes);
		eyes.cubeList.add(new ModelBox(eyes, 18, 19, -2.5F, -5.0F, -2.0F, 5, 1, 2, 0.0F, false));
		eyes.cubeList.add(new ModelBox(eyes, 0, 19, -3.0F, -4.5F, -2.25F, 6, 1, 3, 0.0F, false));
		
		tentacles = new ModelRenderer(this);
		tentacles.setRotationPoint(4.0F, 0.0F, -2.25F);
		setRotationAngle(tentacles, 0.0F, -0.7854F, 0.0F);
		Body.addChild(tentacles);
		tentacles.cubeList.add(new ModelBox(tentacles, 56, 0, -2.6593F, -3.75F, 6.6593F, 2, 1, 2, 0.0F, false));
		tentacles.cubeList.add(new ModelBox(tentacles, 56, 0, -1.495F, -3.75F, 5.495F, 2, 1, 2, 0.0F, false));
		tentacles.cubeList.add(new ModelBox(tentacles, 56, 0, -0.1161F, -2.25F, 4.1161F, 2, 1, 2, 0.0F, false));
		tentacles.cubeList.add(new ModelBox(tentacles, 56, 0, -1.495F, -2.25F, 5.495F, 2, 1, 2, 0.0F, false));
		tentacles.cubeList.add(new ModelBox(tentacles, 56, 0, -0.1161F, -3.75F, 4.1161F, 2, 1, 2, 0.0F, false));
		tentacles.cubeList.add(new ModelBox(tentacles, 56, 0, 0.9875F, -3.75F, 2.9671F, 2, 1, 2, 0.0F, false));
		
		LeftLimb = new ModelRenderer(this);
		LeftLimb.setRotationPoint(2.0F, 0.0F, 2.0F);
		squid.addChild(LeftLimb);
		LeftLimb.cubeList.add(new ModelBox(LeftLimb, 0, 23, 0.0F, -3.0F, 0.0F, 2, 3, 3, 0.0F, false));
		LeftLimb.cubeList.add(new ModelBox(LeftLimb, 0, 29, -1.0F, -3.0F, 3.0F, 3, 3, 4, 0.0F, false));
		
		RightLimb = new ModelRenderer(this);
		RightLimb.setRotationPoint(-2.0F, 0.0F, 2.0F);
		squid.addChild(RightLimb);
		RightLimb.cubeList.add(new ModelBox(RightLimb, 10, 23, -2.0F, -3.0F, 0.0F, 2, 3, 3, 0.0F, true));
		RightLimb.cubeList.add(new ModelBox(RightLimb, 14, 29, -2.0F, -3.0F, 3.0F, 3, 3, 4, 0.0F, true));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		squid.render(f5);
	}
	
	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z)
	{
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
	
	@Override
	public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime)
	{
		super.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTickTime);
		
		if(!entitylivingbaseIn.isRiding())
		{
			float yDiff = (float) (entitylivingbaseIn.posY - entitylivingbaseIn.prevPosY) * 1.1f;
			squid.rotateAngleX = (float) -Math.min(Math.PI / 2, Math.max(-Math.PI / 2, yDiff));
		}
		
		if(entitylivingbaseIn.onGround)
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