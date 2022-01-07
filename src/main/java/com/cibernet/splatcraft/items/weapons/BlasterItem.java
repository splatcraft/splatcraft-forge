package com.cibernet.splatcraft.items.weapons;

import com.cibernet.splatcraft.entities.InkProjectileEntity;
import com.cibernet.splatcraft.registries.SplatcraftSounds;
import com.cibernet.splatcraft.util.InkBlockUtils;
import com.cibernet.splatcraft.util.PlayerCooldown;
import com.cibernet.splatcraft.util.WeaponStat;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class BlasterItem extends ShooterItem
{
    public int projLifespan;
    public int startupTicks;
    public int cooldown;
    public float splashDamage;

    public BlasterItem(String name, float projectileSize, float projectileSpeed, float inaccuracy, int startupTicks, int cooldown, float damage, float splashDamage, float inkConsumption, int projectileLifespan)
    {
        super(name, projectileSize, projectileSpeed, inaccuracy, cooldown, damage, inkConsumption);
        this.projLifespan = projectileLifespan;
        this.startupTicks = startupTicks;
        this.cooldown = cooldown;
        this.splashDamage = splashDamage;


        addStat(new WeaponStat("range", (stack, level) -> (int) (projectileSpeed / projectileLifespan * 100)));
        addStat(new WeaponStat("impact", (stack, level) -> (int) (damage / 20 * 100)));
        addStat(new WeaponStat("fire_rate", (stack, level) -> (int) ((11 - cooldown * 0.5f) * 10)));
    }

    public BlasterItem(String name, BlasterItem parent)
    {
        this(name, parent.projectileSize, parent.projectileSpeed, parent.inaccuracy, parent.startupTicks, parent.firingSpeed, parent.damage, parent.splashDamage, parent.inkConsumption, parent.projLifespan);
    }

    @Override
    public void weaponUseTick(World level, LivingEntity entity, ItemStack stack, int timeLeft)
    {
        CooldownTracker cooldownTracker = ((PlayerEntity) entity).getCooldowns();
        if (!cooldownTracker.isOnCooldown(this))
        {
            if (getInkAmount(entity, stack) > inkConsumption)
            {
                PlayerCooldown.setPlayerCooldown((PlayerEntity) entity, new PlayerCooldown(startupTicks, ((PlayerEntity) entity).inventory.selected, entity.getUsedItemHand(), true, false, true, entity.isOnGround()));
                if (!level.isClientSide)
                {
                    cooldownTracker.addCooldown(this, cooldown);
                }
            } else if (timeLeft % cooldown == 0)
            {
                sendNoInkMessage(entity);
            }
        }
    }

    @Override
    public void onPlayerCooldownEnd(World level, PlayerEntity player, ItemStack stack, PlayerCooldown cooldown)
    {
        if (getInkAmount(player, stack) >= inkConsumption)
        {
            if (!level.isClientSide)
            {
                InkProjectileEntity proj = new InkProjectileEntity(level, player, stack, InkBlockUtils.getInkType(player), projectileSize, damage).setShooterTrail();
                proj.setBlasterStats(projLifespan, splashDamage);
                proj.shootFromRotation(player, player.xRot, player.yRot, 0.0f, projectileSpeed, inaccuracy);
                level.addFreshEntity(proj);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SplatcraftSounds.blasterShot, SoundCategory.PLAYERS, 0.7F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
                reduceInk(player, inkConsumption);

            }
        } else
        {
            sendNoInkMessage(player, null);
        }
    }
}
