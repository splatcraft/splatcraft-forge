package net.splatcraft.forge.data;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.splatcraft.forge.data.capabilities.saveinfo.SaveInfoCapability;
import net.splatcraft.forge.registries.SplatcraftGameRules;
import net.splatcraft.forge.util.ClientUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Stage
{
	public static final ArrayList<String> VALID_SETTINGS = new ArrayList<>();

	public BlockPos cornerA;
	public BlockPos cornerB;
	public ResourceLocation dimID;

	private final HashMap<String, Boolean> settings = new HashMap<>();
	private final HashMap<String, Integer> teams = new HashMap<>();

	static
	{
		registerGameruleSetting(SplatcraftGameRules.INK_DECAY);
		registerGameruleSetting(SplatcraftGameRules.UNIVERSAL_INK);
		registerGameruleSetting(SplatcraftGameRules.REQUIRE_INK_TANK);
		registerGameruleSetting(SplatcraftGameRules.KEEP_MATCH_ITEMS);
		registerGameruleSetting(SplatcraftGameRules.WATER_DAMAGE);
		registerGameruleSetting(SplatcraftGameRules.INK_FRIENDLY_FIRE);
		registerGameruleSetting(SplatcraftGameRules.INK_HEALING);
		registerGameruleSetting(SplatcraftGameRules.INK_HEALING_CONSUMES_HUNGER);
		registerGameruleSetting(SplatcraftGameRules.INKABLE_GROUND);
		registerGameruleSetting(SplatcraftGameRules.INK_DESTROYS_FOLIAGE);
		registerGameruleSetting(SplatcraftGameRules.RECHARGEABLE_INK_TANK);
		registerGameruleSetting(SplatcraftGameRules.GLOBAL_SUPERJUMPING);
	}

	public CompoundTag writeData()
	{
		CompoundTag nbt = new CompoundTag();

		nbt.put("CornerA", NbtUtils.writeBlockPos(cornerA));
		nbt.put("CornerB", NbtUtils.writeBlockPos(cornerB));
		nbt.putString("Dimension", dimID.toString());

		CompoundTag settingsNbt = new CompoundTag();
		CompoundTag teamsNbt = new CompoundTag();

		for(Map.Entry<String, Boolean> setting : settings.entrySet())
			settingsNbt.putBoolean(setting.getKey(), setting.getValue());
		nbt.put("Settings", settingsNbt);

		for(Map.Entry<String, Integer> team : teams.entrySet())
			teamsNbt.putInt(team.getKey(), team.getValue());
		nbt.put("Teams", teamsNbt);

		return nbt;
	}

	public boolean hasSetting(String key)
	{
		return settings.containsKey(key);
	}

	public boolean hasSetting(GameRules.Key<GameRules.BooleanValue> rule)
	{
		return hasSetting(rule.toString().replace("splatcraft.", ""));
	}

	public boolean getSetting(String key)
	{
		return settings.get(key);
	}

	public boolean getSetting(GameRules.Key<GameRules.BooleanValue> rule)
	{
		return getSetting(rule.toString().replace("splatcraft.", ""));
	}

	public void applySetting(String key, @Nullable Boolean value)
	{
		if(value == null)
			settings.remove(key);
		else settings.put(key, value);
	}

	public boolean hasTeam(String teamId)
	{
		return teams.containsKey(teamId);
	}

	public int getTeamColor(String teamId)
	{
		return hasTeam(teamId) ? teams.get(teamId) : -1;
	}

	public void setTeamColor(String teamId, int teamColor)
	{
		teams.put(teamId, teamColor);
	}

	public void removeTeam(String teamId)
	{
		teams.remove(teamId);
	}

	public Collection<String> getTeamIds()
	{
		return teams.keySet();
	}

	public AABB getBounds()
	{
		return new AABB(cornerA, cornerB);
	}

	public Stage(CompoundTag nbt)
	{
		cornerA = NbtUtils.readBlockPos(nbt.getCompound("CornerA"));
		cornerB = NbtUtils.readBlockPos(nbt.getCompound("CornerB"));
		dimID = new ResourceLocation(nbt.getString("Dimension"));

		settings.clear();

		CompoundTag settingsNbt = nbt.getCompound("Settings");
		for(String key : settingsNbt.getAllKeys())
			settings.put(key, settingsNbt.getBoolean(key));

		CompoundTag teamsNbt = nbt.getCompound("Teams");
		for(String key : teamsNbt.getAllKeys())
			teams.put(key, teamsNbt.getInt(key));
	}

	public Stage(Level level, BlockPos posA, BlockPos posB)
	{
		dimID = level.dimension().location();

		cornerA = posA;
		cornerB = posB;
	}

	public static void registerGameruleSetting(GameRules.Key<GameRules.BooleanValue> rule)
	{
		VALID_SETTINGS.add(rule.toString().replace("splatcraft.", ""));
	}

	public static boolean targetsOnSameStage(Level level, Vec3 targetA, Vec3 targetB)
	{
		return !getStagesForPosition(level, targetA).stream().filter(stage -> stage.getBounds().contains(targetB)).toList().isEmpty();
	}

	public static ArrayList<Stage> getAllStages(Level level)
	{
		return new ArrayList<>(level.isClientSide ? ClientUtils.clientStages.values() : SaveInfoCapability.get(level.getServer()).getStages().values());
	}

	public static ArrayList<Stage> getStagesForPosition(Level level, Vec3 pos)
	{
		ArrayList<Stage> stages = getAllStages(level);
		stages.removeIf(stage -> !stage.dimID.equals(level.dimension().location()) || !stage.getBounds().contains(pos));
		return stages;
	}


}
