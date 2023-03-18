package net.splatcraft.forge.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.network.c2s.ChargeableReleasePacket;
import net.splatcraft.forge.network.c2s.CraftWeaponPacket;
import net.splatcraft.forge.network.c2s.DodgeRollPacket;
import net.splatcraft.forge.network.c2s.PlayerSetSquidServerPacket;
import net.splatcraft.forge.network.c2s.RequestPlayerInfoPacket;
import net.splatcraft.forge.network.c2s.SwapSlotWithOffhandPacket;
import net.splatcraft.forge.network.c2s.UpdateBlockColorPacket;
import net.splatcraft.forge.network.s2c.PlayerColorPacket;
import net.splatcraft.forge.network.s2c.PlayerSetSquidClientPacket;
import net.splatcraft.forge.network.s2c.SendScanTurfResultsPacket;
import net.splatcraft.forge.network.s2c.UpdateBooleanGamerulesPacket;
import net.splatcraft.forge.network.s2c.UpdateClientColorsPacket;
import net.splatcraft.forge.network.s2c.UpdateColorScoresPacket;
import net.splatcraft.forge.network.s2c.UpdateInkOverlayPacket;
import net.splatcraft.forge.network.s2c.UpdateIntGamerulesPacket;
import net.splatcraft.forge.network.s2c.UpdatePlayerInfoPacket;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SplatcraftPacketHandler
{
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Splatcraft.MODID, "main"),
            () -> Splatcraft.version,
            Splatcraft.version::equals,
            Splatcraft.version::equals);
    private static int ID = 0;

    public static void registerMessages()
    {
        //INSTANCE.registerMessage(ID++, PlayerColorPacket.class, SplatcraftPacket::encode, PlayerColorPacket::decode, SplatcraftPacket::consume);
        registerMessage(UpdatePlayerInfoPacket.class, UpdatePlayerInfoPacket::decode);
        registerMessage(PlayerColorPacket.class, PlayerColorPacket::decode);
        registerMessage(PlayerSetSquidServerPacket.class, PlayerSetSquidServerPacket::decode);
        registerMessage(PlayerSetSquidClientPacket.class, PlayerSetSquidClientPacket::decode);
        registerMessage(UpdateBooleanGamerulesPacket.class, UpdateBooleanGamerulesPacket::decode);
        registerMessage(UpdateIntGamerulesPacket.class, UpdateIntGamerulesPacket::decode);
        registerMessage(RequestPlayerInfoPacket.class, RequestPlayerInfoPacket::decode);
        registerMessage(SendScanTurfResultsPacket.class, SendScanTurfResultsPacket::decode);
        registerMessage(UpdateColorScoresPacket.class, UpdateColorScoresPacket::decode);
        registerMessage(UpdateBlockColorPacket.class, UpdateBlockColorPacket::decode);
        registerMessage(DodgeRollPacket.class, DodgeRollPacket::decode);
        registerMessage(CraftWeaponPacket.class, CraftWeaponPacket::decode);
        registerMessage(UpdateClientColorsPacket.class, UpdateClientColorsPacket::decode);
        registerMessage(UpdateInkOverlayPacket.class, UpdateInkOverlayPacket::decode);
        registerMessage(ChargeableReleasePacket.class, ChargeableReleasePacket::decode);
        registerMessage(SwapSlotWithOffhandPacket.class, SwapSlotWithOffhandPacket::decode);
    }

    private static <MSG extends SplatcraftPacket> void registerMessage(Class<MSG> messageType, Function<PacketBuffer, MSG> decoder)
    {
        registerMessage(messageType, SplatcraftPacket::encode, decoder, SplatcraftPacket::consume);
    }

    private static <MSG> void registerMessage(Class<MSG> messageType, BiConsumer<MSG, PacketBuffer> encoder, Function<PacketBuffer, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer)
    {
        INSTANCE.registerMessage(ID++, messageType, encoder, decoder, messageConsumer);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayerEntity player)
    {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToDim(MSG message, RegistryKey<World> level) {
        INSTANCE.send(PacketDistributor.DIMENSION.with(() -> level), message);
    }

    public static <MSG> void sendToTrackers(MSG message, Entity trackedEntity) {
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> trackedEntity), message);
    }

    public static <MSG> void sendToTrackersAndSelf(MSG message, Entity trackedEntity) {
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> trackedEntity), message);
    }

    public static <MSG> void sendToAll(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }
}
