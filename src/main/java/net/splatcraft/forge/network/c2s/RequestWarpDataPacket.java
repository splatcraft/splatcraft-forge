package net.splatcraft.forge.network.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.s2c.SendStageWarpDataToPadPacket;

public class RequestWarpDataPacket extends PlayC2SPacket
{
	@Override
	public void encode(FriendlyByteBuf buffer) {

	}

	public static RequestWarpDataPacket decode(FriendlyByteBuf buf)
	{
		return new RequestWarpDataPacket();
	}

	@Override
	public void execute(Player player)
	{
		SplatcraftPacketHandler.sendToPlayer(SendStageWarpDataToPadPacket.compile(player), (ServerPlayer) player);

	}
}
