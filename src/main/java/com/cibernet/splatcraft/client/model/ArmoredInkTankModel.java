package com.cibernet.splatcraft.client.model;// Made with Blockbench 3.5.4
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


import net.minecraft.client.renderer.model.ModelRenderer;

import java.util.ArrayList;

public class ArmoredInkTankModel extends AbstractInkTankModel {
    private final ModelRenderer Torso;
    private final ModelRenderer Ink_Tank;
    private final ModelRenderer Left_Arm;
    private final ModelRenderer Right_Arm;

    public ArmoredInkTankModel() {
        textureWidth = 128;
        textureHeight = 128;

        Torso = new ModelRenderer(this);
        Torso.setRotationPoint(0.0F, -0.25F, 0.0F);
        Torso.setTextureOffset(16, 0).addBox(-4, 0, -2, 8, 12, 4, 1);


        Ink_Tank = new ModelRenderer(this);
        Ink_Tank.setRotationPoint(0.0F, -2.25F, -1.225F);
        Torso.addChild(Ink_Tank);
        Ink_Tank.setTextureOffset(0, 19).addBox(-2.0F, 3.25F, 3.25F, 4.0F, 1.0F, 4.0F, 0.0F, false);
        Ink_Tank.setTextureOffset(16, 19).addBox(-2.0F, 10.25F, 3.25F, 4.0F, 1.0F, 4.0F, 0.0F, false);
        Ink_Tank.setTextureOffset(0, 24).addBox(1.0F, 4.25F, 3.25F, 1.0F, 6.0F, 1.0F, 0.0F, false);
        Ink_Tank.setTextureOffset(6, 24).addBox(1.0F, 4.25F, 6.25F, 1.0F, 6.0F, 1.0F, 0.0F, false);
        Ink_Tank.setTextureOffset(10, 24).addBox(-2.0F, 4.25F, 6.25F, 1.0F, 6.0F, 1.0F, 0.0F, false);
        Ink_Tank.setTextureOffset(14, 24).addBox(-2.0F, 4.25F, 3.25F, 1.0F, 6.0F, 1.0F, 0.0F, false);

        Left_Arm = new ModelRenderer(this);
        Left_Arm.setRotationPoint(0.0F, 0.0F, 0.0F);
        Left_Arm.setTextureOffset(40, 0).addBox(-1, -2, -2, 4, 12, 4, 1);


        Right_Arm = new ModelRenderer(this);
        Right_Arm.setRotationPoint(0.0F, 0.0F, 0.0F);
        Right_Arm.setTextureOffset(40, 0).addBox(-3, -2, -2, 4, 12, 4, 1, true);

        bipedBody = new ModelRenderer(this);
        bipedBody.addChild(Torso);
        bipedLeftArm.addChild(Left_Arm);
        bipedRightArm.addChild(Right_Arm);

        inkPieces = new ArrayList<>();
        inkBarY = 23.25f;

        for (int i = 0; i < 6; i++) {
            ModelRenderer ink = new ModelRenderer(this);
            ink.setRotationPoint(0.0F, inkBarY, -0.75F);
            Ink_Tank.addChild(ink);

            ink.setTextureOffset(115, 0).addBox(-1.5F, -13.0F, 4.5F, 3, 1, 3, 0.0F);

            inkPieces.add(ink);
        }

    }
}
