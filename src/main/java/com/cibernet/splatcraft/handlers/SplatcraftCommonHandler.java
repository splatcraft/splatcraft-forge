package com.cibernet.splatcraft.handlers;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.capabilities.PlayerInfoCapability;
import com.cibernet.splatcraft.client.renderer.InkSquidRenderer;
import com.cibernet.splatcraft.network.PlayerColorPacket;
import com.cibernet.splatcraft.network.SplatcraftPacketHandler;
import com.cibernet.splatcraft.network.UpdatePlayerInfoPacket;
import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class SplatcraftCommonHandler
{
	@SubscribeEvent
	public static void onPlayerClone(final PlayerEvent.Clone event)
	{
		System.out.println("A");
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
			System.out.println("!");
			ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();
			SplatcraftPacketHandler.sendToDim(new UpdatePlayerInfoPacket(player), event.getWorld());
		}
	}
}
