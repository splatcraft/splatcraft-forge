package com.cibernet.splatcraft.client.renderer;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.client.layer.InkSquidColorLayer;
import com.cibernet.splatcraft.client.model.InkSquidModel;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

public class InkSquidRenderer extends LivingRenderer<LivingEntity, InkSquidModel> //implements IEntityRenderer<LivingEntity, InkSquidModel>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Splatcraft.MODID, "textures/entity/ink_squid_overlay.png");

    public InkSquidRenderer(EntityRendererManager manager)
    {
        super(manager, new InkSquidModel(), 0.5f);
        addLayer(new InkSquidColorLayer(this));
    }

    @Override
    protected boolean shouldShowName(LivingEntity entity)
    {
        return super.shouldShowName(entity) && (entity.shouldShowName() || entity.hasCustomName() && entity == this.entityRenderDispatcher.crosshairPickEntity);
    }

    @Override
    public ResourceLocation getTextureLocation(LivingEntity entity)
    {
        return TEXTURE;
    }
}
