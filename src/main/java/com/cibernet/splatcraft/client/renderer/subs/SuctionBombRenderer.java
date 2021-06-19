package com.cibernet.splatcraft.client.renderer.subs;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.client.model.subs.SuctionBombModel;
import com.cibernet.splatcraft.entities.subs.SuctionBombEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class SuctionBombRenderer extends SubWeaponRenderer<SuctionBombEntity, SuctionBombModel>
{
    private static final SuctionBombModel MODEL = new SuctionBombModel();
    private static final ResourceLocation TEXTURE = new ResourceLocation(Splatcraft.MODID, "textures/weapons/sub/suction_bomb.png");
    private static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation(Splatcraft.MODID, "textures/weapons/sub/suction_bomb_ink.png");

    public SuctionBombRenderer(EntityRendererManager manager)
    {
        super(manager);
    }

    @Override
    public void render(SuctionBombEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {

        matrixStackIn.push();
        //matrixStackIn.translate(0.0D, 0.2/*0.15000000596046448D*/, 0.0D);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.prevRotationYaw, entityIn.rotationYaw) - 180.0F));
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.prevRotationPitch, entityIn.rotationPitch)+90F));
        matrixStackIn.scale(1, -1, 1);

        float f = entityIn.getFlashIntensity(partialTicks);
        float f1 = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.01F;
        f = MathHelper.clamp(f, 0.0F, 1.0F);
        f = f * f;
        f = f * f;
        float f2 = (1.0F + f * 0.4F) * f1;
        float f3 = (1.0F + f * 0.1F) / f1;
        matrixStackIn.scale(f2, f3, f2);

        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.pop();
    }

    protected float getOverlayProgress(SuctionBombEntity livingEntityIn, float partialTicks) {
        float f = livingEntityIn.getFlashIntensity(partialTicks);
        return (int)(f * 10.0F) % 2 == 0 ? 0.0F : MathHelper.clamp(f, 0.5F, 1.0F);
    }


    @Override
    public ResourceLocation getEntityTexture(SuctionBombEntity entity)
    {
        return TEXTURE;
    }

    @Override
    public SuctionBombModel getModel()
    {
        return MODEL;
    }

    @Override
    public ResourceLocation getOverlayTexture(SuctionBombEntity entity)
    {
        return OVERLAY_TEXTURE;
    }

}
