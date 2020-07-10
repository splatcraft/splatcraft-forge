package com.cibernet.splatcraft.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class SplatcraftPacket
{
	abstract void encode(PacketBuffer buffer);
	
	void consume(Supplier<NetworkEvent.Context> ctx)
	{
		if(getDirection() == EnumDirection.PLAY_TO_CLIENT || getDirection() == EnumDirection.BOTH)
		{
			if(ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
				ctx.get().enqueueWork(() -> this.execute(Minecraft.getInstance().player));
			
		}
		if(getDirection() == EnumDirection.PLAY_TO_SERVER || getDirection() == EnumDirection.BOTH)
			if(ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER)
				ctx.get().enqueueWork(() -> this.execute(ctx.get().getSender()));
		
		ctx.get().setPacketHandled(true);
	}
	
	abstract void execute(PlayerEntity player);
	
	abstract EnumDirection getDirection();
	
	enum EnumDirection
	{
		NONE,
		PLAY_TO_CLIENT,
		PLAY_TO_SERVER,
		BOTH
	}
}
