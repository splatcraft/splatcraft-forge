package net.splatcraft.forge.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.util.math.BlockPos;
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
                        )));
    }

    private static int execute(CommandContext<CommandSource> context) throws CommandSyntaxException
    {
        return execute(context.getSource(), BlockPosArgument.getLoadedBlockPos(context, "from"), BlockPosArgument.getLoadedBlockPos(context, "to"));
    }

    private static int execute(CommandSource source, BlockPos from, BlockPos to)
    {
        RemoteItem.RemoteResult result = InkDisruptorItem.clearInk(source.getLevel(), from, to);

        source.sendSuccess(result.getOutput(), true);
        return result.getCommandResult();
    }
}
