package com.cibernet.splatcraft.handlers;

import com.cibernet.splatcraft.SplatCraftConfig;
import com.cibernet.splatcraft.entities.renderers.RenderInklingSquid;
import com.cibernet.splatcraft.items.ItemDualieBase;
import com.cibernet.splatcraft.items.ItemWeaponBase;
import com.cibernet.splatcraft.network.PacketPlayerSetTransformed;
import com.cibernet.splatcraft.network.SplatCraftPacketHandler;
import com.cibernet.splatcraft.utils.ColorItemUtils;
import com.cibernet.splatcraft.world.save.SplatCraftPlayerData;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import static com.cibernet.splatcraft.handlers.SplatCraftKeyHandler.*;

public class ClientEventHandler
{
	public static ClientEventHandler instance = new ClientEventHandler();

	public static final AttributeModifier IN_USE_SPEED_BOOST = (new AttributeModifier( "Weapon use speed boost", 4D, 2)).setSaved(false);
	private static final AttributeModifier SQUID_LAND_SPEED = (new AttributeModifier( "Squid in land speed penalty", -0.4D, 2)).setSaved(false);
	private static final AttributeModifier SQUID_SWIM_SPEED = (new AttributeModifier( "Squid swim speed boost", 1.25D, 2)).setSaved(false);
	private static final AttributeModifier ENEMY_INK_SPEED = (new AttributeModifier( "Enemy ink speed penalty", -0.3D, 2)).setSaved(false);

	
	private static int sneakTime = 0;
	
	@SubscribeEvent
	public void clientTick(TickEvent.ClientTickEvent event)
	{
		EntityPlayer player = Minecraft.getMinecraft().player;

		if(player == null)
			return;

		IAttributeInstance attributeInstance = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
		ItemStack weapon = player.getActiveItemStack();
		MovementInput input = Minecraft.getMinecraft().player.movementInput;

		if(attributeInstance.hasModifier(SQUID_LAND_SPEED))
			attributeInstance.removeModifier(SQUID_LAND_SPEED);
		if(attributeInstance.hasModifier(SQUID_SWIM_SPEED) && player.onGround)
			attributeInstance.removeModifier(SQUID_SWIM_SPEED);
		if(attributeInstance.hasModifier(IN_USE_SPEED_BOOST))
			attributeInstance.removeModifier(IN_USE_SPEED_BOOST);
		if(attributeInstance.hasModifier(ENEMY_INK_SPEED))
			attributeInstance.removeModifier(ENEMY_INK_SPEED);
		
		AttributeModifier weaponMod = getWeaponMod(attributeInstance);
		if(weaponMod != null)
			attributeInstance.removeModifier(weaponMod);
		AttributeModifier noInkMod = getNoInkWeaponMod(attributeInstance);
		if(noInkMod != null)
			attributeInstance.removeModifier(noInkMod);
		
		
		if(SplatCraftUtils.onEnemyInk(player.world, player) && !attributeInstance.hasModifier(ENEMY_INK_SPEED))
			attributeInstance.applyModifier(ENEMY_INK_SPEED);
		
		boolean isSquid = SplatCraftPlayerData.getIsSquid(player);
		if(isSquid)
		{
			/* TODO super jump
			if(player.isSneaking())
			{
				sneakTime++;
				if(sneakTime >= 40)
				{
					BlockPos point = player.world.getSpawnPoint().add(0.5f, 0, 0.5f);
					
					double x = (player.posX-point.getX());
					double z = (player.posZ-point.getZ());
					
					double angle = Math.toDegrees(Math.atan(x/z));
					
					if(z < 0)
						angle += 180;
					else if(x < 0)
						angle += 360;
					
					System.out.println(point);
					
					player.setPositionAndRotation(player.posX,player.posY,player.posZ, (float) -angle+180, player.rotationPitch);
					float velocity = (float) (Math.sqrt(Math.pow(x,2)+Math.pow(z,2))/6.95f);
					player.moveRelative(0, 0, 1, velocity);
					player.motionY = 5f;
					sneakTime = 0;
				}
			} else sneakTime = 0;
			*/
			if(SplatCraftUtils.canSquidHide(player.world, player))
			{
				if(!attributeInstance.hasModifier(SQUID_SWIM_SPEED))
					attributeInstance.applyModifier(SQUID_SWIM_SPEED);
				
				if(SplatCraftUtils.canSquidClimb(player.world, player))
				{
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
		
		if(weapon.getItem() instanceof ItemWeaponBase && player.isHandActive() && player.onGround)
		{
			ItemWeaponBase weaponItem = ((ItemWeaponBase) weapon.getItem());
			AttributeModifier speedMod = weaponItem.getSpeedModifier();
			
			if(!weaponItem.hasInk(player, ColorItemUtils.getInkColor(weapon)) && weaponItem.getNoInkSpeed() != null)
				speedMod = weaponItem.getNoInkSpeed();
			
			if(!isSquid && speedMod != null && !attributeInstance.hasModifier(speedMod))
				attributeInstance.applyModifier(speedMod);
		}
	}
	
	@SubscribeEvent
	public void renderHand(RenderHandEvent event)
	{
		if(SplatCraftPlayerData.getIsSquid(Minecraft.getMinecraft().player))
			event.setCanceled(true);
	}
	
	@SubscribeEvent
	public void stopItemSlowdown(PlayerSPPushOutOfBlocksEvent event)
	{
		EntityPlayerSP player = (EntityPlayerSP) event.getEntityPlayer();
		if (player.isHandActive())
		{
			ItemStack stack = player.getActiveItemStack();
			if (!stack.isEmpty())
			{
				if (stack.getItem() instanceof ItemWeaponBase)
				{
					MovementInput input = player.movementInput;
					input.moveStrafe *= 5.0F;
					input = player.movementInput;
					input.moveForward *= 5.0F;
					
					if(stack.getItem() instanceof ItemDualieBase && player.getCooldownTracker().getCooldown(stack.getItem(), 0) > 0)
					{
						input.moveForward = 0;
						input.moveStrafe = 0;
						input.jump = false;
						
						if(Math.abs(player.motionX) <= 0.1 && Math.abs(player.motionZ) <= 0.1)
							input.sneak = true;
					}
				}
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
		int squidPhase = SplatCraftPlayerData.getPlayerData(player).isSquid;
		
		if(squidPhase == 1)
			event.getRenderer().getRenderManager().setRenderShadow(true);
		
		if(squidPhase != 0)
		{
			event.setCanceled(true);
			
			if(!SplatCraftUtils.canSquidHide(player.world, player))
			{
				RenderInklingSquid render = new RenderInklingSquid(event.getRenderer().getRenderManager());
				render.doRender(player, event.getX(), event.getY(), event.getZ(), player.rotationYawHead, event.getPartialRenderTick());
			}
			else if(squidPhase == 2) event.getRenderer().getRenderManager().setRenderShadow(false);
		}
	}
	
	private static boolean isSquidKeyHeld = false;
	
	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event)
	{
		EntityPlayer player = Minecraft.getMinecraft().player;
		
		if(Keyboard.getEventKey() == squidKey.getKeyCode())
		{
			if(Keyboard.getEventKeyState())
			{
				boolean isSquid = SplatCraftPlayerData.getIsSquid(player);
				
				if(SplatCraftConfig.holdKeyToSquid)
					isSquid = false;
				else if(isSquidKeyHeld) return;
				
				AxisAlignedBB axisalignedbb = player.getEntityBoundingBox();
				axisalignedbb = new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + 0.6, axisalignedbb.minY + (isSquid ? 1.8 : 0.6), axisalignedbb.minZ + 0.6);
				
				if(!player.world.collidesWithAnyBlock(axisalignedbb))
				{
					SplatCraftPacketHandler.instance.sendToServer(new PacketPlayerSetTransformed(player.getUniqueID(), !isSquid));
					SplatCraftPlayerData.setIsSquid(player, !isSquid);
				}
				isSquidKeyHeld = true;
			}
			else
			{
				if(!Keyboard.getEventKeyState() && SplatCraftConfig.holdKeyToSquid)
				{
					AxisAlignedBB axisalignedbb = player.getEntityBoundingBox();
					axisalignedbb = new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + 0.6, axisalignedbb.minY + (1.8), axisalignedbb.minZ + 0.6);
					
					if(!player.world.collidesWithAnyBlock(axisalignedbb))
					{
						SplatCraftPacketHandler.instance.sendToServer(new PacketPlayerSetTransformed(player.getUniqueID(), false));
						SplatCraftPlayerData.setIsSquid(player, false);
					}
				}
				isSquidKeyHeld = false;
			}
		}
	}
	
	
	
	private AttributeModifier getWeaponMod(IAttributeInstance instance)
	{
		if(instance == null)
			return null;
		for(ItemWeaponBase item : ItemWeaponBase.weapons)
		{
			if(item.getSpeedModifier() == null)
				continue;
			if(instance.hasModifier(item.getSpeedModifier()))
				return item.getSpeedModifier();
		}
		return null;
	}
	private AttributeModifier getNoInkWeaponMod(IAttributeInstance instance)
	{
		if(instance == null)
			return null;
		for(ItemWeaponBase item : ItemWeaponBase.weapons)
		{
			if(item.getNoInkSpeed() == null)
				continue;
			if(instance.hasModifier(item.getNoInkSpeed()))
				return item.getNoInkSpeed();
		}
		return null;
	}
	
}
