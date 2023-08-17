package net.splatcraft.forge.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.client.models.SquidBumperModel;
import net.splatcraft.forge.entities.SquidBumperEntity;

public class SquidBumperRenderer extends LivingEntityRenderer<SquidBumperEntity, SquidBumperModel> //implements IEntityRenderer<LivingEntity, InkSquidModel>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(Splatcraft.MODID, "textures/entity/squid_bumper_overlay.png");

	
	public SquidBumperRenderer(EntityRendererProvider.Context context)
	{
		super(context, new SquidBumperModel(context.bakeLayer(SquidBumperModel.LAYER_LOCATION)), 0.5f);
		//addLayer(new SquidBumperColorLayer(this));
		//addLayer(new SquidBumperOverlayLayer(this));
	}

	@Override
	protected boolean shouldShowName(SquidBumperEntity entity)
	{
		return !entity.hasCustomName() && !(entity.getInkHealth() >= 20) || super.shouldShowName(entity) && (entity.shouldShowName() || entity == this.entityRenderDispatcher.crosshairPickEntity);
	}

	@Override
	protected void renderNameTag(SquidBumperEntity entityIn, Component displayNameIn, PoseStack PoseStackIn, MultiBufferSource bufferIn, int packedLightIn)
	{
		if (entityIn.hasCustomName())
		{
			super.renderNameTag(entityIn, displayNameIn, PoseStackIn, bufferIn, packedLightIn);
		} else
		{
			float health = 20 - entityIn.getInkHealth();
			super.renderNameTag(entityIn, new TextComponent((health >= 20 ? ChatFormatting.DARK_RED : "") + String.format("%.1f", health)), PoseStackIn, bufferIn, packedLightIn);

		}
	}

	@Override
	protected void setupRotations(SquidBumperEntity entityLiving, PoseStack PoseStackIn, float ageInTicks, float rotationYaw, float partialTicks)
	{
		//PoseStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F - rotationYaw));
		float punchTime = (float) (entityLiving.level.getGameTime() - entityLiving.punchCooldown) + partialTicks;
		float hurtTime = (float) (entityLiving.level.getGameTime() - entityLiving.hurtCooldown) + partialTicks;


		if (punchTime < 5.0F)
		{
			PoseStackIn.mulPose(Vector3f.YP.rotationDegrees(Mth.sin(punchTime / 1.5F * (float) Math.PI) * 3.0F));
		}
		if (hurtTime < 5.0F)
		{
			PoseStackIn.mulPose(Vector3f.ZP.rotationDegrees(Mth.sin(hurtTime / 1.5F * (float) Math.PI) * 3.0F));
		}

	}

	@Override
	public ResourceLocation getTextureLocation(SquidBumperEntity entity)
	{
		return TEXTURE;
	}
}
