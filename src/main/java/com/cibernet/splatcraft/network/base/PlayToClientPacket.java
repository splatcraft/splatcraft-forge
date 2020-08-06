package com.cibernet.splatcraft.network.base;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class PlayToClientPacket extends SplatcraftPacket
{
	@Override
	public void consume(Supplier<NetworkEvent.Context> ctx)
	{
		if(ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
			ctx.get().enqueueWork(() -> this.execute());
		ctx.get().setPacketHandled(true);
	}
	
	public abstract void execute();
}
