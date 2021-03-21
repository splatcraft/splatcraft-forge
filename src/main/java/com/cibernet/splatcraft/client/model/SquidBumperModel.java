package com.cibernet.splatcraft.client.model;// Made with Blockbench 3.5.4
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


import com.cibernet.splatcraft.entities.SquidBumperEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.math.MathHelper;

public class SquidBumperModel extends EntityModel<SquidBumperEntity> {
    private final ModelRenderer Base;
    private final ModelRenderer Bumper;
    private final ModelRenderer Left_Side;
    private final ModelRenderer Right_Side;

    public SquidBumperModel() {
        textureWidth = 128;
        textureHeight = 128;

        Base = new ModelRenderer(this);
        Base.setRotationPoint(0.0F, 24.0F, 0.0F);
        Base.setTextureOffset(0, 46).addBox(-5.0F, -2.0F, -5.0F, 10.0F, 2.0F, 10.0F, 0.0F, false);

        Bumper = new ModelRenderer(this);
        Bumper.setRotationPoint(0.0F, 24.0F, 0.0F);
        Bumper.setTextureOffset(0, 0).addBox(-7.0F, -16.0F, -7.0F, 14.0F, 14.0F, 14.0F, 0.0F, false);
        Bumper.setTextureOffset(0, 28).addBox(-6.0F, -22.0F, -6.0F, 12.0F, 6.0F, 12.0F, 0.0F, false);
        Bumper.setTextureOffset(56, 1).addBox(-5.0F, -27.0F, -5.0F, 10.0F, 5.0F, 10.0F, 0.0F, false);
        Bumper.setTextureOffset(56, 17).addBox(-4.0F, -30.0F, -4.0F, 8.0F, 3.0F, 8.0F, 0.0F, false);

        Left_Side = new ModelRenderer(this);
        Left_Side.setRotationPoint(3.3308F, -12.7034F, 0.5F);
        Bumper.addChild(Left_Side);
        setRotationAngle(Left_Side, 0.0F, 0.0F, 0.7854F);
        Left_Side.setTextureOffset(72, 28).addBox(-11.3308F, -12.0465F, -1.5F, 10.0F, 10.0F, 2.0F, 0.0F, false);

        Right_Side = new ModelRenderer(this);
        Right_Side.setRotationPoint(-3.3308F, -12.7034F, 0.5F);
        Bumper.addChild(Right_Side);
        setRotationAngle(Right_Side, 0.0F, 0.0F, -0.7854F);
        Right_Side.setTextureOffset(48, 28).addBox(1.3261F, -12.0465F, -1.5F, 10.0F, 10.0F, 2.0F, 0.0F, true);
    }

    @Override
    public void setRotationAngles(SquidBumperEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        //previously the render function, render code was moved to a method below
    }

    @Override
    public void setLivingAnimations(SquidBumperEntity entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);

        Bumper.rotateAngleY = (float) Math.PI / 180F * MathHelper.interpolateAngle(partialTick, entityIn.prevRotationYawHead, entityIn.prevRotationYawHead) + (float) Math.PI;

        Base.rotateAngleX = 0.0F;
        Base.rotateAngleY = 0.0F;
        Base.rotateAngleZ = 0.0F;

        float scale = (10 - Math.min(entityIn.getRespawnTime(), 10)) / 10f;

        Bumper.rotationPointY = 24;

        if (entityIn.getInkHealth() <= 0f)
            Bumper.rotationPointY *= 1 / scale;
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        renderBase(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        renderBumper(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void render(SquidBumperEntity entityIn, MatrixStack matrixStackIn, IVertexBuilder builderIn, int packedLightIn) {
        float scale = (10 - Math.min(entityIn.getRespawnTime(), 10)) / 10f;
        int color = entityIn.getColor();
        float r = (float) (Math.floor((float) color / (256 * 256)) / 255f);
        float g = (float) (Math.floor((float) color / 256) % 256 / 255f);
        float b = (color % 256) / 255f;

        renderBase(matrixStackIn, builderIn, packedLightIn, OverlayTexture.NO_OVERLAY, r, g, b, 1);
        matrixStackIn.push();
        matrixStackIn.translate(0, 24 * (scale > 0 ? 1 / scale : 0), 0);
        matrixStackIn.scale(scale, scale, scale);
        renderBumper(matrixStackIn, builderIn, packedLightIn, OverlayTexture.NO_OVERLAY, r, g, b, 1);
        matrixStackIn.pop();

    }


    public void renderBase(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        Base.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void renderBumper(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        Bumper.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
