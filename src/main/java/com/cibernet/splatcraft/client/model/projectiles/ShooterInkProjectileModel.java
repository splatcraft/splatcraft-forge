package com.cibernet.splatcraft.client.model.projectiles;// Made with Blockbench 3.8.0
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


import com.cibernet.splatcraft.entities.InkProjectileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ShooterInkProjectileModel extends InkProjectileModel
{
    private final ModelRenderer main;
    private final ModelRenderer middle;
    private final ModelRenderer back;

    public ShooterInkProjectileModel()
    {
        texWidth = 16;
        texHeight = 16;

        main = new ModelRenderer(this);
        main.setPos(0.0F, 0.0F, 0.0F);
        main.texOffs(0, 5).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 3.0F, 0.0F, false);

        middle = new ModelRenderer(this);
        middle.setPos(0.0F, -1.0F, 1.5F);
        main.addChild(middle);
        middle.texOffs(0, 0).addBox(-1.0F, -1.0F, -0.75F, 2.0F, 2.0F, 3.0F, -0.3F, false);

        back = new ModelRenderer(this);
        back.setPos(0.0F, 0.0F, 2.0F);
        middle.addChild(back);
        back.texOffs(7, 0).addBox(-0.5F, -0.5F, -0.75F, 1.0F, 1.0F, 2.0F, -0.15F, false);
    }

    @Override
    public void setupAnim(InkProjectileEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        //previously the render function, render code was moved to a method below
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        main.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}
