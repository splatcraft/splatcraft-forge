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

public class SquidSoulParticleData implements IParticleData
{
    public static final IDeserializer<SquidSoulParticleData> DESERIALIZER = new IDeserializer<SquidSoulParticleData>()
    {
        @Override
        public SquidSoulParticleData fromCommand(ParticleType<SquidSoulParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException
        {
            reader.expect(' ');
            return new SquidSoulParticleData(InkColorArgument.parseStatic(reader));
        }

        @Override
        public SquidSoulParticleData fromNetwork(ParticleType<SquidSoulParticleData> particleTypeIn, PacketBuffer buffer)
        {
            return new SquidSoulParticleData(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
        }
    };
    public static final Codec<SquidSoulParticleData> CODEC = RecordCodecBuilder.create(
            p_239803_0_ -> p_239803_0_.group(
                    Codec.FLOAT.fieldOf("r").forGetter(p_239807_0_ -> p_239807_0_.red),
                    Codec.FLOAT.fieldOf("g").forGetter(p_239806_0_ -> p_239806_0_.green),
                    Codec.FLOAT.fieldOf("b").forGetter(p_239805_0_ -> p_239805_0_.blue)
            ).apply(p_239803_0_, SquidSoulParticleData::new)
    );
    protected final float red;
    protected final float green;
    protected final float blue;

    public SquidSoulParticleData(int color)
    {
        this(ColorUtils.hexToRGB(color));
    }

    private SquidSoulParticleData(float[] rgb)
    {
        this(rgb[0], rgb[1], rgb[2]);
    }

    public SquidSoulParticleData(float red, float green, float blue)
    {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    @Override
    public ParticleType<?> getType()
    {
        return SplatcraftParticleTypes.SQUID_SOUL;
    }

    @Override
    public void writeToNetwork(PacketBuffer buffer)
    {
        buffer.writeFloat(red);
        buffer.writeFloat(green);
        buffer.writeFloat(blue);
    }

    @Override
    public String writeToString()
    {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f", Registry.PARTICLE_TYPE.getKey(this.getType()), this.red, this.green, this.blue);
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
}
