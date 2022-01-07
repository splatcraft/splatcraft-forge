package com.cibernet.splatcraft.entities.subs;

import com.cibernet.splatcraft.entities.IColoredEntity;
import com.cibernet.splatcraft.handlers.WeaponHandler;
import com.cibernet.splatcraft.util.ColorUtils;
import com.cibernet.splatcraft.util.InkBlockUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;

public abstract class AbstractSubWeaponEntity extends ProjectileItemEntity implements IColoredEntity
{
    protected static final String SPLASH_DAMAGE_TYPE = "ink";
    protected static final DamageSource SPLASH_DAMAGE_SOURCE = new DamageSource(SPLASH_DAMAGE_TYPE);

    private static final DataParameter<Integer> COLOR = EntityDataManager.defineId(AbstractSubWeaponEntity.class, DataSerializers.INT);

    public boolean damageMobs = false;
    public InkBlockUtils.InkType inkType;
    public ItemStack sourceWeapon = ItemStack.EMPTY;

    @Deprecated //use AbstractWeaponEntity.create
    public AbstractSubWeaponEntity(EntityType<? extends AbstractSubWeaponEntity> type, World level)
    {
        super(type, level);
    }

    public static <A extends AbstractSubWeaponEntity> A create(EntityType<A> type, World level, @Nonnull LivingEntity thrower, ItemStack sourceWeapon)
    {
        return create(type, level, thrower, ColorUtils.getInkColor(sourceWeapon), InkBlockUtils.getInkType(thrower), sourceWeapon);
    }

    public static <A extends AbstractSubWeaponEntity> A create(EntityType<A> type, World level, LivingEntity thrower, int color, InkBlockUtils.InkType inkType, ItemStack sourceWeapon)
    {
        A result = create(type, level, thrower.getX(), thrower.getEyeY() - (double)0.1F, thrower.getZ(), color, inkType, sourceWeapon);
        result.setOwner(thrower);

        return result;
    }
    public static <A extends AbstractSubWeaponEntity> A create(EntityType<A> type, World level, double x, double y, double z, int color, InkBlockUtils.InkType inkType, ItemStack sourceWeapon)
    {
        A result = type.create(level);
        result.setPos(x, y, z);
        result.setColor(color);
        result.inkType = inkType;
        result.sourceWeapon = sourceWeapon;

        return result;
    }

    @Override
    public void tick() {
        super.tick();

        Vector3d motion = this.getDeltaMovement();
    }

    public void shoot(Entity thrower, float pitch, float yaw, float pitchOffset, float velocity, float inaccuracy)
    {
        super.shootFromRotation(thrower, pitch, yaw, pitchOffset, velocity, inaccuracy);

        Vector3d posDiff = new Vector3d(0, 0, 0);

        if (thrower instanceof PlayerEntity)
        {
            try
            {
                posDiff = thrower.position().subtract(WeaponHandler.getPlayerPrevPos((PlayerEntity) thrower));
                if(thrower.isOnGround())
                    posDiff.multiply(1, 0, 1);

            } catch (NullPointerException ignored)
            {
            }
        }


        moveTo(getX() + posDiff.x(), getY() + posDiff.y(), getZ() + posDiff.z());
        setDeltaMovement(getDeltaMovement().add(posDiff.multiply(0.8, 0.8, 0.8)));
    }

    @Override
    protected float getGravity() {
        return 0.09f;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt)
    {
        nbt.putInt("Color", getColor());
        nbt.putBoolean("DamageMobs", damageMobs);
        nbt.putString("InkType", inkType.getSerializedName());
        nbt.put("SourceWeapon", sourceWeapon.save(new CompoundNBT()));
    }


    @Override
    public void readAdditionalSaveData(CompoundNBT nbt)
    {
        if(nbt.contains("Color"))
            setColor(ColorUtils.getColorFromNbt(nbt));
        damageMobs = nbt.getBoolean("DamageMobs");
        inkType = InkBlockUtils.InkType.values.getOrDefault(new ResourceLocation(nbt.getString("InkType")), InkBlockUtils.InkType.NORMAL);
        sourceWeapon = ItemStack.of(nbt.getCompound("SourceWeapon"));
    }


    protected void onBlockHit(BlockRayTraceResult result) { }

    @Override
    protected void onHit(RayTraceResult result)
    {
        RayTraceResult.Type rayType = result.getType();
        if (rayType == RayTraceResult.Type.ENTITY)
        {
            this.onHitEntity((EntityRayTraceResult) result);
        } else if (rayType == RayTraceResult.Type.BLOCK)
        {
            onBlockHit((BlockRayTraceResult) result);
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(COLOR, ColorUtils.DEFAULT);
    }

    @Override
    public int getColor() {
        return entityData.get(COLOR);
    }

    @Override
    public void setColor(int color)
    {
        entityData.set(COLOR, color);
    }


    @Override
    public IPacket<?> getAddEntityPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
