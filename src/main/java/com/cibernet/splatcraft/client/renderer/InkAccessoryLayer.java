package com.cibernet.splatcraft.client.renderer;

import com.cibernet.splatcraft.util.InkBlockUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;

public class InkAccessoryLayer extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>>
{
    BipedModel MODEL = new BipedModel(1.0F);

    public InkAccessoryLayer(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> renderer)
    {
        super(renderer);
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, AbstractClientPlayerEntity entity, float v, float v1, float v2, float v3, float v4, float v5)
    {
        ItemStack stack = InkBlockUtils.getBandStack(entity, InkBlockUtils.getInkType(entity));
        if(stack.isEmpty() || entity.getHeldItemMainhand().equals(stack) || entity.getHeldItemOffhand().equals(stack))
            return;

        ResourceLocation stackLoc = stack.getItem().getRegistryName();
        ResourceLocation texture = new ResourceLocation(stackLoc.getNamespace(), "textures/models/" + stackLoc.getPath() + ".png");

        MODEL.bipedLeftArm.showModel = entity.getPrimaryHand() == HandSide.LEFT;
        MODEL.bipedLeftLeg.showModel = false;
        MODEL.bipedRightArm.showModel = entity.getPrimaryHand() == HandSide.RIGHT;
        MODEL.bipedRightLeg.showModel = false;


        this.getEntityModel().setModelAttributes(MODEL);
        this.func_241738_a_(matrixStack, iRenderTypeBuffer, i, stack.hasEffect(), MODEL, 1.0F, 1.0F, 1.0F, texture);
    }

    private void func_241738_a_(MatrixStack p_241738_1_, IRenderTypeBuffer p_241738_2_, int p_241738_3_, boolean p_241738_5_, BipedModel p_241738_6_, float p_241738_8_, float p_241738_9_, float p_241738_10_, ResourceLocation armorResource) {
        IVertexBuilder ivertexbuilder = ItemRenderer.getArmorVertexBuilder(p_241738_2_, RenderType.getArmorCutoutNoCull(armorResource), false, p_241738_5_);
        p_241738_6_.render(p_241738_1_, ivertexbuilder, p_241738_3_, OverlayTexture.NO_OVERLAY, p_241738_8_, p_241738_9_, p_241738_10_, 1.0F);
    }
}
