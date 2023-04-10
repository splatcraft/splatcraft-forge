package net.splatcraft.forge.util;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.data.capabilities.inkoverlay.InkOverlayCapability;
import net.splatcraft.forge.data.capabilities.inkoverlay.InkOverlayInfo;
import net.splatcraft.forge.entities.IColoredEntity;
import net.splatcraft.forge.entities.SpawnShieldEntity;
import net.splatcraft.forge.entities.SquidBumperEntity;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.s2c.UpdateInkOverlayPacket;
import net.splatcraft.forge.registries.SplatcraftGameRules;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class InkDamageUtils {

    public static final DamageSource ENEMY_INK = new DamageSource(Splatcraft.MODID + ":enemyInk");
    public static final DamageSource WATER = new DamageSource(Splatcraft.MODID + ":water");
    public static final DamageSource VOID_DAMAGE = new DamageSource(Splatcraft.MODID + ":outOfStage").bypassArmor();


    public static boolean doSplatDamage(Level level, LivingEntity target, float damage, int color, Entity source, ItemStack sourceItem, boolean damageMobs) {
        return doDamage(level, target, damage, color, source, source, sourceItem, damageMobs, "splat", false);
    }

    public static boolean doRollDamage(Level level, LivingEntity target, float damage, int color, Entity source, ItemStack sourceItem, boolean damageMobs)
    {
        return doRollDamage(level, target, damage, color, source, source, sourceItem, damageMobs);
    }

    public static boolean doRollDamage(Level level, LivingEntity target, float damage, int color, Entity source, Entity directSource, ItemStack sourceItem, boolean damageMobs) {
        return doDamage(level, target, damage, color, directSource, source, sourceItem, damageMobs, "roll", true);
    }

    public static boolean canDamage(Entity target, Entity source)
    {
        return canDamage(target, ColorUtils.getEntityColor(source));
    }

    public static boolean canDamage(Entity target, int color)
    {
        boolean result = canDamageColor(target.level, target.blockPosition(), ColorUtils.getEntityColor(target), color);

        if(result)
            for(SpawnShieldEntity shield : target.level.getEntitiesOfClass(SpawnShieldEntity.class, target.getBoundingBox()))
                if(ColorUtils.colorEquals(target.level, target.blockPosition(), ColorUtils.getEntityColor(shield), ColorUtils.getEntityColor(target)))
                    return false;

        return result;
    }

    public static boolean canDamageColor(Level level, BlockPos pos, int targetColor, int sourceColor) {
        return SplatcraftGameRules.getLocalizedRule(level, pos, SplatcraftGameRules.INK_FRIENDLY_FIRE) || !ColorUtils.colorEquals(level, pos, targetColor, sourceColor);
    }

    public static boolean doDamage(Level level, LivingEntity target, float damage, int color, Entity source, Entity directSource, ItemStack sourceItem, boolean damageMobs, String name, boolean applyHurtCooldown)
    {
        InkDamageSource damageSource = new InkDamageSource(Splatcraft.MODID + ":" + name, directSource, source, sourceItem);
        if (damage <= 0 || (target.isInvulnerableTo(damageSource) && !(target instanceof SquidBumperEntity)))
            return false;

        float mobDmgPctg = SplatcraftGameRules.getIntRuleValue(level, SplatcraftGameRules.INK_MOB_DAMAGE_PERCENTAGE) * 0.01f;

        int targetColor = ColorUtils.getEntityColor(target);
        boolean doDamage = target instanceof Player || damageMobs || mobDmgPctg > 0;
        boolean canInk = canDamage(target, color);

        if (targetColor > -1) {
            doDamage = canInk;
        }

        if (target instanceof IColoredEntity) {
            target.invulnerableTime = (!applyHurtCooldown && !SplatcraftGameRules.getBooleanRuleValue(level, SplatcraftGameRules.INK_DAMAGE_COOLDOWN)) ? 1 : 20;
            doDamage = ((IColoredEntity) target).onEntityInked(damageSource, damage, color);
        } else if (target instanceof Sheep) {
            if (!((Sheep) target).isSheared()) {
                doDamage = false;
                canInk = false;
                targetColor = 1;

                InkOverlayInfo info = InkOverlayCapability.get(target);

                info.setWoolColor(color);
                if (!level.isClientSide)
                    SplatcraftPacketHandler.sendToAll(new UpdateInkOverlayPacket(target, info));
            }
        }

        if (!(target instanceof SquidBumperEntity) && doDamage) {
            Vec3 deltaMovement = target.getDeltaMovement();
            doDamage = target.hurt(damageSource, damage * (target instanceof Player || target instanceof IColoredEntity || damageMobs ? 1 : mobDmgPctg));
            target.setDeltaMovement(deltaMovement); // trying to prevent knockback... (this game is so dumb)
            target.hurtMarked = false;
        }

        if ((targetColor <= -1 || canInk) && !target.isInWater() && !(target instanceof IColoredEntity && !((IColoredEntity) target).handleInkOverlay())) {
            if (InkOverlayCapability.hasCapability(target)) {
                InkOverlayInfo info = InkOverlayCapability.get(target);

                if (info.getAmount() < target.getMaxHealth() * 1.5)
                    info.addAmount(damage * (target instanceof IColoredEntity || damageMobs ? 1 : Math.max(0.5f, mobDmgPctg)));
                info.setColor(color);
                if (!level.isClientSide)
                    SplatcraftPacketHandler.sendToAll(new UpdateInkOverlayPacket(target, info));
            }
        }

        if (!applyHurtCooldown && !SplatcraftGameRules.getBooleanRuleValue(level, SplatcraftGameRules.INK_DAMAGE_COOLDOWN))
            target.hurtTime = 1;

        return doDamage;
    }

    public static boolean isSplatted(LivingEntity target) {
        return target instanceof SquidBumperEntity ? ((SquidBumperEntity) target).getInkHealth() <= 0 : target.isDeadOrDying();
    }

    public static class InkDamageSource extends IndirectEntityDamageSource
    {
        private final ItemStack weapon;

        public InkDamageSource(String damageTypeIn, Entity source, @Nullable Entity indirectEntityIn, ItemStack weapon)
        {
            super(damageTypeIn, source, indirectEntityIn);
            this.weapon = weapon;
        }

        @Override
        public Component getLocalizedDeathMessage(LivingEntity entityLivingBaseIn)
        {
            String base = "death.attack." + this.msgId;

            if(getEntity() == null && entity == null)
            {
                return !weapon.isEmpty() ? new TranslatableComponent(base + ".item", entityLivingBaseIn.getDisplayName(), weapon.getDisplayName()) : new TranslatableComponent(base, entityLivingBaseIn.getDisplayName());
            }
            base += ".player";

            Component itextcomponent = this.getEntity() == null ? Objects.requireNonNull(this.entity).getDisplayName() : this.getEntity().getDisplayName();

            return !weapon.isEmpty() ? new TranslatableComponent(base + ".item", entityLivingBaseIn.getDisplayName(), itextcomponent, weapon.getDisplayName()) : new TranslatableComponent(base, entityLivingBaseIn.getDisplayName(), itextcomponent);
        }
    }
}
