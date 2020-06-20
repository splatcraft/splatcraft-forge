package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.blocks.IInked;
import com.cibernet.splatcraft.commands.CommandTurfWar;
import com.cibernet.splatcraft.network.PacketSendColorScores;
import com.cibernet.splatcraft.network.SplatCraftPacketHandler;
import com.cibernet.splatcraft.tileentities.TileEntityColor;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import com.cibernet.splatcraft.utils.TabSplatCraft;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.TreeMap;

public class ItemTurfScanner extends ItemRemote
{
	public ItemTurfScanner()
	{
		super();
		
		setUnlocalizedName("turfScanner");
		setRegistryName("turf_scanner");
		setMaxStackSize(1);
		setCreativeTab(TabSplatCraft.main);
		
		totalModes = 2;
	}
	
	@Override
	public RemoteResult onRemoteUse(World world, BlockPos blockpos, BlockPos blockpos1, ItemStack stack, int colorIn, int mode)
	{
		BlockPos blockpos2 = new BlockPos(Math.min(blockpos.getX(), blockpos1.getX()), Math.min(blockpos.getY(), Math.min(blockpos1.getY(), blockpos.getY())), Math.min(blockpos.getZ(), blockpos1.getZ()));
		BlockPos blockpos3 = new BlockPos(Math.max(blockpos.getX(), blockpos1.getX()), Math.max(blockpos.getY(), Math.max(blockpos1.getY(), blockpos.getY())), Math.max(blockpos.getZ(), blockpos1.getZ()));
		
		
		if (!(blockpos2.getY() >= 0 && blockpos3.getY() < 256))
			return createResult(false, new TextComponentTranslation("commands.turfWar.outOfWorld"));
		
		for(int j = blockpos2.getZ(); j <= blockpos3.getZ(); j += 16)
		{
			for(int k = blockpos2.getX(); k <= blockpos3.getX(); k += 16)
			{
				if(!world.isBlockLoaded(new BlockPos(k, blockpos3.getY() - blockpos2.getY(), j)))
				{
					return createResult(false, new TextComponentTranslation("commands.turfWar.outOfWorld"));
				}
			}
		}
		
		if(world.isRemote)
			return createResult(true, null);
		TreeMap<Integer, Integer> scores = new TreeMap<>();
		int blockTotal = 0;
		
		if(mode == 0)
		{
			for(int x = blockpos2.getX(); x <= blockpos3.getX(); x++)
				for(int z = blockpos2.getZ(); z <= blockpos3.getZ(); z++)
				{
					int y = CommandTurfWar.getTopSolidOrLiquidBlock(new BlockPos(x, 1, z), world, Math.min(blockpos3.getY() + 2, 255)).down().getY();
					
					if(y > blockpos3.getY() || y < blockpos2.getY())
						continue;
					
					BlockPos checkPos = new BlockPos(x, y, z);
					IBlockState checkState = world.getBlockState(checkPos);
					
					if(!SplatCraftUtils.canInk(world, checkPos))
						continue;
					
					if(!checkState.getMaterial().blocksMovement() || checkState.getMaterial().isLiquid() || !SplatCraftUtils.canInk(world, checkPos))
						continue;
					
					blockTotal++;
					
					if(world.getTileEntity(checkPos) instanceof TileEntityColor && world.getBlockState(checkPos).getBlock() instanceof IInked)
					{
						TileEntityColor te = (TileEntityColor) world.getTileEntity(checkPos);
						IInked block = (IInked) world.getBlockState(checkPos).getBlock();
						int color = te.getColor();
						
						if(block.countsTowardsScore())
						{
							if(scores.containsKey(color))
								scores.replace(color, scores.get(color) + 1);
							else scores.put(color, 1);
						}
						
						
					}
				}
		}
		else if(mode == 1)
		{
			for(int x = blockpos2.getX(); x <= blockpos3.getX(); x++)
				for(int z = blockpos2.getZ(); z <= blockpos3.getZ(); z++)
					for(int y = blockpos2.getY(); y <= blockpos3.getY(); y++)
					{
						BlockPos checkPos = new BlockPos(x, y, z);
						IBlockState checkState = world.getBlockState(checkPos);
						boolean isWall = false;
						
						for(int j = 1; j <= 2; j++)
						{
							if(world.isOutsideBuildHeight(checkPos.up(j)))
								break;
							if(!SplatCraftUtils.canInkPassthrough(world, checkPos.up(j)))
							{
								isWall = true;
								break;
							}
							
							if(j > blockpos3.getY())
								break;
						}
						
						
						if(isWall || !SplatCraftUtils.canInk(world, checkPos))
							continue;
						
						if(!checkState.getMaterial().blocksMovement() || checkState.getMaterial().isLiquid() || !SplatCraftUtils.canInk(world, checkPos))
							continue;
						
						blockTotal++;
						
						if(world.getTileEntity(checkPos) instanceof TileEntityColor && world.getBlockState(checkPos).getBlock() instanceof IInked)
						{
							TileEntityColor te = (TileEntityColor) world.getTileEntity(checkPos);
							IInked block = (IInked) world.getBlockState(checkPos).getBlock();
							int color = te.getColor();
							
							if(block.countsTowardsScore())
							{
								if(scores.containsKey(color))
									scores.replace(color, scores.get(color) + 1);
								else scores.put(color, 1);
							}
							
							
						}
					}
		}
		
		Integer colors[] = new Integer[scores.size()];
		Float colorScores[] = new Float[scores.size()];
		
		int i = 0;
		for(Map.Entry<Integer, Integer> entry : scores.entrySet())
		{
			//world.getMinecraftServer().getPlayerList().sendMessage(new TextComponentTranslation("commands.turfWar.score", SplatCraftUtils.getColorName(entry.getKey()), String.format("%.1f",(entry.getValue()/(float)blockTotal)*100)));
			colors[i] = entry.getKey();
			colorScores[i] = (entry.getValue()/(float)blockTotal)*100;
			i++;
		}
		
		if(scores.isEmpty())
			return createResult(false, new TextComponentTranslation("commands.turfWar.noInk"));
		else SplatCraftPacketHandler.instance.sendToDimension(new PacketSendColorScores(colors, colorScores), world.provider.getDimension());
		return createResult(true, null);
	}
}
