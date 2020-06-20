package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.world.save.SplatCraftPlayerData;
import javafx.util.Pair;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemRemote extends Item
{
	
	protected int totalModes = 1;
	
	public ItemRemote()
	{
		this.addPropertyOverride(new ResourceLocation("active"), new IItemPropertyGetter()
		{
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
			{
				return hasCoordSet(stack) ? 1.0F : 0.0F;
			}
		});
		
		this.addPropertyOverride(new ResourceLocation("mode"), new IItemPropertyGetter()
		{
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
			{
				return getRemoteMode(stack);
			}
		});
	}
	
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
		
		if(playerIn.world.isRemote)
			playerIn.sendStatusMessage(new TextComponentString(I18n.format("status.coordSet."+key, pos.getX(), pos.getY(), pos.getZ())), true);
		
		return true;
	}
	
	public static int getRemoteMode(ItemStack stack)
	{
		NBTTagCompound compound = checkTagCompound(stack);
		return compound.getInteger("mode");
	}
	
	public static void setRemoteMode(ItemStack stack, int mode)
	{
		NBTTagCompound compound = checkTagCompound(stack);
		compound.setInteger("mode", mode);
	}
	
	public static int cycleRemoteMode(ItemStack stack)
	{
		int mode = getRemoteMode(stack)+1;
		if(stack.getItem() instanceof ItemRemote)
			mode %= ((ItemRemote) stack.getItem()).totalModes;
		setRemoteMode(stack, mode);
		return mode;
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		return addCoords(player, player.getHeldItem(hand), pos) ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		ItemStack stack = playerIn.getHeldItem(handIn);
		int mode = getRemoteMode(stack);
		
		if(playerIn.isSneaking())
		{
			if(totalModes > 1)
			{
				mode = cycleRemoteMode(stack);
				String unformatedMode = getUnlocalizedName()+".mode."+mode;
				String modeName = net.minecraft.util.text.translation.I18n.translateToLocal(unformatedMode);
				
				if(modeName.equals(unformatedMode))
					modeName = mode + "";
				
				playerIn.sendStatusMessage(new TextComponentTranslation("status.remoteMode", modeName), true);
			}
		}
		else if(hasCoordSet(stack))
		{
			RemoteResult result = onRemoteUse(worldIn, stack, SplatCraftPlayerData.getInkColor(playerIn), mode);
			
			if(result.getOutput() != null)
				playerIn.sendStatusMessage(result.getOutput(), true);
			
			return new ActionResult<>(result.wasSuccessful() ? EnumActionResult.SUCCESS : EnumActionResult.FAIL, stack);
		}
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}
	
	public RemoteResult onRemoteUse(World world, BlockPos posA, BlockPos posB, @Nullable ItemStack stack, int colorIn, int mode)
	{
		return createResult(false, null);
	}
	
	public RemoteResult onRemoteUse(World world, ItemStack stack, int colorIn, int mode)
	{
		BlockPos[] coordSet = getCoordSet(stack);
		BlockPos blockpos = coordSet[0];
		BlockPos blockpos1 = coordSet[1];
		
		return onRemoteUse(world, blockpos, blockpos1, stack, colorIn, mode);
	}
	
	public static RemoteResult createResult(boolean success, TextComponentBase output)
	{
		return new RemoteResult(success, output);
	}
	
	public static class RemoteResult
	{
		boolean success;
		TextComponentBase output;
		
		public RemoteResult(boolean success, TextComponentBase output)
		{
			this.success = success;
			this.output = output;
		}
		
		public boolean wasSuccessful() {return success;}
		public TextComponentBase getOutput() {return output;}
	}
}
