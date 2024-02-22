package net.splatcraft.forge.network.c2s;

import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.splatcraft.forge.data.Stage;
import net.splatcraft.forge.items.remotes.TurfScannerItem;

import java.util.ArrayList;

public class RequestTurfScanPacket extends PlayC2SPacket
{
	final String stageId;
	final boolean isTopDown;

	public RequestTurfScanPacket(String stageId, boolean isTopDown) {
		this.stageId = stageId;
		this.isTopDown = isTopDown;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeUtf(stageId);
		buffer.writeBoolean(isTopDown);
	}

	public static RequestTurfScanPacket decode(FriendlyByteBuf buffer)
	{
		return new RequestTurfScanPacket(buffer.readUtf(), buffer.readBoolean());
	}

	@Override
	public void execute(Player player)
	{
		Stage stage = Stage.getStage(player.level, stageId);

		ServerLevel stageLevel = player.level.getServer().getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, stage.dimID));
		ArrayList<ServerPlayer> playerList = new ArrayList<>(stageLevel.getEntitiesOfClass(ServerPlayer.class, stage.getBounds()));
		if(!playerList.contains(player))
			playerList.add(0, (ServerPlayer) player);

		player.displayClientMessage(TurfScannerItem.scanTurf(stageLevel, stageLevel, stage.cornerA, stage.cornerB, isTopDown ? 0 : 1, playerList).getOutput(), true);
	}
}
