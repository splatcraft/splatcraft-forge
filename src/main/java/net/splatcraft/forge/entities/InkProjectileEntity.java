package net.splatcraft.forge.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.splatcraft.forge.blocks.ColoredBarrierBlock;
import net.splatcraft.forge.blocks.StageBarrierBlock;
import net.splatcraft.forge.client.particles.InkExplosionParticleData;
import net.splatcraft.forge.client.particles.InkSplashParticleData;
import net.splatcraft.forge.handlers.DataHandler;
import net.splatcraft.forge.handlers.WeaponHandler;
import net.splatcraft.forge.items.weapons.SplatlingItem;
import net.splatcraft.forge.items.weapons.WeaponBaseItem;
import net.splatcraft.forge.items.weapons.settings.*;
import net.splatcraft.forge.registries.SplatcraftEntities;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.InkBlockUtils;
import net.splatcraft.forge.util.InkDamageUtils;
import net.splatcraft.forge.util.InkExplosion;

public class InkProjectileEntity extends ThrowableItemProjectile implements IColoredEntity {

    private static final EntityDataAccessor<String> PROJ_TYPE = SynchedEntityData.defineId(InkProjectileEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(InkProjectileEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> PROJ_SIZE = SynchedEntityData.defineId(InkProjectileEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> GRAVITY = SynchedEntityData.defineId(InkProjectileEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> STRAIGHT_SHOT_TIME = SynchedEntityData.defineId(InkProjectileEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> MAX_VELOCITY = SynchedEntityData.defineId(InkProjectileEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> MIN_VELOCITY = SynchedEntityData.defineId(InkProjectileEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Vec3> SHOOT_DIRECTION = SynchedEntityData.defineId(InkProjectileEntity.class, new EntityDataSerializer<Vec3>()
    {
        @Override
        public void write(FriendlyByteBuf buf, Vec3 shootVelocity)
        {
            buf.writeDouble(shootVelocity.x);
            buf.writeDouble(shootVelocity.y);
            buf.writeDouble(shootVelocity.z);
        }

        @Override
        public Vec3 read(FriendlyByteBuf buf)
        {
            return new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        }

        @Override
        public Vec3 copy(Vec3 shootVelocity)
        {
            return new Vec3(shootVelocity.x, shootVelocity.y, shootVelocity.z);
        }
    });

    protected int straightShotTime = -1;
    public int lifespan = 600;
    public boolean explodes = false;
    public boolean bypassMobDamageMultiplier = false;
    public boolean canPierce = false;
    public boolean persistent = false;
    public ItemStack sourceWeapon = ItemStack.EMPTY;
    public float impactCoverage;
    public float trailSize = 0;
    public int trailCooldown = 0;
    public String damageType = "splat";
    public boolean causesHurtCooldown = false;
    public boolean throwerAirborne = false;
    public float charge;
    public boolean isOnRollCooldown = false;

    public AbstractWeaponSettings damage = ShooterWeaponSettings.DEFAULT;
    public InkBlockUtils.InkType inkType;

    public InkProjectileEntity(EntityType<InkProjectileEntity> type, Level level) {
        super(type, level);
    }

    public InkProjectileEntity(Level level, LivingEntity thrower, int color, InkBlockUtils.InkType inkType, float projectileSize, AbstractWeaponSettings damage, ItemStack sourceWeapon) {
        super(SplatcraftEntities.INK_PROJECTILE.get(), thrower, level);
        setColor(color);
        setProjectileSize(projectileSize);
        this.impactCoverage = projectileSize * 0.85f;
        this.throwerAirborne = !thrower.isOnGround();
        this.damage = damage;
        this.inkType = inkType;
        this.sourceWeapon = sourceWeapon;
    }

    public InkProjectileEntity(Level level, LivingEntity thrower, int color, InkBlockUtils.InkType inkType, float projectileSize, AbstractWeaponSettings damage) {
        this(level, thrower, color, inkType, projectileSize, damage, ItemStack.EMPTY);
    }

    public InkProjectileEntity(Level level, LivingEntity thrower, ItemStack sourceWeapon, InkBlockUtils.InkType inkType, float projectileSize, AbstractWeaponSettings damage) {
        this(level, thrower, ColorUtils.getInkColor(sourceWeapon), inkType, projectileSize, damage, sourceWeapon);
    }

    public static void registerDataAccessors()
    {
        EntityDataSerializers.registerSerializer(SHOOT_DIRECTION.getSerializer());
    }

    public InkProjectileEntity setShooterTrail() {
        trailCooldown = 4;
        trailSize = getProjectileSize() * 0.75f;
        return this;
    }

    public InkProjectileEntity setChargerStats(float charge, ChargerWeaponSettings settings)
    {
        this.charge = charge;
        trailSize = settings.projectileInkTrailCoverage;
        trailCooldown = settings.projectileInkTrailCooldown;
        lifespan = (int) (settings.minProjectileLifeTicks + (settings.maxProjectileLifeTicks - settings.minProjectileLifeTicks) * charge);
        impactCoverage = settings.projectileInkCoverage;

        setGravity(0);
        this.canPierce = charge >= settings.piercesAtCharge;
        setProjectileType(Types.CHARGER);
        return this;
    }

    public InkProjectileEntity setBlasterStats(BlasterWeaponSettings settings) {
        this.lifespan = settings.projectileLifeTicks;
        setGravity(0);
        trailSize = settings.projectileInkTrailCoverage;
        trailCooldown = settings.projectileInkTrailCooldown;
        impactCoverage = settings.projectileExplosionRadius;

        explodes = true;
        setProjectileType(Types.BLASTER);
        return this;
    }

    public InkProjectileEntity setSlosherStats(SlosherWeaponSettings settings)
    {
        trailSize = settings.projectileInkTrailCoverage;
        trailCooldown = settings.projectileInkTrailCooldown;
        impactCoverage = settings.projectileInkCoverage;

        setProjectileType(Types.SHOOTER);
        return this;
    }

    public InkProjectileEntity setShooterStats(ShooterWeaponSettings settings)
    {
        trailSize = settings.projectileInkTrailCoverage;
        trailCooldown = settings.projectileInkTrailCooldown;
        impactCoverage = settings.projectileInkCoverage;

        setGravity(settings.projectileGravity);
        setStraightShotTime(settings.straightShotTickTime);

        lifespan = settings.projectileLifeTicks;
        entityData.set(MAX_VELOCITY, settings.projectileSpeed);
        entityData.set(MIN_VELOCITY, settings.projectileDecayedSpeed);
        setProjectileType(Types.SHOOTER);
        return this;
    }

    public InkProjectileEntity setSplatlingStats(SplatlingWeaponSettings settings, float charge)
    {
        SplatlingWeaponSettings.FiringData firingData =  charge > 1 ? settings.secondChargeLevelData : settings.firstChargeLevelData;

        trailSize = firingData.projectileInkTrailCoverage;
        trailCooldown = firingData.projectileInkTrailCooldown;
        impactCoverage = firingData.projectileInkCoverage;

        setGravity(SplatlingItem.getScaledSettingFloat(settings, charge, SplatlingWeaponSettings.FiringData::getProjectileGravity));
        setStraightShotTime(SplatlingItem.getScaledSettingInt(settings, charge, SplatlingWeaponSettings.FiringData::getStraightShotTickTime));

        lifespan = SplatlingItem.getScaledSettingInt(settings, charge, SplatlingWeaponSettings.FiringData::getProjectileLifeTicks);
        entityData.set(MAX_VELOCITY, SplatlingItem.getScaledSettingFloat(settings, charge, SplatlingWeaponSettings.FiringData::getProjectileSpeed));
        entityData.set(MIN_VELOCITY, SplatlingItem.getScaledSettingFloat(settings, charge, SplatlingWeaponSettings.FiringData::getProjectileDecayedSpeed));
        setProjectileType(Types.SHOOTER);
        return this;
    }

    public InkProjectileEntity setDualieStats(DualieWeaponSettings.FiringData firingData)
    {
        trailSize = firingData.projectileInkTrailCoverage;
        trailCooldown = firingData.projectileInkTrailCooldown;
        impactCoverage = firingData.projectileInkCoverage;

        setGravity(firingData.projectileGravity);
        setStraightShotTime(firingData.straightShotTickTime);

        lifespan = firingData.projectileLifeTicks;
        entityData.set(MAX_VELOCITY, firingData.projectileSpeed);
        entityData.set(MIN_VELOCITY, firingData.projectileDecayedSpeed);
        setProjectileType(Types.SHOOTER);
        return this;
    }

    public InkProjectileEntity setRollerSwingStats() {
        setProjectileType(Types.ROLLER);

        if (throwerAirborne) {
            trailSize = getProjectileSize() * 0.5f;
        }
        return this;
    }

    @Override
    protected void defineSynchedData()
    {
        entityData.define(COLOR, ColorUtils.DEFAULT);
        entityData.define(PROJ_TYPE, Types.SHOOTER);
        entityData.define(PROJ_SIZE, 1.0f);
        entityData.define(GRAVITY, 0.075f);
        entityData.define(STRAIGHT_SHOT_TIME, 0);
        entityData.define(MAX_VELOCITY, 0f);
        entityData.define(MIN_VELOCITY, 0f);
        entityData.define(SHOOT_DIRECTION, new Vec3(0,0,0));
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> dataParameter) {
        if (dataParameter.equals(PROJ_SIZE))
            refreshDimensions();
        else if(dataParameter.equals(STRAIGHT_SHOT_TIME))
            straightShotTime = entityData.get(STRAIGHT_SHOT_TIME);

        super.onSyncedDataUpdated(dataParameter);
    }

    @Override
    protected Item getDefaultItem() {
        return SplatcraftItems.splattershot.get();
    }

    @Override
    public void tick()
    {

        setDeltaMovement(getDeltaMovement().add(getShootVelocity()));
        super.tick();
        setDeltaMovement(getDeltaMovement().subtract(getShootVelocity().scale(0.99)));

        if(straightShotTime >= 0)
            straightShotTime--;

        if (isInWater()) {
            discard();
            return;
        }

        if(isRemoved())
            return;

        if (!level.isClientSide && !persistent && lifespan-- <= 0)
        {
            float dmg = damage.calculateDamage(this.tickCount - Math.max(0, straightShotTime), throwerAirborne, charge, isOnRollCooldown);
            InkExplosion.createInkExplosion(level, getOwner(), blockPosition(), impactCoverage, dmg, explodes ? damage.getMinDamage() : dmg, bypassMobDamageMultiplier, getColor(), inkType, sourceWeapon);
            if (explodes) {
                level.broadcastEntityEvent(this, (byte) 3);
                level.playSound(null, getX(), getY(), getZ(), SplatcraftSounds.blasterExplosion, SoundSource.PLAYERS, 0.8F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
            }
            discard();
        }
        else if (trailSize > 0 && (trailCooldown == 0 || tickCount % trailCooldown == 0))
        {
            if (!isInvisible())
                level.broadcastEntityEvent(this, (byte) 1);
            InkExplosion.createInkExplosion(level, getOwner(), blockPosition(), trailSize, 0, 0, bypassMobDamageMultiplier, getColor(), inkType, sourceWeapon);

            for (int y = getBlockY(); getBlockY() - y <= 8; y--)
            {
                BlockPos inkPos = new BlockPos(getBlockX(), y, getBlockZ());
                if(!level.isInWorldBounds(inkPos))
                    break;

                if (!InkBlockUtils.canInkPassthrough(level, inkPos))
                {
                    InkExplosion.createInkExplosion(level, getOwner(), inkPos.relative(Direction.UP), trailSize, 0, 0, bypassMobDamageMultiplier, getColor(), inkType, sourceWeapon);
                    break;
                }
            }
        }

    }

    private Vec3 getShootVelocity()
    {
        double minVelocity = entityData.get(MIN_VELOCITY);
        double maxVelocity = entityData.get(MAX_VELOCITY);
        double lerpedVelocity = straightShotTime <= 0 ? minVelocity : minVelocity +  (maxVelocity - minVelocity) * ((double) straightShotTime / entityData.get(STRAIGHT_SHOT_TIME));
        return entityData.get(SHOOT_DIRECTION).scale(lerpedVelocity);
    }

    @Override
    protected void updateRotation()
    {
        Vec3 motion = getShootVelocity().add(getDeltaMovement());

        if(!Vec3.ZERO.equals(motion))
        {
            this.setXRot(lerpRotation(this.xRotO, (float) (Mth.atan2(motion.y, motion.horizontalDistance()) * (double) (180F / (float) Math.PI))));
            this.setYRot(lerpRotation(this.yRotO, (float) (Mth.atan2(motion.x, motion.z) * (double) (180F / (float) Math.PI))));
        }

    }

    @Override
    public void handleEntityEvent(byte id) {
        super.handleEntityEvent(id);

        switch (id) {
            case -1 ->
                    level.addParticle(new InkExplosionParticleData(getColor(), .5f), this.getX(), this.getY(), this.getZ(), 0, 0, 0);
            case 1 -> {
                if (getProjectileType().equals(Types.CHARGER))
                    level.addParticle(new InkSplashParticleData(getColor(), getProjectileSize()), this.getX() - this.getDeltaMovement().x() * 0.25D, this.getY() + getBbHeight() * 0.5f - this.getDeltaMovement().y() * 0.25D, this.getZ() - this.getDeltaMovement().z() * 0.25D, 0, -0.1, 0);
                else
                    level.addParticle(new InkSplashParticleData(getColor(), getProjectileSize()), this.getX() - this.getDeltaMovement().x() * 0.25D, this.getY() + getBbHeight() * 0.5f - this.getDeltaMovement().y() * 0.25D, this.getZ() - this.getDeltaMovement().z() * 0.25D, this.getDeltaMovement().x(), this.getDeltaMovement().y(), this.getDeltaMovement().z());
            }
            case 2 ->
                    level.addParticle(new InkSplashParticleData(getColor(), getProjectileSize() * 2), this.getX(), this.getY(), this.getZ(), 0, 0, 0);
            case 3 ->
                    level.addParticle(new InkExplosionParticleData(getColor(), getProjectileSize() * 2), this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }

    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);

        Entity target = result.getEntity();
        float dmg = damage.calculateDamage(this.tickCount - Math.max(0, straightShotTime), throwerAirborne, charge, isOnRollCooldown);

        if (!level.isClientSide() && target instanceof SpawnShieldEntity && !InkDamageUtils.canDamage(target, this)) {
            discard();
            level.broadcastEntityEvent(this, (byte) -1);
        }

        if (target instanceof LivingEntity livingTarget) {
            if (InkDamageUtils.isSplatted(livingTarget)) return;

            if (InkDamageUtils.doDamage(level, livingTarget, dmg, getColor(), getOwner(), this, sourceWeapon, bypassMobDamageMultiplier, damageType, causesHurtCooldown) &&
                    InkDamageUtils.isSplatted(livingTarget) && charge >= 1.0f && getOwner() instanceof ServerPlayer)
                ((ServerPlayer) getOwner()).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
        }

        if (!canPierce) {
            if (explodes) {
                InkExplosion.createInkExplosion(level, getOwner(), blockPosition(), impactCoverage, damage.getMinDamage(), dmg, bypassMobDamageMultiplier, getColor(), inkType, sourceWeapon);
                level.broadcastEntityEvent(this, (byte) 3);
                level.playSound(null, getX(), getY(), getZ(), SplatcraftSounds.blasterExplosion, SoundSource.PLAYERS, 0.8F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
            } else
                level.broadcastEntityEvent(this, (byte) 2);

            if (!level.isClientSide)
                discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (InkBlockUtils.canInkPassthrough(level, result.getBlockPos()))
            return;

        if (level.getBlockState(result.getBlockPos()).getBlock() instanceof ColoredBarrierBlock &&
                ((ColoredBarrierBlock) level.getBlockState(result.getBlockPos()).getBlock()).canAllowThrough(result.getBlockPos(), this))
            return;

        super.onHitBlock(result);

        float dmg = damage.calculateDamage(this.tickCount - Math.max(0, straightShotTime), throwerAirborne, charge, isOnRollCooldown);
        InkExplosion.createInkExplosion(level, getOwner(), blockPosition(), impactCoverage, explodes ? damage.getMinDamage() : 0, dmg, bypassMobDamageMultiplier, getColor(), inkType, sourceWeapon);
        if (explodes) {
            level.broadcastEntityEvent(this, (byte) 3);
            level.playSound(null, getX(), getY(), getZ(), SplatcraftSounds.blasterExplosion, SoundSource.PLAYERS, 0.8F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
        } else if (level.getBlockState(result.getBlockPos()).getBlock() instanceof StageBarrierBlock)
            level.broadcastEntityEvent(this, (byte) -1);
        else level.broadcastEntityEvent(this, (byte) 2);
        if (!level.isClientSide)
            this.discard();
    }

    @Override
    public void shootFromRotation(Entity thrower, float pitch, float yaw, float pitchOffset, float velocity, float inaccuracy)
    {
        super.shootFromRotation(thrower, pitch, yaw, pitchOffset, velocity, inaccuracy);
        InkExplosion.createInkExplosion(level, getOwner(), thrower.blockPosition(), 0.75f, 0, 0, bypassMobDamageMultiplier, getColor(), inkType, sourceWeapon);

        Vec3 posDiff = new Vec3(0, 0, 0);

        if (thrower instanceof Player player)
        {
            if(WeaponHandler.playerHasPrevPos(player))
                posDiff = thrower.position().subtract(WeaponHandler.getPlayerPrevPos(player));
            if (thrower.isOnGround())
                posDiff.multiply(1, 0, 1);
        }


        moveTo(getX() + posDiff.x(), getY() + posDiff.y(), getZ() + posDiff.z());
        Vec3 throwerSpeed = thrower.getDeltaMovement();
        Vec3 speed = getDeltaMovement()
                .subtract(throwerSpeed.x, thrower.isOnGround() ? 0.0 : throwerSpeed.y, throwerSpeed.z)
                .add(Math.min(2.5, throwerSpeed.x * 0.8), 0.0, Math.min(2.5, throwerSpeed.z * 0.8))
                .add(posDiff.multiply(0.8, 0.8, 0.8));
        setDeltaMovement(speed);
    }


    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy)
    {
        Vec3 vec3 = (new Vec3(x, y, z)).normalize().add(this.random.nextGaussian() * (double)0.0075F * (double)inaccuracy, this.random.nextGaussian() * (double)0.0075F * (double)inaccuracy, this.random.nextGaussian() * (double)0.0075F * (double)inaccuracy);

        entityData.set(SHOOT_DIRECTION, vec3.normalize());

        double d0 = vec3.horizontalDistance();
        this.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * (double)(180F / (float)Math.PI)));
        this.setXRot((float)(Mth.atan2(vec3.y, d0) * (double)(180F / (float)Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();

        entityData.set(MIN_VELOCITY, velocity);
        entityData.set(MAX_VELOCITY, velocity);
    }

    @Override
    protected void onHit(HitResult result) {
        HitResult.Type rayType = result.getType();
        if (rayType == HitResult.Type.ENTITY) {
            this.onHitEntity((EntityHitResult) result);
        } else if (rayType == HitResult.Type.BLOCK) {
            onHitBlock((BlockHitResult) result);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt)
    {
        super.readAdditionalSaveData(nbt);

        if (nbt.contains("Size"))
            setProjectileSize(nbt.getFloat("Size"));

        impactCoverage = nbt.contains("ImpactCoverage") ? nbt.getFloat("ImpactCoverage") : getProjectileSize() * 0.85f;

        if (nbt.contains("Color"))
            setColor(ColorUtils.getColorFromNbt(nbt));

        entityData.set(MIN_VELOCITY, nbt.getFloat("MinVelocity"));
        entityData.set(MAX_VELOCITY, nbt.getFloat("MaxVelocity"));

        if (nbt.contains("Gravity"))
            setGravity(nbt.getFloat("Gravity"));
        if (nbt.contains("Lifespan"))
            lifespan = nbt.getInt("Lifespan");
        if (nbt.contains("StraightShotTime"))
            setStraightShotTime(nbt.getInt("StraightShotTime"));
        if (nbt.contains("MaxStraightShotTime"))
            straightShotTime = (nbt.getInt("StraightShotTime"));

        ListTag directionTag = nbt.getList("Direction", DoubleTag.TAG_DOUBLE);
        entityData.set(SHOOT_DIRECTION, new Vec3(directionTag.getDouble(0),directionTag.getDouble(1), directionTag.getDouble(2)));

        trailCooldown = nbt.getInt("TrailCooldown");
        trailSize = nbt.getFloat("TrailSize");
        bypassMobDamageMultiplier = nbt.getBoolean("BypassMobDamageMultiplier");
        canPierce = nbt.getBoolean("CanPierce");
        explodes = nbt.getBoolean("Explodes");
        persistent = nbt.getBoolean("Persistent");
        causesHurtCooldown = nbt.getBoolean("CausesHurtCooldown");
        damageType = nbt.getString("DamageType");

        setInvisible(nbt.getBoolean("Invisible"));

        String type = nbt.getString("ProjectileType");
        setProjectileType(type.isEmpty() ? Types.DEFAULT : type);
        inkType = InkBlockUtils.InkType.values.getOrDefault(new ResourceLocation(nbt.getString("InkType")), InkBlockUtils.InkType.NORMAL);

        sourceWeapon = ItemStack.of(nbt.getCompound("SourceWeapon"));

        AbstractWeaponSettings<?, ?> settings = DataHandler.WeaponStatsListener.SETTINGS.get(new ResourceLocation(nbt.getString(nbt.getString("Settings"))));
        if(settings != null)
            damage = settings;
        else if (sourceWeapon.getItem() instanceof WeaponBaseItem<?> weapon)
            damage = weapon.getSettings(sourceWeapon);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putFloat("Size", getProjectileSize());
        nbt.putInt("Color", getColor());

        nbt.putFloat("MinVelocity", entityData.get(MIN_VELOCITY));
        nbt.putFloat("MaxVelocity", entityData.get(MAX_VELOCITY));
        nbt.putInt("MaxStraightShotTime", getMaxStraightShotTime());
        nbt.putInt("StraightShotTime", straightShotTime);

        ListTag directionTag = new ListTag();
        Vec3 direction = getShotDirection();
        directionTag.add(DoubleTag.valueOf(direction.x));
        directionTag.add(DoubleTag.valueOf(direction.y));
        directionTag.add(DoubleTag.valueOf(direction.z));
        nbt.put("Direction", directionTag);

        nbt.putFloat("Gravity", getGravity());
        nbt.putInt("Lifespan", lifespan);
        nbt.putFloat("TrailSize", trailSize);
        nbt.putInt("TrailCooldown", trailCooldown);
        nbt.putBoolean("BypassMobDamageMultiplier", bypassMobDamageMultiplier);
        nbt.putBoolean("CanPierce", canPierce);
        nbt.putBoolean("Explodes", explodes);
        nbt.putBoolean("Persistent", persistent);
        nbt.putBoolean("CausesHurtCooldown", causesHurtCooldown);

        nbt.putBoolean("Invisible", isInvisible());

        nbt.putString("DamageType", damageType);
        nbt.putString("ProjectileType", getProjectileType());
        nbt.putString("InkType", inkType.getSerializedName());
        nbt.put("SourceWeapon", sourceWeapon.save(new CompoundTag()));

        super.addAdditionalSaveData(nbt);
        nbt.remove("Item");
    }

    @Deprecated //Modify sourceWeapon variable instead
    @Override
    public void setItem(ItemStack itemStack) {}

    @Override
    protected ItemStack getItemRaw() {
        return sourceWeapon;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return super.getDimensions(pose).scale(getProjectileSize() / 2f);
    }

    @Override
    public float getGravity() {
        return entityData.get(GRAVITY);
    }

    public void setGravity(float gravity) {
        entityData.set(GRAVITY, gravity);
    }

    public int getStraightShotTime() {
        return straightShotTime;
    }

    public int getMaxStraightShotTime()
    {
        return entityData.get(STRAIGHT_SHOT_TIME);
    }

    public void setStraightShotTime(int time)
    {
        entityData.set(STRAIGHT_SHOT_TIME, time);
    }

    public Vec3 getShotDirection()
    {
        return entityData.get(SHOOT_DIRECTION);
    }

    @Override
    public boolean isNoGravity() {
        return straightShotTime > 0 || getGravity() == 0 || super.isNoGravity();
    }

    public float getProjectileSize() {
        return entityData.get(PROJ_SIZE);
    }

    public void setProjectileSize(float size) {
        entityData.set(PROJ_SIZE, size);
        reapplyPosition();
        refreshDimensions();
    }

    @Override
    public ItemStack getItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public int getColor() {
        return entityData.get(COLOR);
    }

    @Override
    public void setColor(int color) {
        entityData.set(COLOR, color);
    }

    public String getProjectileType() {
        return entityData.get(PROJ_TYPE);
    }

    public void setProjectileType(String v) {
        entityData.set(PROJ_TYPE, v);
    }

    public static class Types {
        public static final String DEFAULT = "default";
        public static final String SHOOTER = "shooter";
        public static final String CHARGER = "charger";
        public static final String ROLLER = "roller";
        public static final String BLASTER = "blaster";
    }
}
