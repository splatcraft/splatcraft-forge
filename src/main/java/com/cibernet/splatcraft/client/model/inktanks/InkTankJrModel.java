package com.cibernet.splatcraft.client.model.inktanks;// Made with Blockbench 3.5.4
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


import net.minecraft.client.renderer.model.ModelRenderer;

import java.util.ArrayList;

public class InkTankJrModel extends AbstractInkTankModel
{
    private final ModelRenderer Torso;
    private final ModelRenderer Ink_Tank;
    private final ModelRenderer Tag;

    public InkTankJrModel()
    {
        texWidth = 128;
        texHeight = 128;

        Torso = new ModelRenderer(this);
        Torso.setPos(0.0F, -0.25F, 0.0F);
        Torso.texOffs(0, 0).addBox(-4.8056F, -0.25F, -2.5F, 9.0F, 12.0F, 5.0F, 0.0F, false);
        Torso.texOffs(31, 0).addBox(-1.0F, 3.0F, 2.5F, 2.0F, 1.0F, 1.0F, 0.0F, false);

        Ink_Tank = new ModelRenderer(this);
        Ink_Tank.setPos(0.0F, 0.75F, 0.75F);
        Torso.addChild(Ink_Tank);
        Ink_Tank.texOffs(20, 18).addBox(-2.0F, 1.5F, 3.75F, 4.0F, 2.0F, 4.0F, 0.0F, false);
        Ink_Tank.texOffs(8, 33).addBox(-3.5F, 3.15F, 4.25F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        Ink_Tank.texOffs(12, 39).addBox(0.9875F, 5.25F, 7.25F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        Ink_Tank.texOffs(12, 39).addBox(0.9875F, 7.25F, 7.25F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        Ink_Tank.texOffs(12, 39).addBox(0.9875F, 9.25F, 7.25F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        Ink_Tank.texOffs(14, 24).addBox(-3.0F, 4.25F, 3.25F, 1.0F, 7.0F, 1.0F, 0.0F, false);
        Ink_Tank.texOffs(10, 24).addBox(-3.0F, 4.25F, 7.25F, 1.0F, 7.0F, 1.0F, 0.0F, false);
        Ink_Tank.texOffs(6, 24).addBox(2.0F, 4.25F, 7.25F, 1.0F, 7.0F, 1.0F, 0.0F, false);
        Ink_Tank.texOffs(0, 24).addBox(2.0F, 4.25F, 3.25F, 1.0F, 7.0F, 1.0F, 0.0F, false);
        Ink_Tank.texOffs(18, 25).addBox(-3.0F, 11.25F, 3.25F, 6.0F, 1.0F, 5.0F, 0.0F, false);
        Ink_Tank.texOffs(0, 18).addBox(-2.5F, 3.25F, 3.25F, 5.0F, 1.0F, 5.0F, 0.0F, false);
        Ink_Tank.texOffs(31, 2).addBox(-0.5F, 1.75F, 2.0F, 1.0F, 2.0F, 2.0F, 0.0F, false);

        Tag = new ModelRenderer(this);
        Tag.setPos(-3.1168F, 2.8445F, 8.9821F);
        setRotationAngle(Tag, -0.1309F, -0.3927F, -0.3054F);
        Tag.texOffs(8, 63).addBox(-0.8541F, 0.6055F, -2.1381F, 2, 0, 2, 0.0F);
        Torso.addChild(Tag);

        inkPieces = new ArrayList<>();
        inkBarY = 23.25F;

        for (int i = 0; i < 7; i++)
        {
            ModelRenderer ink = new ModelRenderer(this);
            ink.setPos(0.0F, inkBarY, -0.75F);
            Ink_Tank.addChild(ink);

            ink.texOffs(110, 0).addBox(-2.5F, -12.0F, 4.5F, 5, 1, 4, 0.0F);

            inkPieces.add(ink);
        }

        body = new ModelRenderer(this);
        body.addChild(Torso);

    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}
