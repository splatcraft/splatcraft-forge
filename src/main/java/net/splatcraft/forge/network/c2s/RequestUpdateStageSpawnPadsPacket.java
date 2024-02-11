package net.splatcraft.forge.network.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.splatcraft.forge.data.Stage;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.s2c.SendStageWarpDataToPadPacket;

public class RequestUpdateStageSpawnPadsPacket extends PlayC2SPacket
{
	final String stageId;

	public RequestUpdateStageSpawnPadsPacket(String stageId)
	{
		this.stageId = stageId;
	}
	public RequestUpdateStageSpawnPadsPacket(Stage stage)
	{
		this(stage.id);
	}

	@Override
	public void encode(FriendlyByteBuf buffer)
	{
		buffer.writeUtf(stageId);
	}

	public static RequestUpdateStageSpawnPadsPacket decode(FriendlyByteBuf buffer)
	{
		return new RequestUpdateStageSpawnPadsPacket(buffer.readUtf());
	}

	@Override
	public void execute(Player player)
	{
		Stage.getStage(player.level, stageId).updateSpawnPads(player.level);
		SplatcraftPacketHandler.sendToPlayer(SendStageWarpDataToPadPacket.compile(player), (ServerPlayer) player);
	}
}
