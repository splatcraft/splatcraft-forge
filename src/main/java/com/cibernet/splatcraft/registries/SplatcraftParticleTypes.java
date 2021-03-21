package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.client.particles.*;
import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SplatcraftParticleTypes
{

    public static final ParticleType<InkSplashParticleData> INK_SPLASH = new ParticleType<InkSplashParticleData>(false, InkSplashParticleData.DESERIALIZER)
    {
        @Override
        public Codec<InkSplashParticleData> func_230522_e_()
        {
            return InkSplashParticleData.CODEC;
        }
    };
    public static final ParticleType<InkExplosionParticleData> INK_EXPLOSION = new ParticleType<InkExplosionParticleData>(false, InkExplosionParticleData.DESERIALIZER)
    {
        @Override
        public Codec<InkExplosionParticleData> func_230522_e_()
        {
            return InkExplosionParticleData.CODEC;
        }
    };
    public static final ParticleType<SquidSoulParticleData> SQUID_SOUL = new ParticleType<SquidSoulParticleData>(false, SquidSoulParticleData.DESERIALIZER)
    {
        @Override
        public Codec<SquidSoulParticleData> func_230522_e_()
        {
            return SquidSoulParticleData.CODEC;
        }
    };

    @SubscribeEvent
    public static void registerFactories(ParticleFactoryRegisterEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        mc.particles.registerFactory(INK_SPLASH, InkSplashParticle.Factory::new);
        mc.particles.registerFactory(INK_EXPLOSION, InkExplosionParticle.Factory::new);
        mc.particles.registerFactory(SQUID_SOUL, SquidSoulParticle.Factory::new);
    }

    @SubscribeEvent
    public static void registerParticles(RegistryEvent.Register<ParticleType<?>> event)
    {
        IForgeRegistry<ParticleType<?>> registry = event.getRegistry();

        registry.register(INK_SPLASH.setRegistryName("ink_splash"));
        registry.register(INK_EXPLOSION.setRegistryName("ink_explosion"));
        registry.register(SQUID_SOUL.setRegistryName("squid_soul"));
    }
}
