package net.splatcraft.forge.client.particles;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class SquidSoulParticle extends SpriteTexturedParticle
{
    private final IAnimatedSprite spriteProvider;

    public SquidSoulParticle(ClientWorld level, double x, double y, double z, double motionX, double motionY, double motionZ, SquidSoulParticleData data, IAnimatedSprite sprite)
    {
        super(level, x, y, z, motionX, motionY, motionZ);

        rCol = Math.max(0.018f, data.getRed() - 0.018f);
        gCol = Math.max(0.018f, data.getGreen() - 0.018f);
        bCol = Math.max(0.018f, data.getBlue() - 0.018f);

        this.gravity = 0.15f;
        this.lifetime = 20;
        this.quadSize = 0.3f;
        hasPhysics = false;

        spriteProvider = sprite;
    }

    @Override
    public void tick()
    {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime)
        {
            this.remove();
        } else
        {
            this.yd += 0.04D * (double) this.gravity;
            this.move(0, this.yd, 0);
            this.yd *= 0.98F;
        }
    }

    @Override
    public IParticleRenderType getRenderType()
    {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void render(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks)
    {
        Vector3f lvt_4_1_ = renderInfo.getLookVector();
        float lvt_5_1_ = (float) (MathHelper.lerp(partialTicks, this.xo, this.x) - lvt_4_1_.x());
        float lvt_6_1_ = (float) (MathHelper.lerp(partialTicks, this.y, this.yo) - lvt_4_1_.y());
        float lvt_7_1_ = (float) (MathHelper.lerp(partialTicks, this.z, this.zo) - lvt_4_1_.z());
        Quaternion lvt_8_2_;
        if (this.roll == 0.0F)
        {
            lvt_8_2_ = renderInfo.rotation();
        } else
        {
            lvt_8_2_ = new Quaternion(renderInfo.rotation());
            float lvt_9_1_ = MathHelper.lerp(partialTicks, this.roll, this.oRoll);
            lvt_8_2_.mul(Vector3f.ZP.rotation(lvt_9_1_));
        }

        Vector3f lvt_9_2_ = new Vector3f(-1.0F, -1.0F, 0.0F);
        lvt_9_2_.transform(lvt_8_2_);
        Vector3f[] lvt_10_1_ = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float lvt_11_1_ = this.getQuadSize(partialTicks);

        for (int lvt_12_1_ = 0; lvt_12_1_ < 4; ++lvt_12_1_)
        {
            Vector3f lvt_13_1_ = lvt_10_1_[lvt_12_1_];
            lvt_13_1_.transform(lvt_8_2_);
            lvt_13_1_.mul(lvt_11_1_);
            lvt_13_1_.add(lvt_5_1_, lvt_6_1_, lvt_7_1_);
        }


        for (int i = 0; i < 3; i++)
        {
            float r = i == 1 ? this.rCol : 1;
            float g = i == 1 ? this.gCol : 1;
            float b = i == 1 ? this.bCol : 1;
            float a = this.alpha;
            if (age > lifetime - 5)
            {
                a = (1f - Math.max(0, age - lifetime + 5) - partialTicks) * 0.2f;
            }

            setSprite(spriteProvider.get(i + 1, 3));

            float lvt_12_2_ = this.getU0();
            float lvt_13_2_ = this.getU1();
            float lvt_14_1_ = this.getV0();
            float lvt_15_1_ = this.getV1();
            int lvt_16_1_ = 15728880;//this.getBrightnessForRender(partialTicks);

            buffer.vertex(lvt_10_1_[0].x(), lvt_10_1_[0].y(), lvt_10_1_[0].z()).uv(lvt_13_2_, lvt_15_1_).color(r, g, b, a).uv2(lvt_16_1_).endVertex();
            buffer.vertex(lvt_10_1_[1].x(), lvt_10_1_[1].y(), lvt_10_1_[1].z()).uv(lvt_13_2_, lvt_14_1_).color(r, g, b, a).uv2(lvt_16_1_).endVertex();
            buffer.vertex(lvt_10_1_[2].x(), lvt_10_1_[2].y(), lvt_10_1_[2].z()).uv(lvt_12_2_, lvt_14_1_).color(r, g, b, a).uv2(lvt_16_1_).endVertex();
            buffer.vertex(lvt_10_1_[3].x(), lvt_10_1_[3].y(), lvt_10_1_[3].z()).uv(lvt_12_2_, lvt_15_1_).color(r, g, b, a).uv2(lvt_16_1_).endVertex();
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
        public Particle createParticle(SquidSoulParticleData typeIn, ClientWorld levelIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            return new SquidSoulParticle(levelIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn, this.spriteSet);
        }
    }

}
