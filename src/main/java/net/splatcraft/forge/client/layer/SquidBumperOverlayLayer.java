package net.splatcraft.forge.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.client.models.SquidBumperModel;
import net.splatcraft.forge.entities.SquidBumperEntity;

public class SquidBumperOverlayLayer extends RenderLayer<SquidBumperEntity, SquidBumperModel>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(Splatcraft.MODID, "textures/entity/squid_bumper.png");
	private final SquidBumperModel model;

	public SquidBumperOverlayLayer(RenderLayerParent<SquidBumperEntity, SquidBumperModel> renderer, EntityModelSet modelSet)
	{
		super(renderer);
		model = new SquidBumperModel(modelSet.bakeLayer(SquidBumperModel.LAYER_LOCATION));
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, SquidBumperEntity entity, float limbSwing, float limbSwingAmount, float partialTickTime, float ageInTicks, float netHeadYaw, float headPitch)
	{
		getParentModel().copyPropertiesTo(model);
		model.prepareMobModel(entity, limbSwing, limbSwingAmount, headPitch);
		model.setupAnim(entity, limbSwing, limbSwingAmount, partialTickTime, ageInTicks, netHeadYaw);

		VertexConsumer ivertexbuilder = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
		model.renderToBuffer(poseStack, ivertexbuilder, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1, 1, 1, 1.0F);
	}
}
