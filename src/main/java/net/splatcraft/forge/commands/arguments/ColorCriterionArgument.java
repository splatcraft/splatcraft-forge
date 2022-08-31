package net.splatcraft.forge.commands.arguments;

import net.splatcraft.forge.commands.InkColorCommand;
import net.splatcraft.forge.handlers.ScoreboardHandler;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.concurrent.CompletableFuture;

public class ColorCriterionArgument extends InkColorArgument
{

    public static final DynamicCommandExceptionType CRITERION_NOT_FOUND = new DynamicCommandExceptionType(p_208663_0_ -> new TranslationTextComponent("arg.colorCriterion.notFound", p_208663_0_));

    private ColorCriterionArgument()
    {
        super();
    }

    public static ColorCriterionArgument colorCriterion()
    {
        return new ColorCriterionArgument();
    }

    @Override
    public Integer parse(StringReader reader) throws CommandSyntaxException
    {
        int color = super.parse(reader);

        if (!ScoreboardHandler.hasColorCriterion(color))
        {
            throw CRITERION_NOT_FOUND.create(InkColorCommand.getColorName(color));
        }
        return color;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
    {
        return ISuggestionProvider.suggest(ScoreboardHandler.getCriteriaSuggestions(), builder);
    }
}
