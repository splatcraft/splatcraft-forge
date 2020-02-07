package com.cibernet.splatcraft.entities.renderers;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.entities.classes.EntityInkProjectile;
import com.cibernet.splatcraft.entities.models.ModelInkProjectile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLlamaSpit;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerSheepWool;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderInkProjectile extends Render<EntityInkProjectile> {

    ModelInkProjectile model = new ModelInkProjectile();
    final ResourceLocation TEXTURE = new ResourceLocation(SplatCraft.MODID, "textures/entity/ink.png");

    public RenderInkProjectile(RenderManager renderManager)
    {
        super(renderManager);
    }

    @Override
    public void doRender(EntityInkProjectile entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        if(Minecraft.getMinecraft().gameSettings.particleSetting < 2)
            return;

        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float)y + 0.15F, (float)z);
        GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);
        this.bindTexture(TEXTURE);

        if (this.renderOutlines)
        {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        int color = entity.getColor();
        float r = (float) (Math.floor(color / (256*256))/255f);
        float g = (float) ((Math.floor(color / 256) % 256)/255f);
        float b = (color % 256)/255f;

        GlStateManager.color(r,g,b);
        this.model.render(entity, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F, entity.getProjectileSize()*0.25f);

        if (this.renderOutlines)
        {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityInkProjectile entity) {
        return TEXTURE;
    }
}
