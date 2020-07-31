package com.cibernet.splatcraft.handlers;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.capabilities.PlayerInfoCapability;
import com.cibernet.splatcraft.client.renderer.InkSquidRenderer;
import com.cibernet.splatcraft.network.*;
import com.cibernet.splatcraft.registries.SplatcraftGameRules;
import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
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
		
		System.out.println(ColorUtils.getColorName(ColorUtils.getPlayerColor(player)) + " old:" + ColorUtils.getColorName(ColorUtils.getPlayerColor(event.getOriginal())));
		
		PlayerInfoCapability.get(player).readNBT(PlayerInfoCapability.get(event.getOriginal()).writeNBT(new CompoundNBT()));
		SplatcraftPacketHandler.sendToDim(new UpdatePlayerInfoPacket(player), event.getPlayer().world);
		
		System.out.println(ColorUtils.getColorName(ColorUtils.getPlayerColor(player)) + " old:" + ColorUtils.getColorName(ColorUtils.getPlayerColor(event.getOriginal())) + "\n-");
	}
	
	@SubscribeEvent
	public static void onEntityJoinWorld(final EntityJoinWorldEvent event)
	{
		if(event.getEntity() instanceof ServerPlayerEntity)
		{
			ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();
			SplatcraftPacketHandler.sendToDim(new UpdatePlayerInfoPacket(player), event.getWorld());
			SplatcraftPacketHandler.sendToPlayer(new UpdateBooleanGamerulesPacket(SplatcraftGameRules.booleanRules), player);
		}
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
