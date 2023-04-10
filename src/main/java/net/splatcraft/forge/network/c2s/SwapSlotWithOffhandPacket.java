package net.splatcraft.forge.network.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

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
	public void execute(Player player)
	{
		ItemStack stack = player.getOffhandItem();
		player.setItemInHand(InteractionHand.OFF_HAND, player.getInventory().getItem(slot));
		player.getInventory().setItem(slot, stack);
		player.stopUsingItem();
	}

	@Override
	public void encode(FriendlyByteBuf buffer)
	{
		buffer.writeInt(slot);
		buffer.writeBoolean(stopUsing);
	}

	public static SwapSlotWithOffhandPacket decode(FriendlyByteBuf buffer)
	{
		return new SwapSlotWithOffhandPacket(buffer.readInt(), buffer.readBoolean());
	}
}
