package net.splatcraft.forge.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.util.ColorUtils;

import java.util.HashMap;
import java.util.UUID;

public class PlayerInkColoredSkinLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>
{
    public static final HashMap<UUID, ResourceLocation> TEXTURES = new HashMap<>();

    public static final String PATH = "config/skins/";

    PlayerModel<AbstractClientPlayer> MODEL;

    public PlayerInkColoredSkinLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer, PlayerModel<AbstractClientPlayer> model)
    {
        super(renderer);
        this.MODEL = model;
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource iRenderTypeBuffer, int i, AbstractClientPlayer entity, float v, float v1, float v2, float v3, float v4, float v5)
    {
        if(entity.isSpectator() || entity.isInvisible() || !PlayerInfoCapability.hasCapability(entity))
            return;

        int color = ColorUtils.getPlayerColor(entity);
        float r = ((color & 16711680) >> 16) / 255.0f;
        float g = ((color & '\uff00') >> 8) / 255.0f;
        float b = (color & 255) / 255.0f;

        if(TEXTURES.containsKey(entity.getUUID()))
        {
            copyPropertiesFrom(getParentModel(), MODEL);
            this.render(matrixStack, iRenderTypeBuffer, i, MODEL, r, g, b, TEXTURES.get(entity.getUUID()));
        }
    }

    private void render(PoseStack p_241738_1_, MultiBufferSource buffer, int p_241738_3_, PlayerModel<AbstractClientPlayer> p_241738_6_, float p_241738_8_, float p_241738_9_, float p_241738_10_, ResourceLocation armorResource)
    {
	    VertexConsumer ivertexbuilder = buffer.getBuffer(RenderType.entitySmoothCutout(armorResource));
        p_241738_6_.renderToBuffer(p_241738_1_, ivertexbuilder, p_241738_3_, OverlayTexture.NO_OVERLAY, p_241738_8_, p_241738_9_, p_241738_10_, 1.0F);
    }

    public static void renderHand(PlayerModel<AbstractClientPlayer> playermodel, PoseStack matrixStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player, ModelPart arm, ModelPart sleeve)
    {

        if(player == null || !TEXTURES.containsKey(player.getUUID()) ||
                player.isSpectator() || player.isInvisible() || !PlayerInfoCapability.hasCapability(player))
            return;

        int color = ColorUtils.getPlayerColor(player);
        float r = ((color & 16711680) >> 16) / 255.0f;
        float g = ((color & '\uff00') >> 8) / 255.0f;
        float b = (color & 255) / 255.0f;

        VertexConsumer ivertexbuilder = buffer.getBuffer(RenderType.entitySmoothCutout(TEXTURES.get(player.getUUID())));

        //copyPropertiesFrom(getParentModel(), playermodel);
        playermodel.attackTime = 0.0F;
        playermodel.crouching = false;
        playermodel.swimAmount = 0.0F;
        playermodel.setupAnim(player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        arm.xRot = 0.0F;
        arm.render(matrixStack, ivertexbuilder, packedLight, OverlayTexture.NO_OVERLAY, r, g, b, 1);
        sleeve.xRot = 0.0F;
        sleeve.render(matrixStack, ivertexbuilder, packedLight, OverlayTexture.NO_OVERLAY, r, g, b, 1);
    }

    private void copyPropertiesFrom(PlayerModel<AbstractClientPlayer> from, PlayerModel<AbstractClientPlayer> to)
    {
        from.copyPropertiesTo(to);

        to.jacket.copyFrom(from.jacket);
        to.rightSleeve.copyFrom(from.rightSleeve);
        to.leftSleeve.copyFrom(from.leftSleeve);
        to.rightPants.copyFrom(from.rightPants);
        to.leftPants.copyFrom(from.leftPants);

        to.jacket.visible = from.jacket.visible;
        to.rightSleeve.visible = from.rightSleeve.visible;
        to.leftSleeve.visible = from.leftSleeve.visible;
        to.rightPants.visible = from.rightPants.visible;
        to.leftPants.visible = from.leftPants.visible;
    }
}
