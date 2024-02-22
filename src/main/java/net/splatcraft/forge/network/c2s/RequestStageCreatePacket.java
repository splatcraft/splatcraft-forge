package net.splatcraft.forge.network.c2s;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.splatcraft.forge.data.capabilities.saveinfo.SaveInfoCapability;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.s2c.NotifyStageCreatePacket;

public class RequestStageCreatePacket extends PlayC2SPacket
{
	final String stageId;
	final Component stageName;
	final BlockPos corner1;
	final BlockPos corner2;

	public RequestStageCreatePacket(String stageId, Component stageName, BlockPos corner1, BlockPos corner2) {
		this.stageId = stageId;
		this.stageName = stageName;
		this.corner1 = corner1;
		this.corner2 = corner2;
	}

	@Override
	public void encode(FriendlyByteBuf buffer)
	{
		buffer.writeUtf(stageId);
		buffer.writeComponent(stageName);
		buffer.writeBlockPos(corner1);
		buffer.writeBlockPos(corner2);
	}

	public static RequestStageCreatePacket decode(FriendlyByteBuf buf)
	{
		return new RequestStageCreatePacket(buf.readUtf(), buf.readComponent(), buf.readBlockPos(), buf.readBlockPos());
	}

	@Override
	public void execute(Player player)
	{
		SaveInfoCapability.get(player.getServer()).createStage(player.level, stageId, corner1, corner2, stageName);
		SplatcraftPacketHandler.sendToPlayer(new NotifyStageCreatePacket(stageId), (ServerPlayer) player);
	}
}
