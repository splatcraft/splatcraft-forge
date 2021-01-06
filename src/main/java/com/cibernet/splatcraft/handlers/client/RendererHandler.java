package com.cibernet.splatcraft.handlers.client;

import com.cibernet.splatcraft.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.client.renderer.InkSquidRenderer;
import com.cibernet.splatcraft.client.renderer.PlayerSquidRenderer;
import com.cibernet.splatcraft.registries.SplatcraftGameRules;
import com.cibernet.splatcraft.util.ColorUtils;
import com.cibernet.splatcraft.util.InkBlockUtils;
import com.cibernet.splatcraft.util.PlayerCooldown;
import com.google.common.collect.Maps;
import com.mrcrayfish.obfuscate.client.event.PlayerModelEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

//@Mod.EventBusSubscriber(modid = Splatcraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
@Mod.EventBusSubscriber(Dist.CLIENT)
public class RendererHandler
{

	public static final ArrayList<ResourceLocation> textures = new ArrayList<>();
	
	private static PlayerSquidRenderer squidRenderer = null;
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void playerRender(RenderPlayerEvent event)
	{
		PlayerEntity player = event.getPlayer();
		
		if(PlayerInfoCapability.isSquid(player))
		{
			event.setCanceled(true);
			if(squidRenderer == null)
				squidRenderer = new PlayerSquidRenderer(event.getRenderer().getRenderManager());
			if(!InkBlockUtils.canSquidHide(player))
			{

				//squidRenderer.getRenderManager().setRenderShadow(true);
				squidRenderer.render(player, player.rotationYawHead, event.getPartialRenderTick(), event.getMatrixStack(), event.getBuffers(), event.getLight());
			}
			//else player.setInvisible(true);
		}
		//else event.getRenderer().getRenderManager().setRenderShadow(true);
	}

	@SubscribeEvent
	public static void onRenderTick(TickEvent.RenderTickEvent event)
	{
		PlayerEntity player = Minecraft.getInstance().player;
		if(player != null && PlayerCooldown.hasPlayerCooldown(player))
			player.inventory.currentItem = PlayerCooldown.getPlayerCooldown(player).getSlotIndex();
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void playerRenderPost(RenderPlayerEvent.Post event)
	{
	}
	
	private static float tickTime = 0;
	private static float oldCooldown = 0;
	
	@SubscribeEvent
	public static void renderHand(RenderHandEvent event)
	{
		PlayerEntity player = Minecraft.getInstance().player;
		if(PlayerInfoCapability.isSquid(player))
		{
			event.setCanceled(true);
			return;
		}
		
		if(PlayerCooldown.hasPlayerCooldown(player))
		{
			PlayerCooldown cooldown = PlayerCooldown.getPlayerCooldown(player);
			float time = (float)cooldown.getTime();
			float maxTime = (float)cooldown.getMaxTime();
			if(time != oldCooldown)
			{
				oldCooldown = time;
				tickTime = 0;
			}
			tickTime = (tickTime+1) % 10;
			float yOff = -0.5f*((time/maxTime));// - (tickTime/20f));
			event.getMatrixStack().translate(0, yOff, 0);
		}
		else tickTime = 0;
	}
	
	@SubscribeEvent
	public static void onChatMessage(ClientChatReceivedEvent event)
	{
		if(SplatcraftGameRules.getBooleanRuleValue(Minecraft.getInstance().world, SplatcraftGameRules.COLORED_PLAYER_NAMES) && event.getMessage() instanceof TranslationTextComponent)
		{
			TranslationTextComponent component = (TranslationTextComponent) event.getMessage();
			TreeMap<String, AbstractClientPlayerEntity> players = Maps.newTreeMap();
			Minecraft.getInstance().world.getPlayers().forEach(player -> players.put(player.getDisplayName().getString(), player));
			
			for(Object obj : component.getFormatArgs())
			{
				if(!(obj instanceof TextComponent))
					continue;
				TextComponent msgChildren = (TextComponent) obj;
				String key = msgChildren.getString();
				
				if(players.containsKey(key))
					msgChildren.setStyle(Style.EMPTY.setColor(Color.fromInt(ColorUtils.getPlayerColor(players.get(key)))));
			}
		}
	}
	
	@SubscribeEvent
	public static void renderNameplate(RenderNameplateEvent event)
	{
		if(SplatcraftGameRules.getBooleanRuleValue(event.getEntity().world, SplatcraftGameRules.COLORED_PLAYER_NAMES) && event.getEntity() instanceof LivingEntity)
		{
			int color = ColorUtils.getEntityColor((LivingEntity) event.getEntity());
			if(color != -1)
				event.setContent(((TextComponent)event.getContent()).setStyle(Style.EMPTY.setColor(Color.fromInt(color))));
		}
	}
	
	
}
