package net.splatcraft.forge.commands;

import net.splatcraft.forge.items.remotes.RemoteItem;
import net.splatcraft.forge.items.remotes.TurfScannerItem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;

public class ScanTurfCommand
{
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(Commands.literal("scanturf").then(Commands.argument("from", BlockPosArgument.blockPos()).then(Commands.argument("to", BlockPosArgument.blockPos())
                .executes(context -> execute(context, 0))
                .then(Commands.literal("topDown").executes(context -> execute(context, 0)))
                .then(Commands.literal("multiLayered").executes(context -> execute(context, 1)))
        )));
    }

    private static int execute(CommandContext<CommandSource> context, int mode) throws CommandSyntaxException
    {
        return execute(context.getSource(), BlockPosArgument.getLoadedBlockPos(context, "from"), BlockPosArgument.getLoadedBlockPos(context, "to"), mode);
    }


    private static int execute(CommandSource source, BlockPos from, BlockPos to, int mode) throws CommandSyntaxException
    {
        RemoteItem.RemoteResult result = TurfScannerItem.scanTurf(source.getLevel(), source.getLevel(), from, to, mode, source.getEntity() instanceof ServerPlayerEntity ? source.getPlayerOrException() : null);

        source.sendSuccess(result.getOutput() == null ? new TranslationTextComponent("commands.scanturf.success", result.getCommandResult()) : result.getOutput(), true);

        return result.getCommandResult();
    }
}
