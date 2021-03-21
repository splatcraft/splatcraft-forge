package com.cibernet.splatcraft.commands;

import com.cibernet.splatcraft.commands.arguments.InkColorArgument;
import com.cibernet.splatcraft.items.remotes.ColorChangerItem;
import com.cibernet.splatcraft.items.remotes.RemoteItem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.util.math.BlockPos;

public class ReplaceColorCommand
{
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(Commands.literal("replacecolor")
                .then(Commands.argument("from", BlockPosArgument.blockPos())
                        .then(Commands.argument("to", BlockPosArgument.blockPos())
                                .then(Commands.argument("color", InkColorArgument.inkColor())
                                        .executes(context -> execute(context, 0))
                                        .then(Commands.literal("only").then(Commands.argument("affectedColor", InkColorArgument.inkColor()).executes(context -> execute(context, 1))))
                                        .then(Commands.literal("keep").then(Commands.argument("affectedColor", InkColorArgument.inkColor()).executes(context -> execute(context, 2))))
                                )))
        );
    }

    public static int execute(CommandContext<CommandSource> context, int mode) throws CommandSyntaxException
    {
        if (mode == 0)
        {
            return execute(context.getSource(), BlockPosArgument.getBlockPos(context, "from"), BlockPosArgument.getBlockPos(context, "to"), InkColorArgument.getInkColor(context, "color"), -1, mode);
        }
        return execute(context.getSource(), BlockPosArgument.getBlockPos(context, "from"), BlockPosArgument.getBlockPos(context, "to"), InkColorArgument.getInkColor(context, "color"), InkColorArgument.getInkColor(context, "affectedColor"), mode);
    }

    public static int execute(CommandSource source, BlockPos from, BlockPos to, int color, int affectedColor, int mode)
    {
        RemoteItem.RemoteResult result = ColorChangerItem.replaceColor(source.getWorld(), from, to, color, mode, affectedColor);

        source.sendFeedback(result.getOutput(), true);
        return result.getCommandResult();
    }
}
