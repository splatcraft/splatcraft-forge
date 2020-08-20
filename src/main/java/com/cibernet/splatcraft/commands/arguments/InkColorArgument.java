package com.cibernet.splatcraft.commands.arguments;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.registries.SplatcraftInkColors;
import com.cibernet.splatcraft.util.InkColor;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;

public class InkColorArgument implements ArgumentType<Integer>
{
	
	private static final Collection<String> EXAMPLES = Arrays.asList("splatcraft:orange", "blue", "#C83D79", "4234555");
	public static final DynamicCommandExceptionType COLOR_NOT_FOUND = new DynamicCommandExceptionType((p_208663_0_) -> new TranslationTextComponent("arg.inkColor.notFound", p_208663_0_));
	
	private static final int max = 0xFFFFFF;
	
	protected InkColorArgument()
	{
		super();
	}
	
	public static InkColorArgument inkColor()
	{
		return new InkColorArgument();
	}
	
	public static int getInkColor(CommandContext<CommandSource> context, String name)
	{
		return context.getArgument(name, Integer.class);
	}
	
	@Override
	public Integer parse(StringReader reader) throws CommandSyntaxException
	{
		final int start = reader.getCursor();
		boolean hasInt = false;
		Integer result = null;
		
		try
		{
			result = reader.readInt();
			hasInt = true;
			
		} catch(CommandSyntaxException e) {}
		
		reader.setCursor(start);
		
		if(!hasInt)
		{
			if(reader.read() == '#')
				return parseHex(reader.readString().substring(1).toLowerCase(), reader);
				
			reader.setCursor(start);
			ResourceLocation resourceLocation = ResourceLocation.read(reader);
			
			if(resourceLocation.getNamespace().equals("minecraft"))
				resourceLocation = new ResourceLocation(Splatcraft.MODID, resourceLocation.getPath());
			
			InkColor color = SplatcraftInkColors.REGISTRY.getValue(resourceLocation);
			
			if(color == null)
				throw COLOR_NOT_FOUND.create(resourceLocation);
			
			return color.getColor();
		}
		
		if(result == null)
			return null;
		
		if (result < 0) {
			reader.setCursor(start);
			throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooLow().createWithContext(reader, result, 0);
		}
		if (result > max) {
			reader.setCursor(start);
			throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooHigh().createWithContext(reader, result, max);
		}
		
		return result;
	}
	
	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
	{
		return ISuggestionProvider.suggestIterable(SplatcraftInkColors.REGISTRY.getKeys(), builder);
	}
	
	@Override
	public Collection<String> getExamples()
	{
		return EXAMPLES;
	}
	
	private static int parseHex(String input, StringReader reader) throws CommandSyntaxException
	{
		try
		{
			return Integer.parseInt(input, 16);
		}
		catch (NumberFormatException var2)
		{
			throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().createWithContext(reader, input);
		}
	}
	
	public static class Serializer implements IArgumentSerializer<InkColorArgument>
	{
		
		@Override
		public void write(InkColorArgument argument, PacketBuffer buffer)
		{
		
		}
		
		@Override
		public InkColorArgument read(PacketBuffer buffer)
		{
			return null;
		}
		
		@Override
		public void write(InkColorArgument p_212244_1_, JsonObject p_212244_2_)
		{
		
		}
	}
}
