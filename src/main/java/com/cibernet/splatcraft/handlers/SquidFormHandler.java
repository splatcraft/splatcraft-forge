package com.cibernet.splatcraft.handlers;

import com.cibernet.splatcraft.blocks.InkwellBlock;
import com.cibernet.splatcraft.data.capabilities.playerinfo.IPlayerInfo;
import com.cibernet.splatcraft.data.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.registries.SplatcraftGameRules;
import com.cibernet.splatcraft.registries.SplatcraftSounds;
import com.cibernet.splatcraft.registries.SplatcraftStats;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.util.ColorUtils;
import com.cibernet.splatcraft.util.InkBlockUtils;
import com.cibernet.splatcraft.util.InkDamageUtils;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.Difficulty;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.LinkedHashMap;
import java.util.Map;

@Mod.EventBusSubscriber
public class SquidFormHandler
{
	private static Map<PlayerEntity, Integer> squidSubmergeMode = new LinkedHashMap<>();
	
	@SubscribeEvent
	public static void playerTick(TickEvent.PlayerTickEvent event)
	{
		PlayerEntity player = event.player;
		
		if(InkBlockUtils.onEnemyInk(player))
		{
			if(player.ticksExisted % 20 == 0 && player.getHealth() > 4 && player.world.getDifficulty() != Difficulty.PEACEFUL)
				player.attackEntityFrom(InkDamageUtils.ENEMY_INK, 2f);
			if(player.world.rand.nextFloat() < 0.7f)
				ColorUtils.addStandingInkSplashParticle(player.world, player, 1);
		}
		
		if(player.world.getGameRules().getBoolean(SplatcraftGameRules.WATER_DAMAGE) && player.isInWater() && player.ticksExisted %10 == 0)
			player.attackEntityFrom(InkDamageUtils.WATER, 8f);


		IPlayerInfo info = PlayerInfoCapability.get(player);
		if(event.phase == TickEvent.Phase.START)
		{
			player.setInvisible(shouldBeInvisible(player));

			if(!squidSubmergeMode.containsKey(player))
				squidSubmergeMode.put(player, -2);

			if(InkBlockUtils.canSquidHide(player) && info.isSquid())
			{
				squidSubmergeMode.put(player, Math.min(2,Math.max(squidSubmergeMode.get(player)+1, 1)));
				player.setInvisible(true);
			}
			else squidSubmergeMode.put(player, Math.max(-2,Math.min(squidSubmergeMode.get(player)-1, -1)));


			if(squidSubmergeMode.get(player) == 1)
			{
				player.world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SplatcraftSounds.inkSubmerge, SoundCategory.PLAYERS, 0.5F, ((player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.2F + 1.0F) * 0.95F);

				if(player.world instanceof ServerWorld)
				for(int i = 0; i < 2; i++)
					ColorUtils.addInkSplashParticle((ServerWorld) player.world, player, 1.4f);
			}
			else if(squidSubmergeMode.get(player) == -1)
				player.world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SplatcraftSounds.inkSurface, SoundCategory.PLAYERS, 0.5F, ((player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.2F + 1.0F) * 0.95F);
		}

		if(PlayerInfoCapability.isSquid(player))
		{
			player.setSprinting(false);
			player.distanceWalkedModified = player.prevDistanceWalkedModified;

			player.setPose(Pose.FALL_FLYING);
			player.stopActiveHand();
			
			player.addStat(SplatcraftStats.SQUID_TIME);
			
			if(InkBlockUtils.canSquidHide(player))
			{
				player.fallDistance = 0;
				if(player.world.getGameRules().getBoolean(SplatcraftGameRules.INK_REGEN) && player.ticksExisted % 5 == 0 && player.getActivePotionEffect(Effects.POISON) == null && player.getActivePotionEffect(Effects.WITHER) == null)
					player.heal(0.5f);

				if(player.world.rand.nextFloat() <= 0.6f && (Math.abs(player.getPosX() - player.prevPosX) > 0.14 ||Math.abs(player.getPosY() - player.prevPosY) > 0.07 || Math.abs(player.getPosZ() - player.prevPosZ) > 0.14))
					ColorUtils.addInkSplashParticle(player.world, player, 1.1f);

			}
			
			if(player.world.getBlockState(player.getPosition().down()).getBlock() instanceof InkwellBlock)
			{
				InkColorTileEntity inkwell = (InkColorTileEntity) player.world.getTileEntity(player.getPosition().down());
				
				ColorUtils.setPlayerColor(player, inkwell.getColor());
			}
		}
	}

	@SubscribeEvent
	public static void onEntitySize(EntityEvent.Size event)
	{
		if(!event.getEntity().isAddedToWorld() || !(event.getEntity() instanceof PlayerEntity) || !PlayerInfoCapability.hasCapability((LivingEntity) event.getEntity()))
			return;

		IPlayerInfo info = PlayerInfoCapability.get((LivingEntity) event.getEntity());

		if(info.isSquid())
		{
			event.setNewSize(new EntitySize(0.6f, 0.5f, false));
			event.setNewEyeHeight(InkBlockUtils.canSquidHide((LivingEntity) event.getEntity()) ? 0.3f : 0.45f);
		}
	}

	protected static boolean shouldBeInvisible(PlayerEntity playerEntity)
	{
		return playerEntity.isPotionActive(Effects.INVISIBILITY);
	}


	@SubscribeEvent
	public static void playerVisibility(LivingEvent.LivingVisibilityEvent event)
	{
		if(!(event.getEntityLiving() instanceof PlayerEntity))
			return;

		PlayerEntity player = (PlayerEntity) event.getEntityLiving();

		if(PlayerInfoCapability.hasCapability(player) && PlayerInfoCapability.get(player).isSquid() && InkBlockUtils.canSquidHide(player))
			event.modifyVisibility((Math.abs(player.getPosX() - player.prevPosX) > 0.14 ||Math.abs(player.getPosY() - player.prevPosY) > 0.07 || Math.abs(player.getPosZ() - player.prevPosZ) > 0.14) ? 0.7 : 0);
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
