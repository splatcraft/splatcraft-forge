package com.cibernet.splatcraft.commands;

import com.cibernet.splatcraft.blocks.IInked;
import com.cibernet.splatcraft.utils.SplatCraftPlayerData;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class CommandSplatCraftGamerules extends CommandBase
{
	
	@Override
	public String getName()
	{
		return "splatcraftRules";
	}
	
	@Override
	public String getUsage(ICommandSender sender)
	{
		return "commands.splatcraftRules.usage";
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if(args.length >= 2 && Arrays.asList(SplatCraftPlayerData.getGameruleNames()).contains(args[0]) && (args[1].equals("true") || args[1].equals("false")))
		{
			SplatCraftPlayerData.setGamerule(args[0], args[1].equals("true"));
			sender.sendMessage(new TextComponentTranslation("commands.splatcraftRules.success", args[0], args[1].equals("true")));
		}
		else throw new WrongUsageException("commands.splatcraftRules.usage", new Object[0]);
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, SplatCraftPlayerData.getGameruleNames());
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
