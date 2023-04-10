package net.splatcraft.forge.client.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.splatcraft.forge.commands.arguments.InkColorArgument;
import net.splatcraft.forge.registries.SplatcraftParticleTypes;
import net.splatcraft.forge.util.ColorUtils;

import java.util.Locale;

public class InkSplashParticleData implements ParticleOptions
{
    @SuppressWarnings("deprecation")
    public static final Deserializer<InkSplashParticleData> DESERIALIZER = new Deserializer<InkSplashParticleData>()
    {
        @Override
        public InkSplashParticleData fromCommand(ParticleType<InkSplashParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException
        {
            reader.expect(' ');
            Integer color = InkColorArgument.parseStatic(reader);
            reader.expect(' ');
            return new InkSplashParticleData(color, reader.readFloat());
        }

        @Override
        public InkSplashParticleData fromNetwork(ParticleType<InkSplashParticleData> particleTypeIn, FriendlyByteBuf buffer)
        {
            return new InkSplashParticleData(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
        }
    };
    public static final Codec<InkSplashParticleData> CODEC = RecordCodecBuilder.create(
            p_239803_0_ -> p_239803_0_.group(
                    Codec.FLOAT.fieldOf("r").forGetter(p_239807_0_ -> p_239807_0_.red),
                    Codec.FLOAT.fieldOf("g").forGetter(p_239806_0_ -> p_239806_0_.green),
                    Codec.FLOAT.fieldOf("b").forGetter(p_239805_0_ -> p_239805_0_.blue),
                    Codec.FLOAT.fieldOf("scale").forGetter(p_239804_0_ -> p_239804_0_.scale)
            ).apply(p_239803_0_, InkSplashParticleData::new)
    );
    protected final float red;
    protected final float green;
    protected final float blue;
    protected final float scale;

    public InkSplashParticleData(Integer color, float scale)
    {
        this(ColorUtils.hexToRGB(color), scale);
    }

    private InkSplashParticleData(float[] rgb, float scale)
    {
        this(rgb[0], rgb[1], rgb[2], scale);
    }

    public InkSplashParticleData(float red, float green, float blue, float scale)
    {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.scale = scale;
    }

    @Override
    public ParticleType<?> getType()
    {
        return SplatcraftParticleTypes.INK_SPLASH;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer)
    {
        buffer.writeFloat(red);
        buffer.writeFloat(green);
        buffer.writeFloat(blue);
        buffer.writeFloat(scale);
    }

    @Override
    public String writeToString()
    {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", Registry.PARTICLE_TYPE.getKey(this.getType()), this.red, this.green, this.blue, this.scale);
    }

    @OnlyIn(Dist.CLIENT)
    public float getRed()
    {
        return this.red;
    }

    @OnlyIn(Dist.CLIENT)
    public float getGreen()
    {
        return this.green;
    }

    @OnlyIn(Dist.CLIENT)
    public float getBlue()
    {
        return this.blue;
    }

    @OnlyIn(Dist.CLIENT)
    public float getScale()
    {
        return this.scale;
    }
}
