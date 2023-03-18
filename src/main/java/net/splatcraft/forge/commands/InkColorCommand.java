package net.splatcraft.forge.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.splatcraft.forge.commands.arguments.InkColorArgument;
import net.splatcraft.forge.util.ColorUtils;

import java.util.Collection;

public class InkColorCommand
{
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {

        dispatcher.register(Commands.literal("inkcolor").requires(commandSource -> commandSource.hasPermission(2))
                .then(Commands.argument("color", InkColorArgument.inkColor()).executes(
                        context -> setColor(context.getSource(), InkColorArgument.getInkColor(context, "color"))
                ).then(Commands.argument("targets", EntityArgument.players()).executes(
                        context -> setColor(context.getSource(), InkColorArgument.getInkColor(context, "color"), EntityArgument.getPlayers(context, "targets"))
                ))));
    }

    private static int setColor(CommandSource source, int color) throws CommandSyntaxException
    {
        ColorUtils.setPlayerColor(source.getPlayerOrException(), color);

        source.sendSuccess(new TranslationTextComponent("commands.inkcolor.success.single", source.getPlayerOrException().getDisplayName(), getColorName(color))/*ColorUtils.getFormatedColorName(color, false)*/, true);

        return 1;
    }

    //TODO server friendly feedback message
    public static IFormattableTextComponent getColorName(int color)
    {
        return new StringTextComponent("#" + String.format("%06X", color).toUpperCase()).setStyle(Style.EMPTY.withColor(Color.fromRgb(color)));
    }

    private static int setColor(CommandSource source, int color, Collection<ServerPlayerEntity> targets)
    {
        targets.forEach(player -> ColorUtils.setPlayerColor(player, color));

        if (targets.size() == 1)
        {
            source.sendSuccess(new TranslationTextComponent("commands.inkcolor.success.single", targets.iterator().next().getDisplayName(), getColorName(color)), true);
        } else
        {
            source.sendSuccess(new TranslationTextComponent("commands.inkcolor.success.multiple", targets.size(), getColorName(color)), true);
        }

        return targets.size();
    }
}
