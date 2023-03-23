package net.splatcraft.forge.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.splatcraft.forge.blocks.SpawnPadBlock;
import net.splatcraft.forge.commands.arguments.InkColorArgument;
import net.splatcraft.forge.commands.arguments.StageSettingArgument;
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

	private static final DynamicCommandExceptionType NO_SPAWN_PADS_FOUND = new DynamicCommandExceptionType(p_208663_0_ -> new TranslationTextComponent("arg.stageWarp.noSpawnPads", p_208663_0_));
	private static final DynamicCommandExceptionType NO_PLAYERS_FOUND = new DynamicCommandExceptionType(p_208663_0_ -> new TranslationTextComponent("arg.stageWarp.noPlayers", p_208663_0_));
	public static final DynamicCommandExceptionType TEAM_NOT_FOUND = new DynamicCommandExceptionType(p_208663_0_ -> new TranslationTextComponent("arg.stageTeam.notFound", p_208663_0_));
	public static final DynamicCommandExceptionType STAGE_NOT_FOUND = new DynamicCommandExceptionType(p_208663_0_ -> new TranslationTextComponent("arg.stage.notFound", p_208663_0_));
	public static final DynamicCommandExceptionType STAGE_ALREADY_EXISTS = new DynamicCommandExceptionType(p_208663_0_ -> new TranslationTextComponent("arg.stage.alreadyExists", p_208663_0_));


	public static void register(CommandDispatcher<CommandSource> dispatcher)
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
						.then(Commands.argument("setting", new StageSettingArgument())
								.then(Commands.literal("true").executes(context -> setSetting(context, true)))
								.then(Commands.literal("false").executes(context -> setSetting(context, false)))
								.then(Commands.literal("default").executes(context -> setSetting(context, null))))))
				.then(Commands.literal("teams").then(stageId("stage")
						.then(Commands.literal("set")
								.then(stageTeam("teamName", "stage")
										.then(Commands.argument("teamColor", InkColorArgument.inkColor()).executes(StageCommand::setTeam))))
						.then(Commands.literal("get")
								.then(stageTeam("teamName", "stage").executes(StageCommand::getTeam)))))
				.then(Commands.literal("warp").then(stageId("stage").executes(StageCommand::warpSelf)
						.then(Commands.argument("players", EntityArgument.players()).executes(context -> warp(context, false))
								.then(Commands.literal("setSpawn").executes(context -> warp(context, true))))))
		);
	}

	public static RequiredArgumentBuilder<CommandSource, String> stageId(String argumentName)
	{
		return Commands.argument(argumentName, StringArgumentType.word()).suggests((context, builder) -> ISuggestionProvider.suggest(ClientUtils.clientStages.keySet(), builder));
	}

	public static RequiredArgumentBuilder<CommandSource, String> stageTeam(String argumentName, String stageArgumentName)
	{
		return Commands.argument(argumentName, StringArgumentType.word()).suggests((context, builder) ->
		{
			try
			{
				Stage stage = ClientUtils.clientStages.get(StringArgumentType.getString(context, stageArgumentName));
				if(stage == null)
					return Suggestions.empty();

				return ISuggestionProvider.suggest(stage.getTeamIds(), builder);
			} catch (IllegalArgumentException ignored) {} //happens when used inside execute, vanilla won't bother to fix it so neither will i >_>

			return Suggestions.empty();
		});
	}

	private static int add(CommandContext<CommandSource> context) throws CommandSyntaxException {
		return add(context.getSource(), StringArgumentType.getString(context, "name"), BlockPosArgument.getOrLoadBlockPos(context, "from"), BlockPosArgument.getOrLoadBlockPos(context, "to"));
	}

	private static int remove(CommandContext<CommandSource> context) throws CommandSyntaxException {
		return remove(context.getSource(), StringArgumentType.getString(context, "stage"));
	}

	private static int setSetting(CommandContext<CommandSource> context, @Nullable Boolean value) throws CommandSyntaxException
	{
		return setSetting(context.getSource(), StringArgumentType.getString(context, "stage"), StringArgumentType.getString(context, "setting"), value);
	}


	private static int setTeam(CommandContext<CommandSource> context) throws CommandSyntaxException {
		return setTeam(context.getSource(), StringArgumentType.getString(context, "stage"), StringArgumentType.getString(context, "teamName"), InkColorArgument.getInkColor(context, "teamColor"));
	}

	private static int getTeam(CommandContext<CommandSource> context) throws CommandSyntaxException {
		return getTeam(context.getSource(), StringArgumentType.getString(context, "stage"), StringArgumentType.getString(context, "teamName"));
	}

	private static int warp(CommandContext<CommandSource> context, boolean setSpawn) throws CommandSyntaxException {
		return warpPlayers(context.getSource(), StringArgumentType.getString(context, "stage"), EntityArgument.getPlayers(context, "players"), setSpawn);
	}

	private static int warpSelf(CommandContext<CommandSource> context) throws CommandSyntaxException {
		return warpPlayers(context.getSource(), StringArgumentType.getString(context, "stage"), Collections.singleton(context.getSource().getPlayerOrException()), false);
	}


	private static int add(CommandSource source, String stageId, BlockPos from, BlockPos to) throws CommandSyntaxException {
		HashMap<String, Stage> stages = SaveInfoCapability.get(source.getServer()).getStages();
		
		if(stages.containsKey(stageId))
			throw STAGE_ALREADY_EXISTS.create(stageId);

		stages.put(stageId, new Stage(source.getLevel(), from, to));

		source.sendSuccess(new TranslationTextComponent("commands.stage.add.success", stageId), true);

		SplatcraftPacketHandler.sendToAll(new UpdateStageListPacket(stages));

		return 1;
	}

	private static int remove(CommandSource source, String stageId) throws CommandSyntaxException {
		HashMap<String, Stage> stages = SaveInfoCapability.get(source.getServer()).getStages();

		if(!stages.containsKey(stageId))
			throw STAGE_NOT_FOUND.create(stageId);

		stages.remove(stageId);

		source.sendSuccess(new TranslationTextComponent("commands.stage.remove.success", stageId), true);

		SplatcraftPacketHandler.sendToAll(new UpdateStageListPacket(stages));

		return 1;
	}

	private static int setSetting(CommandSource source, String stageId, String setting, @Nullable Boolean value) throws CommandSyntaxException {
		HashMap<String, Stage> stages = SaveInfoCapability.get(source.getServer()).getStages();

		if(!stages.containsKey(stageId))
			throw STAGE_NOT_FOUND.create(stageId);

		if(!Stage.VALID_SETTINGS.contains(setting))
			throw StageSettingArgument.SETTING_NOT_FOUND.create(setting);

		Stage stage = stages.get(stageId);

		stage.applySetting(setting, value);

		if(value == null)
			source.sendSuccess(new TranslationTextComponent("commands.stage.setting.success.default", setting, stageId), true);
		else source.sendSuccess(new TranslationTextComponent("commands.stage.setting.success", setting, stageId, value), true);

		SplatcraftPacketHandler.sendToAll(new UpdateStageListPacket(stages));

		return 1;
	}

	private static int setTeam(CommandSource source, String stageId, String teamId, int teamColor) throws CommandSyntaxException {
		HashMap<String, Stage> stages = SaveInfoCapability.get(source.getServer()).getStages();

		if(!stages.containsKey(stageId))
			throw STAGE_NOT_FOUND.create(stageId);

		Stage stage = stages.get(stageId);
		World stageLevel = source.getServer().getLevel(RegistryKey.create(Registry.DIMENSION_REGISTRY, stage.dimID));

		System.out.println(stage.dimID + " " + stageLevel);

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
		source.sendSuccess(new TranslationTextComponent("commands.stage.teams.set.success", affectedBlocks, stageId, new StringTextComponent(teamId).withStyle(Style.EMPTY.withColor(Color.fromRgb(teamColor)))), true);

		SplatcraftPacketHandler.sendToAll(new UpdateStageListPacket(stages));

		return 1;
	}



	private static int getTeam(CommandSource source, String stageId, String teamId) throws CommandSyntaxException {
		HashMap<String, Stage> stages = SaveInfoCapability.get(source.getServer()).getStages();

		if(!stages.containsKey(stageId))
			throw STAGE_NOT_FOUND.create(stageId);

		Stage stage = stages.get(stageId);

		if(!stage.hasTeam(teamId))
			throw TEAM_NOT_FOUND.create(teamId);

		int teamColor = stage.getTeamColor(teamId);

		source.sendSuccess(new TranslationTextComponent("commands.stage.teams.get.success", teamId, stageId, ColorUtils.getFormatedColorName(teamColor, false)), true);
		return teamColor;
	}

	private static int warpPlayers(CommandSource source, String stageId, Collection<ServerPlayerEntity> targets, boolean setSpawn) throws CommandSyntaxException
	{
		HashMap<String, Stage> stages = SaveInfoCapability.get(source.getServer()).getStages();

		if (!stages.containsKey(stageId))
			throw STAGE_NOT_FOUND.create(stageId);

		Stage stage = stages.get(stageId);
		World stageLevel = source.getServer().getLevel(RegistryKey.create(Registry.DIMENSION_REGISTRY, stage.dimID));


		BlockPos blockpos2 = new BlockPos(Math.min(stage.cornerA.getX(), stage.cornerB.getX()), Math.min(stage.cornerB.getY(), stage.cornerA.getY()), Math.min(stage.cornerA.getZ(), stage.cornerB.getZ()));
		BlockPos blockpos3 = new BlockPos(Math.max(stage.cornerA.getX(), stage.cornerB.getX()), Math.max(stage.cornerB.getY(), stage.cornerA.getY()), Math.max(stage.cornerA.getZ(), stage.cornerB.getZ()));

		HashMap<Integer, ArrayList<SpawnPadTileEntity>> spawnPads = new HashMap<>();

		for (int x = blockpos2.getX(); x <= blockpos3.getX(); x++)
			for (int y = blockpos2.getY(); y <= blockpos3.getY(); y++)
				for (int z = blockpos2.getZ(); z <= blockpos3.getZ(); z++) {
					BlockPos pos = new BlockPos(x, y, z);
					if (stageLevel.getBlockEntity(pos) instanceof SpawnPadTileEntity) {
						SpawnPadTileEntity te = (SpawnPadTileEntity) stageLevel.getBlockEntity(pos);

						if(!spawnPads.containsKey(te.getColor()))
							spawnPads.put(te.getColor(), new ArrayList<>());
						spawnPads.get(te.getColor()).add(te);
					}
				}

		if(spawnPads.isEmpty())
			throw NO_SPAWN_PADS_FOUND.create(stageId);

		HashMap<Integer, Integer> playersTeleported = new HashMap<>();
		for (ServerPlayerEntity player : targets) {
			int playerColor = ColorUtils.getPlayerColor(player);

			if (spawnPads.containsKey(playerColor)) {
				if (!playersTeleported.containsKey(playerColor))
					playersTeleported.put(playerColor, 0);

				SpawnPadTileEntity te = spawnPads.get(playerColor).get(playersTeleported.get(playerColor) % spawnPads.get(playerColor).size());

				float pitch = te.getLevel().getBlockState(te.getBlockPos()).getValue(SpawnPadBlock.DIRECTION).toYRot();

				if (stageLevel == player.level)
					player.connection.teleport(te.getBlockPos().getX() + .5, te.getBlockPos().getY() + .5, te.getBlockPos().getZ(), pitch, 0);
				else
					player.teleportTo((ServerWorld) stageLevel, te.getBlockPos().getX() + .5, te.getBlockPos().getY() + .5, te.getBlockPos().getZ(), pitch, 0);

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

		source.sendSuccess(new TranslationTextComponent("commands.stage.warp.success", result, stageId), true);
		return result;
	}
}
