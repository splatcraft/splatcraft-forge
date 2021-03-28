package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.Splatcraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SplatcraftSounds
{

    private static final List<SoundEvent> sounds = new ArrayList<>();

    public static SoundEvent squidTransform;
    public static SoundEvent squidRevert;
    public static SoundEvent inkSubmerge;
    public static SoundEvent inkSurface;
    public static SoundEvent noInkMain;
    public static SoundEvent noInkSub;
    public static SoundEvent shooterShot;
    public static SoundEvent blasterShot;
    public static SoundEvent blasterExplosion;
    public static SoundEvent rollerFling;
    public static SoundEvent rollerRoll;
    public static SoundEvent brushFling;
    public static SoundEvent brushRoll;
    public static SoundEvent chargerCharge;
    public static SoundEvent chargerReady;
    public static SoundEvent chargerShot;
    public static SoundEvent dualieShot;
    public static SoundEvent dualieDodge;
    public static SoundEvent slosherShot;
    public static SoundEvent subThrow;
    public static SoundEvent subDetonating;
    public static SoundEvent subDetonate;
    public static SoundEvent remoteUse;
    public static SoundEvent powerEggCanOpen;

    public static void initSounds()
    {
        squidTransform = createSoundEvent("squid_transform");
        squidRevert = createSoundEvent("squid_revert");
        inkSubmerge = createSoundEvent("ink_submerge");
        inkSurface = createSoundEvent("ink_surface");
        noInkMain = createSoundEvent("no_ink");
        noInkSub = createSoundEvent("no_ink_sub");
        shooterShot = createSoundEvent("shooter_firing");
        blasterShot = createSoundEvent("blaster_firing");
        blasterExplosion = createSoundEvent("blaster_explosion");
        rollerFling = createSoundEvent("roller_fling");
        rollerRoll = createSoundEvent("roller_roll");
        brushFling = createSoundEvent("brush_fling");
        brushRoll = createSoundEvent("brush_roll");
        chargerCharge = createSoundEvent("charger_charge");
        chargerReady = createSoundEvent("charger_ready");
        chargerShot = createSoundEvent("charger_shot");
        dualieShot = createSoundEvent("dualie_firing");
        dualieDodge = createSoundEvent("dualie_dodge");
        slosherShot = createSoundEvent("slosher_shot");
        subThrow = createSoundEvent("sub_throw");
        subDetonating = createSoundEvent("sub_detonating");
        subDetonate = createSoundEvent("sub_detonate");
        remoteUse = createSoundEvent("remote_use");
        powerEggCanOpen = createSoundEvent("power_egg_can_open");
    }

    private static SoundEvent createSoundEvent(String id)
    {
        ResourceLocation loc = new ResourceLocation(Splatcraft.MODID, id);
        SoundEvent sound = new SoundEvent(loc).setRegistryName(loc);
        sounds.add(sound);
        return sound;
    }

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event)
    {
        initSounds();

        IForgeRegistry<SoundEvent> registry = event.getRegistry();
        for (SoundEvent sound : sounds)
        {
            registry.register(sound);
        }
    }
}
