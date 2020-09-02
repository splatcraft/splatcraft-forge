package com.cibernet.splatcraft.handlers;

import com.cibernet.splatcraft.blocks.InkwellBlock;
import com.cibernet.splatcraft.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.registries.SplatcraftGameRules;
import com.cibernet.splatcraft.registries.SplatcraftStats;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.util.ColorUtils;
import com.cibernet.splatcraft.util.InkBlockUtils;
import com.cibernet.splatcraft.util.InkDamageUtils;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Difficulty;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class SquidFormHandler
{
	@SubscribeEvent
	public static void playerTick(TickEvent.PlayerTickEvent event)
	{
		PlayerEntity player = event.player;
		
		if(InkBlockUtils.onEnemyInk(player) && player.ticksExisted % 20 == 0 && player.getHealth() > 4 && player.world.getDifficulty() != Difficulty.PEACEFUL)
			player.attackEntityFrom(InkDamageUtils.ENEMY_INK, 2f);
		
		if(player.world.getGameRules().getBoolean(SplatcraftGameRules.WATER_DAMAGE) && player.isInWater() && player.ticksExisted %10 == 0)
			player.attackEntityFrom(InkDamageUtils.WATER, 8f);
		
		if(PlayerInfoCapability.isSquid(player))
		{
			player.setPose(Pose.FALL_FLYING);
			player.stopActiveHand();
			
			player.addStat(SplatcraftStats.SQUID_TIME);
			
			if(InkBlockUtils.canSquidSwim(player))
			{
				player.fallDistance = 0;
				if(player.ticksExisted % 5 == 0 && player.getActivePotionEffect(Effects.POISON) == null && player.getActivePotionEffect(Effects.WITHER) == null)
					player.heal(0.5f);
			}
			
			if(player.world.getBlockState(player.getPosition().down()).getBlock() instanceof InkwellBlock)
			{
				InkColorTileEntity inkwell = (InkColorTileEntity) player.world.getTileEntity(player.getPosition().down());
				
				ColorUtils.setPlayerColor(player, inkwell.getColor());
			}
		}
	}
	
	
	
	
	@SubscribeEvent
	public static void playerVisibility(PlayerEvent.Visibility event)
	{
		if(PlayerInfoCapability.get(event.getPlayer()).isSquid() && InkBlockUtils.canSquidHide(event.getPlayer()))
			event.modifyVisibility(0);
	}
	
	@SubscribeEvent
	public static void playerBreakSpeed(PlayerEvent.BreakSpeed event)
	{
		if(PlayerInfoCapability.isSquid(event.getPlayer()))
			event.setNewSpeed(0);
	}
	
	@SubscribeEvent
	public static void onPlayerAttackEntity(AttackEntityEvent event)
	{
		if(PlayerInfoCapability.isSquid(event.getPlayer()))
			event.setCanceled(true);
	}
	
	@SubscribeEvent
	public static void onPlayerInteract(PlayerInteractEvent event)
	{
		if(PlayerInfoCapability.isSquid(event.getPlayer()) && event.isCancelable())
			event.setCanceled(true);
	}
}
