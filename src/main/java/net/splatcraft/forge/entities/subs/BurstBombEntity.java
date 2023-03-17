package net.splatcraft.forge.entities.subs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.splatcraft.forge.client.particles.InkExplosionParticleData;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.InkDamageUtils;
import net.splatcraft.forge.util.InkExplosion;

public class BurstBombEntity extends AbstractSubWeaponEntity
{

    public static final float DAMAGE = 6;
    public static final float DIRECT_DAMAGE = 12;
    public static final float EXPLOSION_SIZE = 2;

    public BurstBombEntity(EntityType<? extends AbstractSubWeaponEntity> type, World level)
    {
        super(type, level);
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult result)
    {
        super.onHitEntity(result);

        Entity target = result.getEntity();
        if (target instanceof LivingEntity)
            InkDamageUtils.doDamage(level, (LivingEntity) target, DIRECT_DAMAGE, getColor(), getOwner(), sourceWeapon, bypassMobDamageMultiplier, SPLASH_DAMAGE_TYPE, false);
        InkExplosion.createInkExplosion(level, getOwner(), blockPosition(), 2, DAMAGE, DIRECT_DAMAGE, bypassMobDamageMultiplier, getColor(), inkType, sourceWeapon);
        level.broadcastEntityEvent(this, (byte) 1);
        level.playSound(null, getX(), getY(), getZ(), SplatcraftSounds.subDetonate, SoundCategory.PLAYERS, 0.8F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
        if (!level.isClientSide())
            remove();
    }

    @Override
    protected void onBlockHit(BlockRayTraceResult result)
    {
        InkExplosion.createInkExplosion(level, getOwner(), blockPosition(), EXPLOSION_SIZE, DAMAGE, DIRECT_DAMAGE, bypassMobDamageMultiplier, getColor(), inkType, sourceWeapon);
        level.broadcastEntityEvent(this, (byte) 1);
        level.playSound(null, getX(), getY(), getZ(), SplatcraftSounds.subDetonate, SoundCategory.PLAYERS, 0.8F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
        remove();
    }


    @Override
    public void handleEntityEvent(byte id) {
        super.handleEntityEvent(id);
        if (id == 1) {
            level.addAlwaysVisibleParticle(new InkExplosionParticleData(getColor(), EXPLOSION_SIZE * 2), this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }

    }

    @Override
    protected Item getDefaultItem()
    {
        return SplatcraftItems.burstBomb;
    }

}
