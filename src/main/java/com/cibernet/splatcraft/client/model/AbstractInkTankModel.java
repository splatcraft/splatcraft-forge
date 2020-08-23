package com.cibernet.splatcraft.client.model;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class AbstractInkTankModel extends BipedModel<LivingEntity>
{
	
	protected List<ModelRenderer> inkPieces = new ArrayList<>();
	
	protected float inkBarY = 0;
	
	public AbstractInkTankModel()
	{
		super(1);
	}
	
	@Override
	public void setLivingAnimations(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float partialTick)
	{
		super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
		
	}
	
	public void setInkLevels(float inkPctg)
	{
		for(int i = 1; i <= inkPieces.size(); i++)
		{
			ModelRenderer box = inkPieces.get(i-1);
			if(inkPctg == 0)
			{
				box.showModel = false;
				continue;
			}
			box.showModel = true;
			box.rotationPointY = 23.25F - Math.min(i*(inkPctg), i);
		}
	}
	
	@Override
	public void setRotationAngles(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
	{
		super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		
	}
}
