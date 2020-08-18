package com.cibernet.splatcraft.handlers;


import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.items.WeaponBaseItem;
import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.LinkedHashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Splatcraft.MODID)
public class WeaponHandler
{
	private static Map<PlayerEntity, Vector3d> prevPosMap = new LinkedHashMap<>();
	
	@SubscribeEvent
	public static void onLivingDamage(LivingDamageEvent event)
	{
		if(event.getEntityLiving() instanceof PlayerEntity)
		{
			PlayerEntity target = (PlayerEntity) event.getEntityLiving();
			
			if(target.getHealth() > 0 && target.getHealth() - event.getAmount() <= 0)
			{
				target.getWorldScoreboard().forAllObjectives(ScoreboardHandler.getDeathsAsColor(ColorUtils.getPlayerColor(target)), target.getScoreboardName(), score -> score.increaseScore(1));
				
				if(event.getSource().getImmediateSource() instanceof PlayerEntity)
				{
					PlayerEntity source = (PlayerEntity) event.getSource().getTrueSource();
					target.getWorldScoreboard().forAllObjectives(ScoreboardHandler.getColorKills(ColorUtils.getPlayerColor(target)), source.getScoreboardName(), score -> score.increaseScore(1));
					target.getWorldScoreboard().forAllObjectives(ScoreboardHandler.getKillsAsColor(ColorUtils.getPlayerColor(source)), source.getScoreboardName(), score -> score.increaseScore(1));
				}
			}
		
		
		
		}
	}
	
	
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event)
	{
		if(event.phase == TickEvent.Phase.END)
			return;
		
		PlayerEntity player = event.player;
		//Vector3d prevPos = PlayerInfoCapability.get(player).getPrevPos();
		
		if(player.getActiveHand() != null && player.getItemInUseCount() > 0)
		{
			ItemStack stack = player.getHeldItem(player.getActiveHand());
			if(stack.getItem() instanceof WeaponBaseItem)
			{
				((WeaponBaseItem) stack.getItem()).weaponUseTick(player.world, player, stack, player.getItemInUseCount());
			}
		}
		
		prevPosMap.put(player, player.getPositionVec());
	}
	
	public static Vector3d getPlayerPrevPos(PlayerEntity player) { return prevPosMap.get(player);}
}
