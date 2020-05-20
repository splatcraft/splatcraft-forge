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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.TreeMap;

public class ItemTurfScanner extends ItemCoordSet
{
	public ItemTurfScanner()
	{
		setUnlocalizedName("turfScanner");
		setRegistryName("turf_scanner");
		setMaxStackSize(1);
		setCreativeTab(TabSplatCraft.main);
		
		this.addPropertyOverride(new ResourceLocation("active"), new IItemPropertyGetter()
		{
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
			{
				return hasCoordSet(stack) ? 1.0F : 0.0F;
			}
		});
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer playerIn, EnumHand handIn)
	{
		ItemStack stack = playerIn.getHeldItem(handIn);
		if(!hasCoordSet(stack))
			return new ActionResult<>(EnumActionResult.FAIL, stack);
		
		BlockPos[] coordSet = getCoordSet(stack);
		BlockPos blockpos = coordSet[0];
		BlockPos blockpos1 = coordSet[1];
		BlockPos blockpos2 = new BlockPos(Math.min(blockpos.getX(), blockpos1.getX()), Math.min(blockpos.getY(), Math.min(blockpos1.getY(), blockpos.getY())), Math.min(blockpos.getZ(), blockpos1.getZ()));
		BlockPos blockpos3 = new BlockPos(Math.max(blockpos.getX(), blockpos1.getX()), Math.max(blockpos.getY(), Math.max(blockpos1.getY(), blockpos.getY())), Math.max(blockpos.getZ(), blockpos1.getZ()));
		
		
		if (!(blockpos2.getY() >= 0 && blockpos3.getY() < 256))
			playerIn.sendStatusMessage(new TextComponentTranslation("commands.turfWar.outOfWorld"), true);
			
		for(int j = blockpos2.getZ(); j <= blockpos3.getZ(); j += 16)
		{
			for(int k = blockpos2.getX(); k <= blockpos3.getX(); k += 16)
			{
				if(!world.isBlockLoaded(new BlockPos(k, blockpos3.getY() - blockpos2.getY(), j)))
				{
					playerIn.sendStatusMessage(new TextComponentTranslation("commands.turfWar.outOfWorld"), true);
				}
			}
		}
		
		if(world.isRemote)
			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		TreeMap<Integer, Integer> scores = new TreeMap<>();
		int blockTotal = 0;
		
		for(int x = blockpos2.getX(); x <= blockpos3.getX(); x++)
			for(int z = blockpos2.getZ(); z <= blockpos3.getZ(); z++)
			{
				int y = Math.min(blockpos3.getY(), Math.max(blockpos2.getY(), CommandTurfWar.getTopSolidOrLiquidBlock(new BlockPos(x,1, z), world, blockpos3.getY()).down().getY()));
				
				BlockPos checkPos = new BlockPos(x,y,z);
				IBlockState checkState = world.getBlockState(checkPos);
				
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
			playerIn.sendStatusMessage(new TextComponentTranslation("commands.turfWar.noInk"), true);
		else SplatCraftPacketHandler.instance.sendToDimension(new PacketSendColorScores(colors, colorScores), playerIn.dimension);
		
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}
}
