package com.cibernet.splatcraft.handlers;

import com.cibernet.splatcraft.blocks.InkwellBlock;
import com.cibernet.splatcraft.data.capabilities.inkoverlay.InkOverlayCapability;
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
    private static final Map<PlayerEntity, Integer> squidSubmergeMode = new LinkedHashMap<>();

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event)
    {
        PlayerEntity player = event.player;

        if (InkBlockUtils.onEnemyInk(player))
        {
            if (player.tickCount % 20 == 0 && player.getHealth() > 4 && player.level.getDifficulty() != Difficulty.PEACEFUL)
                player.hurt(InkDamageUtils.ENEMY_INK, 2f);
            if (player.level.getRandom().nextFloat() < 0.7f)
                ColorUtils.addStandingInkSplashParticle(player.level, player, 1);
        }

        if (player.level.getGameRules().getBoolean(SplatcraftGameRules.WATER_DAMAGE) && player.isInWater() && player.tickCount % 10 == 0)
            player.hurt(InkDamageUtils.WATER, 8f);


        IPlayerInfo info = PlayerInfoCapability.get(player);
        if (event.phase == TickEvent.Phase.START)
        {
            player.setInvisible(shouldBeInvisible(player));

            if (!squidSubmergeMode.containsKey(player))
                squidSubmergeMode.put(player, -2);

            if (InkBlockUtils.canSquidHide(player) && info.isSquid())
            {
                squidSubmergeMode.put(player, Math.min(2, Math.max(squidSubmergeMode.get(player) + 1, 1)));
                player.setInvisible(true);
            } else squidSubmergeMode.put(player, Math.max(-2, Math.min(squidSubmergeMode.get(player) - 1, -1)));


            if (squidSubmergeMode.get(player) == 1)
            {
                player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SplatcraftSounds.inkSubmerge, SoundCategory.PLAYERS, 0.5F, ((player.level.getRandom().nextFloat() - player.level.getRandom().nextFloat()) * 0.2F + 1.0F) * 0.95F);

                if (player.level instanceof ServerWorld)
                {
                    for (int i = 0; i < 2; i++)
                        ColorUtils.addInkSplashParticle((ServerWorld) player.level, player, 1.4f);
                }
            } else if (squidSubmergeMode.get(player) == -1)
                player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SplatcraftSounds.inkSurface, SoundCategory.PLAYERS, 0.5F, ((player.level.getRandom().nextFloat() - player.level.getRandom().nextFloat()) * 0.2F + 1.0F) * 0.95F);
        }

        if (PlayerInfoCapability.isSquid(player))
        {
            player.setSprinting(player.isInWater());
            player.walkDist = player.walkDistO;

            player.setPose(Pose.SWIMMING);
            player.stopUsingItem();

            player.awardStat(SplatcraftStats.SQUID_TIME);

            if (InkBlockUtils.canSquidHide(player))
            {
                player.fallDistance = 0;
                if (player.level.getGameRules().getBoolean(SplatcraftGameRules.INK_REGEN) && player.tickCount % 5 == 0 && !player.hasEffect(Effects.POISON) && !player.hasEffect(Effects.WITHER))
                {
                    player.heal(0.5f);
                    if (InkOverlayCapability.hasCapability(player))
                    {
                        InkOverlayCapability.get(player).addAmount(-0.49f);
                    }
                }

                if (player.level.getRandom().nextFloat() <= 0.6f && (Math.abs(player.getX() - player.xo) > 0.14 || Math.abs(player.getY() - player.yo) > 0.07 || Math.abs(player.getZ() - player.zo) > 0.14))
                {
                    ColorUtils.addInkSplashParticle(player.level, player, 1.1f);
                }

            }

            if (player.level.getBlockState(player.blockPosition().below()).getBlock() instanceof InkwellBlock)
            {
                InkColorTileEntity inkwell = (InkColorTileEntity) player.level.getBlockEntity(player.blockPosition().below());

                ColorUtils.setPlayerColor(player, inkwell.getColor());
            }
        }
        if (InkOverlayCapability.hasCapability(player))
        {
            InkOverlayCapability.get(player).addAmount(-0.01f);
        }
    }

    @SubscribeEvent
    public static void onPlayerJump(LivingEvent.LivingJumpEvent event)
    {
        if (!(event.getEntityLiving() instanceof PlayerEntity) || !PlayerInfoCapability.hasCapability(event.getEntityLiving()))
        {
            return;
        }

        PlayerEntity player = (PlayerEntity) event.getEntityLiving();

        if (PlayerInfoCapability.get(player).isSquid() && InkBlockUtils.canSquidSwim(player))
        {
            player.causeFoodExhaustion(1F);
            player.setDeltaMovement(player.getDeltaMovement().x(), player.getDeltaMovement().y() * 1.1, player.getDeltaMovement().z());
        }
    }

    @SubscribeEvent
    public static void onEntitySize(EntityEvent.Size event)
    {
        if (!event.getEntity().isAddedToWorld() || !(event.getEntity() instanceof PlayerEntity) || !PlayerInfoCapability.hasCapability((LivingEntity) event.getEntity()))
            return;

        IPlayerInfo info = PlayerInfoCapability.get((LivingEntity) event.getEntity());

        if (info.isSquid())
        {
            event.setNewSize(new EntitySize(0.6f, 0.5f, false));
            event.setNewEyeHeight(InkBlockUtils.canSquidHide((LivingEntity) event.getEntity()) ? 0.3f : 0.4f);
        }
    }

    protected static boolean shouldBeInvisible(PlayerEntity playerEntity)
    {
        return playerEntity.hasEffect(Effects.INVISIBILITY);
    }


    @SubscribeEvent
    public static void playerVisibility(LivingEvent.LivingVisibilityEvent event)
    {
        if (!(event.getEntityLiving() instanceof PlayerEntity))
        {
            return;
        }

        PlayerEntity player = (PlayerEntity) event.getEntityLiving();

        if (PlayerInfoCapability.hasCapability(player) && PlayerInfoCapability.get(player).isSquid() && InkBlockUtils.canSquidHide(player))
        {
            event.modifyVisibility(Math.abs(player.getX() - player.xo) > 0.14 || Math.abs(player.getY() - player.yo) > 0.07 || Math.abs(player.getZ() - player.zo) > 0.14 ? 0.7 : 0);
        }
    }

    @SubscribeEvent
    public static void playerBreakSpeed(PlayerEvent.BreakSpeed event)
    {
        if (PlayerInfoCapability.isSquid(event.getPlayer()))
        {
            event.setNewSpeed(0);
        }
    }

    @SubscribeEvent
    public static void onPlayerAttackEntity(AttackEntityEvent event)
    {
        if (PlayerInfoCapability.isSquid(event.getPlayer()))
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent event)
    {
        if (PlayerInfoCapability.isSquid(event.getPlayer()) && event.isCancelable())
        {
            event.setCanceled(true);
        }
    }
}
