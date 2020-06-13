package com.cibernet.splatcraft.commands;

import com.cibernet.splatcraft.network.PacketPlayerReturnColor;
import com.cibernet.splatcraft.network.SplatCraftPacketHandler;
import com.cibernet.splatcraft.utils.InkColors;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import com.cibernet.splatcraft.world.save.SplatCraftPlayerData;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandSetInkColor extends CommandBase
{
	@Override
	public String getName()
	{
		return "inkcolor";
	}
	
	@Override
	public String getUsage(ICommandSender sender)
	{
		return "commands.inkcolor.usage";
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length != 2)
			throw new WrongUsageException("commands.inkcolor.usage", new Object[0]);
		
		EntityPlayer player = getPlayer(server, sender, args[0]);
		int color;
		
		InkColors ink = InkColors.getByName(args[1]);
		
		if(ink != null)
			color = ink.getColor();
		else if(args[1].charAt(0) == '#')
			color = parseHex(args[1].substring(1).toLowerCase(), 0, 0xFFFFFF);
		else color = parseInt(args[1], 0, 0xFFFFFF);
		
		if(SplatCraftPlayerData.getInkColor(player) != color)
		{
			SplatCraftPlayerData.setInkColor(player, color);
			SplatCraftPacketHandler.instance.sendToDimension(new PacketPlayerReturnColor(player.getUniqueID(), color), player.dimension);
		}
		
		notifyCommandListener(sender, this, "commands.inkcolor.success", new Object[] {player.getName(), SplatCraftUtils.getColorName(color)});
	}
	
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
		}
		else
		{
			return args.length == 2 ? getListOfStringsMatchingLastWord(args, InkColors.getNameSet()) : Collections.emptyList();
		}
	}
	
	/**
	 * Return whether the specified command parameter index is a username parameter.
	 */
	public boolean isUsernameIndex(String[] args, int index)
	{
		return index == 0;
	}
	
	@Override
	public int getRequiredPermissionLevel()
	{
		return 1;
	}
	
	private static int parseHex(String input, int min, int max) throws NumberInvalidException
	{
		int i = parseHex(input);
		
		if (i < 0)
		{
			throw new NumberInvalidException("commands.generic.num.tooSmall", new Object[] {i, min});
		}
		else if (i > 0xFFFFFF)
		{
			throw new NumberInvalidException("commands.generic.num.tooBig", new Object[] {i, max});
		}
		else
		{
			return i;
		}
	}
	
	private static int parseHex(String input) throws NumberInvalidException
	{
		try
		{
			return Integer.parseInt(input, 16);
		}
		catch (NumberFormatException var2)
		{
			throw new NumberInvalidException("commands.generic.num.invalid", new Object[] {input});
		}
	}
}
