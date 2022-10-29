package net.splatcraft.forge.entities.subs;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.splatcraft.forge.client.particles.InkExplosionParticleData;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.InkExplosion;
import org.jetbrains.annotations.Nullable;

public class SuctionBombEntity extends AbstractSubWeaponEntity {
    public static final float DAMAGE = 6;
    public static final float DIRECT_DAMAGE = 36;
    public static final float EXPLOSION_SIZE = 3.75f;

    private static final DataParameter<Boolean> ACTIVATED = EntityDataManager.defineId(SuctionBombEntity.class, DataSerializers.BOOLEAN);

    public static final int FUSE_START = 10;

    protected int fuseTime = 40;
    protected int prevFuseTime = 40;
    @Nullable
    private BlockState inBlockState;
    @Nullable
    private Direction stickFacing;
    protected boolean inGround;
    public int shakeTime;
    protected boolean playedActivationSound = false;

    public SuctionBombEntity(EntityType<? extends AbstractSubWeaponEntity> type, World level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(ACTIVATED, false);
    }

    @Override
    protected Item getDefaultItem() {
        return SplatcraftItems.suctionBomb;
    }

    @Override
    public void tick() {
        super.tick();
        BlockState state = this.level.getBlockState(blockPosition());

        if (shakeTime > 0)
            --shakeTime;

        prevFuseTime = fuseTime;

        if (isActivated()) {
            fuseTime--;
            if (fuseTime <= 0) {
                InkExplosion.createInkExplosion(level, getOwner(), SPLASH_DAMAGE_SOURCE, blockPosition(), EXPLOSION_SIZE, DAMAGE, DAMAGE, DIRECT_DAMAGE, damageMobs, getColor(), inkType, sourceWeapon);
                level.broadcastEntityEvent(this, (byte) 1);
                level.playSound(null, getX(), getY(), getZ(), SplatcraftSounds.subDetonate, SoundCategory.PLAYERS, 0.8F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
                if(!level.isClientSide())
                    remove();
                return;
            } else if (fuseTime <= 20 && !playedActivationSound) {
                level.playSound(null, getX(), getY(), getZ(), SplatcraftSounds.subDetonating, SoundCategory.PLAYERS, 0.8F, 1f);
                playedActivationSound = true;
            }
        }

        if(inGround)
            if(inBlockState != state && this.level.noCollision((new AxisAlignedBB(this.position(), this.position())).inflate(0.06D)))
            {
                this.inGround = false;
                Vector3d vector3d = this.getDeltaMovement();
                this.setDeltaMovement(vector3d.multiply(this.random.nextFloat() * 0.2F, this.random.nextFloat() * 0.2F, this.random.nextFloat() * 0.2F));
            }
            else
            {
                setDeltaMovement(0, 0, 0);
                setStickFacing();
            }

        checkInsideBlocks();
    }


    @Override
    public void handleEntityEvent(byte id)
    {
        super.handleEntityEvent(id);
        if (id == 1)
            level.addAlwaysVisibleParticle(new InkExplosionParticleData(getColor(), EXPLOSION_SIZE * 2), this.getX(), this.getY(), this.getZ(), 0, 0, 0);
    }

    public void setStickFacing()
    {

        if(stickFacing.get2DDataValue() >= 0)
        {
            yRot = 180- stickFacing.toYRot();
            yRotO = yRot;
        }
        else
        {
            xRot = stickFacing.equals(Direction.UP) ? -90 : 90;
            yRot = yRotO;
            xRotO = xRot;
        }

    }



    public float getFlashIntensity(float partialTicks)
    {
        return 1f-Math.min(FUSE_START, MathHelper.lerp(partialTicks, prevFuseTime, fuseTime)*0.5f)/(float)FUSE_START;
    }

    @Override
    protected void onBlockHit(BlockRayTraceResult result)
    {
        if(!inGround)
        {
            shakeTime = 7;
            inGround = true;
            inBlockState = level.getBlockState(result.getBlockPos());

            setActivated(true);

            Vector3d vector3d = result.getLocation().subtract(this.getX(), this.getY(), this.getZ());
            this.setDeltaMovement(vector3d);
            Vector3d vector3d1 = vector3d.normalize().scale(0.05F);
            this.setPosRaw(this.getX() - vector3d1.x, this.getY() - vector3d1.y, this.getZ() - vector3d1.z);

            stickFacing = result.getDirection();
            setStickFacing();
        }
    }

    public void setActivated(boolean v)
    {
        entityData.set(ACTIVATED, v);
    }
    public boolean isActivated() { return entityData.get(ACTIVATED);}

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt)
    {
        super.readAdditionalSaveData(nbt);
        setActivated(nbt.getBoolean("Activated"));
        if(nbt.contains("StickFacing"))
            stickFacing = Direction.byName(nbt.getString("StickFacing"));
        inGround = nbt.getBoolean("InGround");
        shakeTime = nbt.getInt("ShakeTime");
        if (nbt.contains("InBlockState", 10))
            this.inBlockState = NBTUtil.readBlockState(nbt.getCompound("inBlockState"));

        fuseTime = nbt.getInt("FuseTime");
        prevFuseTime = fuseTime;

    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt)
    {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("Activated", isActivated());
        if(stickFacing != null)
            nbt.putString("StickFacing", stickFacing.name());
        nbt.putBoolean("InGround", inGround);
        nbt.putInt("ShakeTime", shakeTime);
        if (this.inBlockState != null)
            nbt.put("InBlockState", NBTUtil.writeBlockState(this.inBlockState));

        nbt.putInt("FuseTime", fuseTime);
    }

}
