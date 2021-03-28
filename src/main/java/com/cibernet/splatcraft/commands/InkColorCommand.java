package com.cibernet.splatcraft.commands;

import com.cibernet.splatcraft.commands.arguments.InkColorArgument;
import com.cibernet.splatcraft.util.ColorUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;

public class InkColorCommand
{
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {

        dispatcher.register(Commands.literal("inkcolor").requires(commandSource -> commandSource.hasPermissionLevel(2))
                .then(Commands.argument("color", InkColorArgument.inkColor()).executes(
                        context -> setColor(context.getSource(), InkColorArgument.getInkColor(context, "color"))
                ).then(Commands.argument("targets", EntityArgument.players()).executes(
                        context -> setColor(context.getSource(), InkColorArgument.getInkColor(context, "color"), EntityArgument.getPlayers(context, "targets"))
                ))));
    }

    private static int setColor(CommandSource source, int color) throws CommandSyntaxException
    {
        ColorUtils.setPlayerColor(source.asPlayer(), color);

        //TODO server friendly feedback message
        source.sendFeedback(new TranslationTextComponent("commands.inkcolor.success.single", source.asPlayer().getDisplayName(), new StringTextComponent("#" + String.format("%06X", color).toUpperCase()).setStyle(Style.EMPTY.setColor(Color.fromInt(color))))/*ColorUtils.getFormatedColorName(color, false)*/, true);

        return 1;
    }

    private static int setColor(CommandSource source, int color, Collection<ServerPlayerEntity> targets)
    {
        targets.forEach(player -> ColorUtils.setPlayerColor(player, color));

        if (targets.size() == 1)
        {
            source.sendFeedback(new TranslationTextComponent("commands.inkcolor.success.single", new StringTextComponent("#" + String.format("%06X", color).toUpperCase()).setStyle(Style.EMPTY.setColor(Color.fromInt(color))),
                    targets.iterator().next().getDisplayName()), true);
        } else
        {
            source.sendFeedback(new TranslationTextComponent("commands.inkcolor.success.multiple", new StringTextComponent("#" + String.format("%06X", color).toUpperCase()).setStyle(Style.EMPTY.setColor(Color.fromInt(color))),
                    targets.size()), true);
        }

        return targets.size();
    }
}
