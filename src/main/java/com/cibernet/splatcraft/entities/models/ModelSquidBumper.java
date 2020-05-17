package com.cibernet.splatcraft.entities.models;// Made with Blockbench
// Paste this code into your mod.
// Make sure to generate all required imports

import com.cibernet.splatcraft.entities.classes.EntitySquidBumper;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class ModelSquidBumper extends ModelBase
{
	private final ModelRenderer Base;
	private final ModelRenderer Bumper;
	private final ModelRenderer Left_Side;
	private final ModelRenderer Right_Side;

	public ModelSquidBumper() {
		textureWidth = 128;
		textureHeight = 128;

		Base = new ModelRenderer(this);
		Base.setRotationPoint(0.0F, 24.0F, 0.0F);
		Base.cubeList.add(new ModelBox(Base, 0, 46, -5.0F, -2.0F, -5.0F, 10, 2, 10, 0.0F, false));
		
		Bumper = new ModelRenderer(this);
		Bumper.setRotationPoint(0.0F, 24.0F, 0.0F);
		Bumper.cubeList.add(new ModelBox(Bumper, 0, 0, -7.0F, -16.0F, -7.0F, 14, 14, 14, 0.0F, false));
		Bumper.cubeList.add(new ModelBox(Bumper, 0, 28, -6.0F, -22.0F, -6.0F, 12, 6, 12, 0.0F, false));
		Bumper.cubeList.add(new ModelBox(Bumper, 56, 1, -5.0F, -27.0F, -5.0F, 10, 5, 10, 0.0F, false));
		Bumper.cubeList.add(new ModelBox(Bumper, 56, 17, -4.0F, -30.0F, -4.0F, 8, 3, 8, 0.0F, false));
		
		Left_Side = new ModelRenderer(this);
		Left_Side.setRotationPoint(3.3308F, -12.7034F, 0.5F);
		setRotationAngle(Left_Side, 0.0F, 0.0F, 0.7854F);
		Bumper.addChild(Left_Side);
		Left_Side.cubeList.add(new ModelBox(Left_Side, 72, 28, -11.3308F, -12.0465F, -1.5F, 10, 10, 2, 0.0F, false));

		Right_Side = new ModelRenderer(this);
		Right_Side.setRotationPoint(-3.3308F, -12.7034F, 0.5F);
		setRotationAngle(Right_Side, 0.0F, 0.0F, -0.7854F);
		Bumper.addChild(Right_Side);
		Right_Side.cubeList.add(new ModelBox(Right_Side, 48, 28, 1.3261F, -12.0465F, -1.5F, 10, 10, 2, 0.0F, true));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		Base.render(f5);
		GlStateManager.pushMatrix();
		float scale = (10 - Math.min(((EntitySquidBumper)entity).getRespawnTime(), 10))/10f;
		
		if(((EntitySquidBumper)entity).getInkHealth() <= 0f)
			GlStateManager.scale(scale, scale, scale);
		Bumper.render(f5);
		GlStateManager.popMatrix();
		
	}
	
	@Override
	public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTickTime)
	{
		super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTickTime);
		
		float scale = (10 - Math.min(((EntitySquidBumper)entity).getRespawnTime(), 10))/10f;
		
		Bumper.rotationPointY = 24;
		
		if(((EntitySquidBumper)entity).getInkHealth() <= 0f)
			Bumper.rotationPointY *= 1/scale;
	}
	
	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
	{
		Bumper.rotateAngleY = entityIn.rotationYaw * 0.017453292F;
	}
	
	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}