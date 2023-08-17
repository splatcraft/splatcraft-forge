package net.splatcraft.forge.entities.subs;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
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

    public BurstBombEntity(EntityType<? extends AbstractSubWeaponEntity> type, Level level)
    {
        super(type, level);
    }

    @Override
    protected void onHitEntity(EntityHitResult result)
    {
        super.onHitEntity(result);

        if (result.getEntity() instanceof LivingEntity target)
            InkDamageUtils.doDamage(level, target, DIRECT_DAMAGE, getColor(), getOwner(), this, sourceWeapon, bypassMobDamageMultiplier, SPLASH_DAMAGE_TYPE, false);
        InkExplosion.createInkExplosion(level, getOwner(), blockPosition(), EXPLOSION_SIZE, DAMAGE, DIRECT_DAMAGE, bypassMobDamageMultiplier, getColor(), inkType, sourceWeapon);

        level.broadcastEntityEvent(this, (byte) 1);
        level.playSound(null, getX(), getY(), getZ(), SplatcraftSounds.subDetonate, SoundSource.PLAYERS, 0.8F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
        if (!level.isClientSide())
            discard();
    }

    @Override
    protected void onBlockHit(BlockHitResult result)
    {
        InkExplosion.createInkExplosion(level, getOwner(), blockPosition(), EXPLOSION_SIZE, DAMAGE, DIRECT_DAMAGE, bypassMobDamageMultiplier, getColor(), inkType, sourceWeapon);
        level.broadcastEntityEvent(this, (byte) 1);
        level.playSound(null, getX(), getY(), getZ(), SplatcraftSounds.subDetonate, SoundSource.PLAYERS, 0.8F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
        discard();
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
        return SplatcraftItems.burstBomb.get();
    }
}
