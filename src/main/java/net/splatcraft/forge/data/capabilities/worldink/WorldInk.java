package net.splatcraft.forge.data.capabilities.worldink;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.splatcraft.forge.registries.SplatcraftInkColors;
import net.splatcraft.forge.util.InkBlockUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;


/*  TODO
	make old inked blocks decay instantly
	piston push interactions
	fix rendering bugs (See WorldInkHandler.Render comment)
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
		return getInk(pos) != null;
	}

	public void ink(BlockPos pos, int color, InkBlockUtils.InkType type)
	{
		INK_MAP.put(localizeBlockPos(pos), new Entry(color, type));
	}

	public boolean clearInk(BlockPos pos)
	{
		if(!isInked(pos) || getInk(pos).equals(getPermanentInk(pos)))
			return false;
		
		if(hasPermanentInk(pos))
			INK_MAP.put(localizeBlockPos(pos), getPermanentInk(pos));
		else INK_MAP.remove(localizeBlockPos(pos));
		return true;
	}

	public HashMap<BlockPos, Entry> getInkInChunk()
	{
		return INK_MAP;
	}

	public HashMap<BlockPos, Entry> getPermanentInkInChunk()
	{
		return PERMANENT_INK_MAP;
	}

	public Entry getInk(BlockPos pos)
	{
		return INK_MAP.get(localizeBlockPos(pos));
	}

	public boolean hasPermanentInk(BlockPos pos)
	{
		return getPermanentInk(pos) != null;
	}

	public Entry getPermanentInk(BlockPos pos)
	{
		return PERMANENT_INK_MAP.get(localizeBlockPos(pos));
	}

	public boolean removePermanentInk(BlockPos pos)
	{
		if(hasPermanentInk(pos))
		{
			PERMANENT_INK_MAP.remove(localizeBlockPos(pos));
			return true;
		}
		return false;
	}

	public void setPermanentInk(BlockPos pos, int color, InkBlockUtils.InkType type)
	{
		PERMANENT_INK_MAP.put(localizeBlockPos(pos), new Entry(color, type));
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

	protected BlockPos localizeBlockPos(BlockPos pos)
	{

		return new BlockPos(Math.floorMod(pos.getX(), 16), pos.getY(), Math.floorMod(pos.getZ(), 16));
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

		INK_MAP.values();
	}

	public record Entry(int color, InkBlockUtils.InkType type)
	{
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Entry entry = (Entry) o;
			return color == entry.color && Objects.equals(type, entry.type);
		}

		@Override
		public int hashCode() {
			return Objects.hash(color, type);
		}
	}
}
