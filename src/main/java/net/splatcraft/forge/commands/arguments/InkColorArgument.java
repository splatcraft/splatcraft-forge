package net.splatcraft.forge.commands.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.registries.SplatcraftInkColors;
import net.splatcraft.forge.util.InkColor;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class InkColorArgument implements ArgumentType<Integer>
{

    public static final DynamicCommandExceptionType COLOR_NOT_FOUND = new DynamicCommandExceptionType(p_208663_0_ -> new TranslatableComponent("arg.inkColor.notFound", p_208663_0_));
    public static final int max = 0xFFFFFF;
    private static final Collection<String> EXAMPLES = Arrays.asList("splatcraft:orange", "blue", "#C83D79", "4234555");

    protected InkColorArgument()
    {
        super();
    }

    public static InkColorArgument inkColor()
    {
        return new InkColorArgument();
    }

    public static int getInkColor(CommandContext<CommandSourceStack> context, String name)
    {
        return context.getArgument(name, Integer.class);
    }

    public static Integer parseStatic(StringReader reader) throws CommandSyntaxException
    {
        final int start = reader.getCursor();
        boolean hasInt = false;
        Integer result = null;

        try
        {
            result = Integer.parseInt(reader.readString());
            hasInt = true;

            if (result < 0)
            {
                reader.setCursor(start);
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooLow().createWithContext(reader, result, 0);
            }
            if (result > max)
            {
                reader.setCursor(start);
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooHigh().createWithContext(reader, result, max);
            }
            return result;

        } catch (NumberFormatException ignored)
        {
        }

        reader.setCursor(start);

        if (!hasInt)
        {
            if (reader.read() == '#')
            {
                return parseHex(reader.readString().toLowerCase(), reader);
            }

            reader.setCursor(start);
            ResourceLocation resourceLocation = ResourceLocation.read(reader);
            InkColor color = SplatcraftInkColors.REGISTRY.get().getValue(resourceLocation);

            if (color == null)
            {
                throw COLOR_NOT_FOUND.create(resourceLocation);
            }

            return color.getColor();
        }

        if (result < 0)
        {
            reader.setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooLow().createWithContext(reader, result, 0);
        }
        reader.setCursor(start);
        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooHigh().createWithContext(reader, result, max);

    }

    static CompletableFuture<Suggestions> suggestIterable(Iterable<ResourceLocation> p_197014_0_, SuggestionsBuilder builder)
    {
        String s = builder.getRemaining().toLowerCase(Locale.ROOT);
        func_210512_a(p_197014_0_, s, p_210517_0_ -> p_210517_0_, p_210513_1_ -> builder.suggest(p_210513_1_.toString()));
        return builder.buildFuture();
    }

    static <T> void func_210512_a(Iterable<T> p_210512_0_, String p_210512_1_, Function<T, ResourceLocation> p_210512_2_, Consumer<T> p_210512_3_)
    {
        boolean flag = p_210512_1_.indexOf(58) > -1;

        for (T t : p_210512_0_)
        {
            ResourceLocation resourcelocation = p_210512_2_.apply(t);
            if (flag)
            {
                String s = resourcelocation.toString();
                if (SharedSuggestionProvider.matchesSubStr(p_210512_1_, s))
                {
                    p_210512_3_.accept(t);
                }
            } else if (SharedSuggestionProvider.matchesSubStr(p_210512_1_, resourcelocation.getNamespace()) || resourcelocation.getNamespace().equals(Splatcraft.MODID) && SharedSuggestionProvider.matchesSubStr(p_210512_1_, resourcelocation.getPath()))
            {
                p_210512_3_.accept(t);
            }
        }

    }

    public static int parseHex(String input, StringReader reader) throws CommandSyntaxException
    {
        try
        {
            return Integer.parseInt(input, 16);
        } catch (NumberFormatException var2)
        {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().createWithContext(reader, input);
        }
    }

    @Override
    public Integer parse(StringReader reader) throws CommandSyntaxException
    {
        return parseStatic(reader);
    }


    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
    {
        return suggestIterable(SplatcraftInkColors.REGISTRY.get().getKeys(), builder);
    }

    @Override
    public Collection<String> getExamples()
    {
        return EXAMPLES;
    }

    public static class Serializer implements ArgumentSerializer<InkColorArgument>
    {
        @Override
        public void serializeToNetwork(InkColorArgument argument, FriendlyByteBuf buffer)
        {

        }

        @Override
        public InkColorArgument deserializeFromNetwork(FriendlyByteBuf buffer)
        {
            return null;
        }

        @Override
        public void serializeToJson(InkColorArgument p_212244_1_, JsonObject p_212244_2_)
        {

        }
    }
}
