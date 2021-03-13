package com.cibernet.splatcraft.client.particles;

import com.cibernet.splatcraft.commands.arguments.InkColorArgument;
import com.cibernet.splatcraft.registries.SplatcraftParticleTypes;
import com.cibernet.splatcraft.util.ColorUtils;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Locale;

public class InkSplashParticleData implements IParticleData
{
    protected final float red;
    protected final float green;
    protected final float blue;
    protected final float scale;


    public static final IDeserializer<InkSplashParticleData> DESERIALIZER = new IDeserializer<InkSplashParticleData>() {
        public InkSplashParticleData deserialize(ParticleType<InkSplashParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException
        {
            reader.expect(' ');
            int color = InkColorArgument.parseStatic(reader);
            reader.expect(' ');
            return new InkSplashParticleData(color, reader.readFloat());
        }

        public InkSplashParticleData read(ParticleType<InkSplashParticleData> particleTypeIn, PacketBuffer buffer)
        {
            return new InkSplashParticleData(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
        }
    };

    public static final Codec<InkSplashParticleData> CODEC = RecordCodecBuilder.create((p_239803_0_) -> {
        return p_239803_0_.group(Codec.FLOAT.fieldOf("r").forGetter((p_239807_0_) -> {
            return p_239807_0_.red;
        }), Codec.FLOAT.fieldOf("g").forGetter((p_239806_0_) -> {
            return p_239806_0_.green;
        }), Codec.FLOAT.fieldOf("b").forGetter((p_239805_0_) -> {
            return p_239805_0_.blue;
        }), Codec.FLOAT.fieldOf("scale").forGetter((p_239804_0_) -> {
            return p_239804_0_.scale;
        })).apply(p_239803_0_, InkSplashParticleData::new);
    });

    public InkSplashParticleData(int color, float scale) {
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
    public ParticleType<?> getType() {
        return SplatcraftParticleTypes.INK_SPLASH;
    }

    @Override
    public void write(PacketBuffer buffer)
    {
        buffer.writeFloat(red);
        buffer.writeFloat(green);
        buffer.writeFloat(blue);
        buffer.writeFloat(scale);
    }

    public String getParameters() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", Registry.PARTICLE_TYPE.getKey(this.getType()), this.red, this.green, this.blue, this.scale);
    }
    @OnlyIn(Dist.CLIENT)
    public float getRed() {
        return this.red;
    }

    @OnlyIn(Dist.CLIENT)
    public float getGreen() {
        return this.green;
    }

    @OnlyIn(Dist.CLIENT)
    public float getBlue() {
        return this.blue;
    }

    @OnlyIn(Dist.CLIENT)
    public float getScale() {
        return this.scale;
    }
}
