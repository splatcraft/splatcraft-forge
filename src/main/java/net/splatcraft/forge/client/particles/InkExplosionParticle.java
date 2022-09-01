package net.splatcraft.forge.client.particles;

import net.minecraft.block.material.Material;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class InkExplosionParticle extends SpriteTexturedParticle
{
    private final IAnimatedSprite spriteProvider;

    public InkExplosionParticle(ClientWorld level, double x, double y, double z, double motionX, double motionY, double motionZ, InkExplosionParticleData data, IAnimatedSprite sprite)
    {
        super(level, x, y, z, motionX, motionY, motionZ);

        this.xd = motionX;
        this.yd = motionY;
        this.zd = motionZ;

        rCol = Math.max(0.018f, data.getRed() - 0.018f);
        gCol = Math.max(0.018f, data.getGreen() - 0.018f);
        bCol = Math.max(0.018f, data.getBlue() - 0.018f);

        this.quadSize = 0.33F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F * data.getScale();
        this.gravity = 0;
        this.lifetime = 6 + this.random.nextInt(4);


        spriteProvider = sprite;
        this.setSpriteFromAge(sprite);
    }

    @Override
    public void tick()
    {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime || this.level.getBlockState(new BlockPos(this.x, this.y, this.z)).getMaterial() == Material.WATER)
        {
            this.remove();
        } else
        {
            this.setSpriteFromAge(this.spriteProvider);
        }
    }

    @Override
    public IParticleRenderType getRenderType()
    {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }


    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<InkExplosionParticleData>
    {

        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite sprite)
        {
            this.spriteSet = sprite;
        }

        @Nullable
        @Override
        public Particle createParticle(InkExplosionParticleData typeIn, ClientWorld levelIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            return new InkExplosionParticle(levelIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn, this.spriteSet);
        }
    }

}
