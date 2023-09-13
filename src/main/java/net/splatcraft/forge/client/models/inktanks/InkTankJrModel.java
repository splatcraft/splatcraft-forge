package net.splatcraft.forge.client.models.inktanks;// Made with Blockbench 4.8.3
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.splatcraft.forge.Splatcraft;

public class InkTankJrModel extends AbstractInkTankModel {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Splatcraft.MODID, "inktankjrmodel"), "main");

    public InkTankJrModel(ModelPart root) {
        super(root);
        ModelPart Ink_Tank = root.getChild("body").getChild("Torso").getChild("Ink_Tank");

        for (int i = 0; i < 7; i++)
            inkPieces.add(Ink_Tank.getChild("InkPiece_" + i));
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        createEmptyMesh(partdefinition);

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition Torso = body.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 0).addBox(-4.8056F, -0.25F, -2.5F, 9.0F, 12.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(31, 0).addBox(-1.0F, 3.0F, 2.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.25F, 0.0F));

        PartDefinition Ink_Tank = Torso.addOrReplaceChild("Ink_Tank", CubeListBuilder.create().texOffs(20, 18).addBox(-2.0F, 1.5F, 3.75F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(8, 33).addBox(-3.5F, 3.15F, 4.25F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(12, 39).addBox(0.9875F, 5.25F, 7.25F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(12, 39).addBox(0.9875F, 7.25F, 7.25F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(12, 39).addBox(0.9875F, 9.25F, 7.25F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(14, 24).addBox(-3.0F, 4.25F, 3.25F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(10, 24).addBox(-3.0F, 4.25F, 7.25F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(6, 24).addBox(2.0F, 4.25F, 7.25F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 24).addBox(2.0F, 4.25F, 3.25F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(18, 25).addBox(-3.0F, 11.25F, 3.25F, 6.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(0, 18).addBox(-2.5F, 3.25F, 3.25F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(31, 2).addBox(-0.5F, 1.75F, 2.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.75F, 0.75F));

        Torso.addOrReplaceChild("Tag", CubeListBuilder.create(), PartPose.offsetAndRotation(-3.1168F, 2.8445F, 8.9821F, -0.1309F, -0.3927F, -0.3054F));

        for (int i = 0; i < 7; i++) {
            Ink_Tank.addOrReplaceChild("InkPiece_" + i, CubeListBuilder.create().texOffs(110, 0)
                    .addBox(-2.5F, -12.0F, 4.5F, 5, 1, 4, new CubeDeformation(0)), PartPose.offset(0.0F, 23.25f, -0.75F));
        }

        return LayerDefinition.create(meshdefinition, 128, 128);
    }
}