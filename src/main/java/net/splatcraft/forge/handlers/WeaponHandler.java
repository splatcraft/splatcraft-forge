package net.splatcraft.forge.handlers;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.client.particles.SquidSoulParticleData;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.items.weapons.WeaponBaseItem;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.s2c.PlayerSetSquidClientPacket;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.PlayerCharge;
import net.splatcraft.forge.util.PlayerCooldown;

import java.util.LinkedHashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Splatcraft.MODID)
public class WeaponHandler {
    private static final Map<PlayerEntity, Vector3d> prevPosMap = new LinkedHashMap<>();

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity && !event.getEntityLiving().isSpectator()) {
            PlayerEntity target = (PlayerEntity) event.getEntityLiving();

            int color = ColorUtils.getPlayerColor(target);
            ((ServerWorld) target.level).sendParticles(new SquidSoulParticleData(color), target.getX(), target.getY() + 0.5f, target.getZ(), 1, 0, 0, 0, 1.5f);

            if (ScoreboardHandler.hasColorCriterion(color)) {
                target.getScoreboard().forAllObjectives(ScoreboardHandler.getDeathsAsColor(color), target.getScoreboardName(), score -> score.add(1));
            }

            if (event.getSource().getDirectEntity() instanceof PlayerEntity) {
                PlayerEntity source = (PlayerEntity) event.getSource().getEntity();
                if (ScoreboardHandler.hasColorCriterion(color) && source != null)
                    target.getScoreboard().forAllObjectives(ScoreboardHandler.getColorKills(color), source.getScoreboardName(), score -> score.add(1));
                if (ScoreboardHandler.hasColorCriterion(ColorUtils.getPlayerColor(source)))
                    target.getScoreboard().forAllObjectives(ScoreboardHandler.getKillsAsColor(ColorUtils.getPlayerColor(source)), source.getScoreboardName(), score -> score.add(1));
            }

        }
    }


    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        PlayerEntity player = event.player;
        if (PlayerCooldown.hasPlayerCooldown(player)) {
            player.inventory.selected = PlayerCooldown.getPlayerCooldown(player).getSlotIndex();
        }

        if (event.phase != TickEvent.Phase.START) {
            return;
        }

        boolean canUseWeapon = true;
        //Vector3d prevPos = PlayerInfoCapability.get(player).getPrevPos();

        if (PlayerCooldown.shrinkCooldownTime(player, 1) != null) {
            player.setSprinting(false);
            PlayerCooldown cooldown = PlayerCooldown.getPlayerCooldown(player);
            PlayerInfoCapability.get(player).setIsSquid(false);
            if (event.side.isServer())
                SplatcraftPacketHandler.sendToDim(new PlayerSetSquidClientPacket(player.getUUID(), false), player.level);
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
        } else if (PlayerCharge.canDischarge(player) || PlayerInfoCapability.isSquid(player)) {
            PlayerCharge.dischargeWeapon(player);
        }

        prevPosMap.put(player, player.position());
    }

    public static Vector3d getPlayerPrevPos(PlayerEntity player) {
        return prevPosMap.get(player);
    }
}
