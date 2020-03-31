package com.cibernet.splatcraft.items;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCoordSet extends Item
{
	
	protected static NBTTagCompound checkTagCompound(ItemStack stack) {
		NBTTagCompound tagCompound = stack.getTagCompound();
		if (tagCompound == null) {
			tagCompound = new NBTTagCompound();
			stack.setTagCompound(tagCompound);
		}
		
		return tagCompound;
	}
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		super.addInformation(stack, worldIn, tooltip, flagIn);
		
		NBTTagCompound compound = checkTagCompound(stack);
		
		
		
		if(hasCoordSet(stack))
			tooltip.add(I18n.format("item.coordSet.tooltip.b", compound.getInteger("pointAX"), compound.getInteger("pointAY"), compound.getInteger("pointAZ"),
					compound.getInteger("pointBX"), compound.getInteger("pointBY"), compound.getInteger("pointBZ")));
		else if(compound.hasKey("pointAX") && compound.hasKey("pointAY") && compound.hasKey("pointAZ"))
			tooltip.add(I18n.format("item.coordSet.tooltip.a", compound.getInteger("pointAX"), compound.getInteger("pointAY"), compound.getInteger("pointAZ")));
	}
	
	public static boolean hasCoordSet(ItemStack stack)
	{
		NBTTagCompound compound = checkTagCompound(stack);
		
		return (compound.hasKey("pointAX") && compound.hasKey("pointAY") && compound.hasKey("pointAZ")) && (compound.hasKey("pointBX") && compound.hasKey("pointBY") && compound.hasKey("pointBZ"));
	}
	
	public static BlockPos[] getCoordSet(ItemStack stack)
	{
		if(!hasCoordSet(stack))
			return new BlockPos[0];
		NBTTagCompound compound = checkTagCompound(stack);
		return new BlockPos[] {new BlockPos(compound.getInteger("pointAX"), compound.getInteger("pointAY"), compound.getInteger("pointAZ")),
				new BlockPos(compound.getInteger("pointBX"), compound.getInteger("pointBY"), compound.getInteger("pointBZ"))};
	}
	
	public static boolean addCoords(EntityPlayer playerIn, ItemStack stack, BlockPos  pos)
	{
		NBTTagCompound compound = checkTagCompound(stack);
		
		if(compound.hasKey("pointBX") && compound.hasKey("pointBY") && compound.hasKey("pointBZ")) return false;
		
		String key = (compound.hasKey("pointAX") && compound.hasKey("pointAY") && compound.hasKey("pointAZ")) ? "B" : "A";
		
		compound.setInteger("point"+key+"X", pos.getX());
		compound.setInteger("point"+key+"Y", pos.getY());
		compound.setInteger("point"+key+"Z", pos.getZ());
		
		playerIn.sendStatusMessage(new TextComponentString(I18n.format("status.coordSet."+key, pos.getX(), pos.getY(), pos.getZ())), true);
		
		return true;
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		return addCoords(player, player.getHeldItem(hand), pos) ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
	}
}
