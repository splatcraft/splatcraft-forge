package net.splatcraft.forge.client.renderer;

import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.SplatcraftConfig;
import net.splatcraft.forge.client.model.projectiles.BlasterInkProjectileModel;
import net.splatcraft.forge.client.model.projectiles.InkProjectileModel;
import net.splatcraft.forge.client.model.projectiles.RollerInkProjectileModel;
import net.splatcraft.forge.client.model.projectiles.ShooterInkProjectileModel;
import net.splatcraft.forge.entities.InkProjectileEntity;
import net.splatcraft.forge.util.ColorUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

import java.util.TreeMap;

public class InkProjectileRenderer extends EntityRenderer<InkProjectileEntity> implements IEntityRenderer<InkProjectileEntity, InkProjectileModel>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Splatcraft.MODID, "textures/entity/shooter_projectile.png");
    private final TreeMap<String, InkProjectileModel> MODELS = new TreeMap<String, InkProjectileModel>()
    {{
        put(InkProjectileEntity.Types.DEFAULT, new InkProjectileModel());
        put(InkProjectileEntity.Types.SHOOTER, new ShooterInkProjectileModel());
        put(InkProjectileEntity.Types.CHARGER, new ShooterInkProjectileModel());
        put(InkProjectileEntity.Types.BLASTER, new BlasterInkProjectileModel());
        put(InkProjectileEntity.Types.ROLLER, new RollerInkProjectileModel());
    }};

    public InkProjectileRenderer(EntityRendererManager manager)
    {
        super(manager);
    }

    @Override
    public void render(InkProjectileEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn)
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
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.yRotO, entityIn.yRot) - 180.0F));
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.xRotO, entityIn.xRot)));
            matrixStackIn.scale(scale, scale, scale);

            InkProjectileModel model = MODELS.getOrDefault(entityIn.getProjectileType(), MODELS.get(InkProjectileEntity.Types.DEFAULT));

            model.setupAnim(entityIn, 0, 0, this.handleRotationFloat(entityIn, partialTicks), entityYaw, entityIn.xRot);
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
        return new ResourceLocation(Splatcraft.MODID, "textures/entity/" + entity.getProjectileType() + "_projectile.png");
    }
}
