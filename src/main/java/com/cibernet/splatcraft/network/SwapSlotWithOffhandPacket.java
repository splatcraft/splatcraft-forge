package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.network.base.PlayToServerPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;

public class SwapSlotWithOffhandPacket extends PlayToServerPacket
{
	final int slot;
	final boolean stopUsing;

	public SwapSlotWithOffhandPacket(int slot, boolean stopUsing)
	{
		this.slot = slot;
		this.stopUsing = stopUsing;
	}

	@Override
	public void execute(PlayerEntity player)
	{
		ItemStack stack = player.getOffhandItem();
		player.setItemInHand(Hand.OFF_HAND, player.inventory.getItem(slot));
		player.inventory.setItem(slot, stack);
		player.stopUsingItem();
	}

	@Override
	public void encode(PacketBuffer buffer)
	{
		buffer.writeInt(slot);
		buffer.writeBoolean(stopUsing);
	}

	public static SwapSlotWithOffhandPacket decode(PacketBuffer buffer)
	{
		return new SwapSlotWithOffhandPacket(buffer.readInt(), buffer.readBoolean());
	}
}
