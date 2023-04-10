package net.splatcraft.forge.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;
import net.splatcraft.forge.commands.arguments.InkColorArgument;
import net.splatcraft.forge.data.Stage;
import net.splatcraft.forge.data.capabilities.saveinfo.SaveInfoCapability;
import net.splatcraft.forge.util.ColorUtils;

import java.util.Collection;
import java.util.HashMap;

public class InkColorCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(Commands.literal("inkcolor").requires(commandSource -> commandSource.hasPermission(2))
                .then(Commands.argument("color", InkColorArgument.inkColor()).executes(
                        context -> setColor(context.getSource(), InkColorArgument.getInkColor(context, "color"))
                ).then(Commands.argument("targets", EntityArgument.players()).executes(
                        context -> setColor(context.getSource(), InkColorArgument.getInkColor(context, "color"), EntityArgument.getPlayers(context, "targets"))
                )))
                .then(StageCommand.stageId("stage").then(StageCommand.stageTeam("team", "stage")
                        .executes(context -> setColorByTeam(context.getSource(), StringArgumentType.getString(context, "stage"), StringArgumentType.getString(context, "team")))
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(context -> setColorByTeam(context.getSource(), StringArgumentType.getString(context, "stage"), StringArgumentType.getString(context, "team"), EntityArgument.getPlayers(context, "targets")))
                ))));
    }

    private static int setColor(CommandSourceStack source, int color) throws CommandSyntaxException
    {
        ColorUtils.setPlayerColor(source.getPlayerOrException(), color);

        source.sendSuccess(new TranslatableComponent("commands.inkcolor.success.single", source.getPlayerOrException().getDisplayName(), getColorName(color))/*ColorUtils.getFormatedColorName(color, false)*/, true);

        return 1;
    }

    //TODO server friendly feedback message
    public static MutableComponent getColorName(int color)
    {
        return new TextComponent("#" + String.format("%06X", color).toUpperCase()).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color)));
    }

    private static int setColor(CommandSourceStack source, int color, Collection<ServerPlayer> targets)
    {
        targets.forEach(player -> ColorUtils.setPlayerColor(player, color));

        if (targets.size() == 1)
        {
            source.sendSuccess(new TranslatableComponent("commands.inkcolor.success.single", targets.iterator().next().getDisplayName(), getColorName(color)), true);
        } else
        {
            source.sendSuccess(new TranslatableComponent("commands.inkcolor.success.multiple", targets.size(), getColorName(color)), true);
        }

        return targets.size();
    }

    private static int setColorByTeam(CommandSourceStack source, String stageId, String teamId, Collection<ServerPlayer> targets) throws CommandSyntaxException
    {
        HashMap<String, Stage> stages = SaveInfoCapability.get(source.getServer()).getStages();
        if(!stages.containsKey(stageId))
            throw StageCommand.STAGE_NOT_FOUND.create(stageId);

        Stage stage = stages.get(stageId);

        if(!stage.hasTeam(teamId))
            throw StageCommand.TEAM_NOT_FOUND.create(new Object[] {teamId, stageId});

        return setColor(source, stage.getTeamColor(teamId), targets);
    }

    private static int setColorByTeam(CommandSourceStack source, String stageId, String teamId) throws CommandSyntaxException
    {
        HashMap<String, Stage> stages = SaveInfoCapability.get(source.getServer()).getStages();
        if(!stages.containsKey(stageId))
            throw StageCommand.STAGE_NOT_FOUND.create(stageId);

        Stage stage = stages.get(stageId);

        if(!stage.hasTeam(teamId))
            throw StageCommand.TEAM_NOT_FOUND.create(new Object[] {teamId, stageId});

        return setColor(source, stage.getTeamColor(teamId));
    }
}
