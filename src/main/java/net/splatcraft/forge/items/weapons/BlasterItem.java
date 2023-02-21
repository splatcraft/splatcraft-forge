package net.splatcraft.forge.items.weapons;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.splatcraft.forge.entities.InkProjectileEntity;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.InkBlockUtils;
import net.splatcraft.forge.util.PlayerCooldown;
import net.splatcraft.forge.util.WeaponTooltip;

public class BlasterItem extends ShooterItem {
    @Deprecated
    public int projLifespan;
    @Deprecated
    public int startupTicks;
    @Deprecated
    public int cooldown;
    @Deprecated
    public float splashDamage;

    @Deprecated
    public BlasterItem(String name, float projectileSize, float projectileSpeed, float inaccuracy, int startupTicks, int cooldown, float damage, float splashDamage, float inkConsumption, int projectileLifespan) {
        super(name, projectileSize, projectileSpeed, inaccuracy, cooldown, damage, inkConsumption);
        this.projLifespan = projectileLifespan;
        this.startupTicks = startupTicks;
        this.cooldown = cooldown;
        this.splashDamage = splashDamage;


        addStat(new WeaponTooltip("range", (stack, level) -> (int) (projectileSpeed / projectileLifespan * 100)));
        addStat(new WeaponTooltip("impact", (stack, level) -> (int) (damage / 20 * 100)));
        addStat(new WeaponTooltip("fire_rate", (stack, level) -> (int) ((15 - cooldown * 0.5f) / 15f * 100)));
    }

    @Deprecated
    public BlasterItem(String name, BlasterItem parent) {
        this(name, parent.projectileSize, parent.projectileSpeed, parent.inaccuracy, parent.startupTicks, parent.firingSpeed, parent.damage, parent.splashDamage, parent.inkConsumption, parent.projLifespan);
    }

    @Override
    public void weaponUseTick(World level, LivingEntity entity, ItemStack stack, int timeLeft) {
        CooldownTracker cooldownTracker = ((PlayerEntity) entity).getCooldowns();
        if (!cooldownTracker.isOnCooldown(this)) {
            if (enoughInk(entity, settings.inkConsumption, timeLeft % settings.cooldown == 0)) {
                PlayerCooldown.setPlayerCooldown((PlayerEntity) entity, new PlayerCooldown(stack, settings.startupTicks, ((PlayerEntity) entity).inventory.selected, entity.getUsedItemHand(), true, false, true, entity.isOnGround()));
                if (!level.isClientSide) {
                    cooldownTracker.addCooldown(this, cooldown);
                }
            }
        }
    }

    @Override
    public void onPlayerCooldownEnd(World level, PlayerEntity player, ItemStack stack, PlayerCooldown cooldown) {
        if (reduceInk(player, settings.inkConsumption, settings.inkRecoveryCooldown, false)) {
            if (!level.isClientSide) {
                InkProjectileEntity proj = new InkProjectileEntity(level, player, stack, InkBlockUtils.getInkType(player), settings).setShooterTrail();
                proj.setBlasterStats(projLifespan, splashDamage);
                proj.shootFromRotation(player, player.xRot, player.yRot, 0.0f, settings.projectileSpeed, player.isOnGround() ? settings.groundInaccuracy : settings.airInaccuracy);
                level.addFreshEntity(proj);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SplatcraftSounds.blasterShot, SoundCategory.PLAYERS, 0.7F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
            }
        }
    }
}
