package net.splatcraft.forge.client.models.subs;// Made with Blockbench 4.7.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.splatcraft.forge.client.models.AbstractSubWeaponModel;
import net.splatcraft.forge.entities.subs.CurlingBombEntity;

public class CurlingBombModel extends AbstractSubWeaponModel<CurlingBombEntity> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "curlingbombmodel"), "main");
	private final ModelPart blades;
	private final ModelPart bumper1;
	private final ModelPart bumper2;
	private final ModelPart bumper3;
	private final ModelPart bumper4;
	private final ModelPart top;
	private final ModelPart bb_main;

	public CurlingBombModel(ModelPart root) {
		this.blades = root.getChild("blades");
		this.bumper1 = root.getChild("bumper1");
		this.bumper2 = root.getChild("bumper2");
		this.bumper3 = root.getChild("bumper3");
		this.bumper4 = root.getChild("bumper4");
		this.top = root.getChild("top");
		this.bb_main = root.getChild("bb_main");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition blades = partdefinition.addOrReplaceChild("blades", CubeListBuilder.create().texOffs(26, 27).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.05F, 0.0F));

		PartDefinition bone3 = blades.addOrReplaceChild("bone3", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = bone3.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(2, 0).addBox(-0.5F, 0.0F, -0.3F, 1.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.3F, 1.0F, 0.0F, 0.0F, 0.4363F));

		PartDefinition bone2 = blades.addOrReplaceChild("bone2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 2.0944F, 0.0F));

		PartDefinition cube_r2 = bone2.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(2, 0).addBox(-0.5F, 0.0F, -0.3F, 1.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.3F, 1.0F, 0.0F, 0.0F, 0.4363F));

		PartDefinition bone4 = blades.addOrReplaceChild("bone4", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -2.0944F, 0.0F));

		PartDefinition cube_r3 = bone4.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(2, 0).addBox(-0.5F, 0.0F, -0.3F, 1.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.3F, 1.0F, 0.0F, 0.0F, 0.4363F));

		PartDefinition bumper1 = partdefinition.addOrReplaceChild("bumper1", CubeListBuilder.create().texOffs(24, 0).addBox(-3.5F, -4.5F, -5.0F, 7.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 13).addBox(-0.5F, -3.5F, -4.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition bumper2 = partdefinition.addOrReplaceChild("bumper2", CubeListBuilder.create().texOffs(24, 0).addBox(-3.5F, -4.5F, -5.0F, 7.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 13).addBox(-0.5F, -3.5F, -4.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition bumper3 = partdefinition.addOrReplaceChild("bumper3", CubeListBuilder.create().texOffs(24, 0).addBox(-3.5F, -4.5F, -5.0F, 7.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 13).addBox(-0.5F, -3.5F, -4.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition bumper4 = partdefinition.addOrReplaceChild("bumper4", CubeListBuilder.create().texOffs(24, 0).addBox(-3.5F, -4.5F, -5.0F, 7.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 13).addBox(-0.5F, -3.5F, -4.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition top = partdefinition.addOrReplaceChild("top", CubeListBuilder.create().texOffs(0, 9).addBox(-3.5F, -0.6F, -3.5F, 7.0F, 3.0F, 7.0F, new CubeDeformation(-0.05F)), PartPose.offset(0.0F, 20.0F, 0.0F));

		PartDefinition handle = top.addOrReplaceChild("handle", CubeListBuilder.create().texOffs(0, 19).addBox(-3.5346F, 0.2775F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(21, 13).addBox(-3.5346F, -0.6225F, -1.0F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.001F)), PartPose.offset(2.5346F, -2.6775F, 0.0F));

		PartDefinition cube_r4 = handle.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(22, 29).addBox(-1.2F, -0.2F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(21, 23).addBox(-1.5F, -0.2F, -1.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3847F));

		PartDefinition cube_r5 = handle.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(6, 29).addBox(-0.925F, -2.25F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.001F)), PartPose.offsetAndRotation(-2.8783F, 2.1816F, 0.0F, 0.0F, 0.0F, 0.2182F));

		PartDefinition cube_r6 = handle.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(0, 9).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.3706F, 2.0834F, 0.0F, 0.0F, 0.0F, -0.6109F));

		PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -1.0F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(0, 19).addBox(-3.5F, -4.5F, -3.5F, 7.0F, 3.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(CurlingBombEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
	{
	}

	@Override
	public void prepareMobModel(CurlingBombEntity entityIn, float limbSwing, float limbSwingAmount, float partialTick)
	{
		super.prepareMobModel(entityIn, limbSwing, limbSwingAmount, partialTick);

		blades.yRot = Mth.lerp(partialTick, entityIn.prevBladeRot, entityIn.bladeRot);

		top.y = 20 - Mth.clamp(30-Mth.lerp(partialTick, entityIn.prevFuseTime, entityIn.fuseTime), 0, .95f) * 3f;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		blades.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		bumper1.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		bumper2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		bumper3.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		bumper4.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		top.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}