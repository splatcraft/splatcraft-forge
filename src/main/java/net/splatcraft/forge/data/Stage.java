package net.splatcraft.forge.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.splatcraft.forge.commands.StageCommand;
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

	private final HashMap<Integer, ArrayList<SpawnPadTileEntity>> spawnPads = new HashMap<>();

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
	}

	public Stage(Level level, BlockPos posA, BlockPos posB, String id)
	{
		dimID = level.dimension().location();
		this.id = id;

		updateBounds(level, posA, posB);
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
		spawnPads.clear();
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

	}

	public void addSpawnPad(SpawnPadTileEntity spawnPad)
	{
		if(!spawnPads.containsKey(spawnPad.getColor()))
			spawnPads.put(spawnPad.getColor(), new ArrayList<>());
		else if(spawnPads.get(spawnPad.getColor()).contains(spawnPad))
			return;

		spawnPads.get(spawnPad.getColor()).add(spawnPad);
	}

	public void removeSpawnPad(SpawnPadTileEntity spawnPad)
	{
		if(spawnPads.containsKey(spawnPad.getColor()))
		{
			spawnPads.get(spawnPad.getColor()).remove(spawnPad);
			if(spawnPads.get(spawnPad.getColor()).isEmpty())
				spawnPads.remove(spawnPad.getColor());
		}
	}

	public boolean hasSpawnPads()
	{
		return !spawnPads.isEmpty();
	}

	public HashMap<Integer, ArrayList<SpawnPadTileEntity>> getSpawnPads()
	{
		return spawnPads;
	}

	public ArrayList<SpawnPadTileEntity> getAllSpawnPads()
	{
		ArrayList<SpawnPadTileEntity> result = new ArrayList<>();
		for(ArrayList<SpawnPadTileEntity> spawnPads : spawnPads.values())
			result.addAll(spawnPads);

		return result;
	}

	public boolean superJumpToStage(ServerPlayer player)
	{
		if(!player.level.dimension().location().equals(dimID) || getSpawnPads().isEmpty())
			return false;

		int playerColor = ColorUtils.getPlayerColor(player);
		if(!getSpawnPads().containsKey(playerColor))
		{
			playerColor = getSpawnPads().keySet().toArray(new Integer[0])[player.getRandom().nextInt(spawnPads.size())];
			ColorUtils.setPlayerColor(player, playerColor);
		}

		BlockPos targetPos = getSpawnPads().get(playerColor).get(player.getRandom().nextInt(getSpawnPads().get(playerColor).size())).getBlockPos();


		return SuperJumpCommand.superJump(player, new Vec3(targetPos.getX() + 0.5, targetPos.getY() + SuperJumpCommand.blockHeight(targetPos, player.level), targetPos.getZ() + 0.5));
	}
}
