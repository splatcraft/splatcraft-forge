package com.cibernet.splatcraft.handlers.client;

import com.cibernet.splatcraft.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.items.weapons.WeaponBaseItem;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import com.cibernet.splatcraft.util.InkBlockUtils;
import com.cibernet.splatcraft.util.PlayerCooldown;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class PlayerMovementHandler
{
	private static final AttributeModifier INK_SWIM_SPEED = (new AttributeModifier( "Ink swimming speed boost", 0D, AttributeModifier.Operation.ADDITION));
	private static final AttributeModifier SQUID_SWIM_SPEED = (new AttributeModifier( "Squid swim speed boost", 0.3D, AttributeModifier.Operation.MULTIPLY_TOTAL));
	private static final AttributeModifier ENEMY_INK_SPEED = (new AttributeModifier( "Enemy ink speed penalty", -0.5D, AttributeModifier.Operation.MULTIPLY_TOTAL));

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void playerMovement(TickEvent.PlayerTickEvent event)
	{
		if(!(event.player instanceof ClientPlayerEntity) || event.phase != TickEvent.Phase.END)
			return;

		ClientPlayerEntity player = (ClientPlayerEntity) event.player;
		//MovementInput input = player.movementInput;
		ModifiableAttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
		ModifiableAttributeInstance swimAttribute = player.getAttribute(ForgeMod.SWIM_SPEED.get());
		
		if(speedAttribute.hasModifier(INK_SWIM_SPEED) && player.isOnGround())
			speedAttribute.removeModifier(INK_SWIM_SPEED);
		if(speedAttribute.hasModifier(ENEMY_INK_SPEED))
			speedAttribute.removeModifier(ENEMY_INK_SPEED);
		if(swimAttribute.hasModifier(SQUID_SWIM_SPEED))
			swimAttribute.removeModifier(SQUID_SWIM_SPEED);
		
		if(InkBlockUtils.onEnemyInk(player))
		{
			//player.setMotion(player.getMotion().x, Math.min(player.getMotion().y, 0.05f), player.getMotion().z);
			if(!speedAttribute.hasModifier(ENEMY_INK_SPEED))
				speedAttribute.applyNonPersistentModifier(ENEMY_INK_SPEED);
		}
		
		if(PlayerInfoCapability.isSquid(player))
		{
			
			if(InkBlockUtils.canSquidSwim(player) && !speedAttribute.hasModifier(INK_SWIM_SPEED))
				speedAttribute.applyNonPersistentModifier(INK_SWIM_SPEED);
			if(!swimAttribute.hasModifier(SQUID_SWIM_SPEED))
				swimAttribute.applyNonPersistentModifier(SQUID_SWIM_SPEED);
		}
		
		if(PlayerCooldown.hasPlayerCooldown(player))
		{
			PlayerCooldown cooldown = PlayerCooldown.getPlayerCooldown(player);
			player.inventory.currentItem = cooldown.getSlotIndex();
		}
		
		if(!player.abilities.isFlying)
		{
			if(speedAttribute.hasModifier(INK_SWIM_SPEED))
			{
				player.moveRelative(((float)player.getAttributeValue(SplatcraftItems.INK_SWIM_SPEED)) * (player.isOnGround() ? 1 : 0.2f), new Vector3d(player.moveStrafing, 0.0f, player.moveForward));
				//player.moveRelative(0, (float) Math.max(player.motionY, 0), 0, 0.06f);
			}
			
		}
		
	}

	@SubscribeEvent
	public static void onInputUpdate(InputUpdateEvent event)
	{

		MovementInput input = event.getMovementInput();
		PlayerEntity player = event.getPlayer();

		float speedMod = !input.sneaking ? (InkBlockUtils.canSquidHide(player) ? 35f : 2f) : 1f;

		input.moveForward *= speedMod;
		//input = player.movementInput;
		input.moveStrafe *= speedMod;
		//input = player.movementInput;

		if(PlayerInfoCapability.isSquid(player) && InkBlockUtils.canSquidClimb(player) && !player.abilities.isFlying)
		{
			double xOff = Math.signum(player.getHorizontalFacing().getXOffset() == 0 ? player.moveStrafing : player.moveForward)*0.1 * player.getHorizontalFacing().getAxisDirection().getOffset();
			double zOff = Math.signum(player.getHorizontalFacing().getZOffset() == 0 ? player.moveStrafing : player.moveForward)*0.1 * player.getHorizontalFacing().getAxisDirection().getOffset();

			//if((player.isOnGround() && player.world.getCollisionShapes(player, player.getBoundingBox().offset(xOff, (double)(player.stepHeight), zOff)).toArray().length == 0) || !player.isOnGround())
			{
				if(player.getMotion().getY() < (input.jump ? 0.46f : 0.4f))
					player.moveRelative(0.055f * (input.jump ? 1.9f : 1.7f), new Vector3d(0.0f, player.moveForward, 0.0f));
				if(player.getMotion().getY() <= 0 && !input.sneaking)
					player.moveRelative(0.035f, new Vector3d(0.0f,1f, 0.0f));

				if(input.sneaking)
					player.setMotion(player.getMotion().x, Math.max(0,player.getMotion().getY()), player.getMotion().z);
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
					//input = player.movementInput;
					input.moveForward *= 5.0F;
					//input = player.movementInput;
				}
			}
		}

		if(PlayerCooldown.hasPlayerCooldown(player))
		{
			PlayerCooldown cooldown = PlayerCooldown.getPlayerCooldown(player);
			if (!cooldown.canMove()) {
				input.moveForward = 0;
				input.moveStrafe = 0;
				input.jump = false;
			}
			if (cooldown.forceCrouch() && cooldown.getTime() > 1)
				input.sneaking = !player.abilities.isFlying;

		}

	}
}
