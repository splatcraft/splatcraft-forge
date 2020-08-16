package com.cibernet.splatcraft.commands;

import com.cibernet.splatcraft.commands.arguments.InkColorArgument;
import com.cibernet.splatcraft.util.ColorUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.impl.EffectCommand;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;
import java.util.Collections;

public class InkColorCommand
{
	public static void register(CommandDispatcher<CommandSource> dispatcher)
	{
		
		dispatcher.register(Commands.literal("inkcolor").requires((commandSource -> commandSource.hasPermissionLevel(2)))
		.then(Commands.argument("color", new InkColorArgument()).executes(
				(context -> setColor(context.getSource(), InkColorArgument.getInkColor(context, "color")))
		).then(Commands.argument("targets", EntityArgument.players()).executes(
						(context) -> setColor(context.getSource(), InkColorArgument.getInkColor(context, "color"), EntityArgument.getPlayers(context, "targets"))
				))));
	}
	
	private static int setColor(CommandSource source, int color) throws CommandSyntaxException
	{
		ColorUtils.setPlayerColor(source.asPlayer(), color);
		
		source.sendFeedback(new TranslationTextComponent("commands.inkcolor.success.single", color, source.asPlayer().getDisplayName()), true);
		
		return 1;
	}
	
	private static int setColor(CommandSource source, int color, Collection<ServerPlayerEntity> targets)
	{
		targets.forEach((player) -> ColorUtils.setPlayerColor(player, color));
		
		if (targets.size() == 1) {
			source.sendFeedback(new TranslationTextComponent("commands.inkcolor.success.single", color, targets.iterator().next().getDisplayName()), true);
		} else {
			source.sendFeedback(new TranslationTextComponent("commands.inkcolor.success.single", color, targets.size()), true);
		}
		
		return targets.size();
	}
}
