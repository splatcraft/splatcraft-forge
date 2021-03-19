package com.cibernet.splatcraft.handlers;


import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.client.particles.InkSplashParticleData;
import com.cibernet.splatcraft.client.particles.SquidSoulParticle;
import com.cibernet.splatcraft.client.particles.SquidSoulParticleData;
import com.cibernet.splatcraft.data.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.items.weapons.WeaponBaseItem;
import com.cibernet.splatcraft.util.ColorUtils;
import com.cibernet.splatcraft.util.PlayerCharge;
import com.cibernet.splatcraft.util.PlayerCooldown;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.LinkedHashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Splatcraft.MODID)
public class WeaponHandler
{
	private static Map<PlayerEntity, Vector3d> prevPosMap = new LinkedHashMap<>();
	
	@SubscribeEvent
	public static void onLivingDeath(LivingDeathEvent event)
	{
		if(event.getEntityLiving() instanceof PlayerEntity)
		{
			PlayerEntity target = (PlayerEntity) event.getEntityLiving();

			int color = ColorUtils.getPlayerColor(target);
			((ServerWorld)target.world).spawnParticle(new SquidSoulParticleData(color), target.getPosX(), target.getPosY()+0.5f, target.getPosZ(), 1,  0, 0, 0, 1.5f);

			if(ScoreboardHandler.hasColorCriterion(color))
				target.getWorldScoreboard().forAllObjectives(ScoreboardHandler.getDeathsAsColor(color), target.getScoreboardName(), score -> score.increaseScore(1));

			if(event.getSource().getImmediateSource() instanceof PlayerEntity)
			{
				PlayerEntity source = (PlayerEntity) event.getSource().getTrueSource();
				if(ScoreboardHandler.hasColorCriterion(ColorUtils.getPlayerColor(source)))
				{
					target.getWorldScoreboard().forAllObjectives(ScoreboardHandler.getColorKills(color), source.getScoreboardName(), score -> score.increaseScore(1));
					target.getWorldScoreboard().forAllObjectives(ScoreboardHandler.getKillsAsColor(ColorUtils.getPlayerColor(source)), source.getScoreboardName(), score -> score.increaseScore(1));
				}
			}

		}
	}
	
	
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event)
	{
		PlayerEntity player = event.player;
		if(PlayerCooldown.hasPlayerCooldown(player))
			player.inventory.currentItem = PlayerCooldown.getPlayerCooldown(player).getSlotIndex();
		
		if(event.phase != TickEvent.Phase.START)
			return;
		
		boolean canUseWeapon = true;
		//Vector3d prevPos = PlayerInfoCapability.get(player).getPrevPos();
		
		if(PlayerCooldown.shrinkCooldownTime(player, 1) != null)
		{
			PlayerCooldown cooldown = PlayerCooldown.getPlayerCooldown(player);
			PlayerInfoCapability.get(player).setIsSquid(false);
			canUseWeapon = !cooldown.preventWeaponUse();
			
			if(cooldown.getTime() == 1 && player.getActiveHand() != null)
			{
				ItemStack stack = player.getHeldItem(player.getActiveHand());
				if(stack.getItem() instanceof WeaponBaseItem)
					((WeaponBaseItem) stack.getItem()).onPlayerCooldownEnd(player.world, player, stack, cooldown);
			}
			
		}
		if(canUseWeapon && player.getActiveHand() != null && player.getItemInUseCount() > 0)
		{
			ItemStack stack = player.getHeldItem(player.getActiveHand());
			if(stack.getItem() instanceof WeaponBaseItem)
			{
				((WeaponBaseItem) stack.getItem()).weaponUseTick(player.world, player, stack, player.getItemInUseCount());
				player.setSprinting(false);
			}
		}
		else if(PlayerCharge.canDischarge(player) || PlayerInfoCapability.isSquid(player))
			PlayerCharge.dischargeWeapon(player);
		
		prevPosMap.put(player, player.getPositionVec());
	}
	
	public static Vector3d getPlayerPrevPos(PlayerEntity player) { return prevPosMap.get(player);}
}
