package com.cibernet.splatcraft.handlers.client;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.capabilities.PlayerInfo;
import com.cibernet.splatcraft.capabilities.PlayerInfoCapability;
import com.cibernet.splatcraft.client.renderer.InkSquidRenderer;
import com.cibernet.splatcraft.util.ColorUtils;
import com.cibernet.splatcraft.util.InkBlockUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

//@Mod.EventBusSubscriber(modid = Splatcraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
@Mod.EventBusSubscriber(Dist.CLIENT)
public class RendererHandler
{
	private static InkSquidRenderer squidRenderer = null;
	@SubscribeEvent
	public static void playerRender(RenderPlayerEvent.Pre event)
	{
		PlayerEntity player = event.getPlayer();
		
		if(PlayerInfoCapability.isSquid(player))
		{
			event.setCanceled(true);
			if(squidRenderer == null)
				squidRenderer = new InkSquidRenderer(event.getRenderer().getRenderManager());
			if(!InkBlockUtils.canSquidHide(player))
			squidRenderer.render(player, player.rotationYawHead, event.getPartialRenderTick(), event.getMatrixStack(), event.getBuffers(), event.getLight());
		}
	}
	
	@SubscribeEvent
	public static void renderHand(RenderHandEvent event)
	{
		PlayerEntity player = Minecraft.getInstance().player;
		if(PlayerInfoCapability.isSquid(player))
			event.setCanceled(true);
	}
}
