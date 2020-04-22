package com.cibernet.splatcraft.entities.models;// Made with Blockbench
// Paste this code into your mod.
// Make sure to generate all required imports

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelInkTank extends ModelBiped
{
	private final ModelRenderer Right_Leg;
	private final ModelRenderer Head;
	private final ModelRenderer Torso;
	private final ModelRenderer Ink_Tank;
	private final ModelRenderer Ink;
	private final ModelRenderer Left_Arm;

	public ModelInkTank(float inkPctg) {
		textureWidth = 128;
		textureHeight = 128;

		Right_Leg = new ModelRenderer(this);
		Right_Leg.setRotationPoint(-1.9F, 12.0F, 0.0F);

		Head = new ModelRenderer(this);
		Head.setRotationPoint(0.0F, 0.0F, 0.0F);
		Head.cubeList.add(new ModelBox(Head, 0, 112, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));

		Torso = new ModelRenderer(this);
		Torso.setRotationPoint(0.0F, -0.25F, 0.0F);
		Torso.cubeList.add(new ModelBox(Torso, 0, 0, -4.75F, -0.25F, -2.5F, 9, 12, 5, 0.0F, false));
		Torso.cubeList.add(new ModelBox(Torso, 31, 0, -1.0F, 3.0F, 2.5F, 2, 1, 1, 0.0F, false));

		Ink_Tank = new ModelRenderer(this);
		Ink_Tank.setRotationPoint(0.0F, 0.75F, 0.75F);
		Torso.addChild(Ink_Tank);
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 31, 2, -0.5F, 1.75F, 2.0F, 1, 2, 2, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 0, 19, -2.0F, 3.25F, 3.25F, 4, 1, 4, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 16, 19, -2.0F, 11.25F, 3.25F, 4, 1, 4, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 0, 24, 1.0F, 4.25F, 3.25F, 1, 7, 1, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 6, 24, 1.0F, 4.25F, 6.25F, 1, 7, 1, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 10, 24, -2.0F, 4.25F, 6.25F, 1, 7, 1, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 14, 24, -2.0F, 4.25F, 3.25F, 1, 7, 1, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 12, 39, 0.0F, 9.25F, 6.25F, 1, 1, 1, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 12, 39, 0.0F, 7.25F, 6.25F, 1, 1, 1, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 12, 39, 0.0F, 5.25F, 6.25F, 1, 1, 1, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 0, 33, -1.0F, 2.25F, 4.25F, 2, 1, 2, 0.0F, false));
		Ink_Tank.cubeList.add(new ModelBox(Ink_Tank, 8, 33, -3.5F, 2.5F, 4.25F, 2, 1, 2, 0.0F, false));

		Ink = new ModelRenderer(this);
		Ink.setRotationPoint(0.0F, 23.25F, -0.75F);
		Ink_Tank.addChild(Ink);
		Ink.cubeList.add(new ModelBox(Ink, 116, 30, -1.5F, -11.25F  - ((8*inkPctg) % 1f), 4.5F, 3, (int) (inkPctg*-8), 3, 0.0F, false));
		
		Left_Arm = new ModelRenderer(this);
		Left_Arm.setRotationPoint(5.0F, 2.0F, 0.0F);
		Left_Arm.cubeList.add(new ModelBox(Left_Arm, 112, 112, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		Right_Leg.render(f5);
		Head.render(f5);
		Torso.render(f5);
		Left_Arm.render(f5);
	}
	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}