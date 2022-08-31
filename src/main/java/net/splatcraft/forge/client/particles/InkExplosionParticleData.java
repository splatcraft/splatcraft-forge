package net.splatcraft.forge.client.particles;

import net.splatcraft.forge.commands.arguments.InkColorArgument;
import net.splatcraft.forge.registries.SplatcraftParticleTypes;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleType;

public class InkExplosionParticleData extends InkSplashParticleData
{
    public static final Codec<InkExplosionParticleData> CODEC = RecordCodecBuilder.create(
            p_239803_0_ -> p_239803_0_.group(
                    Codec.FLOAT.fieldOf("r").forGetter(p_239807_0_ -> p_239807_0_.red),
                    Codec.FLOAT.fieldOf("g").forGetter(p_239806_0_ -> p_239806_0_.green),
                    Codec.FLOAT.fieldOf("b").forGetter(p_239805_0_ -> p_239805_0_.blue),
                    Codec.FLOAT.fieldOf("scale").forGetter(p_239804_0_ -> p_239804_0_.scale)
            ).apply(p_239803_0_, InkExplosionParticleData::new)
    );
    @SuppressWarnings("deprecation")
    public static final IDeserializer<InkExplosionParticleData> DESERIALIZER = new IDeserializer<InkExplosionParticleData>()
    {
        @Override
        public InkExplosionParticleData fromCommand(ParticleType<InkExplosionParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException
        {
            reader.expect(' ');
            Integer color = InkColorArgument.parseStatic(reader);
            reader.expect(' ');
            return new InkExplosionParticleData(color, reader.readFloat());
        }

        @Override
        public InkExplosionParticleData fromNetwork(ParticleType<InkExplosionParticleData> particleTypeIn, PacketBuffer buffer)
        {
            return new InkExplosionParticleData(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
        }
    };

    public InkExplosionParticleData(Integer color, float scale)
    {
        super(color, scale);
    }

    public InkExplosionParticleData(float red, float green, float blue, float scale)
    {
        super(red, green, blue, scale);
    }

    @Override
    public ParticleType<?> getType()
    {
        return SplatcraftParticleTypes.INK_EXPLOSION;
    }
}
