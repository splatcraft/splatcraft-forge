package net.splatcraft.forge.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.splatcraft.forge.commands.arguments.ColorCriterionArgument;
import net.splatcraft.forge.commands.arguments.InkColorArgument;
import net.splatcraft.forge.data.capabilities.saveinfo.SaveInfoCapability;
import net.splatcraft.forge.handlers.ScoreboardHandler;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.s2c.UpdateColorScoresPacket;

import java.util.Collection;

public class ColorScoresCommand {
    private static final SimpleCommandExceptionType CRITERION_ALREADY_EXISTS_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.colorscores.add.duplicate"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("colorscores").requires(commandSource -> commandSource.hasPermission(2))
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

    protected static int add(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
    {
        int color = InkColorArgument.getInkColor(context, "color");
        CommandSourceStack source = context.getSource();

        if (ScoreboardHandler.hasColorCriterion(color))
        {
            throw CRITERION_ALREADY_EXISTS_EXCEPTION.create();
        }
        ScoreboardHandler.createColorCriterion(color);
        SaveInfoCapability.get(context.getSource().getServer()).addInitializedColorScores(color);
        update();

        source.sendSuccess(new TranslatableComponent("commands.colorscores.add.success", InkColorCommand.getColorName(color)), true);

        return color;
    }

    protected static int remove(CommandContext<CommandSourceStack> context)
    {
        int color = ColorCriterionArgument.getInkColor(context, "color");
        ScoreboardHandler.removeColorCriterion(color);
        SaveInfoCapability.get(context.getSource().getServer()).removeColorScore(color);
        update();

        context.getSource().sendSuccess(new TranslatableComponent("commands.colorscores.remove.success", InkColorCommand.getColorName(color)), true);

        return color;
    }

    protected static int list(CommandContext<CommandSourceStack> context)
    {
        Collection<Integer> collection = ScoreboardHandler.getCriteriaKeySet();

        if (collection.isEmpty())
        {
            context.getSource().sendSuccess(new TranslatableComponent("commands.colorscores.list.empty"), false);
        } else
        {
            context.getSource().sendSuccess(new TranslatableComponent("commands.colorscores.list.count", collection.size()), false);
            collection.forEach(color ->
                    context.getSource().sendSuccess(new TranslatableComponent("commands.colorscores.list.entry", ScoreboardHandler.getColorIdentifier(color), InkColorCommand.getColorName(color)), false));
        }

        return collection.size();
    }
}
