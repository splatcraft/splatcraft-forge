package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.capabilities.IPlayerInfo;
import com.cibernet.splatcraft.capabilities.PlayerInfoCapability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

import java.util.UUID;

public class PlayerSetSquidPacket extends SplatcraftPacket
{
	private int squid = -1;
	UUID target;
	EnumDirection direction = EnumDirection.PLAY_TO_SERVER;
	
	public PlayerSetSquidPacket(PlayerEntity player)
	{
		target = player.getUniqueID();
	}
	
	public PlayerSetSquidPacket(PlayerEntity player, boolean set)
	{
		squid = set ? 1 : 0;
		target = player.getUniqueID();
	}
	
	protected PlayerSetSquidPacket(EnumDirection direction, UUID player, int squid)
	{
		this.squid = squid;
		this.target = player;
		this.direction = direction;
	}
	
	@Override
	void encode(PacketBuffer buffer)
	{
		buffer.writeEnumValue(direction);
		buffer.writeUniqueId(target);
		buffer.writeInt(squid);
	}
	
	public static PlayerSetSquidPacket decode(PacketBuffer buffer)
	{
		return new PlayerSetSquidPacket(buffer.readEnumValue(EnumDirection.class), buffer.readUniqueId(),buffer.readInt());
	}
	
	@Override
	void execute(PlayerEntity player)
	{
		IPlayerInfo target = PlayerInfoCapability.get(player.world.getPlayerByUuid(this.target));
		
		if(squid == -1)
			squid = !target.isSquid() ? 1 : 0;
		target.setIsSquid(squid == 1 ? true : false);
		
		if(direction == EnumDirection.PLAY_TO_SERVER)
			SplatcraftPacketHandler.sendToDim(new PlayerSetSquidPacket(EnumDirection.PLAY_TO_CLIENT, this.target, squid), player.world);
	}
	
	@Override
	EnumDirection getDirection()
	{
		return direction;
	}
}
