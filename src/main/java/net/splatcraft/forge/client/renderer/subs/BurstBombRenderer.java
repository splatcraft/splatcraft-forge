package net.splatcraft.forge.client.renderer.subs;

import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.client.model.subs.BurstBombModel;
import net.splatcraft.forge.entities.subs.BurstBombEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class BurstBombRenderer extends SubWeaponRenderer<BurstBombEntity, BurstBombModel>
{
    private static final BurstBombModel MODEL = new BurstBombModel();
    private static final ResourceLocation TEXTURE = new ResourceLocation(Splatcraft.MODID, "textures/weapons/sub/burst_bomb.png");
    private static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation(Splatcraft.MODID, "textures/weapons/sub/burst_bomb_ink.png");

    public BurstBombRenderer(EntityRendererManager manager)
    {
        super(manager);
    }

    @Override
    public void render(BurstBombEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {

        matrixStackIn.pushPose();
        if(!entityIn.isItem)
        {
            matrixStackIn.translate(0.0D, 0.2/*0.15000000596046448D*/, 0.0D);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.yRotO, entityIn.yRot) - 180.0F));
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.xRotO, entityIn.xRot)+90F));
            matrixStackIn.scale(1, -1, 1);
        }
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(BurstBombEntity entity)
    {
        return TEXTURE;
    }

    @Override
    public BurstBombModel getModel()
    {
        return MODEL;
    }

    @Override
    public ResourceLocation getInkTextureLocation(BurstBombEntity entity)
    {
        return OVERLAY_TEXTURE;
    }

}
