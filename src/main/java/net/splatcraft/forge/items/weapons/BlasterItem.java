package net.splatcraft.forge.items.weapons;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.splatcraft.forge.entities.InkProjectileEntity;
import net.splatcraft.forge.items.weapons.settings.WeaponSettings;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.InkBlockUtils;
import net.splatcraft.forge.util.PlayerCooldown;
import net.splatcraft.forge.util.WeaponTooltip;

public class BlasterItem extends ShooterItem {
    public BlasterItem(WeaponSettings settings) {
        super(settings);

        addStat(new WeaponTooltip("range", (stack, level) -> (int) (settings.projectileSpeed / settings.projectileLifespan * 100)));
        addStat(new WeaponTooltip("impact", (stack, level) -> (int) (settings.baseDamage / 20 * 100)));
        addStat(new WeaponTooltip("fire_rate", (stack, level) -> (int) ((15 - settings.firingSpeed * 0.5f) / 15f * 100)));
    }

    @Override
    public void weaponUseTick(World level, LivingEntity entity, ItemStack stack, int timeLeft) {
        CooldownTracker cooldownTracker = ((PlayerEntity) entity).getCooldowns();
        if (!cooldownTracker.isOnCooldown(this)) {
            PlayerCooldown.setPlayerCooldown((PlayerEntity) entity, new PlayerCooldown(stack, settings.startupTicks, ((PlayerEntity) entity).inventory.selected, entity.getUsedItemHand(), true, false, true, entity.isOnGround()));
            if (!level.isClientSide) {
                cooldownTracker.addCooldown(this, settings.firingSpeed);
            }
        }
    }

    @Override
    public void onPlayerCooldownEnd(World level, PlayerEntity player, ItemStack stack, PlayerCooldown cooldown) {
        if (reduceInk(player, settings.inkConsumption, settings.inkRecoveryCooldown, false)) {
            if (!level.isClientSide) {
                InkProjectileEntity proj = new InkProjectileEntity(level, player, stack, InkBlockUtils.getInkType(player), settings.projectileSize, settings).setShooterTrail();
                proj.setBlasterStats(settings.projectileLifespan, settings.minDamage);
                proj.shootFromRotation(player, player.xRot, player.yRot, 0.0f, settings.projectileSpeed, player.isOnGround() ? settings.groundInaccuracy : settings.airInaccuracy);
                level.addFreshEntity(proj);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SplatcraftSounds.blasterShot, SoundCategory.PLAYERS, 0.7F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
            }
        }
    }
}
