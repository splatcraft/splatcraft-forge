package net.splatcraft.forge.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.TeamArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.splatcraft.forge.blocks.SpawnPadBlock;
import net.splatcraft.forge.commands.arguments.InkColorArgument;
import net.splatcraft.forge.data.Stage;
import net.splatcraft.forge.data.capabilities.saveinfo.SaveInfoCapability;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.s2c.UpdateStageListPacket;
import net.splatcraft.forge.tileentities.InkColorTileEntity;
import net.splatcraft.forge.tileentities.SpawnPadTileEntity;
import net.splatcraft.forge.util.ClientUtils;
import net.splatcraft.forge.util.ColorUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class StageCommand
{

	private static final DynamicCommandExceptionType NO_SPAWN_PADS_FOUND = new DynamicCommandExceptionType(p_208663_0_ -> new TranslatableComponent("arg.stageWarp.noSpawnPads", p_208663_0_));
	private static final DynamicCommandExceptionType NO_PLAYERS_FOUND = new DynamicCommandExceptionType(p_208663_0_ -> new TranslatableComponent("arg.stageWarp.noPlayers", p_208663_0_));
	public static final DynamicCommandExceptionType TEAM_NOT_FOUND = new DynamicCommandExceptionType(p_208663_0_ -> new TranslatableComponent("arg.stageTeam.notFound", ((Object[])p_208663_0_)[0], ((Object[])p_208663_0_)[1]));
	public static final DynamicCommandExceptionType STAGE_NOT_FOUND = new DynamicCommandExceptionType(p_208663_0_ -> new TranslatableComponent("arg.stage.notFound", p_208663_0_));
	public static final DynamicCommandExceptionType STAGE_ALREADY_EXISTS = new DynamicCommandExceptionType(p_208663_0_ -> new TranslatableComponent("arg.stage.alreadyExists", p_208663_0_));
	public static final DynamicCommandExceptionType SETTING_NOT_FOUND = new DynamicCommandExceptionType(p_208663_0_ -> new TranslatableComponent("arg.stageSetting.notFound", p_208663_0_));


	public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
	{
		dispatcher.register(Commands.literal("stage").requires(commandSource -> commandSource.hasPermission(2))
				.then(Commands.literal("add")
						.then(Commands.argument("name", StringArgumentType.word())
						.then(Commands.argument("from", BlockPosArgument.blockPos())
						.then(Commands.argument("to", BlockPosArgument.blockPos()).executes(StageCommand::add)))))
				.then(Commands.literal("remove")
						.then(stageId("stage").executes(StageCommand::remove)
				))//.then(Commands.literal("list"))
				.then(Commands.literal("settings").then(stageId("stage")
								.then(Commands.literal("cornerA").executes(context -> getStageCorner(context, true))
										.then(Commands.argument("pos", BlockPosArgument.blockPos()).executes(context -> setStageCorner(context, true))))
								.then(Commands.literal("cornerB").executes(context -> getStageCorner(context, false))
										.then(Commands.argument("pos", BlockPosArgument.blockPos()).executes(context -> setStageCorner(context, false))))
						.then(stageSetting("setting").executes(StageCommand::getSetting)
								.then(Commands.literal("true").executes(context -> setSetting(context, true)))
								.then(Commands.literal("false").executes(context -> setSetting(context, false)))
								.then(Commands.literal("default").executes(context -> setSetting(context, null))))))
				.then(Commands.literal("teams").then(stageId("stage")
						.then(Commands.literal("set")
								.then(stageTeam("teamName", "stage")
										.then(Commands.argument("teamColor", InkColorArgument.inkColor()).executes(StageCommand::setTeam))))
						.then(Commands.literal("remove")
								.then(stageTeam("teamName", "stage").executes(StageCommand::removeTeam)))
						.then(Commands.literal("get")
								.then(stageTeam("teamName", "stage").executes(StageCommand::getTeam)))))
				.then(Commands.literal("warp").then(stageId("stage").executes(StageCommand::warpSelf)
						.then(Commands.argument("players", EntityArgument.players()).executes(context -> warp(context, false))
								.then(Commands.argument("setSpawn", BoolArgumentType.bool())
										.then(Commands.literal("self").executes(context -> warp(context, BoolArgumentType.getBool(context,"setSpawn"))))
										.then(Commands.literal("any").executes(context -> warpAny(context, BoolArgumentType.getBool(context,"setSpawn"))))
										.then(Commands.literal("color").then(Commands.argument("color", InkColorArgument.inkColor()).executes(context -> warp(context, BoolArgumentType.getBool(context,"setSpawn"), InkColorArgument.getInkColor(context, "color")))))
										.then(Commands.literal("team").then(stageTeam("team", "stage").executes(context -> warpToTeam(context, BoolArgumentType.getBool(context,"setSpawn"), StringArgumentType.getString(context, "team")))))
										.executes(context -> warp(context, BoolArgumentType.getBool(context,"setSpawn")))))))
		);
	}

	public static RequiredArgumentBuilder<CommandSourceStack, String> stageId(String argumentName)
	{
		return Commands.argument(argumentName, StringArgumentType.word()).suggests((context, builder) -> SharedSuggestionProvider.suggest((context.getSource().getLevel().isClientSide() ? ClientUtils.clientStages : SaveInfoCapability.get(context.getSource().getServer()).getStages()).keySet(), builder));
	}

	public static RequiredArgumentBuilder<CommandSourceStack, String> stageTeam(String argumentName, String stageArgumentName)
	{
		return Commands.argument(argumentName, StringArgumentType.word()).suggests((context, builder) ->
		{
			try
			{
				Stage stage = (context.getSource().getLevel().isClientSide() ? ClientUtils.clientStages : SaveInfoCapability.get(context.getSource().getServer()).getStages()).get(StringArgumentType.getString(context, stageArgumentName));
				if(stage == null)
					return Suggestions.empty();

				return SharedSuggestionProvider.suggest(stage.getTeamIds(), builder);
			} catch (IllegalArgumentException ignored) {} //happens when used inside execute, vanilla won't bother to fix it so neither will i >_>

			return Suggestions.empty();
		});
	}

	public static RequiredArgumentBuilder<CommandSourceStack, String> stageSetting(String argumentName)
	{
		return Commands.argument(argumentName, StringArgumentType.word()).suggests((context, builder) ->
				SharedSuggestionProvider.suggest(Stage.VALID_SETTINGS, builder));
	}

	private static int add(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		return add(context.getSource(), StringArgumentType.getString(context, "name"), getOrLoadBlockPos(context, "from"), getOrLoadBlockPos(context, "to"));
	}

	private static int remove(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		return remove(context.getSource(), StringArgumentType.getString(context, "stage"));
	}

	private static int setSetting(CommandContext<CommandSourceStack> context, @Nullable Boolean value) throws CommandSyntaxException
	{
		return setSetting(context.getSource(), StringArgumentType.getString(context, "stage"), StringArgumentType.getString(context, "setting"), value);
	}

	private static int setStageCorner(CommandContext<CommandSourceStack> context, boolean isCornerA) throws CommandSyntaxException
	{
		return setStageCoords(context.getSource(), StringArgumentType.getString(context, "stage"), BlockPosArgument.getLoadedBlockPos(context, "pos"), isCornerA);
	}

	private static int getStageCorner(CommandContext<CommandSourceStack> context, boolean isCornerA) throws CommandSyntaxException
	{
		return getStageCoords(context.getSource(), StringArgumentType.getString(context, "stage"), isCornerA);
	}

	private static int getSetting(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		return getSetting(context.getSource(), StringArgumentType.getString(context, "stage"), StringArgumentType.getString(context, "setting"));
	}


	private static int setTeam(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		return setTeam(context.getSource(), StringArgumentType.getString(context, "stage"), StringArgumentType.getString(context, "teamName"), InkColorArgument.getInkColor(context, "teamColor"));
	}

	private static int getTeam(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		return getTeam(context.getSource(), StringArgumentType.getString(context, "stage"), StringArgumentType.getString(context, "teamName"));
	}

	private static int removeTeam(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		return removeTeam(context.getSource(), StringArgumentType.getString(context, "stage"), StringArgumentType.getString(context, "teamName"));
	}

	private static int warp(CommandContext<CommandSourceStack> context, boolean setSpawn) throws CommandSyntaxException {
		return warpPlayers(context.getSource(), StringArgumentType.getString(context, "stage"), EntityArgument.getPlayers(context, "players"), setSpawn);
	}
	private static int warp(CommandContext<CommandSourceStack> context, boolean setSpawn, int color) throws CommandSyntaxException {
		return warpPlayers(context.getSource(), StringArgumentType.getString(context, "stage"), EntityArgument.getPlayers(context, "players"), setSpawn, color);
	}
	private static int warpAny(CommandContext<CommandSourceStack> context, boolean setSpawn) throws CommandSyntaxException {
		return warpPlayersToAny(context.getSource(), StringArgumentType.getString(context, "stage"), EntityArgument.getPlayers(context, "players"), setSpawn);
	}

	private static int warpToTeam(CommandContext<CommandSourceStack> context, boolean setSpawn, String team) throws CommandSyntaxException
	{

		String stageId = StringArgumentType.getString(context, "stage");
		HashMap<String, Stage> stages = SaveInfoCapability.get(context.getSource().getServer()).getStages();
		if (!stages.containsKey(stageId))
			throw STAGE_NOT_FOUND.create(stageId);

		Stage stage = stages.get(stageId);

		if(!stage.hasTeam(team))
			throw TEAM_NOT_FOUND.create(new Object[] {team, stageId});

		return warpPlayers(context.getSource(), stageId, EntityArgument.getPlayers(context, "players"), setSpawn, stage.getTeamColor(team));
	}

	private static int warpSelf(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		return warpPlayers(context.getSource(), StringArgumentType.getString(context, "stage"), Collections.singleton(context.getSource().getPlayerOrException()), false);
	}


	private static int add(CommandSourceStack source, String stageId, BlockPos from, BlockPos to) throws CommandSyntaxException {
		HashMap<String, Stage> stages = SaveInfoCapability.get(source.getServer()).getStages();
		
		if(stages.containsKey(stageId))
			throw STAGE_ALREADY_EXISTS.create(stageId);

		stages.put(stageId, new Stage(source.getLevel(), from, to, stageId));

		source.sendSuccess(new TranslatableComponent("commands.stage.add.success", stageId), true);

		SplatcraftPacketHandler.sendToAll(new UpdateStageListPacket(stages));

		return 1;
	}

	private static int remove(CommandSourceStack source, String stageId) throws CommandSyntaxException {
		HashMap<String, Stage> stages = SaveInfoCapability.get(source.getServer()).getStages();

		if(!stages.containsKey(stageId))
			throw STAGE_NOT_FOUND.create(stageId);

		stages.remove(stageId);

		source.sendSuccess(new TranslatableComponent("commands.stage.remove.success", stageId), true);

		SplatcraftPacketHandler.sendToAll(new UpdateStageListPacket(stages));

		return 1;
	}

	private static int setSetting(CommandSourceStack source, String stageId, String setting, @Nullable Boolean value) throws CommandSyntaxException
	{
		HashMap<String, Stage> stages = SaveInfoCapability.get(source.getServer()).getStages();

		if(!stages.containsKey(stageId))
			throw STAGE_NOT_FOUND.create(stageId);

		if(!Stage.VALID_SETTINGS.contains(setting))
			throw SETTING_NOT_FOUND.create(setting);

		Stage stage = stages.get(stageId);

		stage.applySetting(setting, value);

		if(value == null)
			source.sendSuccess(new TranslatableComponent("commands.stage.setting.success.default", setting, stageId), true);
		else source.sendSuccess(new TranslatableComponent("commands.stage.setting.success", setting, stageId, value), true);

		SplatcraftPacketHandler.sendToAll(new UpdateStageListPacket(stages));

		return 1;
	}

	private static int getSetting(CommandSourceStack source, String stageId, String setting) throws CommandSyntaxException
	{
		HashMap<String, Stage> stages = SaveInfoCapability.get(source.getServer()).getStages();

		if(!stages.containsKey(stageId))
			throw STAGE_NOT_FOUND.create(stageId);

		if(!Stage.VALID_SETTINGS.contains(setting))
			throw SETTING_NOT_FOUND.create(setting);

		Stage stage = stages.get(stageId);


		if(!stage.hasSetting(setting))
			source.sendSuccess(new TranslatableComponent("commands.stage.setting.get.default", setting, stageId), true);
		else source.sendSuccess(new TranslatableComponent("commands.stage.setting.get", setting, stageId, stage.getSetting(setting)), true);

		return 1;
	}

	private static int setTeam(CommandSourceStack source, String stageId, String teamId, int teamColor) throws CommandSyntaxException
	{
		HashMap<String, Stage> stages = SaveInfoCapability.get(source.getServer()).getStages();

		if(!stages.containsKey(stageId))
			throw STAGE_NOT_FOUND.create(stageId);

		Stage stage = stages.get(stageId);
		Level stageLevel = source.getServer().getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, stage.dimID));

		BlockPos blockpos2 = new BlockPos(Math.min(stage.cornerA.getX(), stage.cornerB.getX()), Math.min(stage.cornerB.getY(), stage.cornerA.getY()), Math.min(stage.cornerA.getZ(), stage.cornerB.getZ()));
		BlockPos blockpos3 = new BlockPos(Math.max(stage.cornerA.getX(), stage.cornerB.getX()), Math.max(stage.cornerB.getY(), stage.cornerA.getY()), Math.max(stage.cornerA.getZ(), stage.cornerB.getZ()));

		int affectedBlocks = 0;

		for (int x = blockpos2.getX(); x <= blockpos3.getX(); x++)
			for (int y = blockpos2.getY(); y <= blockpos3.getY(); y++)
				for (int z = blockpos2.getZ(); z <= blockpos3.getZ(); z++)
				{
					BlockPos pos = new BlockPos(x, y, z);

					if (stageLevel.getBlockEntity(pos) instanceof InkColorTileEntity)
					{
						InkColorTileEntity te = ((InkColorTileEntity) stageLevel.getBlockEntity(pos));
						if(te.getColor() == teamColor && !te.getTeam().equals(teamId))
						{
							te.setTeam(teamId);
							affectedBlocks++;
						}
					}
				}

		stage.setTeamColor(teamId, teamColor);
		source.sendSuccess(new TranslatableComponent("commands.stage.teams.set.success", affectedBlocks, stageId, new TextComponent(teamId).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(teamColor)))), true);

		SplatcraftPacketHandler.sendToAll(new UpdateStageListPacket(stages));

		return 1;
	}

	private static int getTeam(CommandSourceStack source, String stageId, String teamId) throws CommandSyntaxException {
		HashMap<String, Stage> stages = SaveInfoCapability.get(source.getServer()).getStages();

		if(!stages.containsKey(stageId))
			throw STAGE_NOT_FOUND.create(stageId);

		Stage stage = stages.get(stageId);

		if(!stage.hasTeam(teamId))
			throw TEAM_NOT_FOUND.create(new Object[] {teamId, stageId});

		int teamColor = stage.getTeamColor(teamId);

		source.sendSuccess(new TranslatableComponent("commands.stage.teams.get.success", teamId, stageId, ColorUtils.getFormatedColorName(teamColor, false)), true);
		return teamColor;
	}

	private static int removeTeam(CommandSourceStack source, String stageId, String teamId) throws CommandSyntaxException {
		HashMap<String, Stage> stages = SaveInfoCapability.get(source.getServer()).getStages();

		if(!stages.containsKey(stageId))
			throw STAGE_NOT_FOUND.create(stageId);

		Stage stage = stages.get(stageId);

		if(!stage.hasTeam(teamId))
			throw TEAM_NOT_FOUND.create(new Object[] {teamId, stageId});

		int teamColor = stage.getTeamColor(teamId);


		Level stageLevel = source.getServer().getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, stage.dimID));
		BlockPos blockpos2 = new BlockPos(Math.min(stage.cornerA.getX(), stage.cornerB.getX()), Math.min(stage.cornerB.getY(), stage.cornerA.getY()), Math.min(stage.cornerA.getZ(), stage.cornerB.getZ()));
		BlockPos blockpos3 = new BlockPos(Math.max(stage.cornerA.getX(), stage.cornerB.getX()), Math.max(stage.cornerB.getY(), stage.cornerA.getY()), Math.max(stage.cornerA.getZ(), stage.cornerB.getZ()));

		int affectedBlocks = 0;

		for (int x = blockpos2.getX(); x <= blockpos3.getX(); x++)
			for (int y = blockpos2.getY(); y <= blockpos3.getY(); y++)
				for (int z = blockpos2.getZ(); z <= blockpos3.getZ(); z++)
				{
					BlockPos pos = new BlockPos(x, y, z);

					if (stageLevel.getBlockEntity(pos) instanceof InkColorTileEntity)
					{
						InkColorTileEntity te = ((InkColorTileEntity) stageLevel.getBlockEntity(pos));
						if(te.getColor() == teamColor && !te.getTeam().equals(teamId))
						{
							te.setTeam("");
							affectedBlocks++;
						}
					}
				}

		stage.removeTeam(teamId);

		source.sendSuccess(new TranslatableComponent("commands.stage.teams.remove.success", new TextComponent(teamId).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(teamColor))), stageId, affectedBlocks), true);
		return teamColor;
	}

	private static int warpPlayers(CommandSourceStack source, String stageId, Collection<ServerPlayer> targets, boolean setSpawn) throws CommandSyntaxException
	{
		return warpPlayers(source, stageId, targets, setSpawn, -1);
	}

	private static int warpPlayers(CommandSourceStack source, String stageId, Collection<ServerPlayer> targets, boolean setSpawn, int color) throws CommandSyntaxException
	{
		HashMap<String, Stage> stages = SaveInfoCapability.get(source.getServer()).getStages();

		if (!stages.containsKey(stageId))
			throw STAGE_NOT_FOUND.create(stageId);

		Stage stage = stages.get(stageId);
		HashMap<Integer, ArrayList<SpawnPadTileEntity>> spawnPads = stage.getSpawnPads(source.getLevel());
		Level stageLevel = source.getServer().getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, stage.dimID));

		if(spawnPads.isEmpty())
			throw NO_SPAWN_PADS_FOUND.create(stageId);

		HashMap<Integer, Integer> playersTeleported = new HashMap<>();
		for (ServerPlayer player : targets) {
			int playerColor = color == -1 ? ColorUtils.getPlayerColor(player) : color;

			if (spawnPads.containsKey(playerColor))
			{
				if (!playersTeleported.containsKey(playerColor))
					playersTeleported.put(playerColor, 0);

				SpawnPadTileEntity te = spawnPads.get(playerColor).get(playersTeleported.get(playerColor) % spawnPads.get(playerColor).size());

				float pitch = te.getLevel().getBlockState(te.getBlockPos()).getValue(SpawnPadBlock.DIRECTION).toYRot();

				if (stageLevel == player.level)
					player.connection.teleport(te.getBlockPos().getX() + .5, te.getBlockPos().getY() + .5, te.getBlockPos().getZ() + .5, pitch, 0);
				else
					player.teleportTo((ServerLevel) stageLevel, te.getBlockPos().getX() + .5, te.getBlockPos().getY() + .5, te.getBlockPos().getZ(), pitch, 0);

				if(setSpawn)
					player.setRespawnPosition(player.level.dimension(), te.getBlockPos(), player.level.getBlockState(te.getBlockPos()).getValue(SpawnPadBlock.DIRECTION).toYRot(), false, true);

				playersTeleported.put(playerColor, playersTeleported.get(playerColor) + 1);
			}
		}

		int result = 0;
		for(int i : playersTeleported.values())
			result += i;

		if(result == 0)
			throw NO_PLAYERS_FOUND.create(stageId);

		source.sendSuccess(new TranslatableComponent("commands.stage.warp.success", result, stageId), true);
		return result;
	}

	private static int warpPlayersToAny(CommandSourceStack source, String stageId, Collection<ServerPlayer> targets, boolean setSpawn) throws CommandSyntaxException
	{
		HashMap<String, Stage> stages = SaveInfoCapability.get(source.getServer()).getStages();

		if (!stages.containsKey(stageId))
			throw STAGE_NOT_FOUND.create(stageId);

		Stage stage = stages.get(stageId);
		Level stageLevel = source.getServer().getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, stage.dimID));
		ArrayList<SpawnPadTileEntity> spawnPads = new ArrayList<>(stage.getAllSpawnPads(source.getLevel()));

		if(spawnPads.isEmpty())
			throw NO_SPAWN_PADS_FOUND.create(stageId);

		int playersTeleported = 0;
		for (ServerPlayer player : targets) {

				SpawnPadTileEntity te = spawnPads.get(playersTeleported % spawnPads.size());

				float pitch = te.getLevel().getBlockState(te.getBlockPos()).getValue(SpawnPadBlock.DIRECTION).toYRot();

				if (stageLevel == player.level)
					player.connection.teleport(te.getBlockPos().getX() + .5, te.getBlockPos().getY() + .5, te.getBlockPos().getZ() + .5, pitch, 0);
				else
					player.teleportTo((ServerLevel) stageLevel, te.getBlockPos().getX() + .5, te.getBlockPos().getY() + .5, te.getBlockPos().getZ(), pitch, 0);

				if(setSpawn)
					player.setRespawnPosition(player.level.dimension(), te.getBlockPos(), player.level.getBlockState(te.getBlockPos()).getValue(SpawnPadBlock.DIRECTION).toYRot(), false, true);

				playersTeleported++;

		}

		int result = playersTeleported;

		if(result == 0)
			throw NO_PLAYERS_FOUND.create(stageId);

		source.sendSuccess(new TranslatableComponent("commands.stage.warp.success", result, stageId), true);
		return result;
	}

	private static int setStageCoords(CommandSourceStack source, String stageId, BlockPos pos, boolean isCornerA) throws CommandSyntaxException {
		HashMap<String, Stage> stages = SaveInfoCapability.get(source.getServer()).getStages();

		if (!stages.containsKey(stageId))
			throw STAGE_NOT_FOUND.create(stageId);

		Stage stage = stages.get(stageId);

		if(isCornerA)
			stage.updateBounds(source.getLevel(), pos, stage.cornerB);
		else stage.updateBounds(source.getLevel(), stage.cornerA, pos);

		SplatcraftPacketHandler.sendToAll(new UpdateStageListPacket(stages));
		source.sendSuccess(new TranslatableComponent("commands.stage.setting.area.success", isCornerA ? "A" : "B", stageId, pos.getX(), pos.getY(), pos.getZ()), true);
		return 1;
	}

	private static int getStageCoords(CommandSourceStack source, String stageId, boolean isCornerA) throws CommandSyntaxException {
		HashMap<String, Stage> stages = SaveInfoCapability.get(source.getServer()).getStages();

		if (!stages.containsKey(stageId))
			throw STAGE_NOT_FOUND.create(stageId);

		Stage stage = stages.get(stageId);

		BlockPos pos = isCornerA ? stage.cornerA : stage.cornerB;

		source.sendSuccess(new TranslatableComponent("commands.stage.setting.area.get", isCornerA ? "A" : "B", stageId, pos.getX(), pos.getY(), pos.getZ()), true);

		return 1;
	}

	public static BlockPos getOrLoadBlockPos(CommandContext<CommandSourceStack> p_118243_, String p_118244_) throws CommandSyntaxException {
		BlockPos blockpos = p_118243_.getArgument(p_118244_, Coordinates.class).getBlockPos(p_118243_.getSource());
		 if (!p_118243_.getSource().getUnsidedLevel().isInWorldBounds(blockpos)) {
			throw BlockPosArgument.ERROR_OUT_OF_WORLD.create();
		} else {
			return blockpos;
		}
	}
}
