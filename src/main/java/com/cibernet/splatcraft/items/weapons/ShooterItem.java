package com.cibernet.splatcraft.items.weapons;

import com.cibernet.splatcraft.entities.InkProjectileEntity;
import com.cibernet.splatcraft.handlers.PlayerPosingHandler;
import com.cibernet.splatcraft.registries.SplatcraftSounds;
import com.cibernet.splatcraft.util.InkBlockUtils;
import com.cibernet.splatcraft.util.WeaponStat;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ShooterItem extends WeaponBaseItem
{
    public float projectileSize;
    public float projectileSpeed;
    public float inaccuracy;
    public int firingSpeed;
    public float damage;
    public float inkConsumption;

    public ShooterItem(String name, float projectileSize, float projectileSpeed, float inaccuracy, int firingSpeed, float damage, float inkConsumption)
    {
        super();
        setRegistryName(name);

        this.projectileSize = projectileSize;
        this.projectileSpeed = projectileSpeed;
        this.inaccuracy = inaccuracy;
        this.firingSpeed = firingSpeed;
        this.damage = damage;
        this.inkConsumption = inkConsumption;

        if (!(this instanceof BlasterItem))
        {
            addStat(new WeaponStat("range", (stack, level) -> (int) (projectileSpeed / 1.2f * 100)));
            addStat(new WeaponStat("damage", (stack, level) -> (int) (damage / 20 * 100)));
            addStat(new WeaponStat("fire_rate", (stack, level) -> (11 - firingSpeed) * 10));
        }
    }

    public ShooterItem(String name, ShooterItem parent)
    {
        this(name, parent.projectileSize, parent.projectileSpeed, parent.inaccuracy, parent.firingSpeed, parent.damage, parent.inkConsumption);
    }

    @Override
    public void weaponUseTick(World level, LivingEntity entity, ItemStack stack, int timeLeft)
    {
        if (!level.isClientSide && (getUseDuration(stack) - timeLeft - 1) % firingSpeed == 0)
        {
            if (getInkAmount(entity, stack) >= inkConsumption)
            {
                InkProjectileEntity proj = new InkProjectileEntity(level, entity, stack, InkBlockUtils.getInkType(entity), projectileSize, damage).setShooterTrail();
                proj.shootFromRotation(entity, entity.xRot, entity.yRot, 0.0f, projectileSpeed, inaccuracy);
                level.addFreshEntity(proj);
                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SplatcraftSounds.shooterShot, SoundCategory.PLAYERS, 0.7F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
                reduceInk(entity, inkConsumption);
            } else
            {
                sendNoInkMessage(entity);
            }
        }
    }

    @Override
    public PlayerPosingHandler.WeaponPose getPose()
    {
        return PlayerPosingHandler.WeaponPose.FIRE;
    }
}
