package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.items.ItemWeaponBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;

import java.util.EnumSet;

public class PacketWeaponLeftClick extends SplatCraftPacket
{
	
	EnumHand hand;
	
	@Override
	public SplatCraftPacket generatePacket(Object... dat)
	{
		data.writeInt( dat[0].equals(EnumHand.MAIN_HAND) ? 1 : 0);
		return this;
	}
	
	@Override
	public SplatCraftPacket consumePacket(ByteBuf data)
	{
		hand = data.readInt() == 1 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
		return this;
	}
	
	@Override
	public void execute(EntityPlayer player)
	{
		ItemStack stack = player.getHeldItem(hand);
		
		if(stack.getItem() instanceof ItemWeaponBase)
			((ItemWeaponBase)stack.getItem()).onItemLeftClick(player.world, player, stack);
	}
	
	@Override
	public EnumSet<Side> getSenderSide()
	{
		return EnumSet.of(Side.CLIENT);
	}
}
