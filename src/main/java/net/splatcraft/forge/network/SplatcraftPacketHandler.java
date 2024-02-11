package net.splatcraft.forge.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.network.c2s.*;
import net.splatcraft.forge.network.s2c.*;

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
        registerMessage(PlayerSetSquidC2SPacket.class, PlayerSetSquidC2SPacket::decode);
        registerMessage(PlayerSetSquidS2CPacket.class, PlayerSetSquidS2CPacket::decode);
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
        registerMessage(ReleaseChargePacket.class, ReleaseChargePacket::decode);
        registerMessage(UpdateChargeStatePacket.class, UpdateChargeStatePacket::decode);
        registerMessage(SwapSlotWithOffhandPacket.class, SwapSlotWithOffhandPacket::decode);
        registerMessage(UpdateStageListPacket.class, UpdateStageListPacket::decode);
        registerMessage(UpdateWeaponSettingsPacket.class, UpdateWeaponSettingsPacket::decode);
        registerMessage(SendPlayerOverlayPacket.class, SendPlayerOverlayPacket::decode);
        registerMessage(ReceivePlayerOverlayPacket.class, ReceivePlayerOverlayPacket::decode);
        registerMessage(UpdateInkPacket.class, UpdateInkPacket::decode);
        registerMessage(WatchInkPacket.class, WatchInkPacket::decode);
        registerMessage(SendJumpLureDataPacket.class, SendJumpLureDataPacket::decode);
        registerMessage(UseJumpLurePacket.class, UseJumpLurePacket::decode);
        registerMessage(SuperJumpToStagePacket.class, SuperJumpToStagePacket::decode);
        registerMessage(SendStageWarpDataToPadPacket.class, SendStageWarpDataToPadPacket::decode);
        registerMessage(RequestUpdateStageSpawnPadsPacket.class, RequestUpdateStageSpawnPadsPacket::decode);

    }

    private static <MSG extends SplatcraftPacket> void registerMessage(Class<MSG> messageType, Function<FriendlyByteBuf, MSG> decoder)
    {
        registerMessage(messageType, SplatcraftPacket::encode, decoder, SplatcraftPacket::consume);
    }

    private static <MSG extends SplatcraftPacket> void registerMessage(Class<MSG> messageType, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer)
    {
        INSTANCE.registerMessage(ID++, messageType, encoder, decoder, messageConsumer);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player)
    {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToDim(MSG message, ResourceKey<Level> level) {
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
