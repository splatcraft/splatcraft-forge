package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PlayerColorPacket extends SplatcraftPacket
{
	private int color;
	
	public PlayerColorPacket(int color)
	{
		direction = EnumDirection.PLAY_TO_CLIENT;
		this.color = color;
	}
	
	@Override
	public void encode(PacketBuffer buffer)
	{
		buffer.writeInt(color);
	}
	
	public static PlayerColorPacket decode(PacketBuffer buffer)
	{
		int color = buffer.readInt();
		return new PlayerColorPacket(color);
	}
	
	@Override
	public void execute(PlayerEntity player)
	{
		ColorUtils.setPlayerColor(player, color, false);
	}
}
