package net.splatcraft.forge.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.splatcraft.forge.commands.arguments.StageIDArgument;
import net.splatcraft.forge.commands.arguments.StageSettingArgument;
import net.splatcraft.forge.data.Stage;
import net.splatcraft.forge.data.capabilities.saveinfo.SaveInfoCapability;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.s2c.UpdateStageListPacket;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class StageCommand
{

	public static void register(CommandDispatcher<CommandSource> dispatcher)
	{
		dispatcher.register(Commands.literal("stage").requires(commandSource -> commandSource.hasPermission(2))
				.then(Commands.literal("add")
						.then(Commands.argument("name", new StageIDArgument(false))
						.then(Commands.argument("from", BlockPosArgument.blockPos())
						.then(Commands.argument("to", BlockPosArgument.blockPos()).executes(StageCommand::add))
				))).then(Commands.literal("remove")
						.then(Commands.argument("stage", new StageIDArgument(true)).executes(StageCommand::remove)
				))//.then(Commands.literal("list"))
				.then(Commands.literal("settings").then(Commands.argument("stage", new StageIDArgument(true))
						.then(Commands.argument("setting", new StageSettingArgument())
								.then(Commands.literal("true").executes(context -> setSetting(context, true)))
								.then(Commands.literal("false").executes(context -> setSetting(context, false)))
								.then(Commands.literal("default").executes(context -> setSetting(context, null)))
						))));
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

	private static int add(CommandSource source, String name, BlockPos from, BlockPos to) throws CommandSyntaxException {
		HashMap<String, Stage> stages = SaveInfoCapability.get(source.getServer()).getStages();
		
		if(stages.containsKey(name))
			throw StageIDArgument.STAGE_ALREADY_EXISTS.create(name);

		stages.put(name, new Stage(source.getLevel(), from, to));

		source.sendSuccess(new TranslationTextComponent("commands.stage.add.success", name), true);

		SplatcraftPacketHandler.sendToAll(new UpdateStageListPacket(stages));

		return 1;
	}

	private static int remove(CommandSource source, String name) throws CommandSyntaxException {
		HashMap<String, Stage> stages = SaveInfoCapability.get(source.getServer()).getStages();

		if(!stages.containsKey(name))
			throw StageIDArgument.STAGE_NOT_FOUND.create(name);

		stages.remove(name);

		source.sendSuccess(new TranslationTextComponent("commands.stage.remove.success", name), true);

		SplatcraftPacketHandler.sendToAll(new UpdateStageListPacket(stages));

		return 1;
	}

	private static int setSetting(CommandSource source, String name, String setting, @Nullable Boolean value) throws CommandSyntaxException {
		HashMap<String, Stage> stages = SaveInfoCapability.get(source.getServer()).getStages();

		if(!stages.containsKey(name))
			throw StageIDArgument.STAGE_NOT_FOUND.create(name);

		if(!Stage.VALID_SETTINGS.contains(setting))
			throw StageSettingArgument.SETTING_NOT_FOUND.create(setting);

		Stage stage = stages.get(name);

		stage.applySetting(setting, value);

		if(value == null)
			source.sendSuccess(new TranslationTextComponent("commands.stage.setting.success.default", setting, name), true);
		else source.sendSuccess(new TranslationTextComponent("commands.stage.setting.success", setting, name, value), true);

		return 1;
	}
}
