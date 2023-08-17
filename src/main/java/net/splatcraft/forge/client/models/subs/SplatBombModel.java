package net.splatcraft.forge.client.models.subs;// Made with Blockbench 4.7.2
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
import net.splatcraft.forge.client.models.AbstractSubWeaponModel;
import net.splatcraft.forge.entities.subs.SplatBombEntity;

public class SplatBombModel extends AbstractSubWeaponModel<SplatBombEntity> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "splatbombmodel"), "main");
	private final ModelPart Main;

	public SplatBombModel(ModelPart root) {
		this.Main = root.getChild("Main");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Main = partdefinition.addOrReplaceChild("Main", CubeListBuilder.create(), PartPose.offset(0.0F, 20.0F, 0.0F));

		PartDefinition bone13 = Main.addOrReplaceChild("bone13", CubeListBuilder.create(), PartPose.offset(0.0F, 3.4F, 0.3F));

		PartDefinition bone = bone13.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(18, 2).addBox(-3.0F, -1.0F, 3.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -1.0F));

		PartDefinition bone2 = bone13.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(18, 0).addBox(0.0F, -0.5F, -1.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -0.5F, 2.0F, 0.0F, 1.0472F, 0.0F));

		PartDefinition bone3 = bone13.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(0, 18).addBox(-6.0F, -0.5F, -1.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -0.5F, 2.0F, 0.0F, -1.0472F, 0.0F));

		PartDefinition bone7 = Main.addOrReplaceChild("bone7", CubeListBuilder.create(), PartPose.offset(-0.25F, 3.65F, -0.7F));

		PartDefinition bone6 = bone7.addOrReplaceChild("bone6", CubeListBuilder.create(), PartPose.offsetAndRotation(3.0F, -0.75F, 3.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition cube_r1 = bone6.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(12, 12).addBox(-6.75F, -0.9F, -0.5F, 7.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.8754F, -0.5F, -2.8148F, 0.0F, 0.0F, 1.0472F));

		PartDefinition bone5 = bone7.addOrReplaceChild("bone5", CubeListBuilder.create(), PartPose.offsetAndRotation(-3.0F, -0.75F, 3.0F, 0.0F, 0.5236F, 0.0F));

		PartDefinition cube_r2 = bone5.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(12, 14).addBox(-0.25F, -0.9F, -0.5F, 7.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3207F, -0.5F, 0.1852F, 0.0F, 0.0F, -1.0472F));

		PartDefinition bone4 = bone7.addOrReplaceChild("bone4", CubeListBuilder.create(), PartPose.offsetAndRotation(3.0F, -0.75F, 3.0F, 0.0F, -0.5236F, 0.0F));

		PartDefinition cube_r3 = bone4.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(12, 16).addBox(-6.75F, -0.9F, -0.5F, 7.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.0F, 0.0F, 0.0F, 1.0472F));

		PartDefinition bone8 = bone7.addOrReplaceChild("bone8", CubeListBuilder.create(), PartPose.offsetAndRotation(-3.0F, -0.75F, 3.0F, 0.0F, 0.5236F, 0.0F));

		PartDefinition cube_r4 = bone8.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(6, 22).addBox(-1.9F, -0.95F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.3207F, -0.5F, 0.1852F, 0.0F, 0.0F, -0.5236F));

		PartDefinition bone9 = bone7.addOrReplaceChild("bone9", CubeListBuilder.create(), PartPose.offsetAndRotation(3.5F, -0.75F, 3.0F, 0.0F, -0.5236F, 0.0F));

		PartDefinition cube_r5 = bone9.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(18, 20).addBox(-0.1433F, -0.925F, -0.9352F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(-0.3207F, -0.5F, 0.1852F, 0.0F, 0.0F, 0.5236F));

		PartDefinition bone10 = bone7.addOrReplaceChild("bone10", CubeListBuilder.create(), PartPose.offsetAndRotation(0.275F, -0.75F, -2.3F, 0.0F, -1.5708F, 0.0F));

		PartDefinition cube_r6 = bone10.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(0, 20).addBox(-1.9084F, -0.95F, -1.1F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.3207F, -0.5F, 0.1852F, 0.0F, 0.0F, -0.5236F));

		PartDefinition bone11 = bone7.addOrReplaceChild("bone11", CubeListBuilder.create().texOffs(12, 18).addBox(-0.825F, -8.2F, 0.35F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition bone17 = Main.addOrReplaceChild("bone17", CubeListBuilder.create(), PartPose.offset(0.0F, 4.0F, 0.0F));

		PartDefinition bone14 = bone17.addOrReplaceChild("bone14", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

		PartDefinition cube_r7 = bone14.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(12, 6).addBox(-3.05F, -6.5981F, -0.434F, 6.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4919F, 0.2349F, -1.1981F, -0.2618F, 0.0F, 0.0F));

		PartDefinition bone15 = bone17.addOrReplaceChild("bone15", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.8F, -1.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

		PartDefinition cube_r8 = bone15.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(0, 12).addBox(-3.0F, -6.4731F, -0.434F, 6.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4919F, 0.2349F, -1.1981F, -0.2618F, 2.0944F, 0.0F));

		PartDefinition bone16 = bone17.addOrReplaceChild("bone16", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.8F, -1.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

		PartDefinition cube_r9 = bone16.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(0, 6).addBox(-4.45F, -5.7981F, -2.884F, 6.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4919F, 0.2349F, -1.1981F, -0.2618F, -2.0944F, 0.0F));

		PartDefinition bone18 = bone17.addOrReplaceChild("bone18", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -0.1F, -3.4F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.7F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(SplatBombEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		Main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}