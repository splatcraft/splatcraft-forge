package net.splatcraft.forge.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.splatcraft.forge.client.models.InkProjectileModel;
import net.splatcraft.forge.entities.InkProjectileEntity;
import net.splatcraft.forge.util.ColorUtils;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.util.Color;

public class InkProjectileRenderer extends GeoNonLivingRenderer<InkProjectileEntity>
{
	public InkProjectileRenderer(EntityRendererProvider.Context renderManager)
	{
		super(renderManager, new InkProjectileModel());
	}

	@Override
	public void render(InkProjectileEntity animatable, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight)
	{
		float scale = animatable.getProjectileSize() * (animatable.getProjectileType().equals(InkProjectileEntity.Types.DEFAULT) ? 1 : 2.5f);

		poseStack.pushPose();
		poseStack.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot()) - 180.0F));
		poseStack.mulPose(Vector3f.XP.rotationDegrees(Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot())));
		poseStack.scale(scale, scale, scale);

		super.render(animatable, entityYaw, partialTick, poseStack, bufferSource, packedLight);
		poseStack.popPose();
	}

	@Override
	public Color getRenderColor(InkProjectileEntity animatable, float partialTick, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight)
	{
		float[] rgb = ColorUtils.hexToRGB(animatable.getColor());
		return Color.ofRGB(rgb[0], rgb[1], rgb[2]);
	}
}
