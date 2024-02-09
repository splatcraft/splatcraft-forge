package net.splatcraft.forge.network.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.splatcraft.forge.items.JumpLureItem;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class UseJumpLurePacket extends PlayC2SPacket
{
	@Nullable
	final UUID targetUUID;
	final int color;

	public UseJumpLurePacket(int color, @Nullable UUID targetUUID)
	{
		this.targetUUID = targetUUID;
		this.color = color;
	}

	@Override
	public void encode(FriendlyByteBuf buffer)
	{
		buffer.writeInt(color);
		buffer.writeBoolean(targetUUID == null);
		if(targetUUID != null)
			buffer.writeUUID(targetUUID);
	}

	public static UseJumpLurePacket decode(FriendlyByteBuf buf)
	{
		return new UseJumpLurePacket(buf.readInt(), buf.readBoolean() ? null : buf.readUUID());
	}

	@Override
	public void execute(Player player)
	{
		JumpLureItem.activate((ServerPlayer) player, targetUUID, color);
	}
}
