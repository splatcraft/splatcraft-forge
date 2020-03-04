package com.cibernet.splatcraft.commands;

import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.tileentities.TileEntityInkedBlock;
import com.cibernet.splatcraft.utils.InkColors;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;

public class CommandTurfWar extends CommandBase
{
	
	@Override
	public String getName()
	{
		return "turfWar";
	}
	
	@Override
	public String getUsage(ICommandSender sender)
	{
		return "commands.turfWar.usage";
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length < 6)
		{
			throw new WrongUsageException("commands.turfWar.usage", new Object[0]);
		}
		
		boolean cleanInk = args.length >= 7 && "true".equals(args[6]);
		
		BlockPos blockpos = parseBlockPos(sender, args, 0, false);
		BlockPos blockpos1 = parseBlockPos(sender, args, 3, false);
		BlockPos blockpos2 = new BlockPos(Math.min(blockpos.getX(), blockpos1.getX()), Math.min(blockpos.getY(), blockpos1.getY()), Math.min(blockpos.getZ(), blockpos1.getZ()));
		BlockPos blockpos3 = new BlockPos(Math.max(blockpos.getX(), blockpos1.getX()), Math.max(blockpos.getY(), blockpos1.getY()), Math.max(blockpos.getZ(), blockpos1.getZ()));
		
		if (!(blockpos2.getY() >= 0 && blockpos3.getY() < 256))
			throw new CommandException("commands.turfWar.outOfWorld", new Object[0]);
		
		World world = sender.getEntityWorld();
		
		for(int j = blockpos2.getZ(); j <= blockpos3.getZ(); j += 16)
		{
			for(int k = blockpos2.getX(); k <= blockpos3.getX(); k += 16)
			{
				if(!world.isBlockLoaded(new BlockPos(k, blockpos3.getY() - blockpos2.getY(), j)))
				{
					throw new CommandException("commands.turfWar.outOfWorld", new Object[0]);
				}
			}
		}
		
		System.out.println(blockpos2);
		System.out.println(blockpos3);
		
		TreeMap<Integer, Integer> scores = new TreeMap<>();
		int blockTotal = 0;
		
		for(int x = blockpos2.getX(); x <= blockpos3.getX(); x++)
			for(int z = blockpos2.getZ(); z <= blockpos3.getZ(); z++)
			{
				int y = Math.min(blockpos3.getY(), Math.max(blockpos2.getY(), world.getTopSolidOrLiquidBlock(new BlockPos(x,1, z)).down().getY()));
				
				BlockPos checkPos = new BlockPos(x,y,z);
				IBlockState checkState = world.getBlockState(checkPos);
				
				if(!checkState.getMaterial().blocksMovement() || checkState.getMaterial().isLiquid() || !SplatCraftUtils.canInk(world, checkPos))
					continue;
				
				blockTotal++;
				
				if(world.getTileEntity(checkPos) instanceof TileEntityInkedBlock)
				{
					TileEntityInkedBlock te = (TileEntityInkedBlock) world.getTileEntity(checkPos);
					int color = te.getColor();
					
					if(scores.containsKey(color))
						scores.replace(color, scores.get(color)+1);
					else scores.put(color, 1);
					
					if(cleanInk)
						world.setBlockState(checkPos, te.getSavedState(), 3);
					
				}
			}
		Map.Entry<Integer, Integer> winner = null;
		for(Map.Entry<Integer, Integer> entry : scores.entrySet())
		{
			world.getMinecraftServer().getPlayerList().sendMessage(new TextComponentTranslation("commands.turfWar.score", SplatCraftUtils.getColorName(entry.getKey()), String.format("%.1f",(entry.getValue()/(float)blockTotal)*100)));
			
			if(winner == null || winner.getValue() < entry.getValue())
				winner = entry;
			
		}
		
		if(winner != null)
			world.getMinecraftServer().getPlayerList().sendMessage(new TextComponentTranslation("commands.turfWar.winner", SplatCraftUtils.getColorName(winner.getKey())));
		else
			throw new CommandException("commands.turfWar.noInk", new Object[0]);
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
	{
		if (args.length > 0 && args.length <= 3)
		{
			return getTabCompletionCoordinate(args, 0, targetPos);
		}
		else if (args.length > 3 && args.length <= 6)
		{
			return getTabCompletionCoordinate(args, 3, targetPos);
		}
		else if (args.length == 7)
			return getListOfStringsMatchingLastWord(args, new String[] {"true", "false"});
		
		return super.getTabCompletions(server, sender, args, targetPos);
	}
	
	@Override
	public int getRequiredPermissionLevel()
	{
		return 0;
	}
}
