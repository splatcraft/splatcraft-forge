package com.cibernet.splatcraft.client.renderer;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.SplatcraftConfig;
import com.cibernet.splatcraft.client.model.BlasterInkProjectileModel;
import com.cibernet.splatcraft.client.model.InkProjectileModel;
import com.cibernet.splatcraft.client.model.RollerInkProjectileModel;
import com.cibernet.splatcraft.client.model.ShooterInkProjectileModel;
import com.cibernet.splatcraft.entities.InkProjectileEntity;
import com.cibernet.splatcraft.util.ColorUtils;
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
        if (entityIn.ticksExisted >= 3 || this.renderManager.info.getRenderViewEntity().getDistanceSq(entityIn) >= 12.25D)
        {
            float scale = entityIn.getProjectileSize() * 2.5f;
            int color = entityIn.getColor();

            if (SplatcraftConfig.Client.getColorLock())
            {
                color = ColorUtils.getLockedColor(color);
            }

            float r = (float) (Math.floor((float) color / (256 * 256)) / 255f);
            float g = (float) (Math.floor((float) color / 256) % 256 / 255f);
            float b = (color % 256) / 255f;

            //0.30000001192092896D
            matrixStackIn.push();
            matrixStackIn.translate(0.0D, 0.4d/*0.15000000596046448D*/, 0.0D);
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.prevRotationYaw, entityIn.rotationYaw) - 180.0F));
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.prevRotationPitch, entityIn.rotationPitch)));
            matrixStackIn.scale(scale, scale, scale);

            InkProjectileModel model = MODELS.getOrDefault(entityIn.getProjectileType(), MODELS.get(InkProjectileEntity.Types.DEFAULT));

            model.setRotationAngles(entityIn, 0, 0, this.handleRotationFloat(entityIn, partialTicks), entityYaw, entityIn.rotationPitch);
            model.render(matrixStackIn, bufferIn.getBuffer(model.getRenderType(getEntityTexture(entityIn))), packedLightIn, OverlayTexture.NO_OVERLAY, r, g, b, 1);
            matrixStackIn.pop();

            super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        }
    }


    protected float handleRotationFloat(InkProjectileEntity livingBase, float partialTicks)
    {
        return (float) livingBase.ticksExisted + partialTicks;
    }

    @Override
    public InkProjectileModel getEntityModel()
    {
        return MODELS.get(InkProjectileEntity.Types.DEFAULT);
    }

    @Override
    public ResourceLocation getEntityTexture(InkProjectileEntity entity)
    {
        return new ResourceLocation(Splatcraft.MODID, "textures/entity/" + entity.getProjectileType() + "_projectile.png");
    }
}
