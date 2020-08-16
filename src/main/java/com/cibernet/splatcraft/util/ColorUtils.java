package com.cibernet.splatcraft.util;

import com.cibernet.splatcraft.capabilities.PlayerInfoCapability;
import com.cibernet.splatcraft.data.tags.SplatcraftTags;
import com.cibernet.splatcraft.entities.IColoredEntity;
import com.cibernet.splatcraft.network.PlayerColorPacket;
import com.cibernet.splatcraft.network.SplatcraftPacketHandler;
import com.cibernet.splatcraft.registries.SplatcraftGameRules;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.*;
import net.minecraft.world.World;

import java.util.Random;

public class ColorUtils
{
	public static final int ORANGE = 0xDF641A;
	public static final int BLUE = 0x26229F;
	public static final int GREEN = 0xc83d79;
	public static final int PINK = 0x409d3b;
	
	public static final int DEFAULT = 0x1F1F2D;
	
	public static final int[] STARTER_COLORS = new int[] {ORANGE, BLUE, GREEN, PINK};
	
	public static int getEntityColor(LivingEntity entity)
	{
		if(entity instanceof PlayerEntity)
			return getPlayerColor((PlayerEntity) entity);
		else if(entity instanceof IColoredEntity)
			return ((IColoredEntity) entity).getColor();
		else return -1;
	}
	
	public static int getPlayerColor(PlayerEntity player)
	{
		try
		{
			return PlayerInfoCapability.get(player).getColor();
		}
		catch(NullPointerException e)
		{
			return 0;
		}
	}
	public static void setPlayerColor(PlayerEntity player, int color, boolean updateClient)
	{
		PlayerInfoCapability.get(player).setColor(color);
		
		World world = player.world;
		if(!world.isRemote && updateClient)
			SplatcraftPacketHandler.sendToDim(new PlayerColorPacket(player, color), world);
	}
	
	public static void setPlayerColor(PlayerEntity player, int color)
	{
		setPlayerColor(player, color, true);
	}
	
	public static int getInkColor(ItemStack stack)
	{
		CompoundNBT nbt = stack.getTag();
		
		if(nbt == null || !nbt.contains("Color"))
			return -1;
		
		return nbt.getInt("Color");
	}
	
	public static ItemStack setInkColor(ItemStack stack, int color)
	{
		stack.getOrCreateTag().putInt("Color", color);
		return stack;
	}
	
	public static int getInkColor(TileEntity te)
	{
		if(!(te instanceof InkColorTileEntity))
			return -1;
		
		return ((InkColorTileEntity) te).getColor();
	}
	
	public static boolean setInkColor(TileEntity te, int color)
	{
		if(!(te instanceof InkColorTileEntity))
			return false;
		
		((InkColorTileEntity) te).setColor(color);
		return true;
	}
	
	public static String getColorName(int color)
	{
		InkColor colorObj = InkColor.getByHex(color);
		
		String colorFormatting = "";//TextFormatting.fromColorIndex(color).toString();
		
		if(colorObj != null)
			return colorObj.getLocalizedName();
		
		String fallbackUnloc = "ink_color."+String.format("%06X", color).toLowerCase();
		String fallbackName = I18n.format(fallbackUnloc);
		
		if(!fallbackName.equals(fallbackUnloc))
			return fallbackUnloc;
		return "#"+String.format("%06X", color).toUpperCase();
		
	}
	
	public static ITextComponent getFormatedColorName(int color, boolean isTooltip)
	{
		if(color == ColorUtils.DEFAULT)
			return new StringTextComponent( (isTooltip ? TextFormatting.GRAY : "") + getColorName(color));
		return new StringTextComponent(getColorName(color)).setStyle(Style.EMPTY.setColor(Color.func_240743_a_(color)));
	}
	
	public static boolean colorEquals(LivingEntity entity, TileEntity te)
	{
		int a = getEntityColor(entity);
		int b = getInkColor(te);
		
		if(a == -1 || b == -1)
			return false;
		return SplatcraftGameRules.getBooleanRuleValue(entity.world, SplatcraftGameRules.UNIVERSAL_INK) ? true : a == b;
	}
	
	public static ItemStack setColorLocked(ItemStack stack, boolean isLocked)
	{
		stack.getOrCreateTag().putBoolean("ColorLocked", isLocked);
		return stack;
	}
	
	public static boolean isColorLocked(ItemStack stack)
	{
		CompoundNBT nbt = stack.getTag();
		
		if(nbt == null || !nbt.contains("ColorLocked"))
			return false;
		
		return nbt.getBoolean("ColorLocked");
	}
	
	public static float[] hexToRGB(int color)
	{
		float r = ((color & 16711680) >> 16) / 255.0f;
		float g = ((color & '\uff00') >> 8) / 255.0f;
		float b = ((color & 255) >> 0) / 255.0f;
		
		return new float[] {r, g, b};
	}
	
	public static int getRandomStarterColor()
	{
		
		return SplatcraftTags.InkColors.STARTER_COLORS.getAllElements().isEmpty() ?
				STARTER_COLORS[(int) (Math.random()*(STARTER_COLORS.length))] : SplatcraftTags.InkColors.STARTER_COLORS.getRandomElement(new Random()).getColor();
	}
}
