package com.cibernet.splatcraft.entities.models;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

import java.util.List;

public abstract class ModelAbstractTank extends ModelBiped
{
	protected List<ModelRenderer> inkPieces;
	protected float inkPctg;
	
	public ModelAbstractTank()
	{
	
	}
	
	public void setInk(float inkPctg) {this.inkPctg = inkPctg;}
	
	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
	{
		super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
		//copyModelAngles(bipedBody, ink);
	}
	
	@Override
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		
		/*
		if(inkPctg != 0)
		{
			GlStateManager.scale(1, inkPctg, 1);
			GlStateManager.translate(0, -0.18 / -inkPctg, 0);
			ink.render(scale);
			GlStateManager.translate(0, 0.18 / -inkPctg, 0);
			GlStateManager.scale(1, 1/inkPctg, 1);
		}
		*/
	}
}
