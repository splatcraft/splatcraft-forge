package net.splatcraft.forge.client.models.inktanks;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class AbstractInkTankModel extends HumanoidModel<LivingEntity>
{

    protected List<ModelPart> inkPieces = new ArrayList<>();

    protected float inkBarY = 0;

    public AbstractInkTankModel(ModelPart root)
    {
        super(root);
    }

    public void setInkLevels(float inkPctg)
    {
        for (int i = 1; i <= inkPieces.size(); i++)
        {
            ModelPart box = inkPieces.get(i - 1);
            if (inkPctg == 0)
            {
                box.visible = false;
                continue;
            }
            box.visible = true;
            box.y = 23.25F - Math.min(i * inkPctg, i);
        }
    }

    public static void createEmptyMesh(PartDefinition partdefinition)
    {
        partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5.0F, 2.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5.0F, 2.0F, 0.0F));
        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));
    }
    @Override
    public void setupAnim(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }
}
