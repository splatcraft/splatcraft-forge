package net.splatcraft.forge.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.data.capabilities.inkoverlay.IInkOverlayInfo;
import net.splatcraft.forge.data.capabilities.inkoverlay.InkOverlayCapability;
import net.splatcraft.forge.entities.IColoredEntity;
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


    public static boolean doSplatDamage(World level, LivingEntity target, float damage, int color, Entity source, ItemStack sourceItem, boolean damageMobs) {
        return doDamage(level, target, damage, color, source, source, sourceItem, damageMobs, "splat", false);
    }

    public static boolean doRollDamage(World level, LivingEntity target, float damage, int color, Entity source, ItemStack sourceItem, boolean damageMobs)
    {
        return doRollDamage(level, target, damage, color, source, source, sourceItem, damageMobs);
    }

    public static boolean doRollDamage(World level, LivingEntity target, float damage, int color, Entity source, Entity directSource, ItemStack sourceItem, boolean damageMobs) {
        return doDamage(level, target, damage, color, directSource, source, sourceItem, damageMobs, "roll", true);
    }

    public static boolean canDamage(Entity target, Entity source) {
        return canDamageColor(source.level, ColorUtils.getEntityColor(target), ColorUtils.getEntityColor(source));
    }

    public static boolean canDamageColor(World level, int targetColor, int sourceColor) {
        return SplatcraftGameRules.getBooleanRuleValue(level, SplatcraftGameRules.INK_FRIENDLY_FIRE) || !ColorUtils.colorEquals(level, targetColor, sourceColor);
    }

    public static boolean doDamage(World level, LivingEntity target, float damage, int color, Entity source, Entity directSource, ItemStack sourceItem, boolean damageMobs, String name, boolean applyHurtCooldown)
    {
        InkDamageSource damageSource = new InkDamageSource(Splatcraft.MODID + ":" + name, directSource, source, sourceItem);
        if (damage <= 0 || (target.isInvulnerableTo(damageSource) && !(target instanceof SquidBumperEntity)))
            return false;

        float mobDmgPctg = SplatcraftGameRules.getIntRuleValue(level, SplatcraftGameRules.INK_MOB_DAMAGE_PERCENTAGE) * 0.01f;

        int targetColor = ColorUtils.getEntityColor(target);
        boolean doDamage = target instanceof PlayerEntity || damageMobs || mobDmgPctg > 0;
        boolean canInk = canDamageColor(level, color, targetColor);

        if (targetColor > -1) {
            doDamage = canInk;
        }

        if (target instanceof IColoredEntity) {
            target.invulnerableTime = (!applyHurtCooldown && !SplatcraftGameRules.getBooleanRuleValue(level, SplatcraftGameRules.INK_DAMAGE_COOLDOWN)) ? 1 : 20;
            doDamage = ((IColoredEntity) target).onEntityInked(damageSource, damage, color);
        } else if (target instanceof SheepEntity) {
            if (!((SheepEntity) target).isSheared()) {
                doDamage = false;
                canInk = false;
                targetColor = 1;

                IInkOverlayInfo info = InkOverlayCapability.get(target);

                info.setWoolColor(color);
                if (!level.isClientSide)
                    SplatcraftPacketHandler.sendToAll(new UpdateInkOverlayPacket(target, info));
            }
        }

        if (!(target instanceof SquidBumperEntity) && doDamage) {
            doDamage = target.hurt(damageSource, damage * (target instanceof PlayerEntity || target instanceof IColoredEntity || damageMobs ? 1 : mobDmgPctg));
        }

        if ((targetColor <= -1 || canInk) && !target.isInWater()) {
            if (InkOverlayCapability.hasCapability(target)) {
                IInkOverlayInfo info = InkOverlayCapability.get(target);

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
        public ITextComponent getLocalizedDeathMessage(LivingEntity entityLivingBaseIn)
        {
            String base = "death.attack." + this.msgId;

            if(getEntity() == null && entity == null)
            {
                return !weapon.isEmpty() ? new TranslationTextComponent(base + ".item", entityLivingBaseIn.getDisplayName(), weapon.getDisplayName()) : new TranslationTextComponent(base, entityLivingBaseIn.getDisplayName());
            }
            base += ".player";

            ITextComponent itextcomponent = this.getEntity() == null ? Objects.requireNonNull(this.entity).getDisplayName() : this.getEntity().getDisplayName();

            return !weapon.isEmpty() ? new TranslationTextComponent(base + ".item", entityLivingBaseIn.getDisplayName(), itextcomponent, weapon.getDisplayName()) : new TranslationTextComponent(base, entityLivingBaseIn.getDisplayName(), itextcomponent);
        }
    }
}
