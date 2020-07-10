package com.cibernet.splatcraft.handlers;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.capabilities.PlayerInfoCapability;
import com.cibernet.splatcraft.network.PlayerColorPacket;
import com.cibernet.splatcraft.network.SplatcraftPacketHandler;
import com.cibernet.splatcraft.network.UpdatePlayerInfoPacket;
import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Splatcraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SplatcraftCommonHandler
{
	@SubscribeEvent
	public void onPlayerClone(final PlayerEvent.Clone event)
	{
		PlayerEntity player = event.getPlayer();
		
		CompoundNBT oldNBT = event.getOriginal().getCapability(PlayerInfoCapability.CAPABILITY).orElseThrow(() -> new NullPointerException("Failed to find Splatcraft Capability")).writeNBT(new CompoundNBT());
		player.getCapability(PlayerInfoCapability.CAPABILITY).orElseThrow(() -> new NullPointerException("Failed to find Splatcraft Capability")).readNBT(oldNBT);
	}
	
	@SubscribeEvent
	public void onEntityJoinWorld(final EntityJoinWorldEvent event)
	{
		if(event.getEntity() instanceof ServerPlayerEntity)
		{
			ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();
			SplatcraftPacketHandler.sendToPlayer(new UpdatePlayerInfoPacket(player), player);
		}
	}
}
