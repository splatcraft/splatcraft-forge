package net.splatcraft.forge.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.SheepRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.client.layer.InkSquidColorLayer;
import net.splatcraft.forge.client.models.InkSquidModel;
import net.splatcraft.forge.entities.InkSquidEntity;

public class InkSquidRenderer extends LivingEntityRenderer<LivingEntity, InkSquidModel> implements RenderLayerParent<LivingEntity, InkSquidModel>
{

	private static EntityRendererProvider.Context squidFormContext;
	private static final ResourceLocation TEXTURE = new ResourceLocation(Splatcraft.MODID, "textures/entity/ink_squid_overlay.png");

	public InkSquidRenderer(EntityRendererProvider.Context context)
	{
		super(context, new InkSquidModel(context.bakeLayer(InkSquidModel.LAYER_LOCATION)), 0.5f);
		addLayer(new InkSquidColorLayer(this, context.getModelSet()));

		if(squidFormContext == null)
			squidFormContext = context;
	}

	public static EntityRendererProvider.Context getContext()
	{
		return squidFormContext;
	}

	@Override
	protected boolean shouldShowName(LivingEntity entity)
	{
		return super.shouldShowName(entity) && (entity.shouldShowName() || entity.hasCustomName() && entity == this.entityRenderDispatcher.crosshairPickEntity);
	}

	@Override
	public void render(LivingEntity entity, float p_115309_, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int p_115313_)
	{
		super.render(entity, p_115309_, partialTicks, poseStack, bufferSource, p_115313_);

		if(entity instanceof InkSquidEntity squid)
			renderLeash(squid, partialTicks, poseStack, bufferSource);
	}

	@Override
	public ResourceLocation getTextureLocation(LivingEntity entity)
	{
		return TEXTURE;
	}

	private void renderLeash(InkSquidEntity squid, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource)
	{
		Entity holder = squid.getLeashHolder();

		if(holder == null)
			return;

		poseStack.pushPose();
		Vec3 vec3 = holder.getRopeHoldPosition(partialTicks);
		double d0 = (double)(Mth.lerp(partialTicks, squid.yBodyRot, squid.yBodyRotO) * ((float)Math.PI / 180F)) + (Math.PI / 2D);
		Vec3 vec31 = squid.getLeashOffset();
		double d1 = Math.cos(d0) * vec31.z + Math.sin(d0) * vec31.x;
		double d2 = Math.sin(d0) * vec31.z - Math.cos(d0) * vec31.x;
		double d3 = Mth.lerp((double)partialTicks, squid.xo, squid.getX()) + d1;
		double d4 = Mth.lerp((double)partialTicks, squid.yo, squid.getY()) + vec31.y;
		double d5 = Mth.lerp((double)partialTicks, squid.zo, squid.getZ()) + d2;
		poseStack.translate(d1, vec31.y, d2);
		float f = (float)(vec3.x - d3);
		float f1 = (float)(vec3.y - d4);
		float f2 = (float)(vec3.z - d5);
		float f3 = 0.025F;
		VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.leash());
		Matrix4f matrix4f = poseStack.last().pose();
		float f4 = Mth.fastInvSqrt(f * f + f2 * f2) * 0.025F / 2.0F;
		float f5 = f2 * f4;
		float f6 = f * f4;
		BlockPos blockpos = new BlockPos(squid.getEyePosition(partialTicks));
		BlockPos blockpos1 = new BlockPos(holder.getEyePosition(partialTicks));
		int i = this.getBlockLightLevel(squid, blockpos);
		int j = getHolderBlockLightLevel(holder, blockpos1);
		int k = squid.level.getBrightness(LightLayer.SKY, blockpos);
		int l = squid.level.getBrightness(LightLayer.SKY, blockpos1);

		for(int i1 = 0; i1 <= 24; ++i1) {
			addVertexPair(vertexconsumer, matrix4f, f, f1, f2, i, j, k, l, 0.025F, 0.025F, f5, f6, i1, false);
		}

		for(int j1 = 24; j1 >= 0; --j1) {
			addVertexPair(vertexconsumer, matrix4f, f, f1, f2, i, j, k, l, 0.025F, 0.0F, f5, f6, j1, true);
		}

		poseStack.popPose();
	}

	@Override
	protected void setupRotations(LivingEntity p_115317_, PoseStack p_115318_, float p_115319_, float p_115320_, float p_115321_) {
		super.setupRotations(p_115317_, p_115318_, p_115319_, p_115320_, p_115321_);
	}

	protected int getHolderBlockLightLevel(Entity p_114496_, BlockPos p_114497_) {
		return p_114496_.isOnFire() ? 15 : p_114496_.level.getBrightness(LightLayer.BLOCK, p_114497_);
	}

	private static void addVertexPair(VertexConsumer p_174308_, Matrix4f p_174309_, float p_174310_, float p_174311_, float p_174312_, int p_174313_, int p_174314_, int p_174315_, int p_174316_, float p_174317_, float p_174318_, float p_174319_, float p_174320_, int p_174321_, boolean p_174322_) {
		float f = (float)p_174321_ / 24.0F;
		int i = (int)Mth.lerp(f, (float)p_174313_, (float)p_174314_);
		int j = (int)Mth.lerp(f, (float)p_174315_, (float)p_174316_);
		int k = LightTexture.pack(i, j);
		float f1 = p_174321_ % 2 == (p_174322_ ? 1 : 0) ? 0.7F : 1.0F;
		float f2 = 0.5F * f1;
		float f3 = 0.4F * f1;
		float f4 = 0.3F * f1;
		float f5 = p_174310_ * f;
		float f6 = p_174311_ > 0.0F ? p_174311_ * f * f : p_174311_ - p_174311_ * (1.0F - f) * (1.0F - f);
		float f7 = p_174312_ * f;
		p_174308_.vertex(p_174309_, f5 - p_174319_, f6 + p_174318_, f7 + p_174320_).color(f2, f3, f4, 1.0F).uv2(k).endVertex();
		p_174308_.vertex(p_174309_, f5 + p_174319_, f6 + p_174317_ - p_174318_, f7 - p_174320_).color(f2, f3, f4, 1.0F).uv2(k).endVertex();
	}
}
