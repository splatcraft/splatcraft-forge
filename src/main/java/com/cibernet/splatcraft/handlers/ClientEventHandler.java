package com.cibernet.splatcraft.handlers;

import com.cibernet.splatcraft.entities.renderers.RenderInklingSquid;
import com.cibernet.splatcraft.network.PacketPlayerData;
import com.cibernet.splatcraft.network.SplatCraftChannelHandler;
import com.cibernet.splatcraft.network.SplatCraftPacket;
import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.utils.SplatCraftPlayerData;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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
		if(SplatCraftPlayerData.getIsSquid(player))
		{
			event.setCanceled(true);
			
			if(!SplatCraftUtils.canSquidHide(player.world, player))
			{
				RenderInklingSquid render = new RenderInklingSquid(event.getRenderer().getRenderManager());
				render.doRender(player, event.getX(), event.getY(), event.getZ(), player.rotationYawHead, event.getPartialRenderTick());
			}
			
		}
	}

	@SubscribeEvent
	public void onLeftClick(PlayerInteractEvent.LeftClickEmpty event)
	{
		SplatCraftChannelHandler.sendToServer(SplatCraftPacket.makePacket(SplatCraftPacket.Type.WEAPON_LEFT_CLICK, event.getHand()));
	}


	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event)
	{
		EntityPlayer player = Minecraft.getMinecraft().player;
		if(SplatCraftKeyHandler.squidKey.isPressed())
		{
			boolean isSquid = SplatCraftPlayerData.getIsSquid(player);
			SplatCraftChannelHandler.sendToServer(SplatCraftPacket.makePacket(SplatCraftPacket.Type.PLAYER_DATA, PacketPlayerData.Data.IS_SQUID, isSquid ? 0 : 1));
			SplatCraftPlayerData.setIsSquid(player, !isSquid);
		}
	}
	
}
