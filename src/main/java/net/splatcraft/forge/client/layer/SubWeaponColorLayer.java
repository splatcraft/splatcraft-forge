package net.splatcraft.forge.client.layer;

public class SubWeaponColorLayer//< M extends AbstractSubWeaponModel<AbstractSubWeaponEntity>> extends LayerRenderer<AbstractSubWeaponEntity, M>
{
    /*
    private final ResourceLocation TEXTURE;
    private final M MODEL;

    public SubWeaponColorLayer(IEntityRenderer<AbstractSubWeaponEntity, M> renderer, String textureName, M model)
    {
        super(renderer);
        TEXTURE = new ResourceLocation(Splatcraft.MODID, "textures/entity/"+textureName+".png");
        MODEL = model;
    }


    @Override
    public void render(PoseStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, AbstractSubWeaponEntity entity, float v, float v1, float v2, float v3, float v4, float v5)
    {
        int color = ColorUtils.getEntityColor(entity);
        if (SplatcraftConfig.Client.getColorLock())
        {
            color = ColorUtils.getLockedColor(color);
        }
        float r = ((color & 16711680) >> 16) / 255.0f;
        float g = ((color & '\uff00') >> 8) / 255.0f;
        float b = (color & 255) / 255.0f;

        IVertexBuilder builder = iRenderTypeBuffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        MODEL.renderToBuffer(matrixStack, builder, i, OverlayTexture.pack(OverlayTexture.u(1.0f), OverlayTexture.v(false)), r, g, b, 1.0F);
    }

     */
}
