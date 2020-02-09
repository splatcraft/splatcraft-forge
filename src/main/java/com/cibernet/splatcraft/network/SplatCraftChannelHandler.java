package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.network.SplatCraftPacket.Type;
import com.cibernet.splatcraft.proxy.ClientProxy;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLIndexedMessageToMessageCodec;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.util.EnumMap;

public class SplatCraftChannelHandler extends FMLIndexedMessageToMessageCodec<SplatCraftPacket>
{
	
	public static SplatCraftChannelHandler instance = new SplatCraftChannelHandler();
	public static EnumMap<Side, FMLEmbeddedChannel> channels;
	public SplatCraftChannelHandler() {
		for(Type type : Type.values())
			addDiscriminator(type.ordinal(), type.packetType);
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, SplatCraftPacket msg, ByteBuf target) throws Exception
	{
		target.writeBytes(msg.data);
	}
	
	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf source, SplatCraftPacket msg)
	{
		msg.consumePacket(source);
	}
	
	private static class SplatCraftPacketHandler extends SimpleChannelInboundHandler<SplatCraftPacket>
	{
		private final Side side;
		private SplatCraftPacketHandler(Side side)
		{
			this.side = side;
		}
		@Override
		protected void channelRead0(ChannelHandlerContext ctx, SplatCraftPacket msg) throws Exception
		{
			switch (side)
			{
				case CLIENT:
					
					ClientProxy.addScheduledTask(() -> msg.execute(ClientProxy.getClientPlayer()));
					
					break;
				case SERVER:
					INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
					EntityPlayerMP player = ((NetHandlerPlayServer) netHandler).player;
					player.getServerWorld().addScheduledTask(() -> msg.execute(player));
					break;
			}
		}
	}
	
	public static void setupChannel()
	{
		if(channels == null)
		{
			channels = NetworkRegistry.INSTANCE.newChannel("MinestuckUniverse", SplatCraftChannelHandler.instance);
			String targetName = channels.get(Side.CLIENT).findChannelHandlerNameForType(SplatCraftChannelHandler.class);
			channels.get(Side.CLIENT).pipeline().addAfter(targetName, "SplatCraftPacketHandler", new SplatCraftChannelHandler.SplatCraftPacketHandler(Side.CLIENT));
			targetName = channels.get(Side.SERVER).findChannelHandlerNameForType(SplatCraftChannelHandler.class);	//Not sure if this is necessary
			channels.get(Side.SERVER).pipeline().addAfter(targetName, "SplatCraftPacketHandler", new SplatCraftChannelHandler.SplatCraftPacketHandler(Side.SERVER));
		}
	}
	
	public static void sendToServer(SplatCraftPacket packet)
	{
		channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
		channels.get(Side.CLIENT).writeOutbound(packet);
	}
	
	public static void sendToPlayer(SplatCraftPacket packet, EntityPlayer player)
	{
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
		channels.get(Side.SERVER).writeOutbound(packet);
	}
	
	public static void sendToAllPlayers(SplatCraftPacket packet)
	{
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
		channels.get(Side.SERVER).writeOutbound(packet);
	}
}
