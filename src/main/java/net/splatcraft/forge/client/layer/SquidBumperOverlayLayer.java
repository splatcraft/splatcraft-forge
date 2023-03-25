package net.splatcraft.forge.client.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.SplatcraftConfig;
import net.splatcraft.forge.client.model.SquidBumperModel;
import net.splatcraft.forge.entities.SquidBumperEntity;
import net.splatcraft.forge.util.ColorUtils;

public class SquidBumperOverlayLayer extends LayerRenderer<SquidBumperEntity, SquidBumperModel>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Splatcraft.MODID, "textures/entity/squid_bumper_overlay.png");
    private final SquidBumperModel MODEL = new SquidBumperModel();

    public SquidBumperOverlayLayer(IEntityRenderer<SquidBumperEntity, SquidBumperModel> renderer)
    {
        super(renderer);
    }

    protected static <T extends SquidBumperEntity> void renderCopyCutoutModel(SquidBumperModel modelParentIn, SquidBumperModel modelIn, ResourceLocation textureLocationIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float partialTicks, float red, float green, float blue)
    {
        if (!entityIn.isInvisible())
        {
            modelParentIn.copyPropertiesTo(modelIn);
            modelIn.prepareMobModel(entityIn, limbSwing, limbSwingAmount, partialTicks);
            modelIn.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            renderCutoutModel(modelIn, textureLocationIn, matrixStackIn, bufferIn, packedLightIn, entityIn, red, green, blue);
        }

    }

    protected static <T extends LivingEntity> void renderCutoutModel(SquidBumperModel modelIn, ResourceLocation textureLocationIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, SquidBumperEntity entityIn, float red, float green, float blue)
    {
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(textureLocationIn));
        modelIn.renderBase(matrixStackIn, ivertexbuilder, packedLightIn, LivingRenderer.getOverlayCoords(entityIn, 0.0F), red, green, blue, 1.0F);

        float scale = entityIn.getBumperScale(Minecraft.getInstance().getDeltaFrameTime());

        matrixStackIn.pushPose();
        matrixStackIn.scale(scale, scale, scale);
        modelIn.renderBumper(matrixStackIn, ivertexbuilder, packedLightIn, LivingRenderer.getOverlayCoords(entityIn, 0.0F), red, green, blue, 1.0F);
        matrixStackIn.popPose();
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int packedLightIn, SquidBumperEntity entity, float v, float v1, float v2, float v3, float v4, float v5)
    {
        renderCopyCutoutModel(getParentModel(), MODEL, TEXTURE, matrixStack, bufferIn, packedLightIn, entity, v, v1, v2, v3, v4, v5, 1, 1, 1);
    }

}
