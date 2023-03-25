package net.splatcraft.forge.entities.subs;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.splatcraft.forge.client.particles.InkExplosionParticleData;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.InkExplosion;

public class SplatBombEntity extends AbstractSubWeaponEntity {
    public static final float DAMAGE = 6;
    public static final float DIRECT_DAMAGE = 36;
    public static final float EXPLOSION_SIZE = 3.25f;
    public static final int FUSE_START = 10;

    protected int fuseTime = 20;
    protected int prevFuseTime = fuseTime;

    public SplatBombEntity(EntityType<? extends AbstractSubWeaponEntity> type, World level) {
        super(type, level);
    }

    @Override
    protected Item getDefaultItem() {
        return SplatcraftItems.splatBomb;
    }

    @Override
    public void tick() {
        super.tick();

        prevFuseTime = fuseTime;

        if (!this.onGround || getHorizontalDistanceSqr(this.getDeltaMovement()) > (double)1.0E-5F)
        {
            float f1 = 0.98F;
            if (this.onGround)
                f1 = this.level.getBlockState(new BlockPos(this.getX(), this.getY() - 1.0D, this.getZ())).getSlipperiness(level, new BlockPos(this.getX(), this.getY() - 1.0D, this.getZ()), this);

            f1 = (float) Math.min(0.98, f1*1.5f);

            this.setDeltaMovement(this.getDeltaMovement().multiply(f1, 0.98D, f1));
            /*if (this.onGround) {
                Vector3d vector3d1 = this.getDeltaMovement();
                if (vector3d1.y < 0.0D) {
                    this.setDeltaMovement(vector3d1.multiply(1.0D, -0.5D, 1.0D));
                }
            }*/
        }



        if(onGround)
            fuseTime--;
        if(fuseTime <= 0)
        {
            InkExplosion.createInkExplosion(level, getOwner(), blockPosition(), EXPLOSION_SIZE, DAMAGE, DAMAGE, DIRECT_DAMAGE, bypassMobDamageMultiplier, getColor(), inkType, sourceWeapon);
            level.broadcastEntityEvent(this, (byte) 1);
            level.playSound(null, getX(), getY(), getZ(), SplatcraftSounds.subDetonate, SoundCategory.PLAYERS, 0.8F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
            if(!level.isClientSide())
                remove();
            return;
        }

        this.move(MoverType.SELF, this.getDeltaMovement().multiply(0,1,0));
        setPos(getX()+getDeltaMovement().x(), getY(), getZ()+getDeltaMovement().z);
    }

    @Override
    public void handleEntityEvent(byte id) {
        super.handleEntityEvent(id);
        if (id == 1) {
            level.addAlwaysVisibleParticle(new InkExplosionParticleData(getColor(), EXPLOSION_SIZE * 2), this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }

    }

    //Ripped and modified from Minestuck's BouncingProjectileEntity class (with permission)
    @Override
    protected void onHitEntity(EntityRayTraceResult result)
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
    protected void onBlockHit(BlockRayTraceResult result)
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
        return 1f-Math.min(FUSE_START, MathHelper.lerp(partialTicks, prevFuseTime, fuseTime)*0.5f)/(float)FUSE_START;
    }
}
