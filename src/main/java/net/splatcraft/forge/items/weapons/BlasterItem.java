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
import net.splatcraft.forge.handlers.PlayerPosingHandler;
import net.splatcraft.forge.items.weapons.settings.BlasterWeaponSettings;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.InkBlockUtils;
import net.splatcraft.forge.util.PlayerCooldown;

public class BlasterItem extends WeaponBaseItem<BlasterWeaponSettings>
{
    public static RegistryObject<BlasterItem> createBlaster(DeferredRegister<Item> registry, String settings, String name)
    {
        return registry.register(name, () -> new BlasterItem(settings));
    }

    public static RegistryObject<BlasterItem> createBlaster(DeferredRegister<Item> registry, RegistryObject<BlasterItem> parent, String name)
    {
        return registry.register(name, () -> new BlasterItem(parent.get().settingsId.toString()));
    }

    protected BlasterItem(String settings) {
        super(settings);

    }

    @Override
    public Class<BlasterWeaponSettings> getSettingsClass() {
        return BlasterWeaponSettings.class;
    }

    @Override
    public void weaponUseTick(Level level, LivingEntity entity, ItemStack stack, int timeLeft) {
        ItemCooldowns cooldownTracker = ((Player) entity).getCooldowns();
        if (!cooldownTracker.isOnCooldown(this))
        {
            BlasterWeaponSettings settings = getSettings(stack);
            PlayerCooldown.setPlayerCooldown((Player) entity, new PlayerCooldown(stack, settings.startupTicks, ((Player) entity).getInventory().selected, entity.getUsedItemHand(), true, false, true, entity.isOnGround()));
            if (!level.isClientSide) {
                cooldownTracker.addCooldown(this, settings.endlagTicks);
            }
        }
    }

    @Override
    public void onPlayerCooldownEnd(Level level, Player player, ItemStack stack, PlayerCooldown cooldown) {
        if (!level.isClientSide)
        {
            BlasterWeaponSettings settings = getSettings(stack);
            if (reduceInk(player, this, settings.inkConsumption, settings.inkRecoveryCooldown, true)) {
                InkProjectileEntity proj = new InkProjectileEntity(level, player, stack, InkBlockUtils.getInkType(player), settings.projectileSize, settings).setShooterTrail();
                proj.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, settings.projectileSpeed, player.isOnGround() ? settings.groundInaccuracy : settings.airInaccuracy);
                proj.setBlasterStats(settings);
                level.addFreshEntity(proj);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SplatcraftSounds.blasterShot, SoundSource.PLAYERS, 0.7F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
            }
        }
    }

    @Override
    public PlayerPosingHandler.WeaponPose getPose(ItemStack stack) {
        return PlayerPosingHandler.WeaponPose.FIRE;
    }
}
