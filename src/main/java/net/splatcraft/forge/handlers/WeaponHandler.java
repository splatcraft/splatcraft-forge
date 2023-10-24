package net.splatcraft.forge.handlers;


import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.client.particles.SquidSoulParticleData;
import net.splatcraft.forge.commands.SuperJumpCommand;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.items.weapons.WeaponBaseItem;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.s2c.PlayerSetSquidClientPacket;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.PlayerCharge;
import net.splatcraft.forge.util.PlayerCooldown;

@Mod.EventBusSubscriber(modid = Splatcraft.MODID)
public class WeaponHandler {
	private static final Map<Player, Vec3> prevPosMap = new LinkedHashMap<>();

	@SubscribeEvent
	public static void onLivingDeath(LivingDeathEvent event) {
		if (event.getEntityLiving() instanceof Player target && !event.getEntityLiving().isSpectator()) {

			int color = ColorUtils.getPlayerColor(target);
			((ServerLevel) target.level).sendParticles(new SquidSoulParticleData(color), target.getX(), target.getY() + 0.5f, target.getZ(), 1, 0, 0, 0, 1.5f);

			if (ScoreboardHandler.hasColorCriterion(color)) {
				target.getScoreboard().forAllObjectives(ScoreboardHandler.getDeathsAsColor(color), target.getScoreboardName(), score -> score.add(1));
			}

			if (event.getSource().getDirectEntity() instanceof Player source) {
				if (ScoreboardHandler.hasColorCriterion(color) && source != null)
					target.getScoreboard().forAllObjectives(ScoreboardHandler.getColorKills(color), source.getScoreboardName(), score -> score.add(1));
				if (ScoreboardHandler.hasColorCriterion(ColorUtils.getPlayerColor(source)))
					target.getScoreboard().forAllObjectives(ScoreboardHandler.getKillsAsColor(ColorUtils.getPlayerColor(source)), source.getScoreboardName(), score -> score.add(1));
			}

		}
	}


	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		Player player = event.player;
		if (PlayerCooldown.hasPlayerCooldown(player)) {
			player.getInventory().selected = PlayerCooldown.getPlayerCooldown(player).getSlotIndex();
		}

		if (event.phase != TickEvent.Phase.START) {
			return;
		}

		boolean canUseWeapon = true;
		//Vec3 prevPos = PlayerInfoCapability.get(player).getPrevPos();

        if (PlayerCooldown.shrinkCooldownTime(player, 1) != null) {
            player.setSprinting(false);
            PlayerCooldown cooldown = PlayerCooldown.getPlayerCooldown(player);
			if (!(cooldown instanceof SuperJumpCommand.SuperJump) && PlayerInfoCapability.isSquid(player)) {
				PlayerInfoCapability.get(player).setIsSquid(false);
				if (event.side.isServer())
					SplatcraftPacketHandler.sendToTrackers(new PlayerSetSquidClientPacket(player.getUUID(), false), player);
			}

			canUseWeapon = !cooldown.preventWeaponUse();

			if (cooldown.getTime() == 1) {
				ItemStack stack = cooldown.storedStack;
				if (stack.getItem() instanceof WeaponBaseItem) {
					((WeaponBaseItem) stack.getItem()).onPlayerCooldownEnd(player.level, player, stack, cooldown);
				}
			}
		}
		if (canUseWeapon && player.getUseItemRemainingTicks() > 0) {
			ItemStack stack = player.getItemInHand(player.getUsedItemHand());
			if (stack.getItem() instanceof WeaponBaseItem) {
				((WeaponBaseItem) stack.getItem()).weaponUseTick(player.level, player, stack, player.getUseItemRemainingTicks());
				player.setSprinting(false);
			}
		} else {
			PlayerCharge.dischargeWeapon(player);
		}

		prevPosMap.put(player, player.position());
	}

	public static Vec3 getPlayerPrevPos(Player player) {
		return prevPosMap.get(player);
	}
}
