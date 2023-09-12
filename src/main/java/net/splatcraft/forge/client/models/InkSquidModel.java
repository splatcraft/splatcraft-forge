package net.splatcraft.forge.client.models;// Made with Blockbench 4.7.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.splatcraft.forge.data.capabilities.inkoverlay.InkOverlayCapability;
import net.splatcraft.forge.data.capabilities.inkoverlay.InkOverlayInfo;

public class InkSquidModel extends EntityModel<LivingEntity> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "inksquidmodel"), "main");
	private final ModelPart squid;
	private final ModelPart rightLimb;
	private final ModelPart leftLimb;

	public InkSquidModel(ModelPart root) {
		this.squid = root.getChild("squid");
		this.rightLimb = squid.getChild("RightLimb");
		this.leftLimb = squid.getChild("LeftLimb");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition squid = partdefinition.addOrReplaceChild("squid", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition Body = squid.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 9).addBox(-6.0F, -5.0F, -6.0F, 12.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(27, 0).addBox(-5.0F, -4.0F, -8.0F, 10.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(32, 6).addBox(-4.0F, -3.0F, -10.0F, 8.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(32, 12).addBox(-2.0F, -2.0F, -12.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition eyes = Body.addOrReplaceChild("eyes", CubeListBuilder.create().texOffs(18, 19).addBox(-2.5F, -5.0F, -2.0F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 19).addBox(-3.0F, -4.5F, -2.25F, 6.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition tentacles = Body.addOrReplaceChild("tentacles", CubeListBuilder.create().texOffs(56, 0).addBox(-2.6593F, -3.75F, 6.6593F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(56, 0).addBox(-1.495F, -3.75F, 5.495F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(56, 0).addBox(-0.1161F, -2.25F, 4.1161F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(56, 0).addBox(-1.495F, -2.25F, 5.495F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(56, 0).addBox(-0.1161F, -3.75F, 4.1161F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(56, 0).addBox(0.9875F, -3.75F, 2.9671F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 0.0F, -2.25F, 0.0F, -0.7854F, 0.0F));

		PartDefinition LeftLimb = squid.addOrReplaceChild("LeftLimb", CubeListBuilder.create().texOffs(0, 23).addBox(0.0F, -3.0F, 0.0F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 29).addBox(-1.0F, -3.0F, 3.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 0.0F, 2.0F));

		PartDefinition RightLimb = squid.addOrReplaceChild("RightLimb", CubeListBuilder.create().texOffs(10, 23).mirror().addBox(-2.0F, -3.0F, 0.0F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(14, 29).mirror().addBox(-2.0F, -3.0F, 3.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-2.0F, 0.0F, 2.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
	{

	}

	@Override
	public void prepareMobModel(LivingEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime)
	{
		super.prepareMobModel(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTickTime);
		boolean isSwimming = entitylivingbaseIn.isSwimming();

		if (!entitylivingbaseIn.isPassenger())
		{
			InkOverlayInfo info = InkOverlayCapability.get(entitylivingbaseIn);

			double angle = isSwimming ? -(entitylivingbaseIn.getXRot() * Math.PI / 180F) : Mth.lerp(partialTickTime, info.getSquidRotO(), info.getSquidRot()) * 1.1f;
			squid.xRot = (float) -Math.min(Math.PI / 2, Math.max(-Math.PI / 2, angle));
		}

		if (entitylivingbaseIn.isOnGround() || isSwimming)
		{
			this.rightLimb.yRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / (isSwimming ? 2.2f : 1.5f);
			this.leftLimb.yRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount / (isSwimming ? 2.2f : 1.5f);
		} else
		{
			if (Math.abs(Math.round(rightLimb.yRot * 100)) != 0)
			{
				this.rightLimb.yRot -= rightLimb.yRot / 8f;
			}
			if (Math.abs(Math.round(leftLimb.yRot * 100)) != 0)
			{
				this.leftLimb.yRot -= leftLimb.yRot / 8f;
			}
		}
	}
	
	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		squid.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}