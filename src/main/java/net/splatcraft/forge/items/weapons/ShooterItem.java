package net.splatcraft.forge.items.weapons;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.splatcraft.forge.entities.InkProjectileEntity;
import net.splatcraft.forge.handlers.PlayerPosingHandler;
import net.splatcraft.forge.items.weapons.settings.WeaponSettings;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.InkBlockUtils;
import net.splatcraft.forge.util.WeaponTooltip;

public class ShooterItem extends WeaponBaseItem {
    public WeaponSettings settings;

    public static RegistryObject<ShooterItem> create(DeferredRegister<Item> registry, WeaponSettings settings)
    {
        return registry.register(settings.name, () -> new ShooterItem(settings));
    }

    public static RegistryObject<ShooterItem> create(DeferredRegister<Item> registry, RegistryObject<ShooterItem> parent, String name)
    {
        return create(registry, parent, name, false);
    }

    public static RegistryObject<ShooterItem> create(DeferredRegister<Item> registry, RegistryObject<ShooterItem> parent, String name, boolean secret)
    {
        return registry.register(name, () -> new ShooterItem(parent.get().settings).setSecret(secret));
    }



    protected ShooterItem(WeaponSettings settings)
    {
        super(settings);
        this.settings = settings;

        if (!(this instanceof BlasterItem)) {
            addStat(new WeaponTooltip("range", (stack, level) -> (int) (settings.projectileSpeed / 1.2f * 100)));
            addStat(new WeaponTooltip("damage", (stack, level) -> (int) (settings.baseDamage / 20 * 100)));
            addStat(new WeaponTooltip("fire_rate", (stack, level) -> (int) ((15 - settings.firingSpeed) / 15f * 100)));
        }
    }

    @Override
    public void weaponUseTick(Level level, LivingEntity entity, ItemStack stack, int timeLeft) {
        if (!level.isClientSide && (getUseDuration(stack) - timeLeft - 1) % settings.firingSpeed == 0) {
            if (reduceInk(entity, this, settings.inkConsumption, settings.inkRecoveryCooldown, true)) {
                InkProjectileEntity proj = new InkProjectileEntity(level, entity, stack, InkBlockUtils.getInkType(entity), settings.projectileSize, settings).setShooterTrail();
                proj.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), 0.0f, settings.projectileSpeed, entity.isOnGround() ? settings.groundInaccuracy : settings.airInaccuracy);
                level.addFreshEntity(proj);
                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SplatcraftSounds.shooterShot, SoundSource.PLAYERS, 0.7F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
            }
        }
    }

    @Override
    public PlayerPosingHandler.WeaponPose getPose()
    {
        return PlayerPosingHandler.WeaponPose.FIRE;
    }
}
