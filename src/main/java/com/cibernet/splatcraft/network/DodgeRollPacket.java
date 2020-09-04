package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.items.DualieItem;
import com.cibernet.splatcraft.network.base.PlayToServerPacket;
import com.cibernet.splatcraft.util.ColorUtils;
import com.cibernet.splatcraft.util.InkBlockUtils;
import com.cibernet.splatcraft.util.InkExplosion;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class DodgeRollPacket extends PlayToServerPacket
{
	UUID target;
	ItemStack mainDualie;
	ItemStack offhandDualie;
	
	public DodgeRollPacket(PlayerEntity player, ItemStack mainDualie, ItemStack offhandDualie)
	{
		this(player.getUniqueID(), mainDualie, offhandDualie);
	}
	
	public DodgeRollPacket(UUID target, ItemStack mainDualie, ItemStack offhandDualie)
	{
		this.target = target;
		this.mainDualie = mainDualie;
		this.offhandDualie = offhandDualie;
	}
	
	@Override
	public void execute(PlayerEntity player)
	{
		PlayerEntity target = player.world.getPlayerByUuid(this.target);
		DualieItem.performRoll(target, mainDualie, offhandDualie);
	}
	
	@Override
	public void encode(PacketBuffer buffer)
	{
		buffer.writeUniqueId(target);
		buffer.writeItemStack(mainDualie);
		buffer.writeItemStack(offhandDualie);
	}
	
	public static DodgeRollPacket decode(PacketBuffer buffer)
	{
		return new DodgeRollPacket(buffer.readUniqueId(), buffer.readItemStack(), buffer.readItemStack());
	}
}
