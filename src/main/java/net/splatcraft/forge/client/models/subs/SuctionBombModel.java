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
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.client.models.AbstractSubWeaponModel;
import net.splatcraft.forge.entities.subs.SuctionBombEntity;

public class SuctionBombModel extends AbstractSubWeaponModel<SuctionBombEntity>
{
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Splatcraft.MODID, "suctionbombmodel"), "main");
	private final ModelPart Main;

	public SuctionBombModel(ModelPart root) {
		this.Main = root.getChild("Main");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Main = partdefinition.addOrReplaceChild("Main", CubeListBuilder.create().texOffs(0, 10).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(14, 15).addBox(-1.0F, -4.25F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition Neck = Main.addOrReplaceChild("Neck", CubeListBuilder.create().texOffs(12, 10).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.2F))
		.texOffs(0, 10).addBox(-0.5F, -1.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, -3.75F, 0.0F));

		PartDefinition Top = Neck.addOrReplaceChild("Top", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -7.7F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(12, 0).addBox(-1.5F, -1.7F, -1.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-0.5F, -1.2F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(-0.1F)), PartPose.offset(0.0F, -2.5F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(SuctionBombEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		Main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}