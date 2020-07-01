package com.cibernet.splatcraft.scoreboard;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.network.PacketPlayerReturnColor;
import com.cibernet.splatcraft.network.SplatCraftPacketHandler;
import com.cibernet.splatcraft.scoreboard.ScoreCriteriaInkColor;
import com.cibernet.splatcraft.utils.InkColors;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import com.cibernet.splatcraft.world.save.SplatCraftGamerules;
import com.cibernet.splatcraft.world.save.SplatCraftPlayerData;
import com.google.common.collect.Maps;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.IScoreCriteria;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import static com.cibernet.splatcraft.scoreboard.SplatcraftScoreboardHandler.*;

import javax.annotation.Nullable;
import java.util.*;

public class CommandColorScores extends CommandBase
{
	
	protected static final Map<Integer, ScoreCriteriaInkColor[]> COLOR_GOALS = Maps.newHashMap();
	
	
	@Override
	public String getName()
	{
		return "colorscores";
	}
	
	@Override
	public String getUsage(ICommandSender sender)
	{
		return "commands.colorscores.usage";
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if(args.length == 1 && args[0].equals("list"))
		{
			listGoals(sender, server);
			return;
		}
		
		if (args.length != 2)
			throw new WrongUsageException("commands.colorscores.usage", new Object[0]);
		
		int color;
		
		InkColors ink = InkColors.getByName(args[1]);
		
		if(ink != null)
			color = ink.getColor();
		else if(args[1].charAt(0) == '#')
			color = parseHex(args[1].substring(1).toLowerCase(), 0, 0xFFFFFF);
		else color = parseInt(args[1], 0, 0xFFFFFF);
		
		switch(args[0])
		{
			case "add":
				if(COLOR_GOALS.containsKey(color))
					throw new CommandException("commands.colorscores.colorExists", SplatCraftUtils.getColorName(color));
				else
				{
					createGoal(color);
					notifyCommandListener(sender, this, "commands.colorscores.add", new Object[] {SplatCraftUtils.getColorName(color)});
				}
			break;
			case "remove":
				if(!COLOR_GOALS.containsKey(color))
					throw new CommandException("commands.colorscores.colorDoesntExist", SplatCraftUtils.getColorName(color));
				else
				{
					List<ScoreObjective> conflictingObjectives = new ArrayList<>();
					for(ScoreCriteriaInkColor goal : COLOR_GOALS.get(color))
						conflictingObjectives.addAll(server.getWorld(0).getScoreboard().getObjectivesFromCriteria(goal));
					
					if(!conflictingObjectives.isEmpty())
					{
						List<String> objectiveNames = new ArrayList<>();
						conflictingObjectives.forEach((objective) -> {objectiveNames.add(objective.getName());});
						throw new CommandException("commands.colorscores.cantRemove", SplatCraftUtils.getColorName(color), String.join(", ", objectiveNames));
					}
					
					for(ScoreCriteriaInkColor goal : COLOR_GOALS.get(color))
						goal.remove();
					COLOR_GOALS.remove(color);
					notifyCommandListener(sender, this, "commands.colorscores.remove", new Object[] {SplatCraftUtils.getColorName(color)});
					
				}
			break;
			default:
				throw new WrongUsageException("commands.colorscores.usage", new Object[0]);
		}
		
	}
	
	@Override
	public int getRequiredPermissionLevel()
	{
		return 2;
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, "add", "remove", "list");
		}
		else
		{
			if(args.length == 2)
				switch(args[0])
				{
					case "add":
						return getListOfStringsMatchingLastWord(args, InkColors.getNameSet());
					case "remove":
						return getListOfStringsMatchingLastWord(args, getRegisteredGoalNames());
					default:
						return Collections.emptyList();
				}
			return Collections.emptyList();
		}
	}
	
	protected static String[] getRegisteredGoalNames()
	{
		List<String> names = new ArrayList<>();
		
		for(int color : COLOR_GOALS.keySet())
			names.add(SplatCraftUtils.getUnlocColorName(color, true));
		
		return names.toArray(new String[names.size()]);
	}
	
	protected void listGoals(ICommandSender sender, MinecraftServer server) throws CommandException
	{
		Collection<Integer> collection = COLOR_GOALS.keySet();
		
		if (collection.isEmpty())
		{
			throw new CommandException("commands.colorscores.list.empty", new Object[0]);
		}
		else
		{
			TextComponentTranslation textcomponenttranslation = new TextComponentTranslation("commands.colorscores.list.count", new Object[] {collection.size()});
			textcomponenttranslation.getStyle().setColor(TextFormatting.DARK_GREEN);
			sender.sendMessage(textcomponenttranslation);
			
			for (int color : collection)
			{
				sender.sendMessage(new TextComponentTranslation("commands.colorscores.list.entry", new Object[] {SplatCraftUtils.getUnlocColorName(color), SplatCraftUtils.getColorName(color)}));
			}
		}
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
