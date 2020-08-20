package com.cibernet.splatcraft.handlers;

import com.cibernet.splatcraft.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.network.*;
import com.cibernet.splatcraft.registries.SplatcraftGameRules;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber
public class SplatcraftCommonHandler
{
	@SubscribeEvent
	public static void onPlayerClone(final PlayerEvent.Clone event)
	{
		PlayerEntity player = event.getPlayer();
		PlayerInfoCapability.get(player).readNBT(PlayerInfoCapability.get(event.getOriginal()).writeNBT(new CompoundNBT()));
	}
	
	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
	{
		PlayerEntity player = event.getPlayer();
		SplatcraftPacketHandler.sendToPlayer(new UpdateBooleanGamerulesPacket(SplatcraftGameRules.booleanRules), (ServerPlayerEntity) player);
		
		int[] colors = new int[ScoreboardHandler.getCriteriaKeySet().size()];
		int i = 0;
		for(int c : ScoreboardHandler.getCriteriaKeySet())
			colors[i++] = c;
		
		SplatcraftPacketHandler.sendToPlayer(new UpdateColorScoresPacket(true, true, colors), (ServerPlayerEntity) player);
	}
	
	@SubscribeEvent
	public static void capabilityUpdateEvent(TickEvent.PlayerTickEvent event)
	{
		try
		{
			if(event.player.deathTime <= 0 && !PlayerInfoCapability.get(event.player).isInitialized())
			{
				if(event.player.world.isRemote)
					SplatcraftPacketHandler.sendToServer(new RequestPlayerInfoPacket(event.player));
				PlayerInfoCapability.get(event.player).setInitialized(true);
			}
		} catch(NullPointerException e) {}
	}
	
	@SubscribeEvent
	public static void gameruleUpdateEvent(TickEvent.WorldTickEvent event)
	{
		World world = event.world;
		if(world.isRemote)
			return;
		for(Map.Entry<Integer, Boolean> rule : SplatcraftGameRules.booleanRules.entrySet())
		{
			boolean worldValue = world.getGameRules().getBoolean(SplatcraftGameRules.getRuleFromIndex(rule.getKey()));
			if(rule.getValue() != worldValue)
			{
				SplatcraftGameRules.booleanRules.put(rule.getKey(), worldValue);
				SplatcraftPacketHandler.sendToAll(new UpdateBooleanGamerulesPacket(SplatcraftGameRules.getRuleFromIndex(rule.getKey()), rule.getValue()));
			}
		}
	}
}
