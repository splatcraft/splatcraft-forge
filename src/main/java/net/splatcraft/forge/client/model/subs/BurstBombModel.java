package net.splatcraft.forge.client.model.subs;// Made with Blockbench 3.8.3
// Exported for Minecraft version 1.15 - 1.16
// Paste this class into your mod and generate all required imports


import net.splatcraft.forge.entities.subs.BurstBombEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;

public class BurstBombModel extends AbstractSubWeaponModel<BurstBombEntity>
{
	private final ModelRenderer bone;

	public BurstBombModel()
	{
		texWidth = 32;
		texHeight = 32;

		bone = new ModelRenderer(this);
		bone.setPos(0.0F, 0F, 0.0F);
		bone.texOffs(0, 0).addBox(-3.0F, -2.5F, -3.0F, 6.0F, 5.0F, 6.0F, 0.0F, false);
		bone.texOffs(12, 12).addBox(-2.0F, -3.5F, -2.0F, 4.0F, 1.0F, 4.0F, 0.0F, false);
		bone.texOffs(0, 11).addBox(-2.0F, 2.5F, -2.0F, 4.0F, 1.0F, 4.0F, 0.0F, false);
		bone.texOffs(0, 16).addBox(-1.5F, -5.5F, -1.5F, 3.0F, 1.0F, 3.0F, 0.0F, false);
		bone.texOffs(12, 17).addBox(-1.0F, -4.5F, -1.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
	}


	@Override
	public void setupAnim(BurstBombEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
	{
		bone.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}
