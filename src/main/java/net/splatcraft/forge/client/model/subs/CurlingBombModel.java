package net.splatcraft.forge.client.model.subs;// Made with Blockbench 4.6.5
// Exported for Minecraft version 1.15 - 1.16 with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.splatcraft.forge.entities.subs.CurlingBombEntity;

public class CurlingBombModel extends AbstractSubWeaponModel<CurlingBombEntity> {
	private final ModelRenderer top;
	private final ModelRenderer handle;
	private final ModelRenderer cube_r1;
	private final ModelRenderer cube_r2;
	private final ModelRenderer cube_r3;

	public CurlingBombModel() {
		texWidth = 64;
		texHeight = 64;

		top = new ModelRenderer(this);
		top.setPos(0.0F, 23.0F, 0.0F);
		top.texOffs(0, 55).addBox(-4.0F, -0.4F, -4.0F, 8.0F, 1.0F, 8.0F, -0.05F, false);

		handle = new ModelRenderer(this);
		handle.setPos(2.5346F, -2.6775F, 0.0F);
		top.addChild(handle);
		handle.texOffs(0, 19).addBox(-3.5346F, 0.2775F, -1.0F, 1.0F, 2.0F, 2.0F, 0.0F, false);
		handle.texOffs(21, 13).addBox(-3.5346F, -0.6225F, -1.0F, 5.0F, 1.0F, 2.0F, 0.001F, false);

		cube_r1 = new ModelRenderer(this);
		cube_r1.setPos(0.0F, 0.0F, 0.0F);
		handle.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.0F, 0.0F, 0.3847F);
		cube_r1.texOffs(22, 29).addBox(-1.2F, -0.2F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
		cube_r1.texOffs(21, 23).addBox(-1.5F, -0.2F, -1.0F, 3.0F, 1.0F, 2.0F, 0.0F, false);

		cube_r2 = new ModelRenderer(this);
		cube_r2.setPos(-2.8783F, 2.1816F, 0.0F);
		handle.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.0F, 0.0F, 0.2182F);
		cube_r2.texOffs(6, 29).addBox(-0.925F, -2.25F, -1.0F, 1.0F, 1.0F, 2.0F, -0.001F, false);

		cube_r3 = new ModelRenderer(this);
		cube_r3.setPos(-2.3706F, 2.0834F, 0.0F);
		handle.addChild(cube_r3);
		setRotationAngle(cube_r3, 0.0F, 0.0F, -0.6109F);
		cube_r3.texOffs(0, 9).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, 0.0F, false);
	}

	@Override
	public void setupAnim(CurlingBombEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		top.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}