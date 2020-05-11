package com.cibernet.splatcraft.entities.models;// Made with Blockbench
// Paste this code into your mod.
// Make sure to generate all required imports

import com.cibernet.splatcraft.entities.models.ModelAbstractTank;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

import java.util.ArrayList;

public class ModelArmoredInkTank extends ModelAbstractTank
{
	private final ModelRenderer Head;
	private final ModelRenderer Torso;
	private final ModelRenderer Ink_Tank;
	private final ModelRenderer Left_Arm;
	private final ModelRenderer Right_Arm;

	public ModelArmoredInkTank() {
		textureWidth = 128;
		textureHeight = 128;
		
		float delta = 1;
		
		Head = new ModelRenderer(this);
		Head.setRotationPoint(0.0F, 0.0F, 0.0F);
		Head.cubeList.add(new ModelBox(Head, 0, 112, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));

		Torso = new ModelRenderer(this);
		Torso.setRotationPoint(0.0F, -0.25F, 0.0F);
		Torso.cubeList.add(new ModelBox(Torso, 16, 0, -4.0F, 0F, -2.0F, 8, 12, 4, delta, false));
		
		//Torso.addBox(-4.0f, 0f, -2f, 8, 12, 4, 0.9f);
		
		Ink_Tank = new ModelRenderer(this);
		Ink_Tank.setRotationPoint(0.0F, -2.25F, -1.225F);
		Torso.addChild(Ink_Tank);
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 0, 19, -2.0F, 3.25F, 3.25F, 4, 1, 4, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 16, 19, -2.0F, 10.25F, 3.25F, 4, 1, 4, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 0, 24, 1.0F, 4.25F, 3.25F, 1, 6, 1, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 6, 24, 1.0F, 4.25F, 6.25F, 1, 6, 1, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 10, 24, -2.0F, 4.25F, 6.25F, 1, 6, 1, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 14, 24, -2.0F, 4.25F, 3.25F, 1, 6, 1, 0.0F, false));
		
		inkPieces = new ArrayList<>();
		for(int i = 0; i < 6; i++)
		{
			ModelRenderer ink = new ModelRenderer(this);
			ink.setRotationPoint(0.0F, 23.25F, -0.75F);
			Ink_Tank.addChild(ink);
			ink.cubeList.add(new ModelBox(ink, 115, 0, -1.5F, -13.0F, 4.5F, 3, 1, 3, 0.0F, false));
			inkPieces.add(ink);
		}
		Left_Arm = new ModelRenderer(this);
		Left_Arm.setRotationPoint(0.0F, 0.0F, 0.0F);
		Left_Arm.cubeList.add(new ModelBox(Left_Arm, 40, 0, -1.0F, -2.0F, -2.0F, 4, 12, 4, delta, false));

		Right_Arm = new ModelRenderer(this);
		Right_Arm.setRotationPoint(-0.0F, 0.0F, 0.0F);
		Right_Arm.cubeList.add(new ModelBox(Right_Arm, 40, 0, -3.0F, -2.0F, -2.0F, 4, 12, 4, delta, true));
		
		bipedBody.addChild(Torso);
		bipedLeftArm.addChild(Left_Arm);
		bipedRightArm.addChild(Right_Arm);
		bipedHead.addChild(Head);
	}
	
}