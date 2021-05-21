package com.cibernet.splatcraft.client.layer;

import com.cibernet.splatcraft.data.capabilities.playerinfo.IPlayerInfo;
import com.cibernet.splatcraft.data.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.util.ColorUtils;
import com.cibernet.splatcraft.util.InkBlockUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.Items;
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
        if(!PlayerInfoCapability.hasCapability(entity))
            return;
        IPlayerInfo info = PlayerInfoCapability.get(entity);
        InkBlockUtils.InkType inkType = info.getInkType();

        if(!inkType.getRepItem().equals(Items.AIR) && ((entity.getHeldItemMainhand().getItem().equals(inkType.getRepItem()) || entity.getHeldItemOffhand().getItem().equals(inkType.getRepItem()))))
            return;

        ResourceLocation stackLoc = inkType.getName();

        String customModelData = "";

        if(info.hasInkTypeData() && Minecraft.getInstance().getTextureManager().getTexture(new ResourceLocation(stackLoc.getNamespace(), "textures/models/" + stackLoc.getPath() + customModelData + ".png")) != null)
            customModelData = "_" + info.getInkTypeData();

        ResourceLocation texture = new ResourceLocation(stackLoc.getNamespace(), "textures/models/" + stackLoc.getPath() + customModelData + ".png");
        ResourceLocation coloredTexture = new ResourceLocation(stackLoc.getNamespace(), "textures/models/" + stackLoc.getPath() + customModelData + "_colored.png");



        MODEL.bipedLeftArm.showModel = entity.getPrimaryHand() == HandSide.LEFT;
        MODEL.bipedLeftLeg.showModel = entity.getPrimaryHand() == HandSide.LEFT;
        MODEL.bipedRightArm.showModel = entity.getPrimaryHand() == HandSide.RIGHT;
        MODEL.bipedRightLeg.showModel = entity.getPrimaryHand() == HandSide.RIGHT;

        int color = ColorUtils.getPlayerColor(entity);
        float r = ((color & 16711680) >> 16) / 255.0f;
        float g = ((color & '\uff00') >> 8) / 255.0f;
        float b = (color & 255) / 255.0f;
        
        if(Minecraft.getInstance().getTextureManager().getTexture(texture) != null)
        {
            this.getEntityModel().setModelAttributes(MODEL);
            this.render(matrixStack, iRenderTypeBuffer, i, false, MODEL, 1.0F, 1.0F, 1.0F, texture);
            if(Minecraft.getInstance().getTextureManager().getTexture(coloredTexture) != null)
                this.render(matrixStack, iRenderTypeBuffer, i, false, MODEL, r, g, b, coloredTexture);
        }
    }

    private void render(MatrixStack p_241738_1_, IRenderTypeBuffer p_241738_2_, int p_241738_3_, boolean p_241738_5_, BipedModel p_241738_6_, float p_241738_8_, float p_241738_9_, float p_241738_10_, ResourceLocation armorResource) {
        IVertexBuilder ivertexbuilder = ItemRenderer.getArmorVertexBuilder(p_241738_2_, RenderType.getArmorCutoutNoCull(armorResource), false, p_241738_5_);
        p_241738_6_.render(p_241738_1_, ivertexbuilder, p_241738_3_, OverlayTexture.NO_OVERLAY, p_241738_8_, p_241738_9_, p_241738_10_, 1.0F);
    }
}
