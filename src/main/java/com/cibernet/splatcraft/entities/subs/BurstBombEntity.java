package com.cibernet.splatcraft.entities.subs;

import com.cibernet.splatcraft.client.particles.InkExplosionParticleData;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import com.cibernet.splatcraft.registries.SplatcraftSounds;
import com.cibernet.splatcraft.util.InkDamageUtils;
import com.cibernet.splatcraft.util.InkExplosion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;

public class BurstBombEntity extends AbstractSubWeaponEntity
{

    public static final float DAMAGE = 6;
    public static final float DIRECT_DAMAGE = 12;
    public static final float EXPLOSION_SIZE = 2;

    public BurstBombEntity(EntityType<? extends AbstractSubWeaponEntity> type, World world)
    {
        super(type, world);
    }

    @Override
    protected void onEntityHit(EntityRayTraceResult result)
    {
        super.onEntityHit(result);

        Entity target = result.getEntity();
        if (target instanceof LivingEntity)
            InkDamageUtils.doDamage(world, (LivingEntity) target, DIRECT_DAMAGE, getColor(), func_234616_v_(), sourceWeapon, damageMobs, inkType, SPLASH_DAMAGE_TYPE, false);
        InkExplosion.createInkExplosion(world, func_234616_v_(), SPLASH_DAMAGE_SOURCE, getPosition(), 2, DAMAGE, DAMAGE, damageMobs, getColor(), inkType, sourceWeapon);
        world.setEntityState(this, (byte) 1);
        world.playSound(null, getPosX(), getPosY(), getPosZ(), SplatcraftSounds.subDetonate, SoundCategory.PLAYERS, 0.8F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.1F + 1.0F) * 0.95F);
        setDead();
    }

    @Override
    protected void onBlockHit(BlockRayTraceResult result)
    {
        InkExplosion.createInkExplosion(world, func_234616_v_(), SPLASH_DAMAGE_SOURCE, getPosition(), EXPLOSION_SIZE, DAMAGE, DAMAGE, damageMobs, getColor(), inkType, sourceWeapon);
        world.setEntityState(this, (byte) 1);
        world.playSound(null, getPosX(), getPosY(), getPosZ(), SplatcraftSounds.subDetonate, SoundCategory.PLAYERS, 0.8F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.1F + 1.0F) * 0.95F);
        setDead();
    }


    @Override
    public void handleStatusUpdate(byte id)
    {
        super.handleStatusUpdate(id);
        switch (id)
        {
            case 1:
                world.addParticle(new InkExplosionParticleData(getColor(), EXPLOSION_SIZE * 2), this.getPosX(), this.getPosY(), this.getPosZ(), 0, 0, 0);
                break;
        }

    }

    @Override
    protected Item getDefaultItem()
    {
        return SplatcraftItems.burstBomb;
    }

}
