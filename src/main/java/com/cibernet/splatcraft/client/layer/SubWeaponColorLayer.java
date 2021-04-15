package com.cibernet.splatcraft.client.layer;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.SplatcraftConfig;
import com.cibernet.splatcraft.client.model.InkSquidModel;
import com.cibernet.splatcraft.client.model.subs.AbstractSubWeaponModel;
import com.cibernet.splatcraft.entities.subs.AbstractSubWeaponEntity;
import com.cibernet.splatcraft.util.ColorUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

public class SubWeaponColorLayer< M extends AbstractSubWeaponModel<AbstractSubWeaponEntity>> extends LayerRenderer<AbstractSubWeaponEntity, M>
{
    private final ResourceLocation TEXTURE;
    private final M MODEL;

    public SubWeaponColorLayer(IEntityRenderer<AbstractSubWeaponEntity, M> renderer, String textureName, M model)
    {
        super(renderer);
        TEXTURE = new ResourceLocation(Splatcraft.MODID, "textures/entity/"+textureName+".png");
        MODEL = model;
    }


    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, AbstractSubWeaponEntity entity, float v, float v1, float v2, float v3, float v4, float v5)
    {
        int color = ColorUtils.getEntityColor(entity);
        if (SplatcraftConfig.Client.getColorLock())
        {
            color = ColorUtils.getLockedColor(color);
        }
        float r = ((color & 16711680) >> 16) / 255.0f;
        float g = ((color & '\uff00') >> 8) / 255.0f;
        float b = (color & 255) / 255.0f;

        IVertexBuilder builder = iRenderTypeBuffer.getBuffer(RenderType.getEntityCutoutNoCull(TEXTURE));
        MODEL.render(matrixStack, builder, i, OverlayTexture.getPackedUV(OverlayTexture.getU(1.0f), OverlayTexture.getV(false)), r, g, b, 1.0F);
    }
}
