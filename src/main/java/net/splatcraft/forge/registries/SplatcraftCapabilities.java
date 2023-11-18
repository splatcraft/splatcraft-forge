package net.splatcraft.forge.registries;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.data.capabilities.inkoverlay.InkOverlayCapability;
import net.splatcraft.forge.data.capabilities.inkoverlay.InkOverlayInfo;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfo;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.data.capabilities.saveinfo.SaveInfo;
import net.splatcraft.forge.data.capabilities.saveinfo.SaveInfoCapability;
import net.splatcraft.forge.data.capabilities.worldink.WorldInkCapability;

@Mod.EventBusSubscriber(modid = Splatcraft.MODID)
public class SplatcraftCapabilities
{
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event)
    {
        event.register(PlayerInfo.class);
        event.register(InkOverlayInfo.class);
        event.register(SaveInfo.class);
    }

    @SubscribeEvent
    public static void attachEntityCapabilities(final AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof Player)
            event.addCapability(new ResourceLocation(Splatcraft.MODID, "player_info"), new PlayerInfoCapability());
        event.addCapability(new ResourceLocation(Splatcraft.MODID, "ink_overlay"), new InkOverlayCapability());

    }

    @SubscribeEvent
    public static void attachWorldCapabilities(final AttachCapabilitiesEvent<Level> event)
    {
        if (event.getObject().dimension() == Level.OVERWORLD)
        {
            event.addCapability(new ResourceLocation(Splatcraft.MODID, "save_info"), new SaveInfoCapability());
        }
    }

    @SubscribeEvent
    public static void attachChunkCapabilities(final AttachCapabilitiesEvent<LevelChunk> event)
    {
        event.addCapability(new ResourceLocation(Splatcraft.MODID, "world_ink"), new WorldInkCapability());
    }
}
