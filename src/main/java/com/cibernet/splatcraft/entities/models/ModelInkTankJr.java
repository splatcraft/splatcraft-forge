package com.cibernet.splatcraft.entities.models;// Made with Blockbench
// Paste this code into your mod.
// Make sure to generate all required imports

import com.cibernet.splatcraft.entities.models.ModelAbstractTank;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import java.util.ArrayList;

public class ModelInkTankJr extends ModelAbstractTank
{
	private final ModelRenderer Head;
	private final ModelRenderer Torso;
	private final ModelRenderer Ink_Tank;
	private final ModelRenderer Tag;

	public ModelInkTankJr() {
		textureWidth = 128;
		textureHeight = 128;

		Head = new ModelRenderer(this);
		Head.setRotationPoint(0.0F, 0.0F, 0.0F);
		Head.cubeList.add(new ModelBox(Head, 0, 112, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));

		Torso = new ModelRenderer(this);
		Torso.setRotationPoint(0.0F, -0.25F, 0.0F);
		Torso.cubeList.add(new ModelBox(Torso, 0, 0, -4.8056F, -0.25F, -2.5F, 9, 12, 5, 0.0F, false));
		Torso.cubeList.add(new ModelBox(Torso, 31, 0, -1.0F, 3.0F, 2.5F, 2, 1, 1, 0.0F, false));
		
		Ink_Tank = new ModelRenderer(this);
		Ink_Tank.setRotationPoint(0.0F, 0.75F, 0.75F);
		Torso.addChild(Ink_Tank);
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 20, 18, -2.0F, 1.5F, 3.75F, 4, 2, 4, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 8, 33, -3.5F, 3.15F, 4.25F, 2, 1, 2, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 12, 39, 0.9875F, 5.25F, 7.25F, 1, 1, 1, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 12, 39, 0.9875F, 7.25F, 7.25F, 1, 1, 1, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 12, 39, 0.9875F, 9.25F, 7.25F, 1, 1, 1, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 14, 24, -3.0F, 4.25F, 3.25F, 1, 7, 1, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 10, 24, -3.0F, 4.25F, 7.25F, 1, 7, 1, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 6, 24, 2.0F, 4.25F, 7.25F, 1, 7, 1, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 0, 24, 2.0F, 4.25F, 3.25F, 1, 7, 1, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 18, 25, -3.0F, 11.25F, 3.25F, 6, 1, 5, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 0, 18, -2.5F, 3.25F, 3.25F, 5, 1, 5, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 31, 2, -0.5F, 1.75F, 2.0F, 1, 2, 2, 0.0F, false));
		
		
		
		inkPieces = new ArrayList<>();
		
		
		for(int i = 0; i < 7; i++)
		{
			ModelRenderer ink = new ModelRenderer(this);
			ink.setRotationPoint(0.0F, 23.25F, -0.75F);
			Ink_Tank.addChild(ink);
			ink.cubeList.add(new ModelBox(ink, 110, 0, -2.5F, -12.0F, 4.5F, 5, 1, 4, 0.0F, false));
			inkPieces.add(ink);
		}
		
		
		Tag = new ModelRenderer(this);
		Tag.setRotationPoint(-3.1168F, 2.8445F, 8.9821F);
		setRotationAngle(Tag, -0.1309F, -0.3927F, -0.3054F);
		Ink_Tank.addChild(Tag);
		Tag.cubeList.add(new ModelBox(Tag, 8, 36, -0.8541F, 0.6055F, -2.1381F, 2, 0, 2, 0.0F, false));

		bipedBody.addChild(Torso);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
	
	@Override
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		//Ink_Tank.render(scale);
	}
}