package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.network.base.PlayToClientPacket;
import com.cibernet.splatcraft.network.base.SplatcraftPacket;
import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

import java.util.UUID;

public class PlayerColorPacket extends PlayToClientPacket
{
	private int color;
	UUID target;
	
	public PlayerColorPacket(UUID player, int color)
	{
		this.color = color;
		this.target = player;
	}
	
	public PlayerColorPacket(PlayerEntity player, int color) { this(player.getUniqueID(), color); }
	
	@Override
	public void encode(PacketBuffer buffer)
	{
		buffer.writeInt(color);
		buffer.writeString(target.toString());
	}
	
	public static PlayerColorPacket decode(PacketBuffer buffer)
	{
		int color = buffer.readInt();
		UUID player = UUID.fromString(buffer.readString());
		return new PlayerColorPacket(player, color);
	}
	
	@Override
	public void execute()
	{
		ColorUtils.setPlayerColor(Minecraft.getInstance().world.getPlayerByUuid(target), color, false);
	}
	
}
