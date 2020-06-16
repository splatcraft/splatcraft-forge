package com.cibernet.splatcraft.entities.models;// Made with Blockbench
// Paste this code into your mod.
// Make sure to generate all required imports

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;

public class ModelBarrier extends ModelBase
{
	private final ModelRenderer block;

	public ModelBarrier() {
		textureWidth = 64;
		textureHeight = 64;

		block = new ModelRenderer(this);
		block.setRotationPoint(0.0F, 16.0F, 0.0F);
		block.cubeList.add(new ModelBox(block, 0, 0, -8.0F, -8.0F, -8.0F, 16, 16, 16, 0.0F, false));
	}

	public void render() {
		block.render(0.0625F);
	}
	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}