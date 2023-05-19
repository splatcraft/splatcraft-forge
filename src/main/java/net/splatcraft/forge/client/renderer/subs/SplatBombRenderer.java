package net.splatcraft.forge.client.renderer.subs;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.splatcraft.forge.client.models.SubWeaponModel;
import net.splatcraft.forge.entities.subs.SplatBombEntity;
import net.splatcraft.forge.entities.subs.SuctionBombEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoModel;

public class SplatBombRenderer extends SubWeaponRenderer<SplatBombEntity>
{
	public SplatBombRenderer(EntityRendererProvider.Context renderManager) {
			super(renderManager, new SubWeaponModel<>(), 2);
	}

	@Override
	public void render(GeoModel model, SplatBombEntity animatable, float partialTick, RenderType type, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
	{
		poseStack.pushPose();

		if(!animatable.isItem)
		{
			poseStack.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot()) * 2f));
			poseStack.mulPose(Vector3f.XP.rotationDegrees(Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot()) * 2f - 90F));

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

		poseStack.popPose();
	}

	@Override
	protected float getOverlayProgress(SplatBombEntity livingEntityIn, float partialTicks) {
		float f = livingEntityIn.getFlashIntensity(partialTicks);
		return (int)(f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
	}
}
