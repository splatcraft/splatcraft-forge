package net.splatcraft.forge.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.splatcraft.forge.entities.subs.AbstractSubWeaponEntity;
import net.splatcraft.forge.items.weapons.SubWeaponItem;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class SubWeaponItemRenderer extends GeoItemRenderer<SubWeaponItem>
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

}
