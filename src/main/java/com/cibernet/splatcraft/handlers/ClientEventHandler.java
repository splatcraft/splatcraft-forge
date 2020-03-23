package com.cibernet.splatcraft.handlers;

import com.cibernet.splatcraft.entities.renderers.RenderInklingSquid;
import com.cibernet.splatcraft.items.ItemWeaponBase;
import com.cibernet.splatcraft.network.PacketGetPlayerData;
import com.cibernet.splatcraft.network.PacketPlayerSetTransformed;
import com.cibernet.splatcraft.network.PacketWeaponLeftClick;
import com.cibernet.splatcraft.network.SplatCraftPacketHandler;
import com.cibernet.splatcraft.utils.SplatCraftPlayerData;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class ClientEventHandler
{
	public static ClientEventHandler instance = new ClientEventHandler();

	public static final AttributeModifier IN_USE_SPEED_BOOST = (new AttributeModifier( "Weapon use speed boost", 4D, 2)).setSaved(false);
	private static final AttributeModifier SQUID_LAND_SPEED = (new AttributeModifier( "Squid in land speed penalty", -0.4D, 2)).setSaved(false);
	private static final AttributeModifier SQUID_SWIM_SPEED = (new AttributeModifier( "Squid swim speed boost", 1.25D, 2)).setSaved(false);
	private static final AttributeModifier ENEMY_INK_SPEED = (new AttributeModifier( "Enemy ink speed penalty", -0.3D, 2)).setSaved(false);


	@SubscribeEvent
	public void clientTick(TickEvent.ClientTickEvent event)
	{
		EntityPlayer player = Minecraft.getMinecraft().player;

		if(player == null)
			return;

		IAttributeInstance attributeInstance = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
		ItemStack weapon = player.getActiveItemStack();

		if(attributeInstance.hasModifier(SQUID_LAND_SPEED))
			attributeInstance.removeModifier(SQUID_LAND_SPEED);
		if(attributeInstance.hasModifier(SQUID_SWIM_SPEED))
			attributeInstance.removeModifier(SQUID_SWIM_SPEED);
		if(attributeInstance.hasModifier(IN_USE_SPEED_BOOST))
			attributeInstance.removeModifier(IN_USE_SPEED_BOOST);
		if(attributeInstance.hasModifier(ENEMY_INK_SPEED))
			attributeInstance.removeModifier(ENEMY_INK_SPEED);

		AttributeModifier weaponMod = getWeaponMod(attributeInstance);
		if(weaponMod != null)
			attributeInstance.removeModifier(weaponMod);
		
		
		if(SplatCraftUtils.onEnemyInk(player.world, player) && !attributeInstance.hasModifier(ENEMY_INK_SPEED))
			attributeInstance.applyModifier(ENEMY_INK_SPEED);
		
		boolean isSquid = SplatCraftPlayerData.getIsSquid(player);
		if(isSquid)
		{
			if(SplatCraftUtils.canSquidHide(player.world, player))
			{
				if(!attributeInstance.hasModifier(SQUID_SWIM_SPEED))
					attributeInstance.applyModifier(SQUID_SWIM_SPEED);
				
				if(SplatCraftUtils.canSquidClimb(player.world, player))
				{
					MovementInput input = Minecraft.getMinecraft().player.movementInput;
					double xOff = Math.signum(player.getHorizontalFacing().getFrontOffsetX() == 0 ? player.moveStrafing : player.moveForward)*0.1 * player.getHorizontalFacing().getAxisDirection().getOffset();
					double zOff = Math.signum(player.getHorizontalFacing().getFrontOffsetZ() == 0 ? player.moveStrafing : player.moveForward)*0.1 * player.getHorizontalFacing().getAxisDirection().getOffset();
					
					if((player.onGround && !player.world.getCollisionBoxes(player, player.getEntityBoundingBox().offset(xOff, (double)(player.stepHeight), zOff)).isEmpty()) || !player.onGround)
					{
						player.moveRelative(0.0f,player.moveForward, 0.0f, 0.055f * (input.jump ? 1.2f : 1f));
						if(player.motionY <= 0 && !player.isSneaking())
							player.moveRelative(0.0f,1f, 0.0f, 0.035f);
						
						if(player.isSneaking())
							player.motionY = Math.max(0,player.motionY);
					}
				}
			}
			else if(!attributeInstance.hasModifier(SQUID_LAND_SPEED))
				attributeInstance.applyModifier(SQUID_LAND_SPEED);

		}
		if(weapon.getItem() instanceof ItemWeaponBase)
		{
			AttributeModifier speedMod = ((ItemWeaponBase) weapon.getItem()).getSpeedModifier();

			if(player.getItemInUseCount() > 0)
			{
				player.moveRelative(player.moveStrafing, 0.0f, player.moveForward, 0.2f);
				if(!isSquid && speedMod != null && !attributeInstance.hasModifier(speedMod))
					attributeInstance.applyModifier(speedMod);
			}
		}
	}

	@SubscribeEvent
	public void updateFOV(FOVUpdateEvent event)
	{
		float fov = event.getNewfov();

		if(event.getEntity().getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).hasModifier(IN_USE_SPEED_BOOST))
			fov -= 2f;

		event.setNewfov(fov);
	}

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
		EntityPlayer player = event.getEntityPlayer();
		if(!player.isSpectator() && !SplatCraftPlayerData.getIsSquid(event.getEntityPlayer()))
			SplatCraftPacketHandler.instance.sendToServer(new PacketWeaponLeftClick(player.getUniqueID()));
	}

	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event)
	{
		EntityPlayer player = Minecraft.getMinecraft().player;
		
		if(SplatCraftKeyHandler.squidKey.isPressed())
		{
			boolean isSquid = SplatCraftPlayerData.getIsSquid(player);
			
			AxisAlignedBB axisalignedbb = player.getEntityBoundingBox();
			axisalignedbb = new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + 0.6, axisalignedbb.minY + (isSquid ? 1.8 : 0.6), axisalignedbb.minZ + 0.6);
			
			if (!player.world.collidesWithAnyBlock(axisalignedbb))
				SplatCraftPacketHandler.instance.sendToServer(new PacketPlayerSetTransformed(player.getUniqueID(), !isSquid));
		}
	}

	private AttributeModifier getWeaponMod(IAttributeInstance instance)
	{
		for(ItemWeaponBase item : ItemWeaponBase.weapons)
		{
			if(item.getSpeedModifier() == null)
				continue;
			if(instance.hasModifier(item.getSpeedModifier()))
				return item.getSpeedModifier();
		}
		return null;
	}
	
}
