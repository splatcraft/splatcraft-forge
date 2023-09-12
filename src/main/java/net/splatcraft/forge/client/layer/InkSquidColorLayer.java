package net.splatcraft.forge.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.SplatcraftConfig;
import net.splatcraft.forge.client.models.InkSquidModel;
import net.splatcraft.forge.util.ColorUtils;

public class InkSquidColorLayer extends RenderLayer<LivingEntity, InkSquidModel>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Splatcraft.MODID, "textures/entity/ink_squid.png");
    private final InkSquidModel model;

    public InkSquidColorLayer(RenderLayerParent<LivingEntity, InkSquidModel> renderer, EntityModelSet modelSet)
    {
        super(renderer);
        model = new InkSquidModel(modelSet.bakeLayer(InkSquidModel.LAYER_LOCATION));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTickTime, float ageInTicks, float netHeadYaw, float headPitch)
    {
        int color = ColorUtils.getEntityColor(entity);
        if (SplatcraftConfig.Client.getColorLock())
        {
            color = ColorUtils.getLockedColor(color);
        }
        float r = ((color & 16711680) >> 16) / 255.0f;
        float g = ((color & '\uff00') >> 8) / 255.0f;
        float b = (color & 255) / 255.0f;

        coloredCutoutModelCopyLayerRender(getParentModel(), model, TEXTURE, poseStack, bufferSource, packedLight, entity, limbSwing, limbSwingAmount, packedLight, ageInTicks, netHeadYaw, headPitch, r, g, b);
    }

    protected static <T extends LivingEntity> void coloredCutoutModelCopyLayerRender(EntityModel<T> parentModel, EntityModel<T> model, ResourceLocation textureLoc, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTickTime, float ageInTicks, float netHeadYaw, float headPitch, float red, float green, float blue) {
        if (!entity.isInvisible()) {
            parentModel.copyPropertiesTo(model);
            model.prepareMobModel(entity, limbSwing, limbSwingAmount, headPitch);
            model.setupAnim(entity, limbSwing, limbSwingAmount, partialTickTime, ageInTicks, netHeadYaw);
            renderColoredCutoutModel(model, textureLoc, poseStack, bufferSource, packedLight, entity, red, green, blue);
        }

    }
}
