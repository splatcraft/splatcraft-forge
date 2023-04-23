package net.splatcraft.forge.client.renderer.subs;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.splatcraft.forge.client.models.SubWeaponModel;
import net.splatcraft.forge.entities.subs.BurstBombEntity;
import net.splatcraft.forge.entities.subs.SuctionBombEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoModel;

public class BurstBombRenderer extends SubWeaponRenderer<BurstBombEntity>
{
	public BurstBombRenderer(EntityRendererProvider.Context renderManager) {
			super(renderManager, new SubWeaponModel<>(), 2);
	}

	@Override
	public void render(GeoModel model, BurstBombEntity animatable, float partialTick, RenderType type, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
	{
		poseStack.pushPose();

		if(!animatable.isItem)
		{
			poseStack.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot())));
			poseStack.mulPose(Vector3f.XP.rotationDegrees(Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot()) - 90F));
		}

		super.render(model, animatable, partialTick, type, poseStack, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha);

		poseStack.popPose();
	}
}
