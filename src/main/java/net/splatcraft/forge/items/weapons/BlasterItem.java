package net.splatcraft.forge.items.weapons;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.splatcraft.forge.entities.InkProjectileEntity;
import net.splatcraft.forge.items.weapons.settings.WeaponSettings;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.InkBlockUtils;
import net.splatcraft.forge.util.PlayerCooldown;
import net.splatcraft.forge.util.WeaponTooltip;

public class BlasterItem extends ShooterItem
{
    public static RegistryObject<BlasterItem> createBlaster(DeferredRegister<Item> registry, WeaponSettings settings)
    {
        return registry.register(settings.name, () -> new BlasterItem(settings));
    }

    public static RegistryObject<BlasterItem> createBlaster(DeferredRegister<Item> registry, RegistryObject<BlasterItem> parent, String name)
    {
        return registry.register(name, () -> new BlasterItem(parent.get().settings));
    }

    protected BlasterItem(WeaponSettings settings) {
        super(settings);

        addStat(new WeaponTooltip("range", (stack, level) -> (int) (settings.projectileSpeed / settings.projectileLifespan * 100)));
        addStat(new WeaponTooltip("impact", (stack, level) -> (int) (settings.projectileSize / 2.0f * 100)));
        addStat(new WeaponTooltip("fire_rate", (stack, level) -> (int) ((15 - settings.firingSpeed * 0.5f) / 15f * 100)));
    }

    @Override
    public void weaponUseTick(Level level, LivingEntity entity, ItemStack stack, int timeLeft) {
        ItemCooldowns cooldownTracker = ((Player) entity).getCooldowns();
        if (!cooldownTracker.isOnCooldown(this)) {
            PlayerCooldown.setPlayerCooldown((Player) entity, new PlayerCooldown(stack, settings.startupTicks, ((Player) entity).getInventory().selected, entity.getUsedItemHand(), true, false, true, entity.isOnGround()));
            if (!level.isClientSide) {
                cooldownTracker.addCooldown(this, settings.firingSpeed);
            }
        }
    }

    @Override
    public void onPlayerCooldownEnd(Level level, Player player, ItemStack stack, PlayerCooldown cooldown) {
        if (!level.isClientSide) {
            if (reduceInk(player, this, settings.inkConsumption, settings.inkRecoveryCooldown, true)) {
                InkProjectileEntity proj = new InkProjectileEntity(level, player, stack, InkBlockUtils.getInkType(player), settings.projectileSize, settings).setShooterTrail();
                proj.setBlasterStats(settings.projectileLifespan);
                proj.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0f, settings.projectileSpeed, player.isOnGround() ? settings.groundInaccuracy : settings.airInaccuracy);
                level.addFreshEntity(proj);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SplatcraftSounds.blasterShot, SoundSource.PLAYERS, 0.7F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
            }
        }
    }
}
