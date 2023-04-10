package net.splatcraft.forge.client.layer;

public class SquidBumperOverlayLayer //extends LayerRenderer<SquidBumperEntity, SquidBumperModel>
{
    /*
    private static final ResourceLocation TEXTURE = new ResourceLocation(Splatcraft.MODID, "textures/entity/squid_bumper_overlay.png");
    private final SquidBumperModel MODEL = new SquidBumperModel();

    public SquidBumperOverlayLayer(IEntityRenderer<SquidBumperEntity, SquidBumperModel> renderer)
    {
        super(renderer);
    }

    protected static <T extends SquidBumperEntity> void renderCopyCutoutModel(SquidBumperModel modelParentIn, SquidBumperModel modelIn, ResourceLocation textureLocationIn, PoseStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float partialTicks, float red, float green, float blue)
    {
        if (!entityIn.isInvisible())
        {
            modelParentIn.copyPropertiesTo(modelIn);
            modelIn.prepareMobModel(entityIn, limbSwing, limbSwingAmount, partialTicks);
            modelIn.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            renderCutoutModel(modelIn, textureLocationIn, matrixStackIn, bufferIn, packedLightIn, entityIn, red, green, blue);
        }

    }

    protected static <T extends LivingEntity> void renderCutoutModel(SquidBumperModel modelIn, ResourceLocation textureLocationIn, PoseStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, SquidBumperEntity entityIn, float red, float green, float blue)
    {
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(textureLocationIn));
        modelIn.renderBase(matrixStackIn, ivertexbuilder, packedLightIn, LivingRenderer.getOverlayCoords(entityIn, 0.0F), red, green, blue, 1.0F);

        float scale = entityIn.getBumperScale(Minecraft.getInstance().getDeltaFrameTime());

        matrixStackIn.pushPose();
        matrixStackIn.scale(scale, scale, scale);
        modelIn.renderBumper(matrixStackIn, ivertexbuilder, packedLightIn, LivingRenderer.getOverlayCoords(entityIn, 0.0F), red, green, blue, 1.0F);
        matrixStackIn.popPose();
    }

    @Override
    public void render(PoseStack matrixStack, IRenderTypeBuffer bufferIn, int packedLightIn, SquidBumperEntity entity, float v, float v1, float v2, float v3, float v4, float v5)
    {
        renderCopyCutoutModel(getParentModel(), MODEL, TEXTURE, matrixStack, bufferIn, packedLightIn, entity, v, v1, v2, v3, v4, v5, 1, 1, 1);
    }
    */
}
