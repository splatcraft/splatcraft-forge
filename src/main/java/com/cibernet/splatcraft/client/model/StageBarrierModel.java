package com.cibernet.splatcraft.client.model;// Made with Blockbench 3.5.4
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class StageBarrierModel extends Model {
    private final ModelRenderer bb_main;

    public StageBarrierModel() {
        super(RenderType::getEntityCutoutNoCull);
        textureWidth = 64;
        textureHeight = 64;

        bb_main = new ModelRenderer(this);
        bb_main.setRotationPoint(0.0F, 24.0F, 0.0F);

    }


    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        bb_main.render(matrixStack, buffer, packedLight, packedOverlay);
    }
}
