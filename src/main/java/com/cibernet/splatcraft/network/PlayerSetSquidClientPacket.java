package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.capabilities.playerinfo.IPlayerInfo;
import com.cibernet.splatcraft.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.network.base.PlayToClientPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

import java.util.UUID;

public class PlayerSetSquidClientPacket extends PlayToClientPacket
{
	private int squid = -1;
	UUID target;
	public PlayerSetSquidClientPacket(PlayerEntity player)
	{
		target = player.getUniqueID();
	}
	
	public PlayerSetSquidClientPacket(PlayerEntity player, boolean set)
	{
		squid = set ? 1 : 0;
		target = player.getUniqueID();
	}
	
	protected PlayerSetSquidClientPacket(UUID player, int squid)
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
	
	public static PlayerSetSquidClientPacket decode(PacketBuffer buffer)
	{
		return new PlayerSetSquidClientPacket(buffer.readUniqueId(),buffer.readInt());
	}
	
	@Override
	public void execute()
	{
		IPlayerInfo target = PlayerInfoCapability.get(Minecraft.getInstance().world.getPlayerByUuid(this.target));
		
		if(squid == -1)
			squid = !target.isSquid() ? 1 : 0;
		target.setIsSquid(squid == 1 ? true : false);
		
	}
	
}
