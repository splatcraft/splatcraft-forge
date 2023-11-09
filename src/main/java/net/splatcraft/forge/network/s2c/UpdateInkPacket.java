package net.splatcraft.forge.network.s2c;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.splatcraft.forge.data.capabilities.worldink.WorldInk;
import net.splatcraft.forge.data.capabilities.worldink.WorldInkCapability;
import net.splatcraft.forge.util.InkBlockUtils;

import java.util.HashMap;

public class UpdateInkPacket extends PlayS2CPacket
{
	private final ChunkPos chunkPos;
	private final HashMap<BlockPos, WorldInk.Entry> dirty;

	public UpdateInkPacket(BlockPos pos, int color, InkBlockUtils.InkType type)
	{
		chunkPos = new ChunkPos(Math.floorDiv(pos.getX(), 16), Math.floorDiv(pos.getZ(), 16));
		this.dirty = new HashMap<>();
		dirty.put(new BlockPos(pos.getX() % 16, pos.getY(), pos.getZ() % 16), new WorldInk.Entry(color, type));
	}

	public UpdateInkPacket(ChunkPos pos, HashMap<BlockPos, WorldInk.Entry> dirty)
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

	public static UpdateInkPacket decode(FriendlyByteBuf buffer)
	{
		ChunkPos pos = buffer.readChunkPos();
		HashMap<BlockPos, WorldInk.Entry> dirty = new HashMap<>();
		int size = buffer.readInt();
		for(int i = 0; i < size; i++)
			dirty.put(buffer.readBlockPos(), new WorldInk.Entry(buffer.readInt(), InkBlockUtils.InkType.values.getOrDefault(buffer.readResourceLocation(), null)));

		return new UpdateInkPacket(pos, dirty);
	}

	@Override
	public void execute()
	{
		ClientLevel level = Minecraft.getInstance().level;

		if(level != null)
		{
			WorldInk worldInk = WorldInkCapability.get(level.getChunk(chunkPos.x, chunkPos.z));

			dirty.forEach((pos, entry) ->
			{
				if (entry == null || entry.type() == null) {
					worldInk.clearInk(pos);
				} else {
					worldInk.ink(pos, entry.color(), entry.type());
				}


				pos = new BlockPos(pos.getX() + chunkPos.x * 16, pos.getY(), pos.getZ() + chunkPos.z * 16);
				BlockState state = level.getBlockState(pos);
				level.sendBlockUpdated(pos, state, state, 0);
			});
		}

	}
}
