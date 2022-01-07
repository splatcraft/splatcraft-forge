package com.cibernet.splatcraft.client.renderer.subs;

import com.cibernet.splatcraft.SplatcraftConfig;
import com.cibernet.splatcraft.client.model.subs.AbstractSubWeaponModel;
import com.cibernet.splatcraft.entities.subs.AbstractSubWeaponEntity;
import com.cibernet.splatcraft.util.ColorUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;

public abstract class SubWeaponRenderer<E extends AbstractSubWeaponEntity, M extends AbstractSubWeaponModel<E>> extends EntityRenderer<E>
{
    protected SubWeaponRenderer(EntityRendererManager manager)
    {
        super(manager);
    }

    @Override
    public void render(E entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn)
    {
        int color = entityIn.getColor();
        if (SplatcraftConfig.Client.getColorLock())
            color = ColorUtils.getLockedColor(color);

        float r = (float) (Math.floor((float) color / (256 * 256)) / 255f);
        float g = (float) (Math.floor((float) color / 256) % 256 / 255f);
        float b = (color % 256) / 255f;

        M model = getModel();
        model.setupAnim(entityIn, 0, 0, this.handleRotationFloat(entityIn, partialTicks), entityYaw, entityIn.xRot);
        model.prepareMobModel(entityIn, 0, 0, partialTicks);
        int i = OverlayTexture.pack(OverlayTexture.u(getOverlayProgress(entityIn, partialTicks)), OverlayTexture.v(false));
        model.renderToBuffer(matrixStackIn, bufferIn.getBuffer(model.renderType(getOverlayTexture(entityIn))), packedLightIn, i, r, g, b, 1);
        model.renderToBuffer(matrixStackIn, bufferIn.getBuffer(model.renderType(getTextureLocation(entityIn))), packedLightIn, i, 1, 1, 1, 1);
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    protected float getOverlayProgress(E entity, float partialTicks)
    {
        return 0;
    }


    public abstract M getModel();
    public abstract ResourceLocation getOverlayTexture(E entity);

    protected float handleRotationFloat(E livingBase, float partialTicks)
    {
        return (float) livingBase.tickCount + partialTicks;
    }
}
