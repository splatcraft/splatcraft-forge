package net.splatcraft.forge.network.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.splatcraft.forge.data.Stage;
import net.splatcraft.forge.items.JumpLureItem;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SuperJumpToStagePacket extends PlayC2SPacket
{
	final String stageId;

	public SuperJumpToStagePacket(String stageId)
	{
		this.stageId = stageId;
	}

	@Override
	public void encode(FriendlyByteBuf buffer)
	{
		buffer.writeUtf(stageId);
	}

	public static SuperJumpToStagePacket decode(FriendlyByteBuf buf)
	{
		return new SuperJumpToStagePacket(buf.readUtf());
	}

	@Override
	public void execute(Player player)
	{
		Stage.getStage(player.level, stageId).superJumpToStage((ServerPlayer) player);
	}
}
