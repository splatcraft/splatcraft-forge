package net.splatcraft.forge.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
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
import net.splatcraft.forge.handlers.WeaponHandler;
import net.splatcraft.forge.items.weapons.WeaponBaseItem;
import net.splatcraft.forge.items.weapons.settings.AbstractWeaponSettings;
import net.splatcraft.forge.items.weapons.settings.WeaponSettings;
import net.splatcraft.forge.registries.SplatcraftEntities;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.InkBlockUtils;
import net.splatcraft.forge.util.InkDamageUtils;
import net.splatcraft.forge.util.InkExplosion;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class InkProjectileEntity extends ThrowableItemProjectile implements IColoredEntity, IAnimatable {

    private static final EntityDataAccessor<String> PROJ_TYPE = SynchedEntityData.defineId(InkProjectileEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(InkProjectileEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> PROJ_SIZE = SynchedEntityData.defineId(InkProjectileEntity.class, EntityDataSerializers.FLOAT);

    public float gravityVelocity = 0.075f;
    public int lifespan = 600;
    public boolean explodes = false;
    public boolean bypassMobDamageMultiplier = false;
    public boolean canPierce = false;
    public boolean persistent = false;
    public ItemStack sourceWeapon = ItemStack.EMPTY;
    public float trailSize = 0;
    public int trailCooldown = 0;
    public String damageType = "splat";
    public boolean causesHurtCooldown = false;
    public boolean throwerAirborne = false;
    public float charge;
    public boolean isOnRollCooldown = false;

    public AbstractWeaponSettings damage = WeaponSettings.DEFAULT;
    public InkBlockUtils.InkType inkType;

    private final AnimationFactory animationFactory = GeckoLibUtil.createFactory(this);


    public InkProjectileEntity(EntityType<InkProjectileEntity> type, Level level) {
        super(type, level);
    }

    public InkProjectileEntity(Level level, LivingEntity thrower, int color, InkBlockUtils.InkType inkType, float projectileSize, AbstractWeaponSettings damage, ItemStack sourceWeapon) {
        super(SplatcraftEntities.INK_PROJECTILE.get(), thrower, level);
        setColor(color);
        setProjectileSize(projectileSize);
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

    public InkProjectileEntity setShooterTrail() {
        trailCooldown = 4;
        trailSize = getProjectileSize() * 0.75f;
        return this;
    }

    public InkProjectileEntity setChargerStats(float charge, int lifespan, boolean canPierce) {
        this.charge = charge;
        trailSize = getProjectileSize() * 1.1f;
        this.lifespan = lifespan;
        gravityVelocity = 0;
        this.canPierce = canPierce;
        setProjectileType(Types.CHARGER);
        return this;
    }

    public InkProjectileEntity setBlasterStats(int lifespan) {
        this.lifespan = lifespan;
        gravityVelocity = 0;
        trailSize = getProjectileSize() * 0.5f;
        explodes = true;
        setProjectileType(Types.BLASTER);
        return this;
    }

    public InkProjectileEntity setRollerSwingStats() {
        setProjectileType(Types.ROLLER);

        if (!throwerAirborne) {
            damageType = "roll";
            causesHurtCooldown = true;
        } else trailSize = getProjectileSize() * 0.5f;
        return this;
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(COLOR, ColorUtils.DEFAULT);
        entityData.define(PROJ_TYPE, Types.SHOOTER);
        entityData.define(PROJ_SIZE, 1.0f);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> dataParameter) {
        if (dataParameter.equals(PROJ_SIZE)) {
            refreshDimensions();
        }

        super.onSyncedDataUpdated(dataParameter);
    }

    @Override
    protected Item getDefaultItem() {
        return SplatcraftItems.splattershot.get();
    }

    @Override
    public void tick() {
        super.tick();

        if (isInWater()) {
            discard();
            return;
        }

        if (!level.isClientSide && !persistent && lifespan-- <= 0) {
            float dmg = damage.calculateDamage(this.tickCount, throwerAirborne, charge, isOnRollCooldown);
            InkExplosion.createInkExplosion(level, getOwner(), blockPosition(), getProjectileSize() * 0.85f, explodes ? damage.getMinDamage() : dmg, dmg, bypassMobDamageMultiplier, getColor(), inkType, sourceWeapon);
            if (explodes) {
                level.broadcastEntityEvent(this, (byte) 3);
                level.playSound(null, getX(), getY(), getZ(), SplatcraftSounds.blasterExplosion, SoundSource.PLAYERS, 0.8F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
            }
            discard();
        }

        if (trailSize > 0 && (trailCooldown == 0 || tickCount % trailCooldown == 0)) {
            for (double y = getY(); y >= 0 && getY() - y <= 8; y--) {
                BlockPos inkPos = new BlockPos(getX(), y, getZ());
                if (!InkBlockUtils.canInkPassthrough(level, inkPos)) {
                    if (!isInvisible())
                        level.broadcastEntityEvent(this, (byte) 1);
                    InkExplosion.createInkExplosion(level, getOwner(), inkPos.relative(Direction.UP), trailSize, 0, 0, bypassMobDamageMultiplier, getColor(), inkType, sourceWeapon);
                    InkExplosion.createInkExplosion(level, getOwner(), blockPosition(), trailSize, 0, 0, bypassMobDamageMultiplier, getColor(), inkType, sourceWeapon);
                    break;
                }
            }
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
                    level.addParticle(new InkSplashParticleData(getColor(), getProjectileSize()), this.getX() - this.getDeltaMovement().x() * 0.25D, this.getY() - this.getDeltaMovement().y() * 0.25D, this.getZ() - this.getDeltaMovement().z() * 0.25D, 0, -0.1, 0);
                else
                    level.addParticle(new InkSplashParticleData(getColor(), getProjectileSize()), this.getX() - this.getDeltaMovement().x() * 0.25D, this.getY() - this.getDeltaMovement().y() * 0.25D, this.getZ() - this.getDeltaMovement().z() * 0.25D, this.getDeltaMovement().x(), this.getDeltaMovement().y(), this.getDeltaMovement().z());
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
        float dmg = damage.calculateDamage(this.tickCount, throwerAirborne, charge, isOnRollCooldown);

        if(!level.isClientSide() && target instanceof SpawnShieldEntity && !InkDamageUtils.canDamage(target, this))
        {
            discard();
            level.broadcastEntityEvent(this, (byte) -1);
        }

        if (target instanceof LivingEntity) {
            if (InkDamageUtils.isSplatted((LivingEntity) target)) return;

            if (InkDamageUtils.doDamage(level, (LivingEntity) target, dmg, getColor(), getOwner(), this, sourceWeapon, bypassMobDamageMultiplier, damageType, causesHurtCooldown) &&
                    charge >= 1.0f && getOwner() instanceof ServerPlayer)
                ((ServerPlayer) getOwner()).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
        }

        if (!canPierce) {
            if (explodes) {
                InkExplosion.createInkExplosion(level, getOwner(), blockPosition(), getProjectileSize() * 0.85f, damage.getMinDamage(), dmg, bypassMobDamageMultiplier, getColor(), inkType, sourceWeapon);
                level.broadcastEntityEvent(this, (byte) 3);
                level.playSound(null, getX(), getY(), getZ(), SplatcraftSounds.blasterExplosion, SoundSource.PLAYERS, 0.8F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
            } else
                level.broadcastEntityEvent(this, (byte) 2);

            if(!level.isClientSide)
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

        float dmg = damage.calculateDamage(this.tickCount, throwerAirborne, charge, isOnRollCooldown);
        InkExplosion.createInkExplosion(level, getOwner(), blockPosition(), getProjectileSize() * 0.85f, explodes ? damage.getMinDamage() : dmg, dmg, bypassMobDamageMultiplier, getColor(), inkType, sourceWeapon);
        if (explodes) {
            level.broadcastEntityEvent(this, (byte) 3);
            level.playSound(null, getX(), getY(), getZ(), SplatcraftSounds.blasterExplosion, SoundSource.PLAYERS, 0.8F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
        } else if(level.getBlockState(result.getBlockPos()).getBlock() instanceof StageBarrierBlock)
            level.broadcastEntityEvent(this, (byte) -1);
        else level.broadcastEntityEvent(this, (byte) 2);
        if (!level.isClientSide)
            this.discard();
    }

    @Override
    public void shootFromRotation(Entity thrower, float pitch, float yaw, float pitchOffset, float velocity, float inaccuracy) {
        super.shootFromRotation(thrower, pitch, yaw, pitchOffset, velocity, inaccuracy);
        InkExplosion.createInkExplosion(level, getOwner(), thrower.blockPosition(), 0.75f, 0, 0, bypassMobDamageMultiplier, getColor(), inkType, sourceWeapon);

        Vec3 posDiff = new Vec3(0, 0, 0);

        if (thrower instanceof Player) {
            posDiff = thrower.position().subtract(WeaponHandler.getPlayerPrevPos((Player) thrower));
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
    protected void onHit(HitResult result) {
        HitResult.Type rayType = result.getType();
        if (rayType == HitResult.Type.ENTITY) {
            this.onHitEntity((EntityHitResult) result);
        } else if (rayType == HitResult.Type.BLOCK) {
            onHitBlock((BlockHitResult) result);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        if (nbt.contains("Size"))
            setProjectileSize(nbt.getFloat("Size"));
        if (nbt.contains("Color"))
            setColor(ColorUtils.getColorFromNbt(nbt));

        if (nbt.contains("GravityVelocity"))
            gravityVelocity = nbt.getFloat("GravityVelocity");
        if (nbt.contains("Lifespan"))
            lifespan = nbt.getInt("Lifespan");

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

        if(sourceWeapon.getItem() instanceof WeaponBaseItem)
            damage = ((WeaponBaseItem) sourceWeapon.getItem()).settings;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putFloat("Size", getProjectileSize());
        nbt.putInt("Color", getColor());

        nbt.putFloat("GravityVelocity", gravityVelocity);
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
        return gravityVelocity;
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

    @Override
    public void registerControllers(AnimationData data)
    {
        data.addAnimationController(new AnimationController<>(this, "controller", 0, (event) ->
        {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.ink_projectile.idle", ILoopType.EDefaultLoopTypes.LOOP));
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimationFactory getFactory() {
        return animationFactory;
    }

    public static class Types {
        public static final String DEFAULT = "default";
        public static final String SHOOTER = "shooter";
        public static final String CHARGER = "charger";
        public static final String ROLLER = "roller";
        public static final String BLASTER = "blaster";
    }
}
