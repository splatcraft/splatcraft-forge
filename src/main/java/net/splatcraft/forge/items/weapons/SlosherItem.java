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
import net.splatcraft.forge.items.weapons.settings.WeaponSettings;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.InkBlockUtils;
import net.splatcraft.forge.util.PlayerCooldown;
import net.splatcraft.forge.util.WeaponTooltip;

public class SlosherItem extends WeaponBaseItem<WeaponSettings>
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

        addStat(new WeaponTooltip("range", (stack, level) -> (int) (getSettings(stack).projectileSpeed / 1.2f * 100)));
        addStat(new WeaponTooltip("damage", (stack, level) -> (int) (getSettings(stack).baseDamage / 20 * 100)));
        addStat(new WeaponTooltip("handling", (stack, level) -> (int) ((15 - getSettings(stack).startupTicks) / 15f * 100)));
    }

    @Override
    public Class<WeaponSettings> getSettingsClass() {
        return WeaponSettings.class;
    }

    public SlosherItem setSlosherType(Type type)
    {
        this.slosherType = type;
        return this;
    }

    @Override
    public void weaponUseTick(Level level, LivingEntity entity, ItemStack stack, int timeLeft)
    {
        WeaponSettings settings = getSettings(stack);
        if (entity instanceof Player && getUseDuration(stack) - timeLeft < settings.startupTicks) {
            ItemCooldowns cooldownTracker = ((Player) entity).getCooldowns();
            if (!cooldownTracker.isOnCooldown(this)) {
                PlayerCooldown.setPlayerCooldown((Player) entity, new PlayerCooldown(stack, settings.startupTicks, ((Player) entity).getInventory().selected, entity.getUsedItemHand(), true, false, true, entity.isOnGround()));
                if (!level.isClientSide && settings.firingSpeed > 0) {
                    cooldownTracker.addCooldown(this, settings.firingSpeed);
                }
            }
        }
    }

    @Override
    public void onPlayerCooldownEnd(Level level, Player player, ItemStack stack, PlayerCooldown cooldown)
    {
        WeaponSettings settings = getSettings(stack);

        if (!level.isClientSide && reduceInk(player, this, settings.inkConsumption, settings.inkRecoveryCooldown, true)) {
            for (int i = 0; i < settings.projectileCount; i++) {
                boolean hasTrail = i == Math.floor((settings.projectileCount - 1) / 2f) || i == Math.ceil((settings.projectileCount - 1) / 2f);
                float angle = settings.groundInaccuracy * i - settings.groundInaccuracy * (settings.projectileCount - 1) / 2;

                InkProjectileEntity proj = new InkProjectileEntity(level, player, stack, InkBlockUtils.getInkType(player), settings.projectileSize * (hasTrail ? 1 : 0.8f), settings);
                proj.setShooterTrail();
                proj.shootFromRotation(player, player.getXRot(), player.getYRot() + angle, settings.pitchCompensation, settings.projectileSpeed, 2);
                level.addFreshEntity(proj);

                switch (slosherType) {
                    case EXPLODING:
                        proj.trailSize = settings.projectileSize * 0.4f;
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
