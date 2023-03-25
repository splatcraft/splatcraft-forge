package net.splatcraft.forge.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.util.math.BlockPos;
import net.splatcraft.forge.commands.arguments.InkColorArgument;
import net.splatcraft.forge.data.Stage;
import net.splatcraft.forge.data.capabilities.saveinfo.SaveInfoCapability;
import net.splatcraft.forge.items.remotes.ColorChangerItem;
import net.splatcraft.forge.items.remotes.RemoteItem;

public class ReplaceColorCommand
{
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(Commands.literal("replacecolor").requires(commandSource -> commandSource.hasPermission(2))
                .then(Commands.argument("from", BlockPosArgument.blockPos()).then(Commands.argument("to", BlockPosArgument.blockPos())
                                .then(Commands.argument("color", InkColorArgument.inkColor())
                                        .executes(context -> execute(context, 0))
                                        .then(Commands.literal("only").then(Commands.argument("affectedColor", InkColorArgument.inkColor()).executes(context -> execute(context, 1))))
                                        .then(Commands.literal("keep").then(Commands.argument("affectedColor", InkColorArgument.inkColor()).executes(context -> execute(context, 2))))
                                )))
                .then(StageCommand.stageId("stage")
                                .then(Commands.argument("color", InkColorArgument.inkColor())
                                        .executes(context -> executeStage(context, 0))
                                        .then(Commands.literal("only")
                                                .then(StageCommand.stageTeam("affectedTeam", "stage").executes(context -> executeStageForTeam(context, 1)))
                                                .then(Commands.argument("affectedColor", InkColorArgument.inkColor()).executes(context -> executeStage(context, 1))))
                                        .then(Commands.literal("keep")
                                                .then(StageCommand.stageTeam("affectedTeam", "stage").executes(context -> executeStageForTeam(context, 2)))
                                                .then(Commands.argument("affectedColor", InkColorArgument.inkColor()).executes(context -> executeStage(context, 2))))
                                ))
        );
    }

    public static int executeStage(CommandContext<CommandSource> context, int mode) throws CommandSyntaxException
    {
        String stageId = StringArgumentType.getString(context, "stage");
        Stage stage = SaveInfoCapability.get(context.getSource().getServer()).getStages().get(stageId);

        if(stage == null)
            throw StageCommand.STAGE_NOT_FOUND.create(stage);

        if (mode == 0)
        {
            return execute(context.getSource(), stage.cornerA, stage.cornerB, InkColorArgument.getInkColor(context, "color"), -1, mode, stageId, "");
        }
        return execute(context.getSource(), stage.cornerA, stage.cornerB, InkColorArgument.getInkColor(context, "color"), InkColorArgument.getInkColor(context, "affectedColor"), mode, stageId, "");
    }

    public static int executeStageForTeam(CommandContext<CommandSource> context, int mode) throws CommandSyntaxException
    {
        String stageId = StringArgumentType.getString(context, "stage");
        Stage stage = SaveInfoCapability.get(context.getSource().getServer()).getStages().get(stageId);

        if(stage == null)
            throw StageCommand.STAGE_NOT_FOUND.create(stage);

        int color = InkColorArgument.getInkColor(context, "color");
        String team = StringArgumentType.getString(context, "affectedTeam");

        if (mode == 0)
            return execute(context.getSource(), stage.cornerA, stage.cornerB, color, -1, mode, stageId, team);

        if(!stage.hasTeam(team))
            throw StageCommand.TEAM_NOT_FOUND.create(new Object[] {team, stageId});

        int teamColor = stage.getTeamColor(team);
        stage.setTeamColor(team, color);

        return execute(context.getSource(), stage.cornerA, stage.cornerB, color, teamColor, mode, stageId, team);
    }

    public static int execute(CommandContext<CommandSource> context, int mode) throws CommandSyntaxException
    {
        if (mode == 0)
        {
            return execute(context.getSource(), BlockPosArgument.getLoadedBlockPos(context, "from"), BlockPosArgument.getLoadedBlockPos(context, "to"), InkColorArgument.getInkColor(context, "color"), -1, mode, "", "");
        }
        return execute(context.getSource(), BlockPosArgument.getOrLoadBlockPos(context, "from"), BlockPosArgument.getOrLoadBlockPos(context, "to"), InkColorArgument.getInkColor(context, "color"), InkColorArgument.getInkColor(context, "affectedColor"), mode, "", "");
    }

    public static int execute(CommandSource source, BlockPos from, BlockPos to, int color, int affectedColor, int mode, String affectedStage, String affectedTeam)
    {
        RemoteItem.RemoteResult result = ColorChangerItem.replaceColor(source.getLevel(), from, to, color, mode, affectedColor, affectedStage, affectedTeam);

        source.sendSuccess(result.getOutput(), true);
        return result.getCommandResult();
    }
}
