package net.splatcraft.forge.items.weapons;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.splatcraft.forge.entities.InkProjectileEntity;
import net.splatcraft.forge.handlers.PlayerPosingHandler;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.InkBlockUtils;
import net.splatcraft.forge.util.WeaponSettings;
import net.splatcraft.forge.util.WeaponTooltip;

public class ShooterItem extends WeaponBaseItem {
    @Deprecated
    public float projectileSize;
    @Deprecated
    public float projectileSpeed;
    @Deprecated
    public float inaccuracy;
    @Deprecated
    public int firingSpeed;
    @Deprecated
    public float damage;
    @Deprecated
    public float inkConsumption;

    public WeaponSettings settings;

    public ShooterItem(WeaponSettings settings) {
        super();
        setRegistryName(settings.name);
        this.settings = settings;
    }

    @Deprecated
    public ShooterItem(String name, float projectileSize, float projectileSpeed, float inaccuracy, int firingSpeed, float damage, float inkConsumption) {
        super();
        setRegistryName(name);

        this.projectileSize = projectileSize;
        this.projectileSpeed = projectileSpeed;
        this.inaccuracy = inaccuracy;
        this.firingSpeed = firingSpeed;
        this.damage = damage;
        this.inkConsumption = inkConsumption;

        if (!(this instanceof BlasterItem)) {
            addStat(new WeaponTooltip("range", (stack, level) -> (int) (projectileSpeed / 1.2f * 100)));
            addStat(new WeaponTooltip("damage", (stack, level) -> (int) (damage / 20 * 100)));
            addStat(new WeaponTooltip("fire_rate", (stack, level) -> (int) ((15 - firingSpeed) / 15f * 100)));
        }
    }

    @Deprecated
    public ShooterItem(String name, ShooterItem parent) {
        this(name, parent.projectileSize, parent.projectileSpeed, parent.inaccuracy, parent.firingSpeed, parent.damage, parent.inkConsumption);
    }

    @Override
    public void weaponUseTick(World level, LivingEntity entity, ItemStack stack, int timeLeft) {
        if (!level.isClientSide && (getUseDuration(stack) - timeLeft - 1) % settings.firingSpeed == 0) {
            if (reduceInk(entity, settings.inkConsumption, settings.inkRecoveryCooldown, true)) {
                InkProjectileEntity proj = new InkProjectileEntity(level, entity, stack, InkBlockUtils.getInkType(entity), settings).setShooterTrail();
                proj.shootFromRotation(entity, entity.xRot, entity.yRot, 0.0f, settings.projectileSpeed, entity.isOnGround() ? settings.groundInaccuracy : settings.airInaccuracy);
                level.addFreshEntity(proj);
                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SplatcraftSounds.shooterShot, SoundCategory.PLAYERS, 0.7F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
            }
        }
    }

    @Override
    public PlayerPosingHandler.WeaponPose getPose()
    {
        return PlayerPosingHandler.WeaponPose.FIRE;
    }
}
