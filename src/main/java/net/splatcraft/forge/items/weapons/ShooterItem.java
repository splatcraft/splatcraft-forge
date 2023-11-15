package net.splatcraft.forge.items.weapons;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.splatcraft.forge.entities.InkProjectileEntity;
import net.splatcraft.forge.handlers.PlayerPosingHandler;
import net.splatcraft.forge.items.weapons.settings.WeaponSettings;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.InkBlockUtils;
import net.splatcraft.forge.util.WeaponTooltip;

public class ShooterItem extends WeaponBaseItem<WeaponSettings>
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
    public Class<WeaponSettings> getSettingsClass() {
        return WeaponSettings.class;
    }

    @Override
    public void weaponUseTick(Level level, LivingEntity entity, ItemStack stack, int timeLeft)
    {
        WeaponSettings settings = getSettings(stack);
        if (!level.isClientSide && settings.firingSpeed > 0 && (getUseDuration(stack) - timeLeft - 1) % settings.firingSpeed == 0)
        {
            if (reduceInk(entity, this, settings.inkConsumption, settings.inkRecoveryCooldown, true)) {

                for(int i = 0; i < settings.projectileCount; i++)
                {
                    InkProjectileEntity proj = new InkProjectileEntity(level, entity, stack, InkBlockUtils.getInkType(entity), settings.projectileSize, settings).setShooterTrail();
                    proj.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), settings.pitchCompensation, settings.projectileSpeed, entity.isOnGround() ? settings.groundInaccuracy : settings.airInaccuracy);
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
