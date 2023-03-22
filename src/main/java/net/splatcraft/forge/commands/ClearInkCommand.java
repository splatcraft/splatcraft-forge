package net.splatcraft.forge.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.util.math.BlockPos;
import net.splatcraft.forge.commands.arguments.StageIDArgument;
import net.splatcraft.forge.data.Stage;
import net.splatcraft.forge.data.capabilities.saveinfo.SaveInfoCapability;
import net.splatcraft.forge.items.remotes.InkDisruptorItem;
import net.splatcraft.forge.items.remotes.RemoteItem;

public class ClearInkCommand
{
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(Commands.literal("clearink").requires(commandSource -> commandSource.hasPermission(2))
                .then(Commands.argument("from", BlockPosArgument.blockPos())
                        .then(Commands.argument("to", BlockPosArgument.blockPos())
                                .executes(ClearInkCommand::execute)
                        )).then(Commands.argument("stage", new StageIDArgument(true)).executes(ClearInkCommand::executeStage)));
    }

    private static int execute(CommandContext<CommandSource> context) throws CommandSyntaxException
    {
        return execute(context.getSource(), BlockPosArgument.getOrLoadBlockPos(context, "from"), BlockPosArgument.getOrLoadBlockPos(context, "to"));
    }

    private static int executeStage(CommandContext<CommandSource> context) throws CommandSyntaxException
    {
        Stage stage = SaveInfoCapability.get(context.getSource().getServer()).getStages().get(StringArgumentType.getString(context, "stage"));

        if(stage == null)
            throw StageIDArgument.STAGE_NOT_FOUND.create(stage);

        return execute(context.getSource(), stage.cornerA, stage.cornerB);
    }

    private static int execute(CommandSource source, BlockPos from, BlockPos to)
    {
        RemoteItem.RemoteResult result = InkDisruptorItem.clearInk(source.getLevel(), from, to);

        source.sendSuccess(result.getOutput(), true);
        return result.getCommandResult();
    }
}
