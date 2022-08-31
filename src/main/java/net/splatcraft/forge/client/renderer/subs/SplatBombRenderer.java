package net.splatcraft.forge.client.renderer.subs;

import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.client.model.subs.SplatBombModel;
import net.splatcraft.forge.entities.subs.SplatBombEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class SplatBombRenderer extends SubWeaponRenderer<SplatBombEntity, SplatBombModel>
{
    private static final SplatBombModel MODEL = new SplatBombModel();
    private static final ResourceLocation TEXTURE = new ResourceLocation(Splatcraft.MODID, "textures/weapons/sub/splat_bomb.png");
    private static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation(Splatcraft.MODID, "textures/weapons/sub/splat_bomb_ink.png");

    public SplatBombRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Override
    public void render(SplatBombEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {

        matrixStackIn.pushPose();

        if(!entityIn.isItem)
        {
            matrixStackIn.translate(0.0D, 0.2, 0.0D);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.yRotO, entityIn.yRot)*2 - 90f));
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.xRotO, entityIn.xRot)*2 - 180));

            float f = entityIn.getFlashIntensity(partialTicks);
            float f1 = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.01F;
            f = MathHelper.clamp(f, 0.0F, 1.0F);
            f = f * f;
            f = f * f;
            float f2 = (1.0F + f * 0.4F) * f1;
            float f3 = (1.0F + f * 0.1F) / f1;
            matrixStackIn.scale(f2, f3, f2);
        }

        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.popPose();
    }

    @Override
    protected float getOverlayProgress(SplatBombEntity livingEntityIn, float partialTicks) {
        float f = livingEntityIn.getFlashIntensity(partialTicks);
        return (int)(f * 10.0F) % 2 == 0 ? 0.0F : MathHelper.clamp(f, 0.5F, 1.0F);
    }

    @Override
    public SplatBombModel getModel() {
        return MODEL;
    }

    @Override
    public ResourceLocation getOverlayTexture(SplatBombEntity entity) {
        return OVERLAY_TEXTURE;
    }

    @Override
    public ResourceLocation getTextureLocation(SplatBombEntity entity) {
        return TEXTURE;
    }
}
