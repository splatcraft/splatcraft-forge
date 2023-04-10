package net.splatcraft.forge.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.splatcraft.forge.data.Stage;
import net.splatcraft.forge.data.capabilities.saveinfo.SaveInfoCapability;
import net.splatcraft.forge.items.remotes.RemoteItem;
import net.splatcraft.forge.items.remotes.TurfScannerItem;
import net.splatcraft.forge.registries.SplatcraftStats;

import java.util.Collection;
import java.util.Collections;

public class ScanTurfCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(Commands.literal("scanturf").requires(commandSource -> commandSource.hasPermission(2)).then(Commands.argument("from", BlockPosArgument.blockPos()).then(Commands.argument("to", BlockPosArgument.blockPos())
                .executes(ScanTurfCommand::executeOnSelf)
                .then(Commands.argument("target", EntityArgument.players()).executes(context -> execute(context, 0))
                        .then(Commands.literal("topDown").executes(context -> execute(context, 0)))
                        .then(Commands.literal("multiLayered").executes(context -> execute(context, 1))))
                ))
                .then(StageCommand.stageId("stage").executes(ScanTurfCommand::executeStageOnSelf)
                .then(Commands.argument("target", EntityArgument.players()).executes(context -> executeStage(context, 0))
                        .then(Commands.literal("topDown").executes(context -> executeStage(context, 0)))
                        .then(Commands.literal("multiLayered").executes(context -> executeStage(context, 1))))));
    }

    private static int executeStageOnSelf(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
    {
        return executeStage(context.getSource(), StringArgumentType.getString(context, "stage"), 0,
                context.getSource().getEntity() instanceof ServerPlayer ? Collections.singletonList((ServerPlayer) context.getSource().getEntity()) : RemoteItem.ALL_TARGETS);
    }
    private static int executeOnSelf(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
    {
        return executeStage(context.getSource(), StringArgumentType.getString(context, "stage"), 0,
                context.getSource().getEntity() instanceof ServerPlayer ? Collections.singletonList((ServerPlayer) context.getSource().getEntity()) : RemoteItem.ALL_TARGETS);
    }

    private static int executeStage(CommandContext<CommandSourceStack> context, int mode) throws CommandSyntaxException
    {
        return executeStage(context.getSource(), StringArgumentType.getString(context, "stage"), mode, EntityArgument.getPlayers(context, "target"));
    }

    private static int execute(CommandContext<CommandSourceStack> context, int mode) throws CommandSyntaxException
    {
        return execute(context.getSource(), StageCommand.getOrLoadBlockPos(context, "from"), StageCommand.getOrLoadBlockPos(context, "to"), mode, EntityArgument.getPlayers(context, "target"));
    }

    private static int executeStage(CommandSourceStack source, String stageId, int mode, Collection<ServerPlayer> targets) throws CommandSyntaxException {

        Stage stage = SaveInfoCapability.get(source.getServer()).getStages().get(stageId);

        if(stage == null)
            throw StageCommand.STAGE_NOT_FOUND.create(stageId);

        int result = execute(source, stage.cornerA, stage.cornerB, mode, targets);

        for(String team : stage.getTeamIds())
        {
            if(stage.getTeamColor(team) == result)
                source.getLevel().getScoreboard().forAllObjectives(Stats.CUSTOM.get(SplatcraftStats.TURF_WARS_WON), "["+team+"]", score -> score.add(1));
        }

        return result;
    }

    private static int execute(CommandSourceStack source, BlockPos from, BlockPos to, int mode, Collection<ServerPlayer> targets) throws CommandSyntaxException
    {
        RemoteItem.RemoteResult result = TurfScannerItem.scanTurf(source.getLevel(), source.getLevel(), from, to, mode, targets);

        source.sendSuccess(result.getOutput(), true);

        return result.getCommandResult();
    }
}
