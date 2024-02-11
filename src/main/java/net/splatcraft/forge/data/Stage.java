package net.splatcraft.forge.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.splatcraft.forge.commands.SuperJumpCommand;
import net.splatcraft.forge.data.capabilities.saveinfo.SaveInfoCapability;
import net.splatcraft.forge.registries.SplatcraftGameRules;
import net.splatcraft.forge.tileentities.SpawnPadTileEntity;
import net.splatcraft.forge.util.ClientUtils;
import net.splatcraft.forge.util.ColorUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Stage
{
	public static final ArrayList<String> VALID_SETTINGS = new ArrayList<>();

	public BlockPos cornerA;
	public BlockPos cornerB;
	public ResourceLocation dimID;

	public final String id;

	private final HashMap<String, Boolean> settings = new HashMap<>();
	private final HashMap<String, Integer> teams = new HashMap<>();

	private final ArrayList<BlockPos> spawnPadPositions = new ArrayList<>();

	private boolean needsSpawnPadUpdate = false;

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

		if(!needsSpawnPadUpdate)
		{
			ListTag list = new ListTag();
			for (BlockPos spawnPadPos : spawnPadPositions)
				list.add(NbtUtils.writeBlockPos(spawnPadPos));

			nbt.put("SpawnPads", list);
		}

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

	public Component getStageName()
	{
		return new TextComponent(id); //TODO use actual name settibng
	}

	public BlockPos getCornerA() {
		return cornerA;
	}

	public BlockPos getCornerB() {
		return cornerB;
	}

	public void updateBounds(@Nullable Level level, BlockPos cornerA, BlockPos cornerB)
	{
		this.cornerA = cornerA;
		this.cornerB = cornerB;

		if(level != null)
			updateSpawnPads(level);
	}


	public Stage(CompoundTag nbt, String id)
	{
		this.id = id;
		dimID = new ResourceLocation(nbt.getString("Dimension"));

		updateBounds(null, NbtUtils.readBlockPos(nbt.getCompound("CornerA")), NbtUtils.readBlockPos(nbt.getCompound("CornerB")));

		settings.clear();

		CompoundTag settingsNbt = nbt.getCompound("Settings");
		for(String key : settingsNbt.getAllKeys())
			settings.put(key, settingsNbt.getBoolean(key));

		CompoundTag teamsNbt = nbt.getCompound("Teams");
		for(String key : teamsNbt.getAllKeys())
			teams.put(key, teamsNbt.getInt(key));

		spawnPadPositions.clear();

		needsSpawnPadUpdate = !nbt.contains("SpawnPads", Tag.TAG_LIST);

		ListTag list = nbt.getList("SpawnPads", Tag.TAG_COMPOUND);
		for (Tag tag : list)
			spawnPadPositions.add(NbtUtils.readBlockPos((CompoundTag) tag));
	}

	public Stage(Level level, BlockPos posA, BlockPos posB, String id)
	{
		dimID = level.dimension().location();
		this.id = id;

		updateBounds(level, posA, posB);
	}

	public boolean needSpawnPadUpdate()
	{
		return needsSpawnPadUpdate;
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
	public static Stage getStage(Level level, String id)
	{
		return (level.isClientSide ? ClientUtils.clientStages : SaveInfoCapability.get(level.getServer()).getStages()).get(id);
	}

	public static ArrayList<Stage> getStagesForPosition(Level level, Vec3 pos)
	{
		ArrayList<Stage> stages = getAllStages(level);
		stages.removeIf(stage -> !stage.dimID.equals(level.dimension().location()) || !stage.getBounds().contains(pos));
		return stages;
	}

	public void updateSpawnPads(Level level)
	{
		spawnPadPositions.clear();
		Level stageLevel = level.getServer().getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, dimID));

		BlockPos blockpos2 = new BlockPos(Math.min(cornerA.getX(), cornerB.getX()), Math.min(cornerB.getY(), cornerA.getY()), Math.min(cornerA.getZ(), cornerB.getZ()));
		BlockPos blockpos3 = new BlockPos(Math.max(cornerA.getX(), cornerB.getX()), Math.max(cornerB.getY(), cornerA.getY()), Math.max(cornerA.getZ(), cornerB.getZ()));

		for (int x = blockpos2.getX(); x <= blockpos3.getX(); x++)
			for (int y = blockpos2.getY(); y <= blockpos3.getY(); y++)
				for (int z = blockpos2.getZ(); z <= blockpos3.getZ(); z++) {
					BlockPos pos = new BlockPos(x, y, z);
					if (stageLevel.getBlockEntity(pos) instanceof SpawnPadTileEntity spawnPad) {
						addSpawnPad(spawnPad);
					}
				}

		needsSpawnPadUpdate = false;
	}

	public void addSpawnPad(SpawnPadTileEntity spawnPad)
	{
		if(!spawnPadPositions.contains(spawnPad.getBlockPos()))
			spawnPadPositions.add(spawnPad.getBlockPos());
	}

	public void removeSpawnPad(SpawnPadTileEntity spawnPad)
	{
		spawnPadPositions.remove(spawnPad.getBlockPos());
	}

	public boolean hasSpawnPads()
	{
		return !spawnPadPositions.isEmpty();
	}

	public ArrayList<BlockPos> getSpawnPadPositions()
	{
		return spawnPadPositions;
	}

	public HashMap<Integer, ArrayList<SpawnPadTileEntity>> getSpawnPads(Level level)
	{
		HashMap<Integer, ArrayList<SpawnPadTileEntity>> result = new HashMap<>();
		Level stageLevel = level.getServer().getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, dimID));

		for (BlockPos pos : spawnPadPositions)
			if(stageLevel.getBlockEntity(pos) instanceof SpawnPadTileEntity pad)
			{
				if(!result.containsKey(pad.getColor()))
					result.put(pad.getColor(), new ArrayList<>());
				result.get(pad.getColor()).add(pad);
			}

		return result;
	}

	public List<SpawnPadTileEntity> getAllSpawnPads(Level level)
	{
		Level stageLevel = level.getServer().getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, dimID));
		return spawnPadPositions.stream().map(pos -> stageLevel.getBlockEntity(pos)).filter(te -> te instanceof SpawnPadTileEntity).map(te -> (SpawnPadTileEntity)te).toList();
	}

	public boolean superJumpToStage(ServerPlayer player)
	{
		if(!player.level.dimension().location().equals(dimID) || getSpawnPadPositions().isEmpty())
			return false;

		int playerColor = ColorUtils.getPlayerColor(player);
		HashMap<Integer, ArrayList<SpawnPadTileEntity>> spawnPads = getSpawnPads(player.level);

		if(!spawnPads.containsKey(playerColor))
		{
			playerColor = spawnPads.keySet().toArray(new Integer[0])[player.getRandom().nextInt(spawnPadPositions.size())];
			ColorUtils.setPlayerColor(player, playerColor);
		}

		BlockPos targetPos = spawnPads.get(playerColor).get(player.getRandom().nextInt(spawnPads.get(playerColor).size())).getBlockPos();


		return SuperJumpCommand.superJump(player, new Vec3(targetPos.getX() + 0.5, targetPos.getY() + SuperJumpCommand.blockHeight(targetPos, player.level), targetPos.getZ() + 0.5));
	}

	static boolean stagesLoaded = false;
}
