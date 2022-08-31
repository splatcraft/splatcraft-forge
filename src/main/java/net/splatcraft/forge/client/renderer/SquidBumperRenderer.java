package net.splatcraft.forge.client.renderer;

import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.client.layer.SquidBumperColorLayer;
import net.splatcraft.forge.client.model.SquidBumperModel;
import net.splatcraft.forge.entities.SquidBumperEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;

public class SquidBumperRenderer extends LivingRenderer<SquidBumperEntity, SquidBumperModel> //implements IEntityRenderer<LivingEntity, InkSquidModel>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Splatcraft.MODID, "textures/entity/squid_bumper_overlay.png");

    public SquidBumperRenderer(EntityRendererManager manager)
    {
        super(manager, new SquidBumperModel(), 0.5f);
        addLayer(new SquidBumperColorLayer(this));
    }

    @Override
    protected boolean shouldShowName(SquidBumperEntity entity)
    {
        return !entity.hasCustomName() && !(entity.getInkHealth() >= 20) || super.shouldShowName(entity) && (entity.shouldShowName() || entity == this.entityRenderDispatcher.crosshairPickEntity);
    }
    
    /*
    @Override
    public void render(SquidBumperEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn)
    {
        getEntityModel().render(entityIn, matrixStackIn, bufferIn.getBuffer(getEntityModel().getRenderType(TEXTURE)), packedLightIn);
    }
    */

    @Override
    public void render(SquidBumperEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn)
    {
        if (MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Pre<>(entityIn, this, partialTicks, matrixStackIn, bufferIn, packedLightIn)))
        {
            return;
        }
        matrixStackIn.pushPose();
        this.model.attackTime = this.getAttackAnim(entityIn, partialTicks);

        boolean shouldSit = entityIn.isPassenger() && entityIn.getVehicle() != null && entityIn.getVehicle().shouldRiderSit();
        this.model.riding = shouldSit;
        this.model.young = entityIn.isBaby();
        float f = MathHelper.lerp(partialTicks, entityIn.yBodyRotO, entityIn.yBodyRot);
        float f1 = MathHelper.lerp(partialTicks, entityIn.yHeadRotO, entityIn.yHeadRot);
        float f2 = f1 - f;
        if (shouldSit && entityIn.getVehicle() instanceof LivingEntity)
        {
            LivingEntity livingentity = (LivingEntity) entityIn.getVehicle();
            f = MathHelper.lerp(partialTicks, livingentity.yBodyRotO, livingentity.yBodyRot);
            f2 = f1 - f;
            float f3 = MathHelper.wrapDegrees(f2);
            if (f3 < -85.0F)
                f3 = -85.0F;

            if (f3 >= 85.0F)
                f3 = 85.0F;

            f = f1 - f3;
            if (f3 * f3 > 2500.0F)
                f += f3 * 0.2F;

            f2 = f1 - f;
        }

        float f6 = MathHelper.lerp(partialTicks, entityIn.xRotO, entityIn.xRot);
        if (entityIn.getPose() == Pose.SLEEPING)
        {
            Direction direction = entityIn.getBedOrientation();
            if (direction != null)
            {
                float f4 = entityIn.getEyeHeight(Pose.STANDING) - 0.1F;
                matrixStackIn.translate((float) -direction.getStepX() * f4, 0.0D, (float) -direction.getStepZ() * f4);
            }
        }

        float f7 = this.getBob(entityIn, partialTicks);
        this.setupRotations(entityIn, matrixStackIn, f7, f, partialTicks);
        matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
        this.scale(entityIn, matrixStackIn, partialTicks);
        matrixStackIn.translate(0.0D, -1.501F, 0.0D);
        float f8 = 0.0F;
        float f5 = 0.0F;
        if (!shouldSit && entityIn.isAlive())
        {
            f8 = MathHelper.lerp(partialTicks, entityIn.animationSpeedOld, entityIn.animationSpeed);
            f5 = entityIn.animationPosition - entityIn.animationSpeed * (1.0F - partialTicks);
            if (entityIn.isBaby())
                f5 *= 3.0F;
            if (f8 > 1.0F)
                f8 = 1.0F;
        }

        this.model.prepareMobModel(entityIn, f5, f8, partialTicks);
        this.model.setupAnim(entityIn, f5, f8, f7, f2, f6);
        Minecraft minecraft = Minecraft.getInstance();
        boolean flag = this.isBodyVisible(entityIn);
        boolean flag1 = !flag && minecraft.player != null && !entityIn.isInvisibleTo(minecraft.player);
        boolean flag2 = minecraft.shouldEntityAppearGlowing(entityIn);
        RenderType rendertype = this.getRenderType(entityIn, flag, flag1, flag2);
        if (rendertype != null)
        {
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(rendertype);
            int i = getPackedLightCoords(entityIn, this.getWhiteOverlayProgress(entityIn, partialTicks));


            this.model.renderBase(matrixStackIn, ivertexbuilder, packedLightIn, i, 1.0F, 1.0F, 1.0F, flag1 ? 0.15F : 1.0F);

            float scale = entityIn.getInkHealth() <= 0 ? (10f - Math.min((float) entityIn.getRespawnTime(), 10f)) / 10f : 1f;
            matrixStackIn.pushPose();
            matrixStackIn.scale(scale, scale, scale);
            this.model.renderBumper(matrixStackIn, ivertexbuilder, packedLightIn, i, 1.0F, 1.0F, 1.0F, flag1 ? 0.15F : 1.0F);
            matrixStackIn.popPose();

        }

        if (!entityIn.isSpectator())
        {
            for (LayerRenderer<SquidBumperEntity, SquidBumperModel> layerrenderer : this.layers)
                layerrenderer.render(matrixStackIn, bufferIn, packedLightIn, entityIn, f5, f8, partialTicks, f7, f2, f6);
        }

        matrixStackIn.popPose();
        net.minecraftforge.client.event.RenderNameplateEvent renderNameplateEvent = new net.minecraftforge.client.event.RenderNameplateEvent(entityIn, entityIn.getDisplayName(), this, matrixStackIn, bufferIn, packedLightIn, partialTicks);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(renderNameplateEvent);
        if (renderNameplateEvent.getResult() != net.minecraftforge.eventbus.api.Event.Result.DENY && (renderNameplateEvent.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW || this.shouldShowName(entityIn)))
            this.renderNameTag(entityIn, renderNameplateEvent.getContent(), matrixStackIn, bufferIn, packedLightIn);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Post<>(entityIn, this, partialTicks, matrixStackIn, bufferIn, packedLightIn));
    }

    @Override
    protected void renderNameTag(SquidBumperEntity entityIn, ITextComponent displayNameIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn)
    {
        if (entityIn.hasCustomName())
        {
            super.renderNameTag(entityIn, displayNameIn, matrixStackIn, bufferIn, packedLightIn);
        } else
        {
            float health = 20 - entityIn.getInkHealth();
            super.renderNameTag(entityIn, new StringTextComponent((health >= 20 ? TextFormatting.DARK_RED : "") + String.format("%.1f", health)), matrixStackIn, bufferIn, packedLightIn);

        }
    }

    @Override
    protected void setupRotations(SquidBumperEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks)
    {
        //matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F - rotationYaw));
        float punchTime = (float) (entityLiving.level.getGameTime() - entityLiving.punchCooldown) + partialTicks;
        float hurtTime = (float) (entityLiving.level.getGameTime() - entityLiving.hurtCooldown) + partialTicks;


        if (punchTime < 5.0F)
        {
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(MathHelper.sin(punchTime / 1.5F * (float) Math.PI) * 3.0F));
        }
        if (hurtTime < 5.0F)
        {
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.sin(hurtTime / 1.5F * (float) Math.PI) * 3.0F));
        }

    }

    @Override
    public ResourceLocation getTextureLocation(SquidBumperEntity entity)
    {
        return TEXTURE;
    }
}
