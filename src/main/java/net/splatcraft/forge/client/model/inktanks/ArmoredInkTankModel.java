package net.splatcraft.forge.client.model.inktanks;// Made with Blockbench 3.5.4
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


import net.minecraft.client.renderer.model.ModelRenderer;

import java.util.ArrayList;

public class ArmoredInkTankModel extends AbstractInkTankModel
{
    private final ModelRenderer Torso;
    private final ModelRenderer Ink_Tank;
    private final ModelRenderer Left_Arm;
    private final ModelRenderer Right_Arm;

    public ArmoredInkTankModel()
    {
        texWidth = 128;
        texHeight = 128;

        Torso = new ModelRenderer(this);
        Torso.setPos(0.0F, -0.25F, 0.0F);
        Torso.texOffs(16, 0).addBox(-4, 0, -2, 8, 12, 4, 1);


        Ink_Tank = new ModelRenderer(this);
        Ink_Tank.setPos(0.0F, -2.25F, -1.225F);
        Torso.addChild(Ink_Tank);
        Ink_Tank.texOffs(0, 19).addBox(-2.0F, 3.25F, 3.25F, 4.0F, 1.0F, 4.0F, 0.0F, false);
        Ink_Tank.texOffs(16, 19).addBox(-2.0F, 10.25F, 3.25F, 4.0F, 1.0F, 4.0F, 0.0F, false);
        Ink_Tank.texOffs(0, 24).addBox(1.0F, 4.25F, 3.25F, 1.0F, 6.0F, 1.0F, 0.0F, false);
        Ink_Tank.texOffs(6, 24).addBox(1.0F, 4.25F, 6.25F, 1.0F, 6.0F, 1.0F, 0.0F, false);
        Ink_Tank.texOffs(10, 24).addBox(-2.0F, 4.25F, 6.25F, 1.0F, 6.0F, 1.0F, 0.0F, false);
        Ink_Tank.texOffs(14, 24).addBox(-2.0F, 4.25F, 3.25F, 1.0F, 6.0F, 1.0F, 0.0F, false);

        Left_Arm = new ModelRenderer(this);
        Left_Arm.setPos(0.0F, 0.0F, 0.0F);
        Left_Arm.texOffs(40, 0).addBox(-1, -2, -2, 4, 12, 4, 1);


        Right_Arm = new ModelRenderer(this);
        Right_Arm.setPos(0.0F, 0.0F, 0.0F);
        Right_Arm.texOffs(40, 0).addBox(-3, -2, -2, 4, 12, 4, 1, true);

        body = new ModelRenderer(this);
        body.addChild(Torso);
        leftArm.addChild(Left_Arm);
        rightArm.addChild(Right_Arm);

        inkPieces = new ArrayList<>();
        inkBarY = 23.25f;

        for (int i = 0; i < 6; i++)
        {
            ModelRenderer ink = new ModelRenderer(this);
            ink.setPos(0.0F, inkBarY, -0.75F);
            Ink_Tank.addChild(ink);

            ink.texOffs(116, 0).addBox(-1.5F, -13.0F, 4.5F, 3, 1, 3, 0.0F);

            inkPieces.add(ink);
        }

    }
}
