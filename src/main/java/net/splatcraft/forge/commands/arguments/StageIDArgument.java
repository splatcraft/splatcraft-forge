package net.splatcraft.forge.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.splatcraft.forge.util.ClientUtils;

import java.util.concurrent.CompletableFuture;

public class StageIDArgument implements ArgumentType<String>
{
	public static final DynamicCommandExceptionType STAGE_NOT_FOUND = new DynamicCommandExceptionType(p_208663_0_ -> new TranslationTextComponent("arg.stage.notFound", p_208663_0_));
	public static final DynamicCommandExceptionType STAGE_ALREADY_EXISTS = new DynamicCommandExceptionType(p_208663_0_ -> new TranslationTextComponent("arg.stage.alreadyExists", p_208663_0_));

	private final boolean suggest;

	public StageIDArgument(boolean suggest) {
		this.suggest = suggest;
	}

	@Override
	public String parse(StringReader reader) throws CommandSyntaxException
	{
		return reader.readString();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
	{
		if(!suggest)
			return ArgumentType.super.listSuggestions(context, builder);

		return ISuggestionProvider.suggest(ClientUtils.clientStages.keySet(), builder);
	}

}
