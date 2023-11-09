package net.splatcraft.forge.client.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.splatcraft.forge.commands.arguments.InkColorArgument;
import net.splatcraft.forge.registries.SplatcraftParticleTypes;
import net.splatcraft.forge.util.ColorUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class InkTerrainParticleData implements ParticleOptions
{

    public static final Deserializer<InkTerrainParticleData> DESERIALIZER = new Deserializer<>() {
        @Override
        public InkTerrainParticleData fromCommand(ParticleType<InkTerrainParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            return new InkTerrainParticleData(InkColorArgument.parseStatic(reader));
        }

        @Override
        public InkTerrainParticleData fromNetwork(ParticleType<InkTerrainParticleData> particleTypeIn, FriendlyByteBuf buffer) {
            return new InkTerrainParticleData(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
        }
    };
    public static final Codec<InkTerrainParticleData> CODEC = RecordCodecBuilder.create(
            p_239803_0_ -> p_239803_0_.group(
                    Codec.FLOAT.fieldOf("r").forGetter(p_239807_0_ -> p_239807_0_.red),
                    Codec.FLOAT.fieldOf("g").forGetter(p_239806_0_ -> p_239806_0_.green),
                    Codec.FLOAT.fieldOf("b").forGetter(p_239805_0_ -> p_239805_0_.blue)
            ).apply(p_239803_0_, InkTerrainParticleData::new)
    );
    protected final float red;
    protected final float green;
    protected final float blue;

    public InkTerrainParticleData(int color)
    {
        this(ColorUtils.hexToRGB(color));
    }

    private InkTerrainParticleData(float[] rgb)
    {
        this(rgb[0], rgb[1], rgb[2]);
    }

    public InkTerrainParticleData(float red, float green, float blue)
    {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    @Override
    public ParticleType<?> getType()
    {
        return SplatcraftParticleTypes.INK_TERRAIN;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer)
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
