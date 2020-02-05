package com.cibernet.splatcraft.handlers;

import com.cibernet.splatcraft.entities.renderers.RenderInklingSquid;
import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ClientEventHandler
{
	public static ClientEventHandler instance = new ClientEventHandler();
	
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void renderPlayerPre(RenderPlayerEvent.Pre event)
	{
		
		EntityPlayer player = event.getEntityPlayer();
		if(SplatCraftKeyHandler.squidKey.isKeyDown())
		{
			event.setCanceled(true);
			
			if(!player.getEntityWorld().getBlockState(new BlockPos(player.posX, player.posY-1, player.posZ)).getBlock().equals(SplatCraftBlocks.inkedBlock))
			{
				RenderInklingSquid render = new RenderInklingSquid(event.getRenderer().getRenderManager());
				render.doRender(player, event.getX(), event.getY(), event.getZ(), player.rotationYaw, event.getPartialRenderTick());
			}
			
		}
	}
	
	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event)
	{
	}
}
