package com.cibernet.splatcraft.handlers;

import com.cibernet.splatcraft.entities.renderers.RenderInklingSquid;
import com.cibernet.splatcraft.items.ItemWeaponBase;
import com.cibernet.splatcraft.network.SplatCraftChannelHandler;
import com.cibernet.splatcraft.network.SplatCraftPacket;
import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.registries.SplatCraftItems;
import com.cibernet.splatcraft.tileentities.TileEntityColor;
import com.cibernet.splatcraft.utils.SplatCraftPlayerData;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.block.BlockWeb;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class CommonEventHandler
{

	private static final AttributeModifier SPRINTING_SPEED_BOOST = (new AttributeModifier( "Sprinting speed boost", 2D, 2)).setSaved(false);


	public static final CommonEventHandler instance = new CommonEventHandler();
	
	@SubscribeEvent
	public void onTick(TickEvent.PlayerTickEvent event)
	{
		float speed = 0.1f;
		EntityPlayer player = event.player;
		BlockPos pos = new BlockPos(player.posX, player.posY, player.posZ);
		
		if(SplatCraftPlayerData.getIsSquid(player))
		{
			SplatCraftUtils.setEntitySize(player, 0.6f, 0.6f);
			
			speed = SplatCraftUtils.canSquidHide(player.world, player) ? 0.2f : 0.05f;
			
			if(player.world.getBlockState(pos.down()).getBlock().equals(SplatCraftBlocks.inkwell))
				if(player.world.getTileEntity(pos.down()) instanceof TileEntityColor)
				{
					TileEntityColor te = (TileEntityColor) player.world.getTileEntity(pos.down());
					
					if(SplatCraftPlayerData.getInkColor(player) != te.getColor())
						SplatCraftPlayerData.setInkColor(player, te.getColor());
				}
		}
		
		
		//System.out.println();
		
		
		if(player.getItemInUseCount() > 0)
		{
			ItemStack weapon = player.getActiveItemStack();

			if(weapon.getItem() instanceof ItemWeaponBase)
			{
				ItemWeaponBase item = (ItemWeaponBase) weapon.getItem();

				item.onItemTickUse(player.world, player, weapon, player.getItemInUseCount());
				speed = item.getUseWalkSpeed();

			}
		}
		
		//if(player.world.isRemote)
			player.capabilities.setPlayerWalkSpeed(speed);
	}

	@SubscribeEvent
	public void onInteract(PlayerInteractEvent event)
	{
		if(SplatCraftPlayerData.getIsSquid(event.getEntityPlayer()) && event.isCancelable())
			event.setCanceled(true);
	}
	
	@SubscribeEvent
	public void onLeftClick(PlayerInteractEvent.LeftClickEmpty event)
	{
		SplatCraftChannelHandler.sendToServer(SplatCraftPacket.makePacket(SplatCraftPacket.Type.WEAPON_LEFT_CLICK, event.getHand()));
	}
	
}
