package com.cibernet.splatcraft.commands;

import com.cibernet.splatcraft.items.remotes.RemoteItem;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class ClearInkCommand
{
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(Commands.literal("clearink")
                .then(Commands.argument("from", BlockPosArgument.blockPos())
                        .then(Commands.argument("to", BlockPosArgument.blockPos())
                                .executes(ClearInkCommand::execute)
                        )));
    }

    private static int execute(CommandContext<CommandSource> context) throws CommandSyntaxException
    {
        return execute(context.getSource(), BlockPosArgument.getBlockPos(context, "from"), BlockPosArgument.getBlockPos(context, "to"));
    }

    private static int execute(CommandSource source, BlockPos from, BlockPos to)
    {
        RemoteItem.RemoteResult result = SplatcraftItems.inkDisruptor.onRemoteUse(source.getWorld(), from, to, ItemStack.EMPTY, -1, 0);

        source.sendFeedback(result.getOutput(), true);
        return result.getCommandResult();
    }
}
