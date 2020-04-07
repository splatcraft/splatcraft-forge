package com.cibernet.splatcraft.commands;

import com.cibernet.splatcraft.blocks.IInked;
import com.cibernet.splatcraft.tileentities.TileEntityColor;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CommandClearInk extends CommandBase
{
	
	@Override
	public String getName()
	{
		return "clearink";
	}
	
	@Override
	public String getUsage(ICommandSender sender)
	{
		return "commands.clearInk.usage";
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length < 6)
		{
			throw new WrongUsageException("commands.clearInk.usage", new Object[0]);
		}
		
		BlockPos blockpos = parseBlockPos(sender, args, 0, false);
		BlockPos blockpos1 = parseBlockPos(sender, args, 3, false);
		BlockPos blockpos2 = new BlockPos(Math.min(blockpos.getX(), blockpos1.getX()), Math.min(blockpos.getY(), Math.min(blockpos1.getY(), blockpos.getY())), Math.min(blockpos.getZ(), blockpos1.getZ()));
		BlockPos blockpos3 = new BlockPos(Math.max(blockpos.getX(), blockpos1.getX()), Math.max(blockpos.getY(), Math.max(blockpos1.getY(), blockpos.getY())), Math.max(blockpos.getZ(), blockpos1.getZ()));
		
		if (!(blockpos2.getY() >= 0 && blockpos3.getY() < 256))
			throw new CommandException("commands.clearInk.outOfWorld", new Object[0]);
		
		World world = sender.getEntityWorld();
		
		for(int j = blockpos2.getZ(); j <= blockpos3.getZ(); j += 16)
		{
			for(int k = blockpos2.getX(); k <= blockpos3.getX(); k += 16)
			{
				if(!world.isBlockLoaded(new BlockPos(k, blockpos3.getY() - blockpos2.getY(), j)))
				{
					throw new CommandException("commands.clearInk.outOfWorld", new Object[0]);
				}
			}
		}
		
		for(int x = blockpos2.getX(); x <= blockpos3.getX(); x++)
			for(int y = blockpos2.getY(); y <= blockpos3.getY(); y++)
				for(int z = blockpos2.getZ(); z <= blockpos3.getZ(); z++)
				{
					BlockPos pos = new BlockPos(x,y,z);
					Block block = world.getBlockState(pos).getBlock();
					if(block instanceof IInked)
						((IInked) block).clearInk(world, pos);
				}
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
		
		return super.getTabCompletions(server, sender, args, targetPos);
	}
	
	@Override
	public int getRequiredPermissionLevel()
	{
		return 1;
	}
	
}
