package net.splatcraft.forge.client.models.inktanks;// Made with Blockbench 4.8.3
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.splatcraft.forge.Splatcraft;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;

public class ArmoredInkTankModel extends AbstractInkTankModel {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Splatcraft.MODID, "armoredinktankmodel"), "main");

	public ArmoredInkTankModel(ModelPart root)
	{
		super(root);
		ModelPart Ink_Tank = root.getChild("body").getChild("Torso").getChild("Ink_Tank");

		for(int i = 0; i < 6; i++)
			inkPieces.add(Ink_Tank.getChild("InkPiece_"+i));
	}


	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		createEmptyMesh(partdefinition);

		CubeDeformation deformation = new CubeDeformation(1);
		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, deformation), PartPose.offset(0.0F, 0.0F, 0.0F));
		partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 0).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformation), PartPose.offset(-5.0F, 2.0F, 0.0F));
		partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 0).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformation), PartPose.offset(5.0F, 2.0F, 0.0F));

		PartDefinition Torso = body.addOrReplaceChild("Torso", CubeListBuilder.create(), PartPose.offset(0.0F, -0.25F, 0.0F));

		PartDefinition Ink_Tank = Torso.addOrReplaceChild("Ink_Tank", CubeListBuilder.create().texOffs(0, 19).addBox(-2.0F, 3.25F, 3.25F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(16, 19).addBox(-2.0F, 10.25F, 3.25F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 24).addBox(1.0F, 4.25F, 3.25F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(6, 24).addBox(1.0F, 4.25F, 6.25F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(10, 24).addBox(-2.0F, 4.25F, 6.25F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(14, 24).addBox(-2.0F, 4.25F, 3.25F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.25F, -1.225F));

		for (int i = 0; i < 6; i++)
		{
			Ink_Tank.addOrReplaceChild("InkPiece_"+i, CubeListBuilder.create().texOffs(116, 0)
					.addBox(-1.5F, -13.0F, 4.5F, 3, 1, 3, new CubeDeformation(0f)), PartPose.offset(0.0F, 23.25f, -0.75F));
		}

		return LayerDefinition.create(meshdefinition, 128, 128);
	}
}