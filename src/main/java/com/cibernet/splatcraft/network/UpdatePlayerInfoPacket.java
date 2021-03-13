package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.data.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.network.base.PlayToClientPacket;
import com.cibernet.splatcraft.util.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

import java.util.UUID;

public class UpdatePlayerInfoPacket extends PlayToClientPacket
{
	UUID target;
	CompoundNBT nbt;
	
	
	protected UpdatePlayerInfoPacket(UUID player, CompoundNBT nbt)
	{
		this.target = player;
		this.nbt = nbt;
	}
	
	public UpdatePlayerInfoPacket(PlayerEntity target)
	{
		this(target.getUniqueID(), PlayerInfoCapability.get(target).writeNBT(new CompoundNBT()));
	}
	
	@Override
	public void encode(PacketBuffer buffer)
	{
		buffer.writeString(target.toString());
		buffer.writeCompoundTag(nbt);
	}
	
	public static UpdatePlayerInfoPacket decode(PacketBuffer buffer)
	{
		return new UpdatePlayerInfoPacket(UUID.fromString(buffer.readString()), buffer.readCompoundTag());
	}
	
	@Override
	public void execute()
	{
		PlayerEntity target = Minecraft.getInstance().world.getPlayerByUuid(this.target);
		
		PlayerInfoCapability.get(target).readNBT(nbt);
		ClientUtils.setClientPlayerColor(target.getDisplayName().getString(), this.nbt.getInt("Color"));
	}
	
}
