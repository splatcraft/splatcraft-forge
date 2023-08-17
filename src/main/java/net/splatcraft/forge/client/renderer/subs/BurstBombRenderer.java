package net.splatcraft.forge.client.renderer.subs;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.client.models.subs.BurstBombModel;
import net.splatcraft.forge.entities.subs.BurstBombEntity;

public class BurstBombRenderer extends SubWeaponRenderer<BurstBombEntity, BurstBombModel>
{
	private final BurstBombModel MODEL;
	private static final ResourceLocation TEXTURE = new ResourceLocation(Splatcraft.MODID, "textures/weapons/sub/burst_bomb.png");
	private static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation(Splatcraft.MODID, "textures/weapons/sub/burst_bomb_ink.png");

	public BurstBombRenderer(EntityRendererProvider.Context context)
	{
		super(context);
		MODEL = createModel(context, BurstBombModel.class);
	}

	@Override
	public void render(BurstBombEntity entityIn, float entityYaw, float partialTicks, PoseStack PoseStackIn, MultiBufferSource bufferIn, int packedLightIn) {

		PoseStackIn.pushPose();
		if(!entityIn.isItem)
		{
			PoseStackIn.translate(0.0D, 0.2/*0.15000000596046448D*/, 0.0D);
			PoseStackIn.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 180.0F));
			PoseStackIn.mulPose(Vector3f.XP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())+90F));
			PoseStackIn.scale(1, -1, 1);
		}
		super.render(entityIn, entityYaw, partialTicks, PoseStackIn, bufferIn, packedLightIn);
		PoseStackIn.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(BurstBombEntity entity)
	{
		return TEXTURE;
	}

	@Override
	public BurstBombModel getModel()
	{
		return MODEL;
	}

	@Override
	public ResourceLocation getInkTextureLocation(BurstBombEntity entity)
	{
		return OVERLAY_TEXTURE;
	}

}
