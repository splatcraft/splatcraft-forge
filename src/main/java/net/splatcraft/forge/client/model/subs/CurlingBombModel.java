package net.splatcraft.forge.client.model.subs;// Made with Blockbench 4.6.4
// Exported for Minecraft version 1.15 - 1.16 with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.splatcraft.forge.entities.subs.CurlingBombEntity;

public class CurlingBombModel extends AbstractSubWeaponModel<CurlingBombEntity> {
	private final ModelRenderer blades;
	private final ModelRenderer bone3;
	private final ModelRenderer cube_r1;
	private final ModelRenderer bone2;
	private final ModelRenderer cube_r2;
	private final ModelRenderer bone4;
	private final ModelRenderer cube_r3;
	private final ModelRenderer bumper1;
	private final ModelRenderer bumper2;
	private final ModelRenderer bumper3;
	private final ModelRenderer bumper4;
	private final ModelRenderer top;
	private final ModelRenderer handle;
	private final ModelRenderer cube_r4;
	private final ModelRenderer cube_r5;
	private final ModelRenderer cube_r6;
	private final ModelRenderer bb_main;

	public CurlingBombModel() {
		texWidth = 64;
		texHeight = 64;

		blades = new ModelRenderer(this);
		blades.setPos(0.0F, 24.05F, 0.0F);
		blades.texOffs(26, 27).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);

		bone3 = new ModelRenderer(this);
		bone3.setPos(0.0F, 0.0F, 0.0F);
		blades.addChild(bone3);
		

		cube_r1 = new ModelRenderer(this);
		cube_r1.setPos(0.0F, -1.3F, 1.0F);
		bone3.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.0F, 0.0F, 0.4363F);
		cube_r1.texOffs(2, 0).addBox(-0.5F, 0.0F, -0.3F, 1.0F, 0.0F, 3.0F, 0.0F, false);

		bone2 = new ModelRenderer(this);
		bone2.setPos(0.0F, 0.0F, 0.0F);
		blades.addChild(bone2);
		setRotationAngle(bone2, 0.0F, 2.0944F, 0.0F);
		

		cube_r2 = new ModelRenderer(this);
		cube_r2.setPos(0.0F, -1.3F, 1.0F);
		bone2.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.0F, 0.0F, 0.4363F);
		cube_r2.texOffs(2, 0).addBox(-0.5F, 0.0F, -0.3F, 1.0F, 0.0F, 3.0F, 0.0F, false);

		bone4 = new ModelRenderer(this);
		bone4.setPos(0.0F, 0.0F, 0.0F);
		blades.addChild(bone4);
		setRotationAngle(bone4, 0.0F, -2.0944F, 0.0F);
		

		cube_r3 = new ModelRenderer(this);
		cube_r3.setPos(0.0F, -1.3F, 1.0F);
		bone4.addChild(cube_r3);
		setRotationAngle(cube_r3, 0.0F, 0.0F, 0.4363F);
		cube_r3.texOffs(2, 0).addBox(-0.5F, 0.0F, -0.3F, 1.0F, 0.0F, 3.0F, 0.0F, false);

		bumper1 = new ModelRenderer(this);
		bumper1.setPos(0.0F, 24.0F, 0.0F);
		setRotationAngle(bumper1, 0.0F, -1.5708F, 0.0F);
		bumper1.texOffs(24, 0).addBox(-3.5F, -4.5F, -5.0F, 7.0F, 3.0F, 1.0F, 0.0F, false);
		bumper1.texOffs(0, 13).addBox(-0.5F, -3.5F, -4.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);

		bumper2 = new ModelRenderer(this);
		bumper2.setPos(0.0F, 24.0F, 0.0F);
		setRotationAngle(bumper2, 0.0F, 3.1416F, 0.0F);
		bumper2.texOffs(24, 0).addBox(-3.5F, -4.5F, -5.0F, 7.0F, 3.0F, 1.0F, 0.0F, false);
		bumper2.texOffs(0, 13).addBox(-0.5F, -3.5F, -4.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);

		bumper3 = new ModelRenderer(this);
		bumper3.setPos(0.0F, 24.0F, 0.0F);
		bumper3.texOffs(24, 0).addBox(-3.5F, -4.5F, -5.0F, 7.0F, 3.0F, 1.0F, 0.0F, false);
		bumper3.texOffs(0, 13).addBox(-0.5F, -3.5F, -4.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);

		bumper4 = new ModelRenderer(this);
		bumper4.setPos(0.0F, 24.0F, 0.0F);
		setRotationAngle(bumper4, 0.0F, 1.5708F, 0.0F);
		bumper4.texOffs(24, 0).addBox(-3.5F, -4.5F, -5.0F, 7.0F, 3.0F, 1.0F, 0.0F, false);
		bumper4.texOffs(0, 13).addBox(-0.5F, -3.5F, -4.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);

		top = new ModelRenderer(this);
		top.setPos(0.0F, 20.0F, 0.0F);
		top.texOffs(0, 9).addBox(-3.5F, -0.6F, -3.5F, 7.0F, 3.0F, 7.0F, -0.05F, false);

		handle = new ModelRenderer(this);
		handle.setPos(2.5346F, -2.6775F, 0.0F);
		top.addChild(handle);
		handle.texOffs(0, 19).addBox(-3.5346F, 0.2775F, -1.0F, 1.0F, 2.0F, 2.0F, 0.0F, false);
		handle.texOffs(21, 13).addBox(-3.5346F, -0.6225F, -1.0F, 5.0F, 1.0F, 2.0F, 0.001F, false);

		cube_r4 = new ModelRenderer(this);
		cube_r4.setPos(0.0F, 0.0F, 0.0F);
		handle.addChild(cube_r4);
		setRotationAngle(cube_r4, 0.0F, 0.0F, 0.3847F);
		cube_r4.texOffs(22, 29).addBox(-1.2F, -0.2F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
		cube_r4.texOffs(21, 23).addBox(-1.5F, -0.2F, -1.0F, 3.0F, 1.0F, 2.0F, 0.0F, false);

		cube_r5 = new ModelRenderer(this);
		cube_r5.setPos(-2.8783F, 2.1816F, 0.0F);
		handle.addChild(cube_r5);
		setRotationAngle(cube_r5, 0.0F, 0.0F, 0.2182F);
		cube_r5.texOffs(6, 29).addBox(-0.925F, -2.25F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);

		cube_r6 = new ModelRenderer(this);
		cube_r6.setPos(-2.3706F, 2.0834F, 0.0F);
		handle.addChild(cube_r6);
		setRotationAngle(cube_r6, 0.0F, 0.0F, -0.6109F);
		cube_r6.texOffs(0, 9).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, 0.0F, false);

		bb_main = new ModelRenderer(this);
		bb_main.setPos(0.0F, 24.0F, 0.0F);
		bb_main.texOffs(0, 0).addBox(-4.0F, -1.0F, -4.0F, 8.0F, 1.0F, 8.0F, 0.0F, false);
		bb_main.texOffs(0, 19).addBox(-3.5F, -4.5F, -3.5F, 7.0F, 3.0F, 7.0F, 0.0F, false);
	}

	@Override
	public void setupAnim(CurlingBombEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void prepareMobModel(CurlingBombEntity  entityIn, float limbSwing, float limbSwingAmount, float partialTick)
	{
		super.prepareMobModel(entityIn, limbSwing, limbSwingAmount, partialTick);

		blades.yRot = MathHelper.lerp(partialTick, entityIn.prevBladeRot, entityIn.bladeRot);

		top.y = 20 - MathHelper.clamp(30-entityIn.getFuseTime() + partialTick, 0, 1) * 3f;
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		blades.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		bumper1.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		bumper2.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		bumper3.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		bumper4.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		top.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		bb_main.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}