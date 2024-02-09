package net.splatcraft.forge.items.weapons;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.splatcraft.forge.entities.InkProjectileEntity;
import net.splatcraft.forge.handlers.PlayerPosingHandler;
import net.splatcraft.forge.items.weapons.settings.ShooterWeaponSettings;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.InkBlockUtils;
import net.splatcraft.forge.util.PlayerCooldown;

public class ShooterItem extends WeaponBaseItem<ShooterWeaponSettings>
{
    public static RegistryObject<ShooterItem> create(DeferredRegister<Item> registry, String settings, String name)
    {
        return registry.register(name, () -> new ShooterItem(settings));
    }

    public static RegistryObject<ShooterItem> create(DeferredRegister<Item> registry, RegistryObject<ShooterItem> parent, String name)
    {
        return create(registry, parent, name, false);
    }

    public static RegistryObject<ShooterItem> create(DeferredRegister<Item> registry, RegistryObject<ShooterItem> parent, String name, boolean secret)
    {
        return registry.register(name, () -> new ShooterItem(parent.get().settingsId.toString()).setSecret(secret));
    }



    protected ShooterItem(String settings)
    {
        super(settings);
    }

    @Override
    public Class<ShooterWeaponSettings> getSettingsClass() {
        return ShooterWeaponSettings.class;
    }

    @Override
    public void weaponUseTick(Level level, LivingEntity entity, ItemStack stack, int timeLeft)
    {
        ShooterWeaponSettings settings = getSettings(stack);

        int time = getUseDuration(stack) - timeLeft;

        if(time <= 0)
        {
            if (settings.startupTicks > 0 && entity instanceof Player player)
                PlayerCooldown.setPlayerCooldown(player, new PlayerCooldown(stack, settings.startupTicks, player.getInventory().selected, player.getUsedItemHand(), true, false, true, player.isOnGround()));
        } else time -= settings.startupTicks;

        if (!level.isClientSide && settings.firingSpeed > 0 && (time - 1) % settings.firingSpeed == 0)
        {
            if (reduceInk(entity, this, settings.inkConsumption, settings.inkRecoveryCooldown, true)) {

                for(int i = 0; i < settings.projectileCount; i++)
                {
                    InkProjectileEntity proj = new InkProjectileEntity(level, entity, stack, InkBlockUtils.getInkType(entity), settings.projectileSize, settings);
                    proj.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), settings.pitchCompensation, settings.projectileSpeed, entity.isOnGround() ? settings.groundInaccuracy : settings.airInaccuracy);
                    proj.setShooterStats(settings);
                    level.addFreshEntity(proj);
                }

                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SplatcraftSounds.shooterShot, SoundSource.PLAYERS, 0.7F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
            }
        }
    }

    @Override
    public PlayerPosingHandler.WeaponPose getPose(ItemStack stack)
    {
        return PlayerPosingHandler.WeaponPose.FIRE;
    }
}
