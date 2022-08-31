package net.splatcraft.forge.client.model;// Made with Blockbench 3.5.4
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

public class InkSquidModel extends EntityModel<LivingEntity>
{
    private final ModelRenderer squid;
    private final ModelRenderer Body;
    private final ModelRenderer eyes;
    private final ModelRenderer tentacles;
    private final ModelRenderer LeftLimb;
    private final ModelRenderer RightLimb;

    public InkSquidModel()
    {
        texWidth = 64;
        texHeight = 64;

        squid = new ModelRenderer(this);
        squid.setPos(0.0F, 24.0F, 0.0F);


        Body = new ModelRenderer(this);
        Body.setPos(0.0F, 0.0F, 0.0F);
        squid.addChild(Body);
        Body.texOffs(0, 0).addBox(-4.0F, -4.0F, -2.0F, 8.0F, 4.0F, 4.0F, 0.0F, false);
        Body.texOffs(0, 9).addBox(-6.0F, -5.0F, -6.0F, 12.0F, 5.0F, 4.0F, 0.0F, false);
        Body.texOffs(27, 0).addBox(-5.0F, -4.0F, -8.0F, 10.0F, 4.0F, 2.0F, 0.0F, false);
        Body.texOffs(32, 6).addBox(-4.0F, -3.0F, -10.0F, 8.0F, 3.0F, 2.0F, 0.0F, false);
        Body.texOffs(32, 12).addBox(-2.0F, -2.0F, -12.0F, 4.0F, 2.0F, 2.0F, 0.0F, false);

        eyes = new ModelRenderer(this);
        eyes.setPos(0.0F, 0.0F, 0.0F);
        Body.addChild(eyes);
        eyes.texOffs(18, 19).addBox(-2.5F, -5.0F, -2.0F, 5.0F, 1.0F, 2.0F, 0.0F, false);
        eyes.texOffs(0, 19).addBox(-3.0F, -4.5F, -2.25F, 6.0F, 1.0F, 3.0F, 0.0F, false);

        tentacles = new ModelRenderer(this);
        tentacles.setPos(4.0F, 0.0F, -2.25F);
        Body.addChild(tentacles);
        setRotationAngle(tentacles, 0.0F, -0.7854F, 0.0F);
        tentacles.texOffs(56, 0).addBox(-2.6593F, -3.75F, 6.6593F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        tentacles.texOffs(56, 0).addBox(-1.495F, -3.75F, 5.495F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        tentacles.texOffs(56, 0).addBox(-0.1161F, -2.25F, 4.1161F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        tentacles.texOffs(56, 0).addBox(-1.495F, -2.25F, 5.495F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        tentacles.texOffs(56, 0).addBox(-0.1161F, -3.75F, 4.1161F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        tentacles.texOffs(56, 0).addBox(0.9875F, -3.75F, 2.9671F, 2.0F, 1.0F, 2.0F, 0.0F, false);

        LeftLimb = new ModelRenderer(this);
        LeftLimb.setPos(2.0F, 0.0F, 2.0F);
        squid.addChild(LeftLimb);
        LeftLimb.texOffs(0, 23).addBox(0.0F, -3.0F, 0.0F, 2.0F, 3.0F, 3.0F, 0.0F, false);
        LeftLimb.texOffs(0, 29).addBox(-1.0F, -3.0F, 3.0F, 3.0F, 3.0F, 4.0F, 0.0F, false);

        RightLimb = new ModelRenderer(this);
        RightLimb.setPos(-2.0F, 0.0F, 2.0F);
        squid.addChild(RightLimb);
        RightLimb.texOffs(10, 23).addBox(-2.0F, -3.0F, 0.0F, 2.0F, 3.0F, 3.0F, 0.0F, true);
        RightLimb.texOffs(14, 29).addBox(-2.0F, -3.0F, 3.0F, 3.0F, 3.0F, 4.0F, 0.0F, true);
    }

    @Override
    public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {

    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        squid.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }

    @Override
    public void prepareMobModel(LivingEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime)
    {
        super.prepareMobModel(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTickTime);
        boolean isSwimming = entitylivingbaseIn.isSwimming();

        if (!entitylivingbaseIn.isPassenger())
        {
            float angle = isSwimming ? (float) -(entitylivingbaseIn.xRot * Math.PI / 180F) : (float) (entitylivingbaseIn.getY() - entitylivingbaseIn.yo) * 1.1f;
            squid.xRot = (float) -Math.min(Math.PI / 2, Math.max(-Math.PI / 2, angle));
        }

        if (entitylivingbaseIn.isOnGround() || isSwimming)
        {
            this.RightLimb.yRot = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / (isSwimming ? 2.2f : 1.5f);
            this.LeftLimb.yRot = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount / (isSwimming ? 2.2f : 1.5f);
        } else
        {
            if (Math.abs(Math.round(RightLimb.yRot * 100)) != 0)
            {
                this.RightLimb.yRot -= RightLimb.yRot / 8f;
            }
            if (Math.abs(Math.round(LeftLimb.yRot * 100)) != 0)
            {
                this.LeftLimb.yRot -= LeftLimb.yRot / 8f;
            }
        }
    }
}
