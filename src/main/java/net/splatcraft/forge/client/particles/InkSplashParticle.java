package net.splatcraft.forge.client.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class InkSplashParticle extends TextureSheetParticle
{
    private final SpriteSet spriteProvider;

    public InkSplashParticle(ClientLevel level, double x, double y, double z, double motionX, double motionY, double motionZ, InkSplashParticleData data, SpriteSet sprite)
    {
        super(level, x, y, z, motionX, motionY, motionZ);

        this.xd = motionX;
        this.yd = motionY;
        this.zd = motionZ;

        rCol = Math.max(0.018f, data.getRed() - 0.018f);
        gCol = Math.max(0.018f, data.getGreen() - 0.018f);
        bCol = Math.max(0.018f, data.getBlue() - 0.018f);

        this.quadSize = 0.33F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F * data.getScale();
        this.gravity = 0;//0.1f;
        this.lifetime = 5;

        spriteProvider = sprite;
        this.setSpriteFromAge(sprite);
    }

    @Override
    public void tick()
    {
        super.tick();
        if (gravity > 0)
        {
            this.yd -= 0.004D + 0.04D * (double) this.gravity;
        }
        if (this.level.getBlockState(new BlockPos(this.x, this.y, this.z)).getMaterial() == Material.WATER)
        {
            this.remove();
        } else
        {
            setSpriteFromAge(spriteProvider);
        }
    }

    @Override
    public ParticleRenderType getRenderType()
    {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks)
    {
        if (!(Minecraft.getInstance().options.getCameraType().equals(CameraType.FIRST_PERSON) && distanceToSqr(Minecraft.getInstance().player, x, y, z) < 0.2))
        {
            super.render(buffer, renderInfo, partialTicks);
        }
    }


    protected double distanceToSqr(Entity entity, double x, double y, double z)
    {
        double d0 = entity.getX() - x;
        double d1 = entity.getEyeY() - y;
        double d2 = entity.getZ() - z;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<InkSplashParticleData>
    {

        private final SpriteSet spriteSet;

        public Factory(SpriteSet sprite)
        {
            this.spriteSet = sprite;
        }

        @Nullable
        @Override
        public Particle createParticle(InkSplashParticleData typeIn, ClientLevel levelIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            return new InkSplashParticle(levelIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn, this.spriteSet);
        }
    }

}
