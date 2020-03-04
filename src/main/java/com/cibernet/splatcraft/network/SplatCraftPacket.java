package com.cibernet.splatcraft.network;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public abstract class SplatCraftPacket implements IMessage
{
	private boolean messageValid = false;
	
	public abstract Side getPacketSide();
	void process(SplatCraftPacket message, MessageContext ctx) {};
	
	
	public static class Handler implements IMessageHandler<SplatCraftPacket, IMessage>
	{
		
		@Override
		public IMessage onMessage(SplatCraftPacket message, MessageContext ctx)
		{
			if(!message.messageValid)
				return null;
			if(message.getPacketSide() == Side.CLIENT)
			{
				if(ctx.side != Side.CLIENT)
					return null;
				Minecraft.getMinecraft().addScheduledTask(() -> message.process(message, ctx));
			}
			else if(message.getPacketSide() == Side.SERVER)
			{
				if(ctx.side != Side.SERVER)
					return null;
				FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> message.process(message, ctx));
			}
			return null;
			
		}
	}
}
