package com.cibernet.splatcraft.client.particles;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class SquidSoulParticle extends SpriteTexturedParticle
{
    private final IAnimatedSprite spriteProvider;

    public SquidSoulParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, SquidSoulParticleData data, IAnimatedSprite sprite)
    {
        super(world, x, y, z, motionX, motionY, motionZ);

        particleRed = Math.max(0.018f, data.getRed() - 0.018f);
        particleGreen = Math.max(0.018f, data.getGreen() - 0.018f);
        particleBlue = Math.max(0.018f, data.getBlue() - 0.018f);

        this.particleGravity = 0.15f;
        this.maxAge = 20;
        this.particleScale = 0.3f;
        canCollide = false;

        spriteProvider = sprite;
    }

    @Override
    public void tick()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.age++ >= this.maxAge)
        {
            this.setExpired();
        } else
        {
            this.motionY += 0.04D * (double) this.particleGravity;
            this.move(0, this.motionY, 0);
            this.motionY *= 0.98F;
        }
    }

    @Override
    public IParticleRenderType getRenderType()
    {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void renderParticle(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks)
    {
        Vector3d lvt_4_1_ = renderInfo.getProjectedView();
        float lvt_5_1_ = (float) (MathHelper.lerp(partialTicks, this.prevPosX, this.posX) - lvt_4_1_.getX());
        float lvt_6_1_ = (float) (MathHelper.lerp(partialTicks, this.prevPosY, this.posY) - lvt_4_1_.getY());
        float lvt_7_1_ = (float) (MathHelper.lerp(partialTicks, this.prevPosZ, this.posZ) - lvt_4_1_.getZ());
        Quaternion lvt_8_2_;
        if (this.particleAngle == 0.0F)
        {
            lvt_8_2_ = renderInfo.getRotation();
        } else
        {
            lvt_8_2_ = new Quaternion(renderInfo.getRotation());
            float lvt_9_1_ = MathHelper.lerp(partialTicks, this.prevParticleAngle, this.particleAngle);
            lvt_8_2_.multiply(Vector3f.ZP.rotation(lvt_9_1_));
        }

        Vector3f lvt_9_2_ = new Vector3f(-1.0F, -1.0F, 0.0F);
        lvt_9_2_.transform(lvt_8_2_);
        Vector3f[] lvt_10_1_ = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float lvt_11_1_ = this.getScale(partialTicks);

        for (int lvt_12_1_ = 0; lvt_12_1_ < 4; ++lvt_12_1_)
        {
            Vector3f lvt_13_1_ = lvt_10_1_[lvt_12_1_];
            lvt_13_1_.transform(lvt_8_2_);
            lvt_13_1_.mul(lvt_11_1_);
            lvt_13_1_.add(lvt_5_1_, lvt_6_1_, lvt_7_1_);
        }


        for (int i = 0; i < 3; i++)
        {
            float r = i == 1 ? this.particleRed : 1;
            float g = i == 1 ? this.particleGreen : 1;
            float b = i == 1 ? this.particleBlue : 1;
            float a = this.particleAlpha;
            if (age > maxAge - 5)
            {
                a = (1f - Math.max(0, age - maxAge + 5) - partialTicks) * 0.2f;
            }

            setSprite(spriteProvider.get(i + 1, 3));

            float lvt_12_2_ = this.getMinU();
            float lvt_13_2_ = this.getMaxU();
            float lvt_14_1_ = this.getMinV();
            float lvt_15_1_ = this.getMaxV();
            int lvt_16_1_ = 15728880;//this.getBrightnessForRender(partialTicks);

            buffer.pos(lvt_10_1_[0].getX(), lvt_10_1_[0].getY(), lvt_10_1_[0].getZ()).tex(lvt_13_2_, lvt_15_1_).color(r, g, b, a).lightmap(lvt_16_1_).endVertex();
            buffer.pos(lvt_10_1_[1].getX(), lvt_10_1_[1].getY(), lvt_10_1_[1].getZ()).tex(lvt_13_2_, lvt_14_1_).color(r, g, b, a).lightmap(lvt_16_1_).endVertex();
            buffer.pos(lvt_10_1_[2].getX(), lvt_10_1_[2].getY(), lvt_10_1_[2].getZ()).tex(lvt_12_2_, lvt_14_1_).color(r, g, b, a).lightmap(lvt_16_1_).endVertex();
            buffer.pos(lvt_10_1_[3].getX(), lvt_10_1_[3].getY(), lvt_10_1_[3].getZ()).tex(lvt_12_2_, lvt_15_1_).color(r, g, b, a).lightmap(lvt_16_1_).endVertex();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<SquidSoulParticleData>
    {

        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite sprite)
        {
            this.spriteSet = sprite;
        }

        @Nullable
        @Override
        public Particle makeParticle(SquidSoulParticleData typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            return new SquidSoulParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn, this.spriteSet);
        }
    }

}
