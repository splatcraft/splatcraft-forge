package com.cibernet.splatcraft.entities;

import com.cibernet.splatcraft.client.particles.InkExplosionParticleData;
import com.cibernet.splatcraft.client.particles.InkSplashParticleData;
import com.cibernet.splatcraft.handlers.WeaponHandler;
import com.cibernet.splatcraft.registries.SplatcraftEntities;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import com.cibernet.splatcraft.registries.SplatcraftSounds;
import com.cibernet.splatcraft.util.ColorUtils;
import com.cibernet.splatcraft.util.InkBlockUtils;
import com.cibernet.splatcraft.util.InkDamageUtils;
import com.cibernet.splatcraft.util.InkExplosion;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class InkProjectileEntity extends ProjectileItemEntity implements IColoredEntity {

    private static final DataParameter<String> PROJ_TYPE = EntityDataManager.createKey(InkProjectileEntity.class, DataSerializers.STRING);
    private static final DataParameter<Integer> COLOR = EntityDataManager.createKey(InkProjectileEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Float> PROJ_SIZE = EntityDataManager.createKey(InkProjectileEntity.class, DataSerializers.FLOAT);
    private static final DamageSource DAMAGE_SOURCE = new DamageSource("ink");

    public float gravityVelocity = 0.075f;
    public int lifespan = 600;
    public boolean explodes = false;
    public float damage = 0;
    public float splashDamage = 0;
    public boolean damageMobs = false;
    public boolean canPierce = false;
    public boolean persistent = false;
    public ItemStack sourceWeapon = ItemStack.EMPTY;
    public float trailSize;
    public float trailCooldown = 0;
    public InkBlockUtils.InkType inkType;


    public InkProjectileEntity(EntityType<? extends ProjectileItemEntity> type, World world) {
        super(type, world);
    }

    public InkProjectileEntity(World world, LivingEntity thrower, int color, InkBlockUtils.InkType inkType, float size, float damage, ItemStack sourceWeapon) {
        super(SplatcraftEntities.INK_PROJECTILE, thrower, world);
        setColor(color);
        setProjectileSize(size);
        this.damage = damage;
        this.inkType = inkType;
        this.sourceWeapon = sourceWeapon;
    }

    public InkProjectileEntity(World world, LivingEntity thrower, int color, InkBlockUtils.InkType inkType, float size, float damage) {
        this(world, thrower, color, inkType, size, damage, ItemStack.EMPTY);
    }

    public InkProjectileEntity(World world, LivingEntity thrower, ItemStack sourceWeapon, InkBlockUtils.InkType inkType, float size, float damage) {
        this(world, thrower, ColorUtils.getInkColor(sourceWeapon), inkType, size, damage, sourceWeapon);
    }

    public InkProjectileEntity setShooterTrail() {
        trailCooldown = 4;
        trailSize = getProjectileSize() * 0.7f;
        return this;
    }

    public InkProjectileEntity setChargerStats(int lifespan, boolean canPierce) {
        trailSize = getProjectileSize() * 0.85f;
        this.lifespan = lifespan;
        gravityVelocity = 0;
        this.canPierce = canPierce;
        setProjectileType(Types.CHARGER);
        return this;
    }

    public InkProjectileEntity setBlasterStats(int lifespan, float splashDamage) {
        this.lifespan = lifespan;
        this.splashDamage = splashDamage;
        gravityVelocity = 0;
        trailSize = getProjectileSize() * 0.35f;
        explodes = true;
        setProjectileType(Types.BLASTER);
        return this;
    }

    @Override
    protected void registerData() {
        dataManager.register(COLOR, ColorUtils.DEFAULT);
        dataManager.register(PROJ_TYPE, Types.SHOOTER);
        dataManager.register(PROJ_SIZE, 1.0f);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> dataParameter) {
        if (dataParameter.equals(PROJ_SIZE))
            recalculateSize();

        super.notifyDataManagerChange(dataParameter);
    }

    @Override
    protected Item getDefaultItem() {
        return SplatcraftItems.splattershot;
    }

    @Override
    public void tick() {
        super.tick();

        if (!world.isRemote && !persistent && lifespan-- <= 0) {
            InkExplosion.createInkExplosion(world, func_234616_v_(), DAMAGE_SOURCE, getPosition(), getProjectileSize() * 0.85f, damage, splashDamage, damageMobs, getColor(), inkType, sourceWeapon);
            if (explodes) {
                world.setEntityState(this, (byte) 3);
                world.playSound(null, getPosX(), getPosY(), getPosZ(), SplatcraftSounds.blasterExplosion, SoundCategory.PLAYERS, 0.8F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.1F + 1.0F) * 0.95F);
            }
            remove();
        }

        if (trailSize > 0 && (trailCooldown == 0 || ticksExisted % trailCooldown == 0))
            for (double y = getPosY(); y >= 0 && getPosY() - y <= 8; y--) {
                BlockPos inkPos = new BlockPos(getPosX(), y, getPosZ());
                if (!InkBlockUtils.canInkPassthrough(world, inkPos)) {
                    world.setEntityState(this, (byte) 1);
                    InkExplosion.createInkExplosion(world, func_234616_v_(), DAMAGE_SOURCE, inkPos.up(), trailSize, 0, 0, damageMobs, getColor(), inkType, sourceWeapon);
                    InkExplosion.createInkExplosion(world, func_234616_v_(), DAMAGE_SOURCE, getPosition(), trailSize, 0, 0, damageMobs, getColor(), inkType, sourceWeapon);
                    break;
                }
            }

    }

    @Override
    public void handleStatusUpdate(byte id) {
        super.handleStatusUpdate(id);
        switch (id) {
            case 1:
                if (getProjectileType().equals(Types.CHARGER))
                    world.addParticle(new InkSplashParticleData(getColor(), getProjectileSize()), this.getPosX() - this.getMotion().getX() * 0.25D, this.getPosY() - this.getMotion().getY() * 0.25D, this.getPosZ() - this.getMotion().getZ() * 0.25D, 0, -0.1, 0);
                else
                    world.addParticle(new InkSplashParticleData(getColor(), getProjectileSize()), this.getPosX() - this.getMotion().getX() * 0.25D, this.getPosY() - this.getMotion().getY() * 0.25D, this.getPosZ() - this.getMotion().getZ() * 0.25D, this.getMotion().getX(), this.getMotion().getY(), this.getMotion().getZ());
                break;
            case 2:
                world.addParticle(new InkSplashParticleData(getColor(), getProjectileSize() * 2), this.getPosX(), this.getPosY(), this.getPosZ(), 0, 0, 0);
                break;
            case 3:
                world.addParticle(new InkExplosionParticleData(getColor(), getProjectileSize() * 2), this.getPosX(), this.getPosY(), this.getPosZ(), 0, 0, 0);
                break;
        }

    }

    @Override
    protected void onEntityHit(EntityRayTraceResult result) {
        super.onEntityHit(result);

        Entity target = result.getEntity();

        if (target instanceof LivingEntity)
            InkDamageUtils.doSplatDamage(world, (LivingEntity) target, damage, getColor(), func_234616_v_(), sourceWeapon, damageMobs, inkType);

        if (!canPierce) {
            if (explodes) {
                InkExplosion.createInkExplosion(world, func_234616_v_(), DAMAGE_SOURCE, getPosition(), getProjectileSize() * 0.85f, damage, splashDamage, damageMobs, getColor(), inkType, sourceWeapon);
                world.setEntityState(this, (byte) 3);
                world.playSound(null, getPosX(), getPosY(), getPosZ(), SplatcraftSounds.blasterExplosion, SoundCategory.PLAYERS, 0.8F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.1F + 1.0F) * 0.95F);
            } else world.setEntityState(this, (byte) 2);

            remove();
        }
    }

    protected void onBlockHit(BlockRayTraceResult result) {
        if (InkBlockUtils.canInkPassthrough(world, result.getPos()))
            return;

        this.func_230299_a_(result);

        InkExplosion.createInkExplosion(world, func_234616_v_(), DAMAGE_SOURCE, getPosition(), getProjectileSize() * 0.85f, damage, splashDamage, damageMobs, getColor(), inkType, sourceWeapon);
        if (explodes) {
            world.setEntityState(this, (byte) 3);
            world.playSound(null, getPosX(), getPosY(), getPosZ(), SplatcraftSounds.blasterExplosion, SoundCategory.PLAYERS, 0.8F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.1F + 1.0F) * 0.95F);
        } else world.setEntityState(this, (byte) 2);
        if (!world.isRemote)
            this.remove();
    }

    public void shoot(Entity thrower, float pitch, float yaw, float pitchOffset, float velocity, float inaccuracy) {
        func_234612_a_(thrower, pitch, yaw, pitchOffset, velocity, inaccuracy);

        Vector3d posDiff = new Vector3d(0, 0, 0);

        if (thrower instanceof PlayerEntity) try {
            posDiff = thrower.getPositionVec().subtract(WeaponHandler.getPlayerPrevPos((PlayerEntity) thrower));
        } catch (NullPointerException ignored) {}


        setPositionAndUpdate(getPosX() + posDiff.getX(), getPosY() + posDiff.getY(), getPosZ() + posDiff.getZ());
        setMotion(getMotion().add(posDiff.mul(0.8, 0.8, 0.8)));
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        RayTraceResult.Type rayType = result.getType();
        if (rayType == RayTraceResult.Type.ENTITY)
            this.onEntityHit((EntityRayTraceResult) result);
        else if (rayType == RayTraceResult.Type.BLOCK)
            onBlockHit((BlockRayTraceResult) result);

    }

    @Override
    public void readAdditional(CompoundNBT nbt) {
        setProjectileSize(nbt.getFloat("Size"));
        setColor(nbt.getInt("Color"));

        gravityVelocity = nbt.getFloat("GravityVelocity");
        lifespan = nbt.getInt("Lifespan");
        damage = nbt.getFloat("Damage");
        splashDamage = nbt.getFloat("SplashDamage");
        damageMobs = nbt.getBoolean("DamageMobs");
        canPierce = nbt.getBoolean("CanPierce");
        explodes = nbt.getBoolean("Explodes");
        persistent = nbt.getBoolean("Persistent");

        String type = nbt.getString("ProjectileType");
        setProjectileType(type.isEmpty() ? Types.DEFAULT : type);
        inkType = InkBlockUtils.InkType.values.get(nbt.getInt("InkType"));

        sourceWeapon = ItemStack.read(nbt.getCompound("SourceWeapon"));
    }

    @Override
    public void writeAdditional(CompoundNBT nbt) {
        nbt.putFloat("Size", getProjectileSize());
        nbt.putInt("Color", getColor());

        nbt.putFloat("GravityVelocity", gravityVelocity);
        nbt.putInt("Lifespan", lifespan);
        nbt.putFloat("Damage", damage);
        nbt.putFloat("SplashDamage", splashDamage);
        nbt.putBoolean("DamageMobs", damageMobs);
        nbt.putBoolean("CanPierce", canPierce);
        nbt.putBoolean("Explodes", explodes);
        nbt.putBoolean("Persistent", persistent);

        nbt.putString("ProjectileType", getProjectileType());
        nbt.putInt("InkType", inkType.getIndex());
        nbt.put("SourceWeapon", sourceWeapon.write(new CompoundNBT()));
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public EntitySize getSize(Pose pose) {
        return super.getSize(pose).scale(getProjectileSize() / 2f);
    }

    @Override
    public float getGravityVelocity() {
        return gravityVelocity;
    }

    public float getProjectileSize() {
        return dataManager.get(PROJ_SIZE);
    }

    public void setProjectileSize(float size) {
        dataManager.set(PROJ_SIZE, size);
        this.recenterBoundingBox();
        this.recalculateSize();
    }

    @Override
    public int getColor() {
        return dataManager.get(COLOR);
    }

    @Override
    public void setColor(int color) {
        dataManager.set(COLOR, color);
    }

    public String getProjectileType() {
        return dataManager.get(PROJ_TYPE);
    }

    public void setProjectileType(String v) {
        dataManager.set(PROJ_TYPE, v);
    }

    public static class Types {
        public static final String DEFAULT = "ink";
        public static final String SHOOTER = "shooter";
        public static final String CHARGER = "charger";
        public static final String ROLLER = "roller";
        public static final String BLASTER = "blaster";
    }
}
