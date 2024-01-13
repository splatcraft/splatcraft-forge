package net.splatcraft.forge.items.weapons.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.splatcraft.forge.util.WeaponTooltip;

import java.util.Optional;

public class DualieWeaponSettings extends AbstractWeaponSettings<DualieWeaponSettings, DualieWeaponSettings.DataRecord>
{
    public FiringData standardData = new FiringData();
    public FiringData turretData = new FiringData();
    public boolean bypassesMobDamage = false;
    
    public float rollCount;
    public float rollSpeed;
    public float rollInkConsumption;
    public int rollInkRecoveryCooldown;
    public int rollCooldown;
    public int lastRollCooldown;
    
    public static class FiringData
    {
        public float projectileSize;
        public float projectileSpeed;
        public float projectileDecayedSpeed;
        public int projectileCount = 1;
        public int projectileLifeTicks = 600;
        public float projectileInkCoverage;
        public float projectileInkTrailCoverage;
        public int projectileInkTrailCooldown = 4;
        public int straightShotTickTime;
        public float straightShotDistance;
        public float projectileGravity = 0.075f;
        public float baseDamage;
        public float decayedDamage;
        public int damageDecayStartTick;
        public float damageDecayPerTick;

        public int firingSpeed;
        public int startupTicks;
        public float groundInaccuracy;
        public float airInaccuracy;
        public float pitchCompensation;

        public float inkConsumption;
        public int inkRecoveryCooldown;
    }
    
    public static final DualieWeaponSettings DEFAULT = new DualieWeaponSettings("default");

    public DualieWeaponSettings(String name) {
        super(name);
    }

    @Override
    public float calculateDamage(int tickCount, boolean airborne, float charge, boolean isOnRollCooldown)
    {
        /*
        if (isOnRollCooldown) {
            int e = tickCount - rollDamageDecayStartTick;
            return Math.max(e > 0 ? rollBaseDamage - (e * rollDamageDecayPerTick) : rollBaseDamage, rollMinDamage);
        }
        */

        int e = tickCount - standardData.damageDecayStartTick;
        return Math.max(e > 0 ? standardData.baseDamage - (e * standardData.damageDecayPerTick) : standardData.baseDamage, standardData.decayedDamage);
    }

    @Override
    public float getMinDamage() {
        return standardData.decayedDamage;
    }

    @Override
    public WeaponTooltip<DualieWeaponSettings>[] tooltipsToRegister()
    {
        return new WeaponTooltip[]
                {
                        new WeaponTooltip<DualieWeaponSettings>("range", WeaponTooltip.Metrics.BLOCKS, settings -> settings.standardData.straightShotDistance, WeaponTooltip.RANKER_ASCENDING),
                        new WeaponTooltip<DualieWeaponSettings>("damage", WeaponTooltip.Metrics.HEALTH, settings -> settings.standardData.baseDamage, WeaponTooltip.RANKER_ASCENDING),
                        new WeaponTooltip<DualieWeaponSettings>("roll_distance", WeaponTooltip.Metrics.BLOCKS, settings -> settings.rollSpeed * 6, WeaponTooltip.RANKER_ASCENDING) //i used desmos to get that 6 B)
                };

    }

    @Override
    public Codec<DataRecord> getCodec() {
        return DataRecord.CODEC;
    }

    @Override
    public void deserialize(DataRecord data)
    {
        ProjectileDataRecord projectile = data.projectile;

        moveSpeed = data.mobility;
        data.bypassesMobDamage.ifPresent(this::setBypassesMobDamage);

        setProjectileSize(projectile.size);
        projectile.lifeTicks.ifPresent(this::setProjectileLifeTicks);

        setProjectileSpeed(projectile.speed);
        projectile.decayedSpeed.ifPresent(this::setProjectileDecayedSpeed);
        projectile.count.ifPresent(this::setProjectileCount);

        projectile.inkCoverageImpact.ifPresent(this::setProjectileInkCoverage);
        projectile.inkTrailCoverage.ifPresent(this::setProjectileInkTrailCoverage);
        projectile.inkTrailCooldown.ifPresent(this::setProjectileInkTrailCooldown);

        setStraightShotDistance(projectile.straightShotDistance);
        projectile.gravity.ifPresent(this::setProjectileGravity);

        setBaseDamage(projectile.baseDamage);
        projectile.decayedDamage.ifPresent(this::setDecayedDamage);
        setDamageDecayStartTick(projectile.damageDecayStartTick.orElse(0));
        setDamageDecayPerTick(projectile.damageDecayPerTick.orElse(0f));

        ShotDataRecord shot = data.shot;

        shot.startupTicks.ifPresent(this::setStartupTicks);
        setFiringSpeed(shot.firingSpeed);

        setGroundInaccuracy(shot.groundInaccuracy);
        shot.airborneInaccuracy.ifPresent(this::setAirInaccuracy);
        shot.pitchCompensation.ifPresent(this::setPitchCompensation);

        setInkConsumption(shot.inkConsumption);
        setInkRecoveryCooldown(shot.inkRecoveryCooldown);

        RollDataRecord roll = data.roll;

        setRollCount(roll.count);
        setRollSpeed(roll.speed);
        setRollInkConsumption(roll.inkConsumption);
        setRollInkRecoveryCooldown(roll.inkRecoveryCooldown);
        setRollCooldown(roll.duration);
        setLastRollCooldown(roll.finalRollDuration);

        if(data.turretProjectile.isPresent())
        {
            OptionalProjectileDataRecord turretProjectile = data.turretProjectile.get();

            turretProjectile.size.ifPresent(this::setTurretProjectileSize);
            turretProjectile.lifeTicks.ifPresent(this::setTurretProjectileLifeTicks);

            turretProjectile.speed.ifPresent(this::setTurretProjectileSpeed);
            turretProjectile.decayedSpeed.ifPresent(this::setTurretProjectileDecayedSpeed);
            turretProjectile.count.ifPresent(this::setTurretProjectileCount);

            turretProjectile.inkCoverageImpact.ifPresent(this::setTurretProjectileInkCoverage);
            turretProjectile.inkTrailCoverage.ifPresent(this::setTurretProjectileInkTrailCoverage);
            turretProjectile.inkTrailCooldown.ifPresent(this::setTurretProjectileInkTrailCooldown);

            turretProjectile.straightShotDistance.ifPresent(this::setTurretStraightShotDistance);
            turretProjectile.gravity.ifPresent(this::setTurretProjectileGravity);
            
            if(turretProjectile.baseDamage.isPresent())
            {
                setTurretBaseDamage(turretProjectile.baseDamage.get());
                turretProjectile.decayedDamage.ifPresent(this::setTurretDecayedDamage);
                setTurretDamageDecayStartTick(turretProjectile.damageDecayStartTick.orElse(0));
                setTurretDamageDecayPerTick(turretProjectile.damageDecayPerTick.orElse(0f));
            }
        }

        if(data.turretShot.isPresent())
        {
            OptionalShotDataRecord turretShot = data.turretShot.get();

            turretShot.startupTicks.ifPresent(this::setTurretStartupTicks);
            turretShot.firingSpeed.ifPresent(this::setTurretFiringSpeed);

            turretShot.groundInaccuracy.ifPresent(this::setTurretGroundInaccuracy);
            turretShot.airborneInaccuracy.ifPresent(this::setTurretAirInaccuracy);
            turretShot.pitchCompensation.ifPresent(this::setTurretPitchCompensation);

            turretShot.inkConsumption.ifPresent(this::setTurretInkConsumption);
            turretShot.inkRecoveryCooldown.ifPresent(this::setTurretInkRecoveryCooldown);
        }
    }

    @Override
    public DataRecord serialize() {
        return new DataRecord(new ProjectileDataRecord(standardData.projectileSize, Optional.of(standardData.projectileLifeTicks), standardData.projectileSpeed, 
                Optional.of(standardData.projectileDecayedSpeed), standardData.straightShotDistance, Optional.of(standardData.projectileGravity), Optional.of(standardData.projectileCount),
                Optional.of(standardData.projectileInkCoverage), Optional.of(standardData.projectileInkTrailCoverage), Optional.of(standardData.projectileInkTrailCooldown),
                standardData.baseDamage, Optional.of(standardData.decayedDamage),
                Optional.of(standardData.damageDecayStartTick), Optional.of(standardData.damageDecayPerTick)),
                new ShotDataRecord(Optional.of(standardData.startupTicks), standardData.firingSpeed, standardData.groundInaccuracy, Optional.of(standardData.airInaccuracy), 
                        Optional.of(standardData.pitchCompensation), standardData.inkConsumption, standardData.inkRecoveryCooldown),
		        Optional.of(new OptionalProjectileDataRecord(Optional.of(turretData.projectileSize), Optional.of(turretData.projectileLifeTicks), Optional.of(turretData.projectileSpeed),
				        Optional.of(turretData.projectileDecayedSpeed), Optional.of(turretData.straightShotDistance), Optional.of(turretData.projectileGravity), Optional.of(turretData.projectileCount),
				        Optional.of(turretData.projectileInkCoverage), Optional.of(turretData.projectileInkTrailCoverage), Optional.of(turretData.projectileInkTrailCooldown),
				        Optional.of(turretData.baseDamage), Optional.of(turretData.decayedDamage),
				        Optional.of(turretData.damageDecayStartTick), Optional.of(turretData.damageDecayPerTick))),
		        Optional.of(new OptionalShotDataRecord(Optional.of(turretData.startupTicks), Optional.of(turretData.firingSpeed), Optional.of(turretData.groundInaccuracy), Optional.of(turretData.airInaccuracy),
				        Optional.of(turretData.pitchCompensation), Optional.of(turretData.inkConsumption), Optional.of(turretData.inkRecoveryCooldown))),
                new RollDataRecord(rollCount, rollSpeed, rollInkConsumption, rollInkRecoveryCooldown, rollCooldown, lastRollCooldown),
                moveSpeed,
                Optional.of(bypassesMobDamage));
    }

    public DualieWeaponSettings setBypassesMobDamage(boolean bypassesMobDamage) {
        this.bypassesMobDamage = bypassesMobDamage;
        return this;
    }

    public DualieWeaponSettings setLastRollCooldown(int lastRollCooldown) {
        this.lastRollCooldown = lastRollCooldown;
        return this;
    }

    public DualieWeaponSettings setRollCooldown(int rollCooldown) {
        this.rollCooldown = rollCooldown;
        return this;
    }

    public DualieWeaponSettings setRollCount(float rollCount) {
        this.rollCount = rollCount;
        return this;
    }

    public DualieWeaponSettings setRollInkConsumption(float rollInkConsumption) {
        this.rollInkConsumption = rollInkConsumption;
        return this;
    }

    public DualieWeaponSettings setRollInkRecoveryCooldown(int rollInkRecoveryCooldown) {
        this.rollInkRecoveryCooldown = rollInkRecoveryCooldown;
        return this;
    }

    public DualieWeaponSettings setRollSpeed(float rollSpeed) {
        this.rollSpeed = rollSpeed;
        return this;
    }

    public DualieWeaponSettings setProjectileSize(float projectileSize)
    {
        standardData.projectileSize = projectileSize;
        standardData.projectileInkCoverage = projectileSize * 0.85f;
        standardData.projectileInkTrailCoverage = projectileSize * 0.75f;
        
        turretData.projectileSize = projectileSize;
        turretData.projectileInkCoverage = projectileSize * 0.85f;
        turretData.projectileInkTrailCoverage = projectileSize * 0.75f;
        return this;
    }

    public DualieWeaponSettings setProjectileLifeTicks(int projectileLifeTicks) {
        standardData.projectileLifeTicks = projectileLifeTicks;
        turretData.projectileLifeTicks = projectileLifeTicks;
        return this;
    }

    public DualieWeaponSettings setProjectileSpeed(float projectileSpeed) {
        standardData.projectileSpeed = projectileSpeed;
        standardData.projectileDecayedSpeed = projectileSpeed;
        turretData.projectileSpeed = projectileSpeed;
        turretData.projectileDecayedSpeed = projectileSpeed;
        return this;
    }

    public DualieWeaponSettings setProjectileDecayedSpeed(float projectileDecayedSpeed) {
        standardData.projectileDecayedSpeed = projectileDecayedSpeed;
        turretData.projectileDecayedSpeed = projectileDecayedSpeed;
        return this;
    }

    public DualieWeaponSettings setProjectileCount(int projectileCount) {
        standardData.projectileCount = projectileCount;
        turretData.projectileCount = projectileCount;
        return this;
    }

    public DualieWeaponSettings setPitchCompensation(float pitchCompensation) {
        standardData.pitchCompensation = pitchCompensation;
        turretData.pitchCompensation = pitchCompensation;
        return this;
    }

    public DualieWeaponSettings setFiringSpeed(int firingSpeed) {
        standardData.firingSpeed = firingSpeed;
        turretData.firingSpeed = firingSpeed;
        return this;
    }

    public DualieWeaponSettings setStartupTicks(int startupTicks) {
        standardData.startupTicks = startupTicks;
        turretData.startupTicks = startupTicks;
        return this;
    }

    public DualieWeaponSettings setGroundInaccuracy(float groundInaccuracy) {
        standardData.groundInaccuracy = groundInaccuracy;
        standardData.airInaccuracy = groundInaccuracy;
        turretData.groundInaccuracy = groundInaccuracy;
        turretData.airInaccuracy = groundInaccuracy;
        return this;
    }

    public DualieWeaponSettings setAirInaccuracy(float airInaccuracy) {
        standardData.airInaccuracy = airInaccuracy;
        turretData.airInaccuracy = airInaccuracy;
        return this;
    }

    public DualieWeaponSettings setInkConsumption(float inkConsumption) {
        standardData.inkConsumption = inkConsumption;
        turretData.inkConsumption = inkConsumption;
        return this;
    }

    public DualieWeaponSettings setInkRecoveryCooldown(int inkRecoveryCooldown) {
        standardData.inkRecoveryCooldown = inkRecoveryCooldown;
        turretData.inkRecoveryCooldown = inkRecoveryCooldown;
        return this;
    }

    public DualieWeaponSettings setBaseDamage(float baseDamage) {
        standardData.baseDamage = baseDamage;
        standardData.decayedDamage = baseDamage;
        turretData.baseDamage = baseDamage;
        turretData.decayedDamage = baseDamage;
        return this;
    }

    public DualieWeaponSettings setDecayedDamage(float decayedDamage) {
        standardData.decayedDamage = decayedDamage;
        turretData.decayedDamage = decayedDamage;
        return this;
    }

    public DualieWeaponSettings setDamageDecayStartTick(int damageDecayStartTick) {
        standardData.damageDecayStartTick = damageDecayStartTick;
        turretData.damageDecayStartTick = damageDecayStartTick;
        return this;
    }

    public DualieWeaponSettings setDamageDecayPerTick(float damageDecayPerTick) {
        standardData.damageDecayPerTick = damageDecayPerTick;
        turretData.damageDecayPerTick = damageDecayPerTick;
        return this;
    }

    public DualieWeaponSettings setProjectileGravity(float projectileGravity) {
        standardData.projectileGravity = projectileGravity;
        turretData.projectileGravity = projectileGravity;
        return this;
    }

    public DualieWeaponSettings setProjectileInkCoverage(float projectileInkCoverage) {
        standardData.projectileInkCoverage = projectileInkCoverage;
        turretData.projectileInkCoverage = projectileInkCoverage;
        return this;
    }

    public DualieWeaponSettings setProjectileInkTrailCooldown(int projectileInkTrailCooldown) {
        standardData.projectileInkTrailCooldown = projectileInkTrailCooldown;
        turretData.projectileInkTrailCooldown = projectileInkTrailCooldown;
        return this;
    }

    public DualieWeaponSettings setProjectileInkTrailCoverage(float projectileInkTrailCoverage) {
        standardData.projectileInkTrailCoverage = projectileInkTrailCoverage;
        turretData.projectileInkTrailCoverage = projectileInkTrailCoverage;
        return this;
    }

    public DualieWeaponSettings setStraightShotDistance(float blocks)
    {
        float speedAvg = (standardData.projectileSpeed + standardData.projectileDecayedSpeed) * 0.5f;
        standardData.straightShotTickTime = (int) (blocks / speedAvg);
        standardData.straightShotDistance = standardData.straightShotTickTime * speedAvg; //math so that weapon stat tooltips always yield accurate results
        return this;
    }

    public DualieWeaponSettings setTurretProjectileSize(float projectileSize)
    {
        turretData.projectileSize = projectileSize;
        turretData.projectileInkCoverage = projectileSize * 0.85f;
        turretData.projectileInkTrailCoverage = projectileSize * 0.75f;
        return this;
    }

    public DualieWeaponSettings setTurretProjectileLifeTicks(int projectileLifeTicks) {
        turretData.projectileLifeTicks = projectileLifeTicks;
        return this;
    }

    public DualieWeaponSettings setTurretProjectileSpeed(float projectileSpeed) {
        turretData.projectileSpeed = projectileSpeed;
        turretData.projectileDecayedSpeed = projectileSpeed;
        return this;
    }

    public DualieWeaponSettings setTurretProjectileDecayedSpeed(float projectileDecayedSpeed) {
        turretData.projectileDecayedSpeed = projectileDecayedSpeed;
        return this;
    }

    public DualieWeaponSettings setTurretProjectileCount(int projectileCount) {
        turretData.projectileCount = projectileCount;
        return this;
    }

    public DualieWeaponSettings setTurretPitchCompensation(float pitchCompensation) {
        turretData.pitchCompensation = pitchCompensation;
        return this;
    }

    public DualieWeaponSettings setTurretFiringSpeed(int firingSpeed) {
        turretData.firingSpeed = firingSpeed;
        return this;
    }

    public DualieWeaponSettings setTurretStartupTicks(int startupTicks) {
        turretData.startupTicks = startupTicks;
        return this;
    }

    public DualieWeaponSettings setTurretGroundInaccuracy(float groundInaccuracy) {
        turretData.groundInaccuracy = groundInaccuracy;
        turretData.airInaccuracy = groundInaccuracy;
        return this;
    }

    public DualieWeaponSettings setTurretAirInaccuracy(float airInaccuracy) {
        turretData.airInaccuracy = airInaccuracy;
        return this;
    }

    public DualieWeaponSettings setTurretInkConsumption(float inkConsumption) {
        turretData.inkConsumption = inkConsumption;
        return this;
    }

    public DualieWeaponSettings setTurretInkRecoveryCooldown(int inkRecoveryCooldown) {
        turretData.inkRecoveryCooldown = inkRecoveryCooldown;
        return this;
    }

    public DualieWeaponSettings setTurretBaseDamage(float baseDamage) {
        turretData.baseDamage = baseDamage;
        turretData.decayedDamage = baseDamage;
        return this;
    }

    public DualieWeaponSettings setTurretDecayedDamage(float decayedDamage) {
        turretData.decayedDamage = decayedDamage;
        return this;
    }

    public DualieWeaponSettings setTurretDamageDecayStartTick(int damageDecayStartTick) {
        turretData.damageDecayStartTick = damageDecayStartTick;
        return this;
    }

    public DualieWeaponSettings setTurretDamageDecayPerTick(float damageDecayPerTick) {
        turretData.damageDecayPerTick = damageDecayPerTick;
        return this;
    }

    public DualieWeaponSettings setTurretProjectileGravity(float projectileGravity) {
        turretData.projectileGravity = projectileGravity;
        return this;
    }

    public DualieWeaponSettings setTurretProjectileInkCoverage(float projectileInkCoverage) {
        turretData.projectileInkCoverage = projectileInkCoverage;
        return this;
    }

    public DualieWeaponSettings setTurretProjectileInkTrailCooldown(int projectileInkTrailCooldown) {
        turretData.projectileInkTrailCooldown = projectileInkTrailCooldown;
        return this;
    }

    public DualieWeaponSettings setTurretProjectileInkTrailCoverage(float projectileInkTrailCoverage) {
        turretData.projectileInkTrailCoverage = projectileInkTrailCoverage;
        return this;
    }

    public DualieWeaponSettings setTurretStraightShotDistance(float blocks)
    {
        float speedAvg = (turretData.projectileSpeed + turretData.projectileDecayedSpeed) * 0.5f;
        turretData.straightShotTickTime = (int) (blocks / speedAvg);
        turretData.straightShotDistance = turretData.straightShotTickTime * speedAvg; //math so that weapon stat tooltips always yield accurate results
        return this;
    }

    public int getDualieOffhandFiringOffset(boolean turret)
    {
        return standardData.firingSpeed / 2;
    }

    public record DataRecord(
        ProjectileDataRecord projectile,
        ShotDataRecord shot,
        Optional<OptionalProjectileDataRecord> turretProjectile,
        Optional<OptionalShotDataRecord> turretShot,
        RollDataRecord roll,
        float mobility,
        Optional<Boolean> bypassesMobDamage
    )
    {
        public static final Codec<DataRecord> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        ProjectileDataRecord.CODEC.fieldOf("projectile").forGetter(DataRecord::projectile),
                        ShotDataRecord.CODEC.fieldOf("shot").forGetter(DataRecord::shot),
                        OptionalProjectileDataRecord.CODEC.optionalFieldOf("turret_projectile").forGetter(DataRecord::turretProjectile),
                        OptionalShotDataRecord.CODEC.optionalFieldOf("turret_shot").forGetter(DataRecord::turretShot),
                        RollDataRecord.CODEC.fieldOf("dodge_roll").forGetter(DataRecord::roll),
                        Codec.FLOAT.fieldOf("mobility").forGetter(DataRecord::mobility),
                        Codec.BOOL.optionalFieldOf("full_damage_to_mobs").forGetter(DataRecord::bypassesMobDamage)
                ).apply(instance, DataRecord::new)
        );
    }


    public record RollDataRecord(
            float count,
            float speed,
            float inkConsumption,
            int inkRecoveryCooldown,
            int duration,
            int finalRollDuration
    )
    {
        public static final Codec<RollDataRecord> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.FLOAT.fieldOf("count").forGetter(RollDataRecord::count),
                        Codec.FLOAT.fieldOf("movement_impulse").forGetter(RollDataRecord::speed),
                        Codec.FLOAT.fieldOf("ink_consumption").forGetter(RollDataRecord::inkConsumption),
                        Codec.INT.fieldOf("ink_recovery_cooldown").forGetter(RollDataRecord::inkRecoveryCooldown),
                        Codec.INT.fieldOf("turret_duration").forGetter(RollDataRecord::duration),
                        Codec.INT.fieldOf("final_roll_turret_duration").forGetter(RollDataRecord::finalRollDuration)
                ).apply(instance, RollDataRecord::new)
        );
    }
    
    record ProjectileDataRecord(
            float size,
            Optional<Integer> lifeTicks,
            float speed,
            Optional<Float> decayedSpeed,
            float straightShotDistance,
            Optional<Float> gravity,
            Optional<Integer> count,
            Optional<Float> inkCoverageImpact,
            Optional<Float> inkTrailCoverage,
            Optional<Integer> inkTrailCooldown,
            float baseDamage,
            Optional<Float> decayedDamage,
            Optional<Integer> damageDecayStartTick,
            Optional<Float> damageDecayPerTick
    )
    {
        public static final Codec<ProjectileDataRecord> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.FLOAT.fieldOf("size").forGetter(ProjectileDataRecord::size),
                        Codec.INT.optionalFieldOf("lifespan").forGetter(ProjectileDataRecord::lifeTicks),
                        Codec.FLOAT.fieldOf("speed").forGetter(ProjectileDataRecord::speed),
                        Codec.FLOAT.optionalFieldOf("decayed_speed").forGetter(ProjectileDataRecord::decayedSpeed),
                        Codec.FLOAT.fieldOf("straight_shot_distance").forGetter(ProjectileDataRecord::straightShotDistance),
                        Codec.FLOAT.optionalFieldOf("gravity").forGetter(ProjectileDataRecord::gravity),
                        Codec.INT.optionalFieldOf("count").forGetter(ProjectileDataRecord::count),
                        Codec.FLOAT.optionalFieldOf("ink_coverage_on_impact").forGetter(ProjectileDataRecord::inkCoverageImpact),
                        Codec.FLOAT.optionalFieldOf("ink_trail_coverage").forGetter(ProjectileDataRecord::inkTrailCoverage),
                        Codec.INT.optionalFieldOf("ink_trail_tick_interval").forGetter(ProjectileDataRecord::inkTrailCooldown),
                        Codec.FLOAT.fieldOf("base_damage").forGetter(ProjectileDataRecord::baseDamage),
                        Codec.FLOAT.optionalFieldOf("decayed_damage").forGetter(ProjectileDataRecord::decayedDamage),
                        Codec.INT.optionalFieldOf("damage_decay_start_tick").forGetter(ProjectileDataRecord::damageDecayStartTick),
                        Codec.FLOAT.optionalFieldOf("damage_decay_per_tick").forGetter(ProjectileDataRecord::damageDecayPerTick)
                ).apply(instance, ProjectileDataRecord::new)
        );
    }

    public record ShotDataRecord(
            Optional<Integer> startupTicks,
            int firingSpeed,
            float groundInaccuracy,
            Optional<Float> airborneInaccuracy,
            Optional<Float> pitchCompensation,
            float inkConsumption,
            int inkRecoveryCooldown
    )
    {
        public static final Codec<ShotDataRecord> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.INT.optionalFieldOf("startup_ticks").forGetter(ShotDataRecord::startupTicks),
                        Codec.INT.fieldOf("firing_speed").forGetter(ShotDataRecord::firingSpeed),
                        Codec.FLOAT.fieldOf("ground_inaccuracy").forGetter(ShotDataRecord::groundInaccuracy),
                        Codec.FLOAT.optionalFieldOf("airborne_inaccuracy").forGetter(ShotDataRecord::airborneInaccuracy),
                        Codec.FLOAT.optionalFieldOf("pitch_compensation").forGetter(ShotDataRecord::pitchCompensation),
                        Codec.FLOAT.fieldOf("ink_consumption").forGetter(ShotDataRecord::inkConsumption),
                        Codec.INT.fieldOf("ink_recovery_cooldown").forGetter(ShotDataRecord::inkRecoveryCooldown)
                ).apply(instance, ShotDataRecord::new)
        );
    }

    record OptionalProjectileDataRecord(
            Optional<Float> size,
            Optional<Integer> lifeTicks,
            Optional<Float> speed,
            Optional<Float> decayedSpeed,
            Optional<Float> straightShotDistance,
            Optional<Float> gravity,
            Optional<Integer> count,
            Optional<Float> inkCoverageImpact,
            Optional<Float> inkTrailCoverage,
            Optional<Integer> inkTrailCooldown,
            Optional<Float> baseDamage,
            Optional<Float> decayedDamage,
            Optional<Integer> damageDecayStartTick,
            Optional<Float> damageDecayPerTick
    )
    {
        public static final Codec<OptionalProjectileDataRecord> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.FLOAT.optionalFieldOf("size").forGetter(OptionalProjectileDataRecord::size),
                        Codec.INT.optionalFieldOf("lifespan").forGetter(OptionalProjectileDataRecord::lifeTicks),
                        Codec.FLOAT.optionalFieldOf("speed").forGetter(OptionalProjectileDataRecord::speed),
                        Codec.FLOAT.optionalFieldOf("decayed_speed").forGetter(OptionalProjectileDataRecord::decayedSpeed),
                        Codec.FLOAT.optionalFieldOf("straight_shot_distance").forGetter(OptionalProjectileDataRecord::straightShotDistance),
                        Codec.FLOAT.optionalFieldOf("gravity").forGetter(OptionalProjectileDataRecord::gravity),
                        Codec.INT.optionalFieldOf("count").forGetter(OptionalProjectileDataRecord::count),
                        Codec.FLOAT.optionalFieldOf("ink_coverage_on_impact").forGetter(OptionalProjectileDataRecord::inkCoverageImpact),
                        Codec.FLOAT.optionalFieldOf("ink_trail_coverage").forGetter(OptionalProjectileDataRecord::inkTrailCoverage),
                        Codec.INT.optionalFieldOf("ink_trail_tick_interval").forGetter(OptionalProjectileDataRecord::inkTrailCooldown),
                        Codec.FLOAT.optionalFieldOf("base_damage").forGetter(OptionalProjectileDataRecord::baseDamage),
                        Codec.FLOAT.optionalFieldOf("decayed_damage").forGetter(OptionalProjectileDataRecord::decayedDamage),
                        Codec.INT.optionalFieldOf("damage_decay_start_tick").forGetter(OptionalProjectileDataRecord::damageDecayStartTick),
                        Codec.FLOAT.optionalFieldOf("damage_decay_per_tick").forGetter(OptionalProjectileDataRecord::damageDecayPerTick)
                ).apply(instance, OptionalProjectileDataRecord::new)
        );
    }

    public record OptionalShotDataRecord(
            Optional<Integer> startupTicks,
            Optional<Integer> firingSpeed,
            Optional<Float> groundInaccuracy,
            Optional<Float> airborneInaccuracy,
            Optional<Float> pitchCompensation,
            Optional<Float> inkConsumption,
            Optional<Integer> inkRecoveryCooldown
    )
    {
        public static final Codec<OptionalShotDataRecord> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.INT.optionalFieldOf("startup_ticks").forGetter(OptionalShotDataRecord::startupTicks),
                        Codec.INT.optionalFieldOf("firing_speed").forGetter(OptionalShotDataRecord::firingSpeed),
                        Codec.FLOAT.optionalFieldOf("ground_inaccuracy").forGetter(OptionalShotDataRecord::groundInaccuracy),
                        Codec.FLOAT.optionalFieldOf("airborne_inaccuracy").forGetter(OptionalShotDataRecord::airborneInaccuracy),
                        Codec.FLOAT.optionalFieldOf("pitch_compensation").forGetter(OptionalShotDataRecord::pitchCompensation),
                        Codec.FLOAT.optionalFieldOf("ink_consumption").forGetter(OptionalShotDataRecord::inkConsumption),
                        Codec.INT.optionalFieldOf("ink_recovery_cooldown").forGetter(OptionalShotDataRecord::inkRecoveryCooldown)
                ).apply(instance, OptionalShotDataRecord::new)
        );
    }
}
