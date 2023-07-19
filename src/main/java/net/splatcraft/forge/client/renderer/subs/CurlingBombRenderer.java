package net.splatcraft.forge.client.renderer.subs;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.splatcraft.forge.client.models.SubWeaponModel;
import net.splatcraft.forge.entities.subs.CurlingBombEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoModel;

public class CurlingBombRenderer extends SubWeaponRenderer<CurlingBombEntity>
{
	public CurlingBombRenderer(EntityRendererProvider.Context renderManager) {
			super(renderManager, new SubWeaponModel<>(), 3);
	}

	@Override
	public Color getRenderColor(CurlingBombEntity animatable, float partialTick, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight)
	{
		if (getRenderLayer() == 2)
		{
			float v = Mth.clamp((CurlingBombEntity.MAX_FUSE_TIME - Mth.lerp(partialTick, animatable.prevFuseTime, animatable.fuseTime)) / CurlingBombEntity.MAX_COOK_TIME, 0, 1);
			return Color.ofRGB(v, 1 - v, 0);
		}
		return super.getRenderColor(animatable, partialTick, poseStack, bufferSource, buffer, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(CurlingBombEntity animatable)
	{
		ResourceLocation ret = super.getTextureLocation(animatable);
		if (getRenderLayer() == 2)
			return new ResourceLocation(ret.getNamespace(), ret.getPath().replace(".png", "_overlay.png"));
		return ret;
	}

	@Override
	public void render(GeoModel model, CurlingBombEntity animatable, float partialTick, RenderType type, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
	{
		if(!animatable.isItem)
		{
			if (model.getBone("blades").isPresent())
				model.getBone("blades").get().setRotationY(Mth.lerp(partialTick, animatable.prevBladeRot, animatable.bladeRot));

			float f = animatable.getFlashIntensity(partialTick);
			float f1 = 1.0F + Mth.sin(f * 100.0F) * f * 0.01F;
			f = Mth.clamp(f, 0.0F, 1.0F);
			f = f * f;
			f = f * f;
			float f2 = (1.0F + f * 0.4F) * f1;
			float f3 = (1.0F + f * 0.1F) / f1;
			poseStack.scale(f2, f3, f2);
		}

		super.render(model, animatable, partialTick, type, poseStack, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected float getOverlayProgress(CurlingBombEntity livingEntityIn, float partialTicks) {
		float f = livingEntityIn.getFlashIntensity(partialTicks);
		return (int)(f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
	}
}
