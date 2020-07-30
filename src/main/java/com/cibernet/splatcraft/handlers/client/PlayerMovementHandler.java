package com.cibernet.splatcraft.handlers.client;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.capabilities.PlayerInfoCapability;
import com.cibernet.splatcraft.items.WeaponBaseItem;
import com.cibernet.splatcraft.util.InkBlockUtils;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class PlayerMovementHandler
{
	private static final AttributeModifier SQUID_SWIM_SPEED = (new AttributeModifier( "Squid swim speed boost", 0D, AttributeModifier.Operation.ADDITION));
	private static final AttributeModifier ENEMY_INK_SPEED = (new AttributeModifier( "Enemy ink speed penalty", -0.5D, AttributeModifier.Operation.MULTIPLY_TOTAL));
	
	@SubscribeEvent
	public static void playerMovement(PlayerSPPushOutOfBlocksEvent event)
	{
		ClientPlayerEntity player = (ClientPlayerEntity) event.getPlayer();
		MovementInput input = player.movementInput;
		ModifiableAttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
		
		if(speedAttribute.hasModifier(SQUID_SWIM_SPEED) && player.isOnGround())
			speedAttribute.removeModifier(SQUID_SWIM_SPEED);
		if(speedAttribute.hasModifier(ENEMY_INK_SPEED))
			speedAttribute.removeModifier(ENEMY_INK_SPEED);
		
		if(InkBlockUtils.onEnemyInk(player))
		{
			player.setMotion(player.getMotion().x, Math.min(player.getMotion().y, 0.05f), player.getMotion().z);
			if(!speedAttribute.hasModifier(ENEMY_INK_SPEED))
				speedAttribute.applyNonPersistentModifier(ENEMY_INK_SPEED);
		}
		
		if(PlayerInfoCapability.isSquid(player))
		{
			
			if(InkBlockUtils.canSquidSwim(player) && !speedAttribute.hasModifier(SQUID_SWIM_SPEED))
				speedAttribute.applyNonPersistentModifier(SQUID_SWIM_SPEED);
			
			float speedMod = InkBlockUtils.canSquidHide(player) ? 20f : 2f;
			
			input.moveForward *= speedMod;
			input = player.movementInput;
			input.moveStrafe *= speedMod;
			input = player.movementInput;
			
			if(InkBlockUtils.canSquidClimb(player) && !player.abilities.isFlying)
			{
				double xOff = Math.signum(player.getHorizontalFacing().getXOffset() == 0 ? player.moveStrafing : player.moveForward)*0.1 * player.getHorizontalFacing().getAxisDirection().getOffset();
				double zOff = Math.signum(player.getHorizontalFacing().getZOffset() == 0 ? player.moveStrafing : player.moveForward)*0.1 * player.getHorizontalFacing().getAxisDirection().getOffset();
				
				//if((player.isOnGround() && player.world.getCollisionShapes(player, player.getBoundingBox().offset(xOff, (double)(player.stepHeight), zOff)).toArray().length == 0) || !player.isOnGround())
				{
					if(player.getMotion().getY() < (input.jump ? 0.46f : 0.4f))
						player.moveRelative(0.055f * (input.jump ? 1.9f : 1.7f), new Vector3d(0.0f, player.moveForward, 0.0f));
					if(player.getMotion().getY() <= 0 && !player.isSneaking())
						player.moveRelative(0.035f, new Vector3d(0.0f,1f, 0.0f));
					
					if(player.isSneaking())
						player.setMotion(player.getMotion().x, Math.max(0,player.getMotion().getY()), player.getMotion().z);
				}
			}
		}
		
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
					input = player.movementInput;
					
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
		
		
		if(!player.abilities.isFlying)
		{
			if(speedAttribute.hasModifier(SQUID_SWIM_SPEED))
			{
				player.moveRelative(0.075f * (player.isOnGround() ? 1 : 0.2f), new Vector3d(player.moveStrafing, 0.0f, player.moveForward));
				//player.moveRelative(0, (float) Math.max(player.motionY, 0), 0, 0.06f);
			}
			
		}
		
	}
}
