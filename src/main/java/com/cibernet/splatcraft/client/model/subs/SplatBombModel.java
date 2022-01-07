package com.cibernet.splatcraft.client.model.subs;// Made with Blockbench 3.8.4
// Exported for Minecraft version 1.15 - 1.16
// Paste this class into your mod and generate all required imports


import com.cibernet.splatcraft.client.model.subs.AbstractSubWeaponModel;
import com.cibernet.splatcraft.entities.subs.SplatBombEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;

public class SplatBombModel extends AbstractSubWeaponModel<SplatBombEntity> {
	private final ModelRenderer Main;
	private final ModelRenderer bone13;
	private final ModelRenderer bone;
	private final ModelRenderer bone2;
	private final ModelRenderer bone3;
	private final ModelRenderer bone7;
	private final ModelRenderer bone6;
	private final ModelRenderer cube_r1;
	private final ModelRenderer bone5;
	private final ModelRenderer cube_r2;
	private final ModelRenderer bone4;
	private final ModelRenderer cube_r3;
	private final ModelRenderer bone8;
	private final ModelRenderer cube_r4;
	private final ModelRenderer bone9;
	private final ModelRenderer cube_r5;
	private final ModelRenderer bone10;
	private final ModelRenderer cube_r6;
	private final ModelRenderer bone11;
	private final ModelRenderer bone17;
	private final ModelRenderer bone14;
	private final ModelRenderer cube_r7;
	private final ModelRenderer bone15;
	private final ModelRenderer cube_r8;
	private final ModelRenderer bone16;
	private final ModelRenderer cube_r9;
	private final ModelRenderer bone18;

	public SplatBombModel() {
		texWidth = 32;
		texHeight = 32;

		Main = new ModelRenderer(this);
		Main.setPos(0.0F, 0.0F, 0.0F);
		

		bone13 = new ModelRenderer(this);
		bone13.setPos(0.0F, 3.4F, 0.3F);
		Main.addChild(bone13);
		

		bone = new ModelRenderer(this);
		bone.setPos(0.0F, 0.0F, -1.0F);
		bone13.addChild(bone);
		bone.texOffs(18, 2).addBox(-3.0F, -1.0F, 3.0F, 6.0F, 1.0F, 1.0F, 0.0F, false);

		bone2 = new ModelRenderer(this);
		bone2.setPos(-3.0F, -0.5F, 2.0F);
		bone13.addChild(bone2);
		setRotationAngle(bone2, 0.0F, 1.0472F, 0.0F);
		bone2.texOffs(18, 0).addBox(0.0F, -0.5F, -1.0F, 6.0F, 1.0F, 1.0F, 0.0F, false);

		bone3 = new ModelRenderer(this);
		bone3.setPos(3.0F, -0.5F, 2.0F);
		bone13.addChild(bone3);
		setRotationAngle(bone3, 0.0F, -1.0472F, 0.0F);
		bone3.texOffs(0, 18).addBox(-6.0F, -0.5F, -1.0F, 6.0F, 1.0F, 1.0F, 0.0F, false);

		bone7 = new ModelRenderer(this);
		bone7.setPos(-0.25F, 3.65F, -0.7F);
		Main.addChild(bone7);
		

		bone6 = new ModelRenderer(this);
		bone6.setPos(3.0F, -0.75F, 3.0F);
		bone7.addChild(bone6);
		setRotationAngle(bone6, 0.0F, 1.5708F, 0.0F);
		

		cube_r1 = new ModelRenderer(this);
		cube_r1.setPos(4.8754F, -0.5F, -2.8148F);
		bone6.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.0F, 0.0F, 1.0472F);
		cube_r1.texOffs(12, 12).addBox(-6.75F, -0.9F, -0.5F, 7.0F, 1.0F, 1.0F, 0.0F, false);

		bone5 = new ModelRenderer(this);
		bone5.setPos(-3.0F, -0.75F, 3.0F);
		bone7.addChild(bone5);
		setRotationAngle(bone5, 0.0F, 0.5236F, 0.0F);
		

		cube_r2 = new ModelRenderer(this);
		cube_r2.setPos(0.3207F, -0.5F, 0.1852F);
		bone5.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.0F, 0.0F, -1.0472F);
		cube_r2.texOffs(12, 14).addBox(-0.25F, -0.9F, -0.5F, 7.0F, 1.0F, 1.0F, 0.0F, false);

		bone4 = new ModelRenderer(this);
		bone4.setPos(3.0F, -0.75F, 3.0F);
		bone7.addChild(bone4);
		setRotationAngle(bone4, 0.0F, -0.5236F, 0.0F);
		

		cube_r3 = new ModelRenderer(this);
		cube_r3.setPos(0.0F, -0.5F, 0.0F);
		bone4.addChild(cube_r3);
		setRotationAngle(cube_r3, 0.0F, 0.0F, 1.0472F);
		cube_r3.texOffs(12, 16).addBox(-6.75F, -0.9F, -0.5F, 7.0F, 1.0F, 1.0F, 0.0F, false);

		bone8 = new ModelRenderer(this);
		bone8.setPos(-3.0F, -0.75F, 3.0F);
		bone7.addChild(bone8);
		setRotationAngle(bone8, 0.0F, 0.5236F, 0.0F);
		

		cube_r4 = new ModelRenderer(this);
		cube_r4.setPos(0.3207F, -0.5F, 0.1852F);
		bone8.addChild(cube_r4);
		setRotationAngle(cube_r4, 0.0F, 0.0F, -0.5236F);
		cube_r4.texOffs(6, 22).addBox(-1.9F, -0.95F, -1.0F, 2.0F, 2.0F, 2.0F, -0.1F, false);

		bone9 = new ModelRenderer(this);
		bone9.setPos(3.5F, -0.75F, 3.0F);
		bone7.addChild(bone9);
		setRotationAngle(bone9, 0.0F, -0.5236F, 0.0F);
		

		cube_r5 = new ModelRenderer(this);
		cube_r5.setPos(-0.3207F, -0.5F, 0.1852F);
		bone9.addChild(cube_r5);
		setRotationAngle(cube_r5, 0.0F, 0.0F, 0.5236F);
		cube_r5.texOffs(18, 20).addBox(-0.1433F, -0.925F, -0.9352F, 2.0F, 2.0F, 2.0F, -0.1F, false);

		bone10 = new ModelRenderer(this);
		bone10.setPos(0.275F, -0.75F, -2.3F);
		bone7.addChild(bone10);
		setRotationAngle(bone10, 0.0F, -1.5708F, 0.0F);
		

		cube_r6 = new ModelRenderer(this);
		cube_r6.setPos(0.3207F, -0.5F, 0.1852F);
		bone10.addChild(cube_r6);
		setRotationAngle(cube_r6, 0.0F, 0.0F, -0.5236F);
		cube_r6.texOffs(0, 20).addBox(-1.9084F, -0.95F, -1.1F, 2.0F, 2.0F, 2.0F, -0.1F, false);

		bone11 = new ModelRenderer(this);
		bone11.setPos(0.0F, 0.0F, 0.0F);
		bone7.addChild(bone11);
		bone11.texOffs(12, 18).addBox(-0.825F, -8.2F, 0.35F, 2.0F, 2.0F, 2.0F, 0.0F, false);

		bone17 = new ModelRenderer(this);
		bone17.setPos(0.0F, 4.0F, 0.0F);
		Main.addChild(bone17);
		

		bone14 = new ModelRenderer(this);
		bone14.setPos(0.0F, -1.0F, 0.0F);
		bone17.addChild(bone14);
		setRotationAngle(bone14, 0.0F, -1.0472F, 0.0F);
		

		cube_r7 = new ModelRenderer(this);
		cube_r7.setPos(0.4919F, 0.2349F, -1.1981F);
		bone14.addChild(cube_r7);
		setRotationAngle(cube_r7, -0.2618F, 0.0F, 0.0F);
		cube_r7.texOffs(12, 6).addBox(-3.05F, -6.5981F, -0.434F, 6.0F, 6.0F, 0.0F, 0.0F, false);

		bone15 = new ModelRenderer(this);
		bone15.setPos(-2.8F, -1.0F, 0.0F);
		bone17.addChild(bone15);
		setRotationAngle(bone15, 0.0F, -1.0472F, 0.0F);
		

		cube_r8 = new ModelRenderer(this);
		cube_r8.setPos(0.4919F, 0.2349F, -1.1981F);
		bone15.addChild(cube_r8);
		setRotationAngle(cube_r8, -0.2618F, 2.0944F, 0.0F);
		cube_r8.texOffs(0, 12).addBox(-3.0F, -6.4731F, -0.434F, 6.0F, 6.0F, 0.0F, 0.0F, false);

		bone16 = new ModelRenderer(this);
		bone16.setPos(-2.8F, -1.0F, 0.0F);
		bone17.addChild(bone16);
		setRotationAngle(bone16, 0.0F, -1.0472F, 0.0F);
		

		cube_r9 = new ModelRenderer(this);
		cube_r9.setPos(0.4919F, 0.2349F, -1.1981F);
		bone16.addChild(cube_r9);
		setRotationAngle(cube_r9, -0.2618F, -2.0944F, 0.0F);
		cube_r9.texOffs(0, 6).addBox(-4.45F, -5.7981F, -2.884F, 6.0F, 6.0F, 0.0F, 0.0F, false);

		bone18 = new ModelRenderer(this);
		bone18.setPos(0.0F, -0.7F, 0.0F);
		bone17.addChild(bone18);
		bone18.texOffs(0, 0).addBox(-3.0F, -0.1F, -3.4F, 6.0F, 0.0F, 6.0F, 0.0F, false);
	}

	@Override
	public void setupAnim(SplatBombEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		Main.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}