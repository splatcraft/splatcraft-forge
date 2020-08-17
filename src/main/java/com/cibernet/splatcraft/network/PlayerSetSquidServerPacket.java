package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.capabilities.playerinfo.IPlayerInfo;
import com.cibernet.splatcraft.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.network.base.PlayToServerPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

import java.util.UUID;

public class PlayerSetSquidServerPacket extends PlayToServerPacket
{
	private int squid = -1;
	UUID target;
	public PlayerSetSquidServerPacket(PlayerEntity player)
	{
		target = player.getUniqueID();
	}
	
	public PlayerSetSquidServerPacket(PlayerEntity player, boolean set)
	{
		squid = set ? 1 : 0;
		target = player.getUniqueID();
	}
	
	protected PlayerSetSquidServerPacket(UUID player, int squid)
	{
		this.squid = squid;
		this.target = player;
	}
	
	@Override
	public void encode(PacketBuffer buffer)
	{
		buffer.writeUniqueId(target);
		buffer.writeInt(squid);
	}
	
	public static PlayerSetSquidServerPacket decode(PacketBuffer buffer)
	{
		return new PlayerSetSquidServerPacket(buffer.readUniqueId(),buffer.readInt());
	}
	
	@Override
	public void execute(PlayerEntity player)
	{
		IPlayerInfo target = PlayerInfoCapability.get(player.world.getPlayerByUuid(this.target));
		
		if(squid == -1)
			squid = !target.isSquid() ? 1 : 0;
		target.setIsSquid(squid == 1 ? true : false);
		
		SplatcraftPacketHandler.sendToDim(new PlayerSetSquidClientPacket(this.target, squid), player.world);
	}
	
}
