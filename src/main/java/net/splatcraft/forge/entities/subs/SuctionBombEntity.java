package net.splatcraft.forge.entities.subs;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.splatcraft.forge.client.particles.InkExplosionParticleData;
import net.splatcraft.forge.entities.SquidBumperEntity;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.InkExplosion;
import org.jetbrains.annotations.Nullable;

public class SuctionBombEntity extends AbstractSubWeaponEntity {
    public static final float DAMAGE = 6;
    public static final float DIRECT_DAMAGE = 36;
    public static final float EXPLOSION_SIZE = 3.75f;

    private static final EntityDataAccessor<Boolean> ACTIVATED = SynchedEntityData.defineId(SuctionBombEntity.class, EntityDataSerializers.BOOLEAN);

    public static final int FUSE_START = 10;

    protected int fuseTime = 40;
    protected int prevFuseTime = 40;
    @Nullable
    private BlockState inBlockState;
    @Nullable
    private Direction stickFacing;
    protected boolean inGround;
    public boolean playShakeAnim;
    protected boolean playedActivationSound = false;

    public SuctionBombEntity(EntityType<? extends AbstractSubWeaponEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(ACTIVATED, false);
    }

    @Override
    protected Item getDefaultItem() {
        return SplatcraftItems.suctionBomb.get();
    }

    @Override
    public void tick() {
        super.tick();
        BlockState state = this.level.getBlockState(blockPosition());

        prevFuseTime = fuseTime;

        if (isActivated()) {
            fuseTime--;
            if (fuseTime <= 0) {
                InkExplosion.createInkExplosion(level, getOwner(), blockPosition(), EXPLOSION_SIZE, DAMAGE, DAMAGE, DIRECT_DAMAGE, bypassMobDamageMultiplier, getColor(), inkType, sourceWeapon);
                level.broadcastEntityEvent(this, (byte) 1);
                level.playSound(null, getX(), getY(), getZ(), SplatcraftSounds.subDetonate, SoundSource.PLAYERS, 0.8F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
                if(!level.isClientSide())
                    discard();
                return;
            } else if (fuseTime <= 20 && !playedActivationSound) {
                level.playSound(null, getX(), getY(), getZ(), SplatcraftSounds.subDetonating, SoundSource.PLAYERS, 0.8F, 1f);
                playedActivationSound = true;
            }
        }

        if(inGround)
            if(inBlockState != state && this.level.noCollision((new AABB(this.position(), this.position())).inflate(0.06D)))
            {
                this.inGround = false;
                Vec3 vector3d = this.getDeltaMovement();
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
            setYRot(180- stickFacing.toYRot());
            yRotO = getYRot();
        }
        else
        {
            setXRot(stickFacing.equals(Direction.UP) ? -90 : 90);
            setYRot(yRotO);
            xRotO = getXRot();
        }

    }



    public float getFlashIntensity(float partialTicks)
    {
        return 1f-Math.min(FUSE_START, Mth.lerp(partialTicks, prevFuseTime, fuseTime)*0.5f)/(float)FUSE_START;
    }

    @Override
    protected void onBlockHit(BlockHitResult result)
    {
        if(!inGround)
        {
            playShakeAnim = true;
            inGround = true;
            inBlockState = level.getBlockState(result.getBlockPos());

            setActivated(true);

            Vec3 vector3d = result.getLocation().subtract(this.getX(), this.getY(), this.getZ());
            this.setDeltaMovement(vector3d);
            Vec3 vector3d1 = vector3d.normalize().scale(0.05F);
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
    public void readAdditionalSaveData(CompoundTag nbt)
    {
        super.readAdditionalSaveData(nbt);
        setActivated(nbt.getBoolean("Activated"));
        if(nbt.contains("StickFacing"))
            stickFacing = Direction.byName(nbt.getString("StickFacing"));
        inGround = nbt.getBoolean("InGround");
        if (nbt.contains("InBlockState", 10))
            this.inBlockState = NbtUtils.readBlockState(nbt.getCompound("inBlockState"));

        fuseTime = nbt.getInt("FuseTime");
        prevFuseTime = fuseTime;

    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt)
    {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("Activated", isActivated());
        if(stickFacing != null)
            nbt.putString("StickFacing", stickFacing.name());
        nbt.putBoolean("InGround", inGround);
        if (this.inBlockState != null)
            nbt.put("InBlockState", NbtUtils.writeBlockState(this.inBlockState));

        nbt.putInt("FuseTime", fuseTime);
    }
}
