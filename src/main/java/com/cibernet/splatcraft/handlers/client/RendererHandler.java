package com.cibernet.splatcraft.handlers.client;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.capabilities.PlayerInfo;
import com.cibernet.splatcraft.capabilities.PlayerInfoCapability;
import com.cibernet.splatcraft.client.renderer.InkSquidRenderer;
import com.cibernet.splatcraft.registries.SplatcraftGameRules;
import com.cibernet.splatcraft.util.ColorUtils;
import com.cibernet.splatcraft.util.InkBlockUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

//@Mod.EventBusSubscriber(modid = Splatcraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
@Mod.EventBusSubscriber(Dist.CLIENT)
public class RendererHandler
{
	public static boolean wasInvisible;
	
	private static InkSquidRenderer squidRenderer = null;
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void playerRender(RenderPlayerEvent.Pre event)
	{
		PlayerEntity player = event.getPlayer();
		
		wasInvisible = player.isInvisible();
		
		if(PlayerInfoCapability.isSquid(player))
		{
			event.setCanceled(true);
			if(squidRenderer == null)
				squidRenderer = new InkSquidRenderer(event.getRenderer().getRenderManager());
			if(!InkBlockUtils.canSquidHide(player))
			{
				//squidRenderer.getRenderManager().setRenderShadow(true);
				squidRenderer.render(player, player.rotationYawHead, event.getPartialRenderTick(), event.getMatrixStack(), event.getBuffers(), event.getLight());
			}
			//else player.setInvisible(true);
		}
		//else event.getRenderer().getRenderManager().setRenderShadow(true);
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void playerRenderPost(RenderPlayerEvent.Post event)
	{
		event.getPlayer().setInvisible(wasInvisible);
	}
	
	@SubscribeEvent
	public static void renderHand(RenderHandEvent event)
	{
		PlayerEntity player = Minecraft.getInstance().player;
		if(PlayerInfoCapability.isSquid(player))
			event.setCanceled(true);
	}
	
	@SubscribeEvent
	public static void renderNameplate(RenderNameplateEvent event)
	{
		if(SplatcraftGameRules.getBooleanRuleValue(event.getEntity().world, SplatcraftGameRules.COLORED_NAMEPLATES) && event.getEntity() instanceof LivingEntity)
		{
			int color = ColorUtils.getEntityColor((LivingEntity) event.getEntity());
			if(color != -1)
				event.setContent(((TextComponent)event.getContent()).setStyle(Style.EMPTY.setColor(Color.func_240743_a_(color))));
		}
	}
	
	
}
