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

public class InkExplosionParticle  extends SpriteTexturedParticle
{
    private final IAnimatedSprite spriteProvider;

    public InkExplosionParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, InkExplosionParticleData data, IAnimatedSprite sprite)
    {
        super(world, x, y, z, motionX, motionY, motionZ);

        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;

        particleRed = Math.max(0.018f,(float) (data.getRed()) - 0.018f);
        particleGreen = Math.max(0.018f,(float) (data.getGreen()) - 0.018f);
        particleBlue = Math.max(0.018f,(float) (data.getBlue()) - 0.018f);

        this.particleScale = 0.33F * (this.rand.nextFloat() * 0.5F + 0.5F) * 2.0F * data.getScale();
        this.particleGravity = 0;
        this.maxAge = 6 + this.rand.nextInt(4);


        spriteProvider = sprite;
        this.selectSpriteWithAge(sprite);
    }

    @Override
    public void tick()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.age++ >= this.maxAge || this.world.getBlockState(new BlockPos(this.posX, this.posY, this.posZ)).getMaterial() == Material.WATER)
            this.setExpired();
        else
            this.selectSpriteWithAge(this.spriteProvider);
    }

    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }


    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<InkExplosionParticleData>
    {

        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite sprite) {
            this.spriteSet = sprite;
        }

        @Nullable
        @Override
        public Particle makeParticle(InkExplosionParticleData typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return  new InkExplosionParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn, this.spriteSet);
        }
    }

}
