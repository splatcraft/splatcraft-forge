package com.cibernet.splatcraft.commands;

import com.cibernet.splatcraft.world.save.SplatCraftGamerules;
import com.cibernet.splatcraft.world.save.SplatCraftPlayerData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class CommandSplatCraftGamerules extends CommandBase
{
	
	@Override
	public String getName()
	{
		return "splatcraftrules";
	}
	
	@Override
	public String getUsage(ICommandSender sender)
	{
		return "commands.splatcraftRules.usage";
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if(Arrays.asList(SplatCraftGamerules.getGameruleNames()).contains(args[0]))
		{
			if(args.length >= 2 && (args[1].equals("true") || args[1].equals("false")))
			{
				SplatCraftGamerules.setGameruleValue(args[0], args[1].equals("true"));
				sender.sendMessage(new TextComponentTranslation("commands.splatcraftRules.success", args[0], args[1].equals("true")));
			}
			else sender.sendMessage(new TextComponentTranslation("commands.splatcraftRules.value", args[0], SplatCraftGamerules.getGameruleValue(args[0])));
			
		}
				
		else throw new WrongUsageException("commands.splatcraftRules.usage", new Object[0]);
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, SplatCraftGamerules.getGameruleNames());
		}
		else if (args.length == 2)
		{
			return getListOfStringsMatchingLastWord(args, new String[] {"true", "false"});
		}
		
		return super.getTabCompletions(server, sender, args, targetPos);
	}
	
	@Override
	public int getRequiredPermissionLevel()
	{
		return 0;
	}
	
}
