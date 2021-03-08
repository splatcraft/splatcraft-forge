package com.cibernet.splatcraft.client.particles;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class InkSplashParticle extends SpriteTexturedParticle
{
    private final IAnimatedSprite spriteProvider;

    public InkSplashParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, InkSplashParticleData data, IAnimatedSprite sprite)
    {
        super(world, x, y, z, motionX, motionY, motionZ);

        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;

        particleRed = Math.max(0.018f,(float) (data.getRed()) - 0.018f);
        particleGreen = Math.max(0.018f,(float) (data.getGreen()) - 0.018f);
        particleBlue = Math.max(0.018f,(float) (data.getBlue()) - 0.018f);

        this.particleScale = 0.33F * (this.rand.nextFloat() * 0.5F + 0.5F) * 2.0F * data.getScale();
        this.particleGravity = 0;//0.1f;
        this.maxAge = 5;

        spriteProvider = sprite;
        this.selectSpriteWithAge(sprite);
    }

    @Override
    public void tick()
    {
        super.tick();
        if(particleGravity > 0)
            this.motionY -= 0.004D + 0.04D * (double)this.particleGravity;
        if (this.world.getBlockState(new BlockPos(this.posX, this.posY, this.posZ)).getMaterial() == Material.WATER)
        {
            this.setExpired();
        }
        else selectSpriteWithAge(spriteProvider);
    }

    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void renderParticle(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks)
    {
        if(!(Minecraft.getInstance().gameSettings.getPointOfView().equals(PointOfView.FIRST_PERSON) && getDistanceSq(Minecraft.getInstance().player, posX, posY, posZ) < 0.2))
            super.renderParticle(buffer, renderInfo, partialTicks);
    }


    protected double getDistanceSq(Entity entity, double x, double y, double z) {
        double d0 = entity.getPosX() - x;
        double d1 = entity.getPosYEye() - y;
        double d2 = entity.getPosZ() - z;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<InkSplashParticleData>
    {

        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite sprite) {
            this.spriteSet = sprite;
        }

        @Nullable
        @Override
        public Particle makeParticle(InkSplashParticleData typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return  new InkSplashParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn, this.spriteSet);
        }
    }

}


