package net.splatcraft.forge.client.renderer.subs;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.client.model.subs.BurstBombModel;
import net.splatcraft.forge.client.model.subs.CurlingBombModel;
import net.splatcraft.forge.entities.subs.BurstBombEntity;
import net.splatcraft.forge.entities.subs.CurlingBombEntity;

public class CurlingBombRenderer extends SubWeaponRenderer<CurlingBombEntity, CurlingBombModel>
{
    private static final CurlingBombModel MODEL = new CurlingBombModel();
    private static final ResourceLocation TEXTURE = new ResourceLocation(Splatcraft.MODID, "textures/weapons/sub/curling_bomb.png");
    private static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation(Splatcraft.MODID, "textures/weapons/sub/curling_bomb_ink.png");

    public CurlingBombRenderer(EntityRendererManager manager)
    {
        super(manager);
    }

    @Override
    public void render(CurlingBombEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {

        matrixStackIn.pushPose();

        if(!entityIn.isItem)
        {
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.yRotO, entityIn.yRot) - 180.0F));
            //matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.xRotO, entityIn.xRot)+90F));
            matrixStackIn.scale(1, -1, 1);
        }


        matrixStackIn.translate(0, -1.5, 0);
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(CurlingBombEntity entity)
    {
        return TEXTURE;
    }

    @Override
    public CurlingBombModel getModel()
    {
        return MODEL;
    }

    @Override
    public ResourceLocation getOverlayTexture(CurlingBombEntity entity)
    {
        return OVERLAY_TEXTURE;
    }

}
