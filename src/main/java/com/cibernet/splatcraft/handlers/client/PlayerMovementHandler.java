package com.cibernet.splatcraft.handlers.client;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.items.WeaponBaseItem;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovementInput;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = Splatcraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PlayerMovementHandler
{
	
	@SubscribeEvent
	public void playerMovement(PlayerSPPushOutOfBlocksEvent event)
	{
		ClientPlayerEntity player = (ClientPlayerEntity) event.getPlayer();
		MovementInput input = player.movementInput;
		//IAttributeInstance attributeInstance = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
		
		if (player.isHandActive())
		{
			ItemStack stack = player.getActiveItemStack();
			if (!stack.isEmpty())
			{
				if (stack.getItem() instanceof WeaponBaseItem)
				{
					input.moveStrafe *= 5.0F;
					input = player.movementInput;
					input.moveForward *= 5.0F;
					
					/*
					if(stack.getItem() instanceof ItemDualieBase && player.getCooldownTracker().getCooldown(stack.getItem(), 0) > 0)
					{
						input.moveForward = 0;
						input.moveStrafe = 0;
						input.jump = false;
						player.capabilities.isFlying = false;
						
						if(Math.abs(player.motionX) <= 0.1 && Math.abs(player.motionZ) <= 0.1)
							input.sneak = true;
					}
					*/
				}
			}
		}
		
		/*
		if(!player.abilities.isFlying)
		{
			if(attributeInstance.hasModifier(SQUID_SWIM_SPEED))
			{
				player.moveRelative(player.moveStrafing, 0.0f, player.moveForward, 0.075f * (player.onGround ? 1 : 0.2f));
				//player.moveRelative(0, (float) Math.max(player.motionY, 0), 0, 0.06f);
			}
			
			else if(attributeInstance.hasModifier(SQUID_LAND_SPEED))
			{
				input.moveForward *= 0.5f;
				input.moveStrafe *= 0.5f;
			}
		}
		*/
	}
}
