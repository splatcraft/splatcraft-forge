package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.capabilities.PlayerInfoCapability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

import java.util.UUID;

public class UpdatePlayerInfoPacket extends SplatcraftPacket
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
	void encode(PacketBuffer buffer)
	{
		buffer.writeString(target.toString());
		buffer.writeCompoundTag(nbt);
	}
	
	public static UpdatePlayerInfoPacket decode(PacketBuffer buffer)
	{
		return new UpdatePlayerInfoPacket(UUID.fromString(buffer.readString()), buffer.readCompoundTag());
	}
	
	@Override
	void execute(PlayerEntity player)
	{
		PlayerEntity target = player.world.getPlayerByUuid(this.target);
		
		PlayerInfoCapability.get(target).readNBT(nbt);
	}
	
	@Override
	EnumDirection getDirection()
	{
		return EnumDirection.PLAY_TO_CLIENT;
	}
}
