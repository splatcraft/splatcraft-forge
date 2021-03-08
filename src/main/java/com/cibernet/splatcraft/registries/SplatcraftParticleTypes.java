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

    public static final ParticleType<InkSplashParticleData> INK = new ParticleType<InkSplashParticleData>(false, InkSplashParticleData.DESERIALIZER) {
        @Override
        public Codec<InkSplashParticleData> func_230522_e_() {
            return InkSplashParticleData.CODEC;
        }
    };

    @SubscribeEvent
    public static void registerFactories(ParticleFactoryRegisterEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        mc.particles.registerFactory(INK, InkSplashParticle.Factory::new);
    }

    @SubscribeEvent
    public static void registerParticles(RegistryEvent.Register<ParticleType<?>> event)
    {
        IForgeRegistry<ParticleType<?>> registry = event.getRegistry();

        registry.register(INK.setRegistryName("ink_splash"));
    }
}
