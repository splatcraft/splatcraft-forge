package net.splatcraft.forge.client.renderer;

public class SubWeaponItemRenderer { /* extends GeoItemRenderer<SubWeaponItem>
{
	public SubWeaponItemRenderer()
	{
		super(new AnimatedGeoModel<>() {
			@Override
			public ResourceLocation getModelLocation(SubWeaponItem object) {
				return null;
			}

			@Override
			public ResourceLocation getTextureLocation(SubWeaponItem object) {
				return null;
			}

			@Override
			public ResourceLocation getAnimationFileLocation(SubWeaponItem animatable) {
				return null;
			}
		});
	}

	@Override
	public void render(GeoModel model, SubWeaponItem animatable, float partialTick, RenderType type, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
	{
		AbstractSubWeaponEntity sub = animatable.entityType.get().create(Minecraft.getInstance().level);
		sub.readItemData(currentItemStack.getOrCreateTag().getCompound("EntityData"));
		sub.isItem = true;
		Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(sub).render(sub, 0, Minecraft.getInstance().getDeltaFrameTime(), poseStack, bufferSource, packedLight);
	}
	*/
}
