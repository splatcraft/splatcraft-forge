package com.cibernet.splatcraft.entities.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBat;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelInkProjectile extends ModelBase
{
    public ModelRenderer cube;

    public ModelInkProjectile()
    {
        this.cube = new ModelRenderer(this, 0, 0);
        this.cube.addBox(-0.5f,0,-0.5f,1,1,1);
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        this.cube.render(scale);
    }
}
