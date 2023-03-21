package net.splatcraft.forge.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.TranslationTextComponent;
import net.splatcraft.forge.data.Stage;
import net.splatcraft.forge.data.capabilities.saveinfo.ISaveInfo;
import net.splatcraft.forge.data.capabilities.saveinfo.SaveInfoCapability;

import java.util.concurrent.CompletableFuture;

public class StageSettingArgument implements ArgumentType<String>
{
	public static final DynamicCommandExceptionType SETTING_NOT_FOUND = new DynamicCommandExceptionType(p_208663_0_ -> new TranslationTextComponent("arg.stageSetting.notFound", p_208663_0_));

	@Override
	public String parse(StringReader reader) throws CommandSyntaxException
	{
		return reader.readString();
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
	{
		return ISuggestionProvider.suggest(Stage.VALID_SETTINGS, builder);
	}

}
