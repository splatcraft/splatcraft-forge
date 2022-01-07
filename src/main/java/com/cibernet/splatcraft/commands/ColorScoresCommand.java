package com.cibernet.splatcraft.commands;

import com.cibernet.splatcraft.commands.arguments.ColorCriterionArgument;
import com.cibernet.splatcraft.commands.arguments.InkColorArgument;
import com.cibernet.splatcraft.data.capabilities.saveinfo.SaveInfoCapability;
import com.cibernet.splatcraft.handlers.ScoreboardHandler;
import com.cibernet.splatcraft.network.SplatcraftPacketHandler;
import com.cibernet.splatcraft.network.UpdateColorScoresPacket;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;

public class ColorScoresCommand
{
    private static final SimpleCommandExceptionType CRITERION_ALREADY_EXISTS_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.colorscores.add.duplicate"));

    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(Commands.literal("colorscores")
                .then(Commands.literal("add").then(Commands.argument("color", InkColorArgument.inkColor()).executes(ColorScoresCommand::add)))
                .then(Commands.literal("remove").then(Commands.argument("color", ColorCriterionArgument.colorCriterion()).executes(ColorScoresCommand::remove)))
                .then(Commands.literal("list").executes(ColorScoresCommand::list))
        );
    }

    protected static void update()
    {
        int[] colors = new int[ScoreboardHandler.getCriteriaKeySet().size()];
        int i = 0;
        for (int c : ScoreboardHandler.getCriteriaKeySet())
            colors[i++] = c;
        SplatcraftPacketHandler.sendToAll(new UpdateColorScoresPacket(true, true, colors));
    }

    protected static int add(CommandContext<CommandSource> context) throws CommandSyntaxException
    {
        int color = InkColorArgument.getInkColor(context, "color");
        CommandSource source = context.getSource();

        if (ScoreboardHandler.hasColorCriterion(color))
        {
            throw CRITERION_ALREADY_EXISTS_EXCEPTION.create();
        }
        ScoreboardHandler.createColorCriterion(color);
        SaveInfoCapability.get(context.getSource().getServer()).addInitializedColorScores(color);
        update();

        source.sendSuccess(new TranslationTextComponent("commands.colorscores.add.success", InkColorCommand.getColorName(color)), true);

        return color;
    }

    protected static int remove(CommandContext<CommandSource> context)
    {
        int color = ColorCriterionArgument.getInkColor(context, "color");
        ScoreboardHandler.removeColorCriterion(color);
        SaveInfoCapability.get(context.getSource().getServer()).removeColorScore(color);
        update();

        context.getSource().sendSuccess(new TranslationTextComponent("commands.colorscores.remove.success", InkColorCommand.getColorName(color)), true);

        return color;
    }

    protected static int list(CommandContext<CommandSource> context)
    {
        Collection<Integer> collection = ScoreboardHandler.getCriteriaKeySet();

        if (collection.isEmpty())
        {
            context.getSource().sendSuccess(new TranslationTextComponent("commands.colorscores.list.empty"), false);
        } else
        {
            context.getSource().sendSuccess(new TranslationTextComponent("commands.colorscores.list.count", collection.size()), false);
            collection.forEach(color ->
                    context.getSource().sendSuccess(new TranslationTextComponent("commands.colorscores.list.entry", ScoreboardHandler.getColorIdentifier(color), InkColorCommand.getColorName(color)), false));
        }

        return collection.size();
    }
}
