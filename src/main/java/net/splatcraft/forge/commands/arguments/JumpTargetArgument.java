package net.splatcraft.forge.commands.arguments;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.splatcraft.forge.data.Stage;
import net.splatcraft.forge.data.capabilities.saveinfo.SaveInfoCapability;
import net.splatcraft.forge.registries.SplatcraftGameRules;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class JumpTargetArgument extends EntityArgument
{
	protected JumpTargetArgument()
	{
		super(true, false);
	}

	public static JumpTargetArgument target()
	{
		return new JumpTargetArgument();
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> command, SuggestionsBuilder suggestionsBuilder) {
		if (command.getSource() instanceof SharedSuggestionProvider) {
			StringReader stringreader = new StringReader(suggestionsBuilder.getInput());
			stringreader.setCursor(suggestionsBuilder.getStart());
			SharedSuggestionProvider sharedsuggestionprovider = (SharedSuggestionProvider)command.getSource();
			EntitySelectorParser entityselectorparser = new EntitySelectorParser(stringreader, sharedsuggestionprovider.hasPermission(2));

			try {
				entityselectorparser.parse();
			} catch (CommandSyntaxException commandsyntaxexception) {
			}

			return entityselectorparser.fillSuggestions(suggestionsBuilder, (p_91457_) -> {
				Collection<String> collection = sharedsuggestionprovider.getOnlinePlayerNames();
				Entity source = ((CommandSourceStack) command.getSource()).getEntity();
				List<Stage> validStages = SaveInfoCapability.get(source.getServer()).getStages().values().stream().filter(stage -> stage.getBounds().contains(source.position())).toList();

				if(!SplatcraftGameRules.getLocalizedRule(source.level, source.blockPosition(), SplatcraftGameRules.GLOBAL_SUPERJUMPING))
					collection.removeIf((str) -> {
						Player player = ((CommandSourceStack)command.getSource()).getServer().getPlayerList().getPlayerByName(str);

						return validStages.stream().filter(stage -> stage.getBounds().contains(player.position())).toList().isEmpty();
					});

				Iterable<String> iterable = Iterables.concat(collection, sharedsuggestionprovider.getSelectedEntities());
				SharedSuggestionProvider.suggest(iterable, p_91457_);
			});
		} else {
			return Suggestions.empty();
		}
	}
}
