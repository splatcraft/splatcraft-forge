package net.splatcraft.forge.client.model.inktanks;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class AbstractInkTankModel extends BipedModel<LivingEntity>
{

    protected List<ModelRenderer> inkPieces = new ArrayList<>();

    protected float inkBarY = 0;

    public AbstractInkTankModel()
    {
        super(1);
    }


    public void setInkLevels(float inkPctg)
    {
        for (int i = 1; i <= inkPieces.size(); i++)
        {
            ModelRenderer box = inkPieces.get(i - 1);
            if (inkPctg == 0)
            {
                box.visible = false;
                continue;
            }
            box.visible = true;
            box.y = 23.25F - Math.min(i * inkPctg, i);
        }
    }

    @Override
    public void setupAnim(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }
}
