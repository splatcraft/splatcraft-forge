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
import net.splatcraft.forge.items.weapons.settings.SlosherWeaponSettings;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.InkBlockUtils;
import net.splatcraft.forge.util.PlayerCooldown;

public class SlosherItem extends WeaponBaseItem<SlosherWeaponSettings>
{
    public Type slosherType = Type.DEFAULT;

    public static RegistryObject<SlosherItem> create(DeferredRegister<Item> register,String settings, String name, Type slosherType)
    {
        return register.register(name, () -> new SlosherItem(settings).setSlosherType(slosherType));
    }

    public static RegistryObject<SlosherItem> create(DeferredRegister<Item> register, RegistryObject<SlosherItem> parent, String name)
    {
        return register.register(name, () -> new SlosherItem(parent.get().settingsId.toString()).setSlosherType(parent.get().slosherType));
    }

    protected SlosherItem(String settings) {
        super(settings);
    }

    @Override
    public Class<SlosherWeaponSettings> getSettingsClass() {
        return SlosherWeaponSettings.class;
    }

    public SlosherItem setSlosherType(Type type)
    {
        this.slosherType = type;
        return this;
    }

    @Override
    public void weaponUseTick(Level level, LivingEntity entity, ItemStack stack, int timeLeft)
    {
        SlosherWeaponSettings settings = getSettings(stack);
        if (entity instanceof Player && getUseDuration(stack) - timeLeft < settings.startupTicks) {
            ItemCooldowns cooldownTracker = ((Player) entity).getCooldowns();
            if (!cooldownTracker.isOnCooldown(this)) {
                PlayerCooldown.setPlayerCooldown((Player) entity, new PlayerCooldown(stack, settings.startupTicks, ((Player) entity).getInventory().selected, entity.getUsedItemHand(), true, false, true, entity.isOnGround()));
                if (!level.isClientSide && settings.endlagTicks > 0) {
                    cooldownTracker.addCooldown(this, settings.endlagTicks);
                }
            }
        }
    }

    @Override
    public void onPlayerCooldownEnd(Level level, Player player, ItemStack stack, PlayerCooldown cooldown)
    {
        SlosherWeaponSettings settings = getSettings(stack);

        if (!level.isClientSide && reduceInk(player, this, settings.inkConsumption, settings.inkRecoveryCooldown, true)) {
            for (int i = 0; i < settings.projectileCount; i++) {
                boolean hasTrail = i == Math.floor((settings.projectileCount - 1) / 2f) || i == Math.ceil((settings.projectileCount - 1) / 2f);
                float angle = settings.angleOffset * i - settings.angleOffset * (settings.projectileCount - 1) / 2;

                InkProjectileEntity proj = new InkProjectileEntity(level, player, stack, InkBlockUtils.getInkType(player), settings.projectileSize * (hasTrail ? 1 : 0.8f), settings);
                proj.setSlosherStats(settings);
                proj.shootFromRotation(player, player.getXRot(), player.getYRot() + angle, settings.pitchCompensation, settings.projectileSpeed, 2);
                level.addFreshEntity(proj);

                switch (slosherType) {
                    case EXPLODING:
                        proj.explodes = true;
                        proj.setProjectileType(InkProjectileEntity.Types.BLASTER);
                    case CYCLONE:
                        proj.canPierce = true;
                }
            }
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SplatcraftSounds.slosherShot, SoundSource.PLAYERS, 0.7F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
        }
    }

    @Override
    public PlayerPosingHandler.WeaponPose getPose(ItemStack stack)
    {
        return PlayerPosingHandler.WeaponPose.BUCKET_SWING;
    }

    public enum Type
    {
        DEFAULT,
        EXPLODING,
        CYCLONE,
        BUBBLES
    }
}
