package net.splatcraft.forge.network.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.splatcraft.forge.client.handlers.JumpLureHudHandler;

import java.util.ArrayList;
import java.util.UUID;

public class SendJumpLureDataPacket extends PlayS2CPacket
{
	final int color;
	final boolean canJumpToSpawn;
	final ArrayList<UUID> uuids;

	public SendJumpLureDataPacket(int color, boolean canJumpToSpawn, ArrayList<UUID> uuids) {
		this.color = color;
		this.canJumpToSpawn = canJumpToSpawn;
		this.uuids = uuids;
	}

	@Override
	public void encode(FriendlyByteBuf buffer)
	{
		buffer.writeInt(color);
		buffer.writeBoolean(canJumpToSpawn);
		buffer.writeInt(uuids.size());
		for(UUID uuid : uuids)
			buffer.writeUUID(uuid);
	}

	public static SendJumpLureDataPacket decode(FriendlyByteBuf buffer)
	{
		int color = buffer.readInt();
		boolean canJump = buffer.readBoolean();
		int uuidCount = buffer.readInt();
		ArrayList<UUID> uuids = new ArrayList<>();
		for(int i = 0; i < uuidCount; i++)
			uuids.add(buffer.readUUID());

		return new SendJumpLureDataPacket(color, canJump, uuids);
	}

	@Override
	public void execute()
	{
		JumpLureHudHandler.updateTargetData(new JumpLureHudHandler.SuperJumpTargets(uuids, canJumpToSpawn, color));
	}
}
