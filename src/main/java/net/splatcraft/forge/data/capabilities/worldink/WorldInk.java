package net.splatcraft.forge.data.capabilities.worldink;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.splatcraft.forge.registries.SplatcraftInkColors;
import net.splatcraft.forge.util.InkBlockUtils;

import java.util.HashMap;


/*  TODO
	S2C syncing
	migrate old ink system to new one
	make old inked blocks decay instantly
	fix rendering bugs
	finish Rubidium support
	add Embeddium support
	add Oculus support
	screw OptiFine
 */
public class WorldInk
{
	private final HashMap<BlockPos, Entry> INK_MAP = new HashMap<>();
	private final HashMap<BlockPos, Entry> PERMANENT_INK_MAP = new HashMap<>();

	public boolean isInked(BlockPos pos)
	{
		return INK_MAP.containsKey(pos);
	}

	public void ink(BlockPos pos, int color, InkBlockUtils.InkType type)
	{
		INK_MAP.put(pos, new Entry(color, type));
	}

	public void clearInk(BlockPos pos)
	{
		if(hasPermanentInk(pos))
			INK_MAP.put(pos, getPermanentInk(pos));
		else INK_MAP.remove(pos);
	}

	public Entry getInk(BlockPos pos)
	{
		return INK_MAP.get(pos);
	}

	public boolean hasPermanentInk(BlockPos pos)
	{
		return PERMANENT_INK_MAP.containsKey(pos);
	}

	public Entry getPermanentInk(BlockPos pos)
	{
		return PERMANENT_INK_MAP.get(pos);
	}

	public void removePermanentInk(BlockPos pos)
	{
		PERMANENT_INK_MAP.remove(pos);
	}

	public void setPermanentInk(BlockPos pos, int color, InkBlockUtils.InkType type)
	{
		PERMANENT_INK_MAP.put(pos, new Entry(color, type));
	}

	public CompoundTag writeNBT(CompoundTag nbt)
	{
		ListTag inkMapList = new ListTag();

		INK_MAP.forEach((pos, entry) ->
		{
			CompoundTag element = new CompoundTag();
			element.put("Pos", NbtUtils.writeBlockPos(pos));
			element.putInt("Color", entry.color);
			element.putString("Type", entry.type.getName().toString());

			inkMapList.add(element);
		});

		nbt.put("Ink", inkMapList);

		ListTag permanentInkMapList = new ListTag();

		PERMANENT_INK_MAP.forEach((pos, entry) ->
		{
			CompoundTag element = new CompoundTag();
			element.put("Pos", NbtUtils.writeBlockPos(pos));
			element.putInt("Color", entry.color);
			element.putString("Type", entry.type.getName().toString());

			permanentInkMapList.add(element);
		});

		nbt.put("PermanentInk", permanentInkMapList);

		return nbt;
	}

	public void readNBT(CompoundTag nbt)
	{
		PERMANENT_INK_MAP.clear();
		nbt.getList("PermanentInk", Tag.TAG_COMPOUND).forEach(tag ->
		{
			CompoundTag element = (CompoundTag) tag;
			setPermanentInk(NbtUtils.readBlockPos(element.getCompound("Pos")), element.getInt("Color"), InkBlockUtils.InkType.values.get(new ResourceLocation(element.getString("Type"))));
		});

		INK_MAP.clear();
		nbt.getList("Ink", Tag.TAG_COMPOUND).forEach(tag ->
		{
			CompoundTag element = (CompoundTag) tag;
			ink(NbtUtils.readBlockPos(element.getCompound("Pos")), element.getInt("Color"), InkBlockUtils.InkType.values.get(new ResourceLocation(element.getString("Type"))));
		});
	}

	public record Entry(int color, InkBlockUtils.InkType type) {}
}
