package com.cibernet.splatcraft.entities.subs;

import com.cibernet.splatcraft.client.particles.InkExplosionParticleData;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import com.cibernet.splatcraft.registries.SplatcraftSounds;
import com.cibernet.splatcraft.util.InkExplosion;
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

import javax.annotation.Nullable;

public class SuctionBombEntity extends AbstractSubWeaponEntity
{
    public static final float DAMAGE = 6;
    public static final float DIRECT_DAMAGE = 44;
    public static final float EXPLOSION_SIZE = 3.5f;

    private static final DataParameter<Boolean> ACTIVATED = EntityDataManager.createKey(SuctionBombEntity.class, DataSerializers.BOOLEAN);

    public static final int FUSE_START = 10;

    protected int fuseTime = 40;
    protected int prevFuseTime = 40;
    @Nullable
    private BlockState inBlockState;
    @Nullable
    private Direction stickFacing;
    protected boolean inGround;
    public int shakeTime;

    public SuctionBombEntity(EntityType<? extends AbstractSubWeaponEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void registerData() {
        super.registerData();
        dataManager.register(ACTIVATED, false);
    }

    @Override
    protected Item getDefaultItem() {
        return SplatcraftItems.suctionBomb;
    }

    @Override
    public void tick()
    {
        super.tick();
        BlockState state = this.world.getBlockState(getPosition());

        if(shakeTime > 0)
            --shakeTime;

        prevFuseTime = fuseTime;

        if(isActivated())
            if(fuseTime > 0)
                --fuseTime;
            else
            {
                InkExplosion.createInkExplosion(world, func_234616_v_(), SPLASH_DAMAGE_SOURCE, getPosition(), EXPLOSION_SIZE, DAMAGE, DAMAGE, DIRECT_DAMAGE, damageMobs, getColor(), inkType, sourceWeapon);
                world.setEntityState(this, (byte) 1);
                world.playSound(null, getPosX(), getPosY(), getPosZ(), SplatcraftSounds.subDetonate, SoundCategory.PLAYERS, 0.8F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.1F + 1.0F) * 0.95F);
                setDead();
                return;
            }

        if(inGround)
            if(inBlockState != state && this.world.hasNoCollisions((new AxisAlignedBB(this.getPositionVec(), this.getPositionVec())).grow(0.06D)))
            {
                this.inGround = false;
                Vector3d vector3d = this.getMotion();
                this.setMotion(vector3d.mul((double)(this.rand.nextFloat() * 0.2F), (double)(this.rand.nextFloat() * 0.2F), (double)(this.rand.nextFloat() * 0.2F)));
            }
            else
            {
                setMotion(0, 0, 0);
                setStickFacing();
            }
        else
        {

        }

        doBlockCollisions();
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

    public void setStickFacing()
    {

        if(stickFacing.getHorizontalIndex() >= 0)
        {
            rotationYaw = 180- stickFacing.getHorizontalAngle();
            prevRotationYaw = rotationYaw;
        }
        else
        {
            rotationPitch = stickFacing.equals(Direction.UP) ? -90 : 90;
            rotationYaw = prevRotationYaw;
            prevRotationPitch = rotationPitch;
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
            inBlockState = world.getBlockState(result.getPos());

            setActivated(true);

            Vector3d vector3d = result.getHitVec().subtract(this.getPosX(), this.getPosY(), this.getPosZ());
            this.setMotion(vector3d);
            Vector3d vector3d1 = vector3d.normalize().scale((double) 0.05F);
            this.setRawPosition(this.getPosX() - vector3d1.x, this.getPosY() - vector3d1.y, this.getPosZ() - vector3d1.z);

            stickFacing = result.getFace();
            setStickFacing();
        }
    }

    public void setActivated(boolean v)
    {
        dataManager.set(ACTIVATED, v);
    }
    public boolean isActivated() { return dataManager.get(ACTIVATED);}

    @Override
    public void readAdditional(CompoundNBT nbt)
    {
        super.readAdditional(nbt);
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
    public void writeAdditional(CompoundNBT nbt)
    {
        super.writeAdditional(nbt);
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
