package net.splatcraft.forge.entities.subs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.splatcraft.forge.client.particles.InkExplosionParticleData;
import net.splatcraft.forge.items.weapons.settings.SubWeaponSettings;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.InkExplosion;

public class SplatBombEntity extends AbstractSubWeaponEntity {
    public static final float DAMAGE = 6;
    public static final float DIRECT_DAMAGE = 36;
    public static final float EXPLOSION_SIZE = 3.25f;
    public static final int FLASH_DURATION = 10;

    protected int fuseTime = 0;
    protected int prevFuseTime = 0;

    public SplatBombEntity(EntityType<? extends AbstractSubWeaponEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected Item getDefaultItem() {
        return SplatcraftItems.splatBomb.get();
    }

    @Override
    public void tick()
    {
        super.tick();

        prevFuseTime = fuseTime;
        SubWeaponSettings settings = getSettings();

        if (!this.onGround || distanceToSqr(this.getDeltaMovement()) > (double)1.0E-5F)
        {
            float f1 = 0.98F;
            if (this.onGround)
                f1 = this.level.getBlockState(new BlockPos(this.getX(), this.getY() - 1.0D, this.getZ())).getFriction(level, new BlockPos(this.getX(), this.getY() - 1.0D, this.getZ()), this);

            f1 = (float) Math.min(0.98, f1*1.5f);

            this.setDeltaMovement(this.getDeltaMovement().multiply(f1, 0.98D, f1));
            /*if (this.onGround) {
                Vec3 vector3d1 = this.getDeltaMovement();
                if (vector3d1.y < 0.0D) {
                    this.setDeltaMovement(vector3d1.multiply(1.0D, -0.5D, 1.0D));
                }
            }*/
        }



        if(onGround)
            fuseTime++;
        if(fuseTime >= settings.fuseTime)
        {
            InkExplosion.createInkExplosion(level, getOwner(), blockPosition(), settings.explosionSize, settings.propDamage, settings.indirectDamage, settings.directDamage, bypassMobDamageMultiplier, getColor(), inkType, sourceWeapon);
            level.broadcastEntityEvent(this, (byte) 1);
            level.playSound(null, getX(), getY(), getZ(), SplatcraftSounds.subDetonate, SoundSource.PLAYERS, 0.8F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
            if(!level.isClientSide())
                discard();
            return;
        }

        this.move(MoverType.SELF, this.getDeltaMovement().multiply(0,1,0));
        setPos(getX()+getDeltaMovement().x(), getY(), getZ()+getDeltaMovement().z);
    }

    @Override
    public void handleEntityEvent(byte id) {
        super.handleEntityEvent(id);
        if (id == 1) {
            level.addAlwaysVisibleParticle(new InkExplosionParticleData(getColor(), getSettings().explosionSize * 2), this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }

    }

    //Ripped and modified from Minestuck's BouncingProjectileEntity class (with permission)
    @Override
    protected void onHitEntity(EntityHitResult result)
    {
        super.onHitEntity(result);

        double velocityX = this.getDeltaMovement().x * 0.3;
        double velocityY = this.getDeltaMovement().y;
        double velocityZ = this.getDeltaMovement().z * 0.3;
        double absVelocityX = Math.abs(velocityX);
        double absVelocityY = Math.abs(velocityY);
        double absVelocityZ = Math.abs(velocityZ);

        if(absVelocityX >= absVelocityY && absVelocityX >= absVelocityZ)
            this.setDeltaMovement(-velocityX, velocityY, velocityZ);
        if(absVelocityY >= .05 && absVelocityY >= absVelocityX && absVelocityY >= absVelocityZ)
            this.setDeltaMovement(velocityX, -velocityY * .5, velocityZ);
        if(absVelocityZ >= absVelocityY && absVelocityZ >= absVelocityX)
            this.setDeltaMovement(velocityX, velocityY, -velocityZ);

    }

    @Override
    protected void onBlockHit(BlockHitResult result)
    {
        if(level.getBlockState(result.getBlockPos()).getCollisionShape(level, result.getBlockPos()).bounds().maxY - (position().y() - blockPosition().getY()) <= 0)
            return;

        double velocityX = this.getDeltaMovement().x;
        double velocityY = this.getDeltaMovement().y;
        double velocityZ = this.getDeltaMovement().z;

        Direction blockFace = result.getDirection();

        if(blockFace == Direction.EAST || blockFace == Direction.WEST)
            this.setDeltaMovement(-velocityX, velocityY, velocityZ);
        if(Math.abs(velocityY) >= 0.05 && (blockFace == Direction.DOWN || blockFace == Direction.UP))
            this.setDeltaMovement(velocityX, -velocityY * .5, velocityZ);
        if(blockFace == Direction.NORTH || blockFace == Direction.SOUTH) this.setDeltaMovement(velocityX, velocityY, -velocityZ);
    }

    @Override
    protected boolean handleMovement() {
        return false;
    }

    public float getFlashIntensity(float partialTicks)
    {
        SubWeaponSettings settings = getSettings();
        return 1f-Math.min(settings.fuseTime-FLASH_DURATION, Mth.lerp(partialTicks, prevFuseTime, fuseTime)*0.5f)/(float) (settings.fuseTime-FLASH_DURATION);
    }
}
