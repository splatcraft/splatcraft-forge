package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.blocks.IInked;
import com.cibernet.splatcraft.utils.TabSplatCraft;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.command.CommandException;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemInkDisruptor extends ItemCoordSet
{
	public ItemInkDisruptor()
	{
		setUnlocalizedName("inkDisruptor");
		setRegistryName("ink_disruptor");
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
			playerIn.sendStatusMessage(new TextComponentTranslation("commands.clearInk.outOfWorld"), true);
		
		
		for(int j = blockpos2.getZ(); j <= blockpos3.getZ(); j += 16)
		{
			for(int k = blockpos2.getX(); k <= blockpos3.getX(); k += 16)
			{
				if(!world.isBlockLoaded(new BlockPos(k, blockpos3.getY() - blockpos2.getY(), j)))
				{
					playerIn.sendStatusMessage(new TextComponentTranslation("commands.clearInk.outOfWorld"), true);
				}
			}
		}
		int count = 0;
		for(int x = blockpos2.getX(); x <= blockpos3.getX(); x++)
			for(int y = blockpos2.getY(); y <= blockpos3.getY(); y++)
				for(int z = blockpos2.getZ(); z <= blockpos3.getZ(); z++)
				{
					BlockPos pos = new BlockPos(x,y,z);
					Block block = world.getBlockState(pos).getBlock();
					if(block instanceof IInked)
					{
						if(((IInked) block).clearInk(world, pos))
							count++;
					}
				}
		if(world.isRemote)
			playerIn.sendStatusMessage(new TextComponentString(I18n.format("commands.clearInk.success", count)), true);
		
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}
}
