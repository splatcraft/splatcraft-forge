package net.splatcraft.forge.client.model;// Made with Blockbench 3.5.4
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


import net.minecraft.client.Minecraft;
import net.splatcraft.forge.entities.SquidBumperEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.math.MathHelper;

public class SquidBumperModel extends EntityModel<SquidBumperEntity>
{
    private final ModelRenderer Base;
    private final ModelRenderer Bumper;
    private final ModelRenderer Left_Side;
    private final ModelRenderer Right_Side;

    public SquidBumperModel()
    {
        texWidth = 128;
        texHeight = 128;

        Base = new ModelRenderer(this);
        Base.setPos(0.0F, 24.0F, 0.0F);
        Base.texOffs(0, 46).addBox(-5.0F, -2.0F, -5.0F, 10.0F, 2.0F, 10.0F, 0.0F, false);

        Bumper = new ModelRenderer(this);
        Bumper.setPos(0.0F, 24.0F, 0.0F);
        Bumper.texOffs(0, 0).addBox(-7.0F, -16.0F, -7.0F, 14.0F, 14.0F, 14.0F, 0.0F, false);
        Bumper.texOffs(0, 28).addBox(-6.0F, -22.0F, -6.0F, 12.0F, 6.0F, 12.0F, 0.0F, false);
        Bumper.texOffs(56, 1).addBox(-5.0F, -27.0F, -5.0F, 10.0F, 5.0F, 10.0F, 0.0F, false);
        Bumper.texOffs(56, 17).addBox(-4.0F, -30.0F, -4.0F, 8.0F, 3.0F, 8.0F, 0.0F, false);

        Left_Side = new ModelRenderer(this);
        Left_Side.setPos(3.3308F, -12.7034F, 0.5F);
        Bumper.addChild(Left_Side);
        setRotationAngle(Left_Side, 0.0F, 0.0F, 0.7854F);
        Left_Side.texOffs(72, 28).addBox(-11.3308F, -12.0465F, -1.5F, 10.0F, 10.0F, 2.0F, 0.0F, false);

        Right_Side = new ModelRenderer(this);
        Right_Side.setPos(-3.3308F, -12.7034F, 0.5F);
        Bumper.addChild(Right_Side);
        setRotationAngle(Right_Side, 0.0F, 0.0F, -0.7854F);
        Right_Side.texOffs(48, 28).addBox(1.3261F, -12.0465F, -1.5F, 10.0F, 10.0F, 2.0F, 0.0F, true);
    }



    @Override
    public void setupAnim(SquidBumperEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        //previously the render function, render code was moved to a method below
    }

    @Override
    public void prepareMobModel(SquidBumperEntity entityIn, float limbSwing, float limbSwingAmount, float partialTick)
    {
        super.prepareMobModel(entityIn, limbSwing, limbSwingAmount, partialTick);

        Bumper.yRot = (float) Math.PI / 180F * MathHelper.lerp(partialTick, entityIn.yHeadRot, entityIn.yHeadRotO) + (float) Math.PI;

        Base.xRot = 0.0F;
        Base.yRot = 0.0F;
        Base.zRot = 0.0F;

        float scale = entityIn.getBumperScale(partialTick);

        Bumper.y = 24;

        if (entityIn.getInkHealth() <= 0f)
        {
            Bumper.y *= 1 / scale;
        }
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
    }

    public void render(SquidBumperEntity entityIn, MatrixStack matrixStackIn, IVertexBuilder builderIn, int packedLightIn)
    {
        float scale = entityIn.getBumperScale(Minecraft.getInstance().getDeltaFrameTime());
        int color = entityIn.getColor();
        float r = (float) (Math.floor((float) color / (256 * 256)) / 255f);
        float g = (float) (Math.floor((float) color / 256) % 256 / 255f);
        float b = (color % 256) / 255f;

        renderBase(matrixStackIn, builderIn, packedLightIn, OverlayTexture.NO_OVERLAY, r, g, b, 1);
        matrixStackIn.pushPose();
        matrixStackIn.translate(0, 24 * (scale > 0 ? 1 / scale : 0), 0);
        matrixStackIn.scale(scale, scale, scale);
        renderBumper(matrixStackIn, builderIn, packedLightIn, OverlayTexture.NO_OVERLAY, r, g, b, 1);
        matrixStackIn.popPose();

    }


    public void renderBase(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        Base.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void renderBumper(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        Bumper.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}
