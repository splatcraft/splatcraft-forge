package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.capabilities.PlayerInfoCapability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

import java.util.UUID;

public class RequestPlayerInfoPacket extends SplatcraftPacket
{
	UUID target;
	
	public RequestPlayerInfoPacket(PlayerEntity target)
	{
		this.target = target.getUniqueID();
	}
	
	private RequestPlayerInfoPacket(UUID target)
	{
		this.target = target;
	}
	
	public static RequestPlayerInfoPacket decode(PacketBuffer buffer)
	{
		return new RequestPlayerInfoPacket(buffer.readUniqueId());
	}
	
	@Override
	void encode(PacketBuffer buffer)
	{
		buffer.writeUniqueId(target);
	}
	
	@Override
	void execute(PlayerEntity player)
	{
		ServerPlayerEntity target = (ServerPlayerEntity) player.world.getPlayerByUuid(this.target);
		SplatcraftPacketHandler.sendToPlayer(new UpdatePlayerInfoPacket(target), (ServerPlayerEntity) player);
	}
	
	@Override
	EnumDirection getDirection()
	{
		return EnumDirection.PLAY_TO_SERVER;
	}
}
