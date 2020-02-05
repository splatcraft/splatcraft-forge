package com.cibernet.splatcraft.handlers;

import com.cibernet.splatcraft.entities.renderers.RenderInklingSquid;
import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class CommonEventHandler
{
	public static final CommonEventHandler instance = new CommonEventHandler();
	
	@SubscribeEvent
	public void onTick(TickEvent.PlayerTickEvent event)
	{
		EntityPlayer player = event.player;
		if(SplatCraftKeyHandler.squidKey.isKeyDown())
		{
			SplatCraftUtils.setEntitySize(player, 0.6f, 0.6f);
			
			float f = event.player.rotationYaw * 0.017453292F;
			
			float speed = player.getEntityWorld().getBlockState(new BlockPos(player.posX, player.posY-1,player.posZ)).getBlock().equals(SplatCraftBlocks.inkedBlock) ? 2f : 0.75f;
			
			player.travel(player.moveStrafing, 0, player.moveForward * speed);
		}
	}
	
	
	
}
