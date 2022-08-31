package net.splatcraft.forge.client.model.projectiles;// Made with Blockbench 3.8.2
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


import net.splatcraft.forge.entities.InkProjectileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;

public class RollerInkProjectileModel extends InkProjectileModel
{
    private final ModelRenderer main;

    public RollerInkProjectileModel()
    {
        texWidth = 16;
        texHeight = 16;

        main = new ModelRenderer(this);
        main.setPos(0.0F, 0.0F, 0.0F);
        main.texOffs(6, 6).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        main.texOffs(5, 0).addBox(-0.75F, -1.25F, 1.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        main.texOffs(0, 4).addBox(1.0F, -2.0F, -0.5F, 1.0F, 1.0F, 3.0F, 0.0F, false);
        main.texOffs(0, 0).addBox(-1.75F, -3.0F, -0.5F, 1.0F, 1.0F, 3.0F, -0.25F, false);
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
