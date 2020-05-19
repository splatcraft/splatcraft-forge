package com.cibernet.splatcraft.entities.models;// Made with Blockbench
// Paste this code into your mod.
// Make sure to generate all required imports

import com.cibernet.splatcraft.entities.models.ModelAbstractTank;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import java.util.ArrayList;

public class ModelClassicInkTank extends ModelAbstractTank
{
	private final ModelRenderer Right_Leg;
	private final ModelRenderer Head;
	private final ModelRenderer Torso;
	private final ModelRenderer Ink_Tank;
	private final ModelRenderer Left_Arm;

	public ModelClassicInkTank() {
		textureWidth = 64;
		textureHeight = 64;

		Right_Leg = new ModelRenderer(this);
		Right_Leg.setRotationPoint(-1.9F, 12.0F, 0.0F);

		Head = new ModelRenderer(this);
		Head.setRotationPoint(0.0F, 0.0F, 0.0F);
		Head.cubeList.add(new ModelBox(Head, 0, 112, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));

		Torso = new ModelRenderer(this);
		Torso.setRotationPoint(0.0F, -0.25F, 0.0F);
		Torso.cubeList.add(new ModelBox(Torso, 0, 0, -4.75F, -0.25F, -2.5F, 9, 12, 5, 0.0F, false));
		Torso.cubeList.add(new ModelBox(Torso, 30, 0, -1.0F, 3.0F, 2.0F, 2, 1, 2, 0.0F, false));

		Ink_Tank = new ModelRenderer(this);
		Ink_Tank.setRotationPoint(0.0F, 0.75F, 0.25F);
		Torso.addChild(Ink_Tank);
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 0, 19, -2.0F, 3.25F, 3.25F, 4, 1, 4, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 20, 28, -1.5F, 2.25F, 3.75F, 3, 1, 3, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 22, 32, -2.0F, 2.0F, 4.75F, 4, 2, 1, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 21, 35, -0.5F, 2.0F, 3.25F, 1, 2, 4, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 16, 19, -2.0F, 11.25F, 3.25F, 4, 1, 4, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 20, 24, -1.5F, 11.75F, 3.75F, 3, 1, 3, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 0, 24, 1.0F, 4.25F, 3.25F, 1, 7, 1, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 6, 24, 1.0F, 4.25F, 6.25F, 1, 7, 1, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 10, 24, -2.0F, 4.25F, 6.25F, 1, 7, 1, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 14, 24, -2.0F, 4.25F, 3.25F, 1, 7, 1, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 12, 39, 0.0F, 9.25F, 6.25F, 1, 1, 1, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 12, 39, 0.0F, 7.25F, 6.25F, 1, 1, 1, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 12, 39, 0.0F, 5.25F, 6.25F, 1, 1, 1, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 0, 33, -1.0F, 0.75F, 4.25F, 2, 2, 2, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 8, 34, -2.75F, 3.75F, 4.75F, 1, 1, 1, 0.0F, false));

		
		inkPieces = new ArrayList<>();
		
		for(int i = 0; i < 7; i++)
		{
			ModelRenderer ink = new ModelRenderer(this);
			ink.setRotationPoint(0.0F, 23.25F, -0.75F);
			Ink_Tank.addChild(ink);
			
			ink.cubeList.add(new ModelBox(ink, 52, 0, -1.5F, -12F  - 0, 4.5F, 3, 1, 3, 0.0F, false));
			
			inkPieces.add(ink);
		}
		
		bipedBody.cubeList.clear();
		bipedBody.addChild(Torso);
		
		Left_Arm = new ModelRenderer(this);
		Left_Arm.setRotationPoint(5.0F, 2.0F, 0.0F);
		Left_Arm.cubeList.add(new ModelBox(Left_Arm, 112, 112, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));
	}
	
	@Override
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
	}
	
	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}