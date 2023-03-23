package net.splatcraft.forge.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.splatcraft.forge.data.Stage;
import net.splatcraft.forge.data.capabilities.saveinfo.SaveInfoCapability;
import net.splatcraft.forge.items.remotes.RemoteItem;
import net.splatcraft.forge.items.remotes.TurfScannerItem;

import java.util.Collection;
import java.util.Collections;

public class ScanTurfCommand
{
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(Commands.literal("scanturf").requires(commandSource -> commandSource.hasPermission(2)).then(Commands.argument("from", BlockPosArgument.blockPos()).then(Commands.argument("to", BlockPosArgument.blockPos())
                .executes(ScanTurfCommand::executeOnSelf)
                .then(Commands.argument("target", EntityArgument.players())
                        .then(Commands.literal("topDown").executes(context -> execute(context, 0)))
                        .then(Commands.literal("multiLayered").executes(context -> execute(context, 1))))
                ))
                .then(StageCommand.stageId("stage").executes(ScanTurfCommand::executeStageOnSelf)
                .then(Commands.argument("target", EntityArgument.players())
                        .then(Commands.literal("topDown").executes(context -> executeStage(context, 0)))
                        .then(Commands.literal("multiLayered").executes(context -> executeStage(context, 1))))));
    }

    private static int executeStageOnSelf(CommandContext<CommandSource> context) throws CommandSyntaxException
    {
        Stage stage = SaveInfoCapability.get(context.getSource().getServer()).getStages().get(StringArgumentType.getString(context, "stage"));

        if(stage == null)
            throw StageCommand.STAGE_NOT_FOUND.create(stage);

        return execute(context.getSource(), stage.cornerA, stage.cornerB, 0,
                context.getSource().getEntity() instanceof ServerPlayerEntity ? Collections.singletonList((ServerPlayerEntity) context.getSource().getEntity()) : RemoteItem.ALL_TARGETS);
    }
    private static int executeOnSelf(CommandContext<CommandSource> context) throws CommandSyntaxException
    {
        return execute(context.getSource(), BlockPosArgument.getLoadedBlockPos(context, "from"), BlockPosArgument.getLoadedBlockPos(context, "to"), 0,
                context.getSource().getEntity() instanceof ServerPlayerEntity ? Collections.singletonList((ServerPlayerEntity) context.getSource().getEntity()) : RemoteItem.ALL_TARGETS);
    }

    private static int executeStage(CommandContext<CommandSource> context, int mode) throws CommandSyntaxException
    {
        Stage stage = SaveInfoCapability.get(context.getSource().getServer()).getStages().get(StringArgumentType.getString(context, "stage"));

        if(stage == null)
            throw StageCommand.STAGE_NOT_FOUND.create(stage);

        return execute(context.getSource(), stage.cornerA, stage.cornerB, mode, EntityArgument.getPlayers(context, "target"));
    }

    private static int execute(CommandContext<CommandSource> context, int mode) throws CommandSyntaxException
    {
        return execute(context.getSource(), BlockPosArgument.getOrLoadBlockPos(context, "from"), BlockPosArgument.getOrLoadBlockPos(context, "to"), mode, EntityArgument.getPlayers(context, "target"));
    }

    private static int execute(CommandSource source, BlockPos from, BlockPos to, int mode, Collection<ServerPlayerEntity> targets) throws CommandSyntaxException
    {
        RemoteItem.RemoteResult result = TurfScannerItem.scanTurf(source.getLevel(), source.getLevel(), from, to, mode, targets);

        source.sendSuccess(result.getOutput() == null ? new TranslationTextComponent("commands.scanturf.success", result.getCommandResult()) : result.getOutput(), true);

        return result.getCommandResult();
    }
}
