package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.network.base.SplatcraftPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SplatcraftPacketHandler
{
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Splatcraft.MODID, "main"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals);
	private static int ID = 0;
	
	public static void registerMessages()
	{
		//INSTANCE.registerMessage(ID++, PlayerColorPacket.class, SplatcraftPacket::encode, PlayerColorPacket::decode, SplatcraftPacket::consume);
		registerMessage(UpdatePlayerInfoPacket.class, UpdatePlayerInfoPacket::decode);
		registerMessage(PlayerColorPacket.class, PlayerColorPacket::decode);
		registerMessage(PlayerSetSquidServerPacket.class, PlayerSetSquidServerPacket::decode);
		registerMessage(PlayerSetSquidClientPacket.class, PlayerSetSquidClientPacket::decode);
		registerMessage(UpdateBooleanGamerulesPacket.class, UpdateBooleanGamerulesPacket::decode);
		registerMessage(RequestPlayerInfoPacket.class, RequestPlayerInfoPacket::decode);
		registerMessage(SendColorScoresPacket.class, SendColorScoresPacket::decode);
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
	
	public static <MSG> void sendToDim(MSG message, RegistryKey<World> world)
	{
		INSTANCE.send(PacketDistributor.DIMENSION.with(() -> world), message);
	}
	
	public static <MSG> void sendToDim(MSG message, World world)
	{
		sendToDim(message, world.func_234923_W_());
	}
	
	public static <MSG> void sendToAll(MSG message)
	{
		INSTANCE.send(PacketDistributor.ALL.noArg(), message);
	}
	
	public static <MSG> void sendToServer(MSG message)
	{
		INSTANCE.sendToServer(message);
	}
}
