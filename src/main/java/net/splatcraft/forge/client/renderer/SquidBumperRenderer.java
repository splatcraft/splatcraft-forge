package net.splatcraft.forge.client.renderer;

import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.client.layer.SquidBumperColorLayer;
import net.splatcraft.forge.client.layer.SquidBumperOverlayLayer;
import net.splatcraft.forge.client.model.SquidBumperModel;
import net.splatcraft.forge.entities.SquidBumperEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;

public class SquidBumperRenderer extends LivingRenderer<SquidBumperEntity, SquidBumperModel> //implements IEntityRenderer<LivingEntity, InkSquidModel>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Splatcraft.MODID, "textures/entity/squid_bumper_overlay.png");

    public SquidBumperRenderer(EntityRendererManager manager)
    {
        super(manager, new SquidBumperModel(), 0.5f);
        addLayer(new SquidBumperColorLayer(this));
        addLayer(new SquidBumperOverlayLayer(this));
    }

    @Override
    protected boolean shouldShowName(SquidBumperEntity entity)
    {
        return !entity.hasCustomName() && !(entity.getInkHealth() >= 20) || super.shouldShowName(entity) && (entity.shouldShowName() || entity == this.entityRenderDispatcher.crosshairPickEntity);
    }

    @Override
    protected void renderNameTag(SquidBumperEntity entityIn, ITextComponent displayNameIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn)
    {
        if (entityIn.hasCustomName())
        {
            super.renderNameTag(entityIn, displayNameIn, matrixStackIn, bufferIn, packedLightIn);
        } else
        {
            float health = 20 - entityIn.getInkHealth();
            super.renderNameTag(entityIn, new StringTextComponent((health >= 20 ? TextFormatting.DARK_RED : "") + String.format("%.1f", health)), matrixStackIn, bufferIn, packedLightIn);

        }
    }

    @Override
    protected void setupRotations(SquidBumperEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks)
    {
        //matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F - rotationYaw));
        float punchTime = (float) (entityLiving.level.getGameTime() - entityLiving.punchCooldown) + partialTicks;
        float hurtTime = (float) (entityLiving.level.getGameTime() - entityLiving.hurtCooldown) + partialTicks;


        if (punchTime < 5.0F)
        {
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(MathHelper.sin(punchTime / 1.5F * (float) Math.PI) * 3.0F));
        }
        if (hurtTime < 5.0F)
        {
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.sin(hurtTime / 1.5F * (float) Math.PI) * 3.0F));
        }

    }

    @Override
    public ResourceLocation getTextureLocation(SquidBumperEntity entity)
    {
        return TEXTURE;
    }
}
