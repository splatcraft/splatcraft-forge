package com.cibernet.splatcraft.client.renderer.subs;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.client.model.subs.BurstBombModel;
import com.cibernet.splatcraft.client.model.subs.SplatBombModel;
import com.cibernet.splatcraft.entities.subs.SplatBombEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class SplatBombRenderer extends SubWeaponRenderer<SplatBombEntity, SplatBombModel>
{
    private static final SplatBombModel MODEL = new SplatBombModel();
    private static final ResourceLocation TEXTURE = new ResourceLocation(Splatcraft.MODID, "textures/weapons/sub/splat_bomb.png");
    private static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation(Splatcraft.MODID, "textures/weapons/sub/splat_bomb_ink.png");

    public SplatBombRenderer(EntityRendererManager manager) {
        super(manager);
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
