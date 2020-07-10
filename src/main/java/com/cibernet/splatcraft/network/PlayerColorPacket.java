package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

import static com.cibernet.splatcraft.network.SplatcraftPacket.EnumDirection.PLAY_TO_CLIENT;

public class PlayerColorPacket extends SplatcraftPacket
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
	public void execute(PlayerEntity player)
	{
		ColorUtils.setPlayerColor(player.world.getPlayerByUuid(target), color, false);
	}
	
	@Override
	EnumDirection getDirection()
	{
		return PLAY_TO_CLIENT;
	}
}
