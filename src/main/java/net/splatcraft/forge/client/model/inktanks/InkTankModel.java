package net.splatcraft.forge.client.model.inktanks;// Made with Blockbench 3.5.4
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


import net.minecraft.client.renderer.model.ModelRenderer;

import java.util.ArrayList;

public class InkTankModel extends AbstractInkTankModel
{
    private final ModelRenderer Head;
    private final ModelRenderer Torso;
    private final ModelRenderer Ink_Tank;

    public InkTankModel()
    {
        texWidth = 128;
        texHeight = 128;

        Head = new ModelRenderer(this);
        Head.setPos(0.0F, 0.0F, 0.0F);
        Head.texOffs(0, 112).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);

        Torso = new ModelRenderer(this);
        Torso.setPos(0.0F, -0.25F, 0.0F);
        Torso.texOffs(0, 0).addBox(-4.75F, -0.25F, -2.5F, 9.0F, 12.0F, 5.0F, 0.0F, false);
        Torso.texOffs(31, 0).addBox(-1.0F, 3.0F, 2.5F, 2.0F, 1.0F, 1.0F, 0.0F, false);

        Ink_Tank = new ModelRenderer(this);
        Ink_Tank.setPos(0.0F, 0.75F, 0.75F);
        Torso.addChild(Ink_Tank);
        Ink_Tank.texOffs(31, 2).addBox(-0.5F, 1.75F, 2.0F, 1.0F, 2.0F, 2.0F, 0.0F, false);
        Ink_Tank.texOffs(0, 19).addBox(-2.0F, 3.25F, 3.25F, 4.0F, 1.0F, 4.0F, 0.0F, false);
        Ink_Tank.texOffs(16, 19).addBox(-2.0F, 11.25F, 3.25F, 4.0F, 1.0F, 4.0F, 0.0F, false);
        Ink_Tank.texOffs(0, 24).addBox(1.0F, 4.25F, 3.25F, 1.0F, 7.0F, 1.0F, 0.0F, false);
        Ink_Tank.texOffs(6, 24).addBox(1.0F, 4.25F, 6.25F, 1.0F, 7.0F, 1.0F, 0.0F, false);
        Ink_Tank.texOffs(10, 24).addBox(-2.0F, 4.25F, 6.25F, 1.0F, 7.0F, 1.0F, 0.0F, false);
        Ink_Tank.texOffs(14, 24).addBox(-2.0F, 4.25F, 3.25F, 1.0F, 7.0F, 1.0F, 0.0F, false);
        Ink_Tank.texOffs(12, 39).addBox(0.0F, 9.25F, 6.25F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        Ink_Tank.texOffs(12, 39).addBox(0.0F, 7.25F, 6.25F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        Ink_Tank.texOffs(12, 39).addBox(0.0F, 5.25F, 6.25F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        Ink_Tank.texOffs(0, 33).addBox(-1.0F, 2.25F, 4.25F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        Ink_Tank.texOffs(8, 33).addBox(-3.5F, 2.5F, 4.25F, 2.0F, 1.0F, 2.0F, 0.0F, false);

        inkPieces = new ArrayList<>();
        inkBarY = 23.25F;

        for (int i = 0; i < 7; i++)
        {
            ModelRenderer ink = new ModelRenderer(this);
            ink.setPos(0.0F, inkBarY, -0.75F);
            Ink_Tank.addChild(ink);

            ink.texOffs(116, 30).addBox(-1.5F, -12F, 4.5F, 3, 1, 3, 0);

            inkPieces.add(ink);
        }

        body = new ModelRenderer(this);
        body.addChild(Torso);
    }
}
