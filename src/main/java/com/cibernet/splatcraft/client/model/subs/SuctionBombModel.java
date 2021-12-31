package com.cibernet.splatcraft.client.model.subs;// Made with Blockbench 3.8.4
// Exported for Minecraft version 1.15 - 1.16
// Paste this class into your mod and generate all required imports


import com.cibernet.splatcraft.entities.subs.SuctionBombEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class SuctionBombModel extends AbstractSubWeaponModel<SuctionBombEntity> {
	private final ModelRenderer Main;
	private final ModelRenderer Neck;
	private final ModelRenderer Top;

	public SuctionBombModel() {
		textureWidth = 32;
		textureHeight = 32;

		Main = new ModelRenderer(this);
		Main.setRotationPoint(0.0F, 0.0F, 0.0F);
		Main.setTextureOffset(0, 10).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 3.0F, 4.0F, 0.0F, false);
		Main.setTextureOffset(14, 15).addBox(-1.0F, -4.25F, -1.0F, 2.0F, 1.0F, 2.0F, 0.2F, false);

		Neck = new ModelRenderer(this);
		Neck.setRotationPoint(0.0F, -3.75F, 0.0F);
		Main.addChild(Neck);
		Neck.setTextureOffset(12, 10).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 2.0F, 2.0F, -0.2F, false);
		Neck.setTextureOffset(0, 10).addBox(-0.5F, -1.5F, -0.5F, 1.0F, 1.0F, 1.0F, 0.1F, false);

		Top = new ModelRenderer(this);
		Top.setRotationPoint(0.0F, -2.5F, 0.0F);
		Neck.addChild(Top);
		Top.setTextureOffset(0, 0).addBox(-2.0F, -7.7F, -2.0F, 4.0F, 6.0F, 4.0F, 0.0F, false);
		Top.setTextureOffset(12, 0).addBox(-1.5F, -1.7F, -1.5F, 3.0F, 1.0F, 3.0F, 0.0F, false);
		Top.setTextureOffset(0, 0).addBox(-0.5F, -1.2F, -0.5F, 1.0F, 2.0F, 1.0F, -0.1F, false);
	}

	@Override
	public void setRotationAngles(SuctionBombEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void setLivingAnimations(SuctionBombEntity entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
		super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);

		float f9 = (float)entityIn.shakeTime - partialTick;
		if(f9 >= 0)
		{
			float f10 = -MathHelper.sin(f9*3f)/6f * f9;
			Neck.rotateAngleX = f10/2f;
			Top.rotateAngleX = f10;
		}
		else
		{
			Neck.rotateAngleX = 0;
			Top.rotateAngleX = 0;
		}
	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		Main.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
