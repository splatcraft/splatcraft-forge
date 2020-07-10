package com.cibernet.splatcraft.util;

import com.cibernet.splatcraft.capabilities.PlayerInfoCapability;
import com.cibernet.splatcraft.network.PlayerColorPacket;
import com.cibernet.splatcraft.network.SplatcraftPacketHandler;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ColorUtils
{
	public static final int ORANGE = 0xDF641A;
	public static final int BLUE = 0x26229F;
	public static final int GREEN = 0xc83d79;
	public static final int PINK = 0x409d3b;
	
	public static final int DEFAULT = 0x1F1F2D;
	
	public static final int[] STARTER_COLORS = new int[] {ORANGE, BLUE, GREEN, PINK};
	
	public static int getPlayerColor(PlayerEntity player)
	{
		try
		{
			return player.getCapability(PlayerInfoCapability.CAPABILITY).orElseThrow(() -> new NullPointerException("player color is null")).getColor();
		}
		catch(NullPointerException e)
		{
			return 0;
		}
	}
	public static void setPlayerColor(PlayerEntity player, int color, boolean updateClient)
	{
		player.getCapability(PlayerInfoCapability.CAPABILITY).orElseThrow(() -> new NullPointerException("Player Color cannot be null!")).setColor(color);
		
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
	
	public static int getRandomStarterColor()
	{
		return STARTER_COLORS[(int) (Math.random()*(STARTER_COLORS.length))];
	}
}
