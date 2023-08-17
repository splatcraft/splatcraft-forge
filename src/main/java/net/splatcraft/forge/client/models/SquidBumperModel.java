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
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.entities.SquidBumperEntity;

public class SquidBumperModel extends EntityModel<SquidBumperEntity> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Splatcraft.MODID, "squid_bumper"), "main");
	private final ModelPart Base;
	private final ModelPart Bumper;

	public SquidBumperModel(ModelPart root) {
		this.Base = root.getChild("Base");
		this.Bumper = root.getChild("Bumper");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Base = partdefinition.addOrReplaceChild("Base", CubeListBuilder.create().texOffs(0, 46).addBox(-5.0F, -2.0F, -5.0F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition Bumper = partdefinition.addOrReplaceChild("Bumper", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -16.0F, -7.0F, 14.0F, 14.0F, 14.0F, new CubeDeformation(0.0F))
		.texOffs(0, 28).addBox(-6.0F, -22.0F, -6.0F, 12.0F, 6.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(56, 1).addBox(-5.0F, -27.0F, -5.0F, 10.0F, 5.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(56, 17).addBox(-4.0F, -30.0F, -4.0F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition Left_Side = Bumper.addOrReplaceChild("Left_Side", CubeListBuilder.create().texOffs(72, 28).addBox(-11.3308F, -12.0465F, -1.5F, 10.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.3308F, -12.7034F, 0.5F, 0.0F, 0.0F, 0.7854F));

		PartDefinition Right_Side = Bumper.addOrReplaceChild("Right_Side", CubeListBuilder.create().texOffs(48, 28).mirror().addBox(1.3261F, -12.0465F, -1.5F, 10.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-3.3308F, -12.7034F, 0.5F, 0.0F, 0.0F, -0.7854F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(SquidBumperEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		Base.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		Bumper.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}