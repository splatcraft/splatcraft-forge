package net.splatcraft.forge.items.weapons;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.splatcraft.forge.entities.InkProjectileEntity;
import net.splatcraft.forge.handlers.PlayerPosingHandler;
import net.splatcraft.forge.items.weapons.settings.WeaponSettings;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.InkBlockUtils;
import net.splatcraft.forge.util.PlayerCooldown;
import net.splatcraft.forge.util.WeaponTooltip;

public class SlosherItem extends WeaponBaseItem
{
    public WeaponSettings settings;
    public Type slosherType = Type.DEFAULT;

    public SlosherItem(WeaponSettings settings) {
        super(settings);
        this.settings = settings;
        setRegistryName(this.settings.name);

        addStat(new WeaponTooltip("range", (stack, level) -> (int) (settings.projectileSpeed / 1.2f * 100)));
        addStat(new WeaponTooltip("damage", (stack, level) -> (int) (settings.baseDamage / 20 * 100)));
        addStat(new WeaponTooltip("handling", (stack, level) -> (int) ((15 - settings.startupTicks) / 15f * 100)));
    }

    public SlosherItem setSlosherType(Type type)
    {
        this.slosherType = type;
        return this;
    }

    @Override
    public void weaponUseTick(World level, LivingEntity entity, ItemStack stack, int timeLeft) {
        if (entity instanceof PlayerEntity && getUseDuration(stack) - timeLeft < settings.startupTicks) {
            CooldownTracker cooldownTracker = ((PlayerEntity) entity).getCooldowns();
            if (!cooldownTracker.isOnCooldown(this)) {
                PlayerCooldown.setPlayerCooldown((PlayerEntity) entity, new PlayerCooldown(stack, settings.startupTicks, ((PlayerEntity) entity).inventory.selected, entity.getUsedItemHand(), true, false, true, entity.isOnGround()));
                if (!level.isClientSide && settings.firingSpeed > 0) {
                    cooldownTracker.addCooldown(this, settings.firingSpeed);
                }
            }
        }
    }

    @Override
    public void onPlayerCooldownEnd(World level, PlayerEntity player, ItemStack stack, PlayerCooldown cooldown) {
        if (!level.isClientSide && reduceInk(player, this, settings.inkConsumption, settings.inkRecoveryCooldown, true)) {
            for (int i = 0; i < settings.projectileCount; i++) {
                boolean hasTrail = i == Math.floor((settings.projectileCount - 1) / 2f) || i == Math.ceil((settings.projectileCount - 1) / 2f);
                float angle = settings.groundInaccuracy * i - settings.groundInaccuracy * (settings.projectileCount - 1) / 2;

                InkProjectileEntity proj = new InkProjectileEntity(level, player, stack, InkBlockUtils.getInkType(player), settings.projectileSize * (hasTrail ? 1 : 0.8f), settings);
                proj.setShooterTrail();
                proj.shootFromRotation(player, player.xRot, player.yRot + angle, -15.0f, settings.projectileSpeed, 2);
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
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SplatcraftSounds.slosherShot, SoundCategory.PLAYERS, 0.7F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
        }
    }

    @Override
    public PlayerPosingHandler.WeaponPose getPose()
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
