package net.splatcraft.forge.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.SplatcraftConfig;
import net.splatcraft.forge.client.models.projectiles.BlasterInkProjectileModel;
import net.splatcraft.forge.client.models.projectiles.InkProjectileModel;
import net.splatcraft.forge.client.models.projectiles.RollerInkProjectileModel;
import net.splatcraft.forge.client.models.projectiles.ShooterInkProjectileModel;
import net.splatcraft.forge.entities.InkProjectileEntity;
import net.splatcraft.forge.util.ColorUtils;

import java.util.TreeMap;

public class InkProjectileRenderer extends EntityRenderer<InkProjectileEntity> implements RenderLayerParent<InkProjectileEntity, InkProjectileModel>
{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Splatcraft.MODID, "textures/entity/shooter_projectile.png");
	private TreeMap<String, InkProjectileModel> MODELS;

	public InkProjectileRenderer(EntityRendererProvider.Context context)
	{
		super(context);

		MODELS = new TreeMap<String, InkProjectileModel>()
		{{
			put(InkProjectileEntity.Types.DEFAULT, new InkProjectileModel(context.bakeLayer(InkProjectileModel.LAYER_LOCATION)));
			put(InkProjectileEntity.Types.SHOOTER, new ShooterInkProjectileModel(context.bakeLayer(ShooterInkProjectileModel.LAYER_LOCATION)));
			put(InkProjectileEntity.Types.CHARGER, new ShooterInkProjectileModel(context.bakeLayer(ShooterInkProjectileModel.LAYER_LOCATION)));
			put(InkProjectileEntity.Types.BLASTER, new BlasterInkProjectileModel(context.bakeLayer(BlasterInkProjectileModel.LAYER_LOCATION)));
			put(InkProjectileEntity.Types.ROLLER, new RollerInkProjectileModel(context.bakeLayer(RollerInkProjectileModel.LAYER_LOCATION)));
		}};
	}

	@Override
	public void render(InkProjectileEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn)
	{
		if(entityIn.isInvisible())
			return;

		if (entityIn.tickCount >= 3 || this.entityRenderDispatcher.camera.getEntity().distanceToSqr(entityIn) >= 12.25D)
		{
			float scale = entityIn.getProjectileSize() * (entityIn.getProjectileType().equals(InkProjectileEntity.Types.DEFAULT) ? 1 : 2.5f);
			int color = entityIn.getColor();

			if (SplatcraftConfig.Client.getColorLock())
				color = ColorUtils.getLockedColor(color);

			float r = (float) (Math.floor((float) color / (256 * 256)) / 255f);
			float g = (float) (Math.floor((float) color / 256) % 256 / 255f);
			float b = (color % 256) / 255f;

			//0.30000001192092896D
			matrixStackIn.pushPose();
			matrixStackIn.translate(0.0D, 0.4d/*0.15000000596046448D*/, 0.0D);
			matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 180.0F));
			matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
			matrixStackIn.scale(scale, scale, scale);

			InkProjectileModel model = MODELS.getOrDefault(entityIn.getProjectileType(), MODELS.get(InkProjectileEntity.Types.DEFAULT));

			model.setupAnim(entityIn, 0, 0, this.handleRotationFloat(entityIn, partialTicks), entityYaw, entityIn.getXRot());
			model.renderToBuffer(matrixStackIn, bufferIn.getBuffer(model.renderType(getTextureLocation(entityIn))), packedLightIn, OverlayTexture.NO_OVERLAY, r, g, b, 1);
			matrixStackIn.popPose();

			super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
		}
	}


	protected float handleRotationFloat(InkProjectileEntity livingBase, float partialTicks)
	{
		return (float) livingBase.tickCount + partialTicks;
	}

	@Override
	public InkProjectileModel getModel()
	{
		return MODELS.get(InkProjectileEntity.Types.DEFAULT);
	}

	@Override
	public ResourceLocation getTextureLocation(InkProjectileEntity entity)
	{
		return new ResourceLocation(Splatcraft.MODID, "textures/entity/ink_projectile_"+ entity.getProjectileType() + ".png");
	}
}
