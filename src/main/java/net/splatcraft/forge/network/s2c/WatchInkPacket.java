package net.splatcraft.forge.network.s2c;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.splatcraft.forge.data.capabilities.worldink.WorldInk;
import net.splatcraft.forge.handlers.WorldInkHandler;
import net.splatcraft.forge.util.InkBlockUtils;

import java.util.HashMap;

public class WatchInkPacket extends PlayS2CPacket
{
	private final ChunkPos chunkPos;
	private final HashMap<BlockPos, WorldInk.Entry> dirty;
	public WatchInkPacket(ChunkPos pos, HashMap<BlockPos, WorldInk.Entry> dirty)
	{
		this.chunkPos = pos;
		this.dirty = dirty;
	}

	@Override
	public void encode(FriendlyByteBuf buffer)
	{
		buffer.writeChunkPos(chunkPos);
		buffer.writeInt(dirty.size());

		dirty.forEach((pos, entry) ->
		{
			buffer.writeBlockPos(pos);
			buffer.writeInt(entry.color());
			buffer.writeResourceLocation(entry.type() == null ? new ResourceLocation("") : entry.type().getName());
		});
	}

	public static WatchInkPacket decode(FriendlyByteBuf buffer)
	{
		ChunkPos pos = buffer.readChunkPos();
		HashMap<BlockPos, WorldInk.Entry> dirty = new HashMap<>();
		int size = buffer.readInt();
		for(int i = 0; i < size; i++)
			dirty.put(buffer.readBlockPos(), new WorldInk.Entry(buffer.readInt(), InkBlockUtils.InkType.values.getOrDefault(buffer.readResourceLocation(), null)));

		return new WatchInkPacket(pos, dirty);
	}

	@Override
	public void execute()
	{
		WorldInkHandler.updateInk(chunkPos, dirty);
	}
}
