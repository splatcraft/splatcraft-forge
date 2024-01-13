package net.splatcraft.forge.items.weapons.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.splatcraft.forge.util.WeaponTooltip;

import java.util.Optional;

public class ShooterWeaponSettings extends AbstractWeaponSettings<ShooterWeaponSettings, ShooterWeaponSettings.DataRecord>
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

    public boolean bypassesMobDamage = false;


    public static final ShooterWeaponSettings DEFAULT = new ShooterWeaponSettings("default");

    public ShooterWeaponSettings(String name) {
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

        int e = tickCount - damageDecayStartTick;
        return Math.max(e > 0 ? baseDamage - (e * damageDecayPerTick) : baseDamage, decayedDamage);
    }

    @Override
    public float getMinDamage() {
        return decayedDamage;
    }

    @Override
    public WeaponTooltip<ShooterWeaponSettings>[] tooltipsToRegister()
    {
        return new WeaponTooltip[]
                {
                        new WeaponTooltip<ShooterWeaponSettings>("range", WeaponTooltip.Metrics.BLOCKS, settings -> settings.straightShotDistance, WeaponTooltip.RANKER_ASCENDING),
                        new WeaponTooltip<ShooterWeaponSettings>("damage", WeaponTooltip.Metrics.HEALTH, settings -> settings.baseDamage, WeaponTooltip.RANKER_ASCENDING),
                        new WeaponTooltip<ShooterWeaponSettings>("fire_rate", WeaponTooltip.Metrics.TICKS, settings -> settings.firingSpeed, WeaponTooltip.RANKER_DESCENDING)
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
    }

    @Override
    public DataRecord serialize() {
        return new DataRecord(new ProjectileDataRecord(projectileSize, Optional.of(projectileLifeTicks), projectileSpeed, Optional.of(projectileDecayedSpeed), straightShotDistance, Optional.of(projectileGravity), Optional.of(projectileCount),
                Optional.of(projectileInkCoverage), Optional.of(projectileInkTrailCoverage), Optional.of(projectileInkTrailCooldown), baseDamage, Optional.of(decayedDamage),
                Optional.of(damageDecayStartTick), Optional.of(damageDecayPerTick)),
                new ShotDataRecord(Optional.of(startupTicks), firingSpeed, groundInaccuracy, Optional.of(airInaccuracy), Optional.of(pitchCompensation), inkConsumption, inkRecoveryCooldown),
                moveSpeed, Optional.of(bypassesMobDamage));
    }

    public ShooterWeaponSettings setProjectileSize(float projectileSize)
    {
        this.projectileSize = projectileSize;
        this.projectileInkCoverage = projectileSize * 0.85f;
        this.projectileInkTrailCoverage = projectileSize * 0.75f;
        return this;
    }

    public ShooterWeaponSettings setProjectileLifeTicks(int projectileLifeTicks) {
        this.projectileLifeTicks = projectileLifeTicks;
        return this;
    }

    public ShooterWeaponSettings setProjectileSpeed(float projectileSpeed) {
        this.projectileSpeed = projectileSpeed;
        this.projectileDecayedSpeed = projectileSpeed;
        return this;
    }

    public ShooterWeaponSettings setProjectileDecayedSpeed(float projectileDecayedSpeed) {
        this.projectileDecayedSpeed = projectileDecayedSpeed;
        return this;
    }

    public ShooterWeaponSettings setProjectileCount(int projectileCount) {
        this.projectileCount = projectileCount;
        return this;
    }

    public ShooterWeaponSettings setPitchCompensation(float pitchCompensation) {
        this.pitchCompensation = pitchCompensation;
        return this;
    }

    public ShooterWeaponSettings setFiringSpeed(int firingSpeed) {
        this.firingSpeed = firingSpeed;
        return this;
    }

    public ShooterWeaponSettings setStartupTicks(int startupTicks) {
        this.startupTicks = startupTicks;
        return this;
    }

    public ShooterWeaponSettings setGroundInaccuracy(float groundInaccuracy) {
        this.groundInaccuracy = groundInaccuracy;
        this.airInaccuracy = groundInaccuracy;
        return this;
    }

    public ShooterWeaponSettings setAirInaccuracy(float airInaccuracy) {
        this.airInaccuracy = airInaccuracy;
        return this;
    }

    public ShooterWeaponSettings setInkConsumption(float inkConsumption) {
        this.inkConsumption = inkConsumption;
        return this;
    }

    public ShooterWeaponSettings setInkRecoveryCooldown(int inkRecoveryCooldown) {
        this.inkRecoveryCooldown = inkRecoveryCooldown;
        return this;
    }

    public ShooterWeaponSettings setBaseDamage(float baseDamage) {
        this.baseDamage = baseDamage;
        this.decayedDamage = baseDamage;
        return this;
    }

    public ShooterWeaponSettings setDecayedDamage(float decayedDamage) {
        this.decayedDamage = decayedDamage;
        return this;
    }

    public ShooterWeaponSettings setDamageDecayStartTick(int damageDecayStartTick) {
        this.damageDecayStartTick = damageDecayStartTick;
        return this;
    }

    public ShooterWeaponSettings setDamageDecayPerTick(float damageDecayPerTick) {
        this.damageDecayPerTick = damageDecayPerTick;
        return this;
    }

    public ShooterWeaponSettings setBypassesMobDamage(boolean bypassesMobDamage) {
        this.bypassesMobDamage = bypassesMobDamage;
        return this;
    }

    public ShooterWeaponSettings setProjectileGravity(float projectileGravity) {
        this.projectileGravity = projectileGravity;
        return this;
    }

    public ShooterWeaponSettings setProjectileInkCoverage(float projectileInkCoverage) {
        this.projectileInkCoverage = projectileInkCoverage;
        return this;
    }

    public ShooterWeaponSettings setProjectileInkTrailCooldown(int projectileInkTrailCooldown) {
        this.projectileInkTrailCooldown = projectileInkTrailCooldown;
        return this;
    }

    public ShooterWeaponSettings setProjectileInkTrailCoverage(float projectileInkTrailCoverage) {
        this.projectileInkTrailCoverage = projectileInkTrailCoverage;
        return this;
    }

    public ShooterWeaponSettings setStraightShotDistance(float blocks)
    {
        float speedAvg = (projectileSpeed + projectileDecayedSpeed) * 0.5f;
        this.straightShotTickTime = (int) (blocks / speedAvg);
        this.straightShotDistance = straightShotTickTime * speedAvg; //math so that weapon stat tooltips always yield accurate results
        return this;
    }

    public int getDualieOffhandFiringOffset()
    {
        return firingSpeed / 2;
    }

    public record DataRecord(
        ProjectileDataRecord projectile,
        ShotDataRecord shot,
        float mobility,
        Optional<Boolean> bypassesMobDamage
    )
    {
        public static final Codec<DataRecord> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        ProjectileDataRecord.CODEC.fieldOf("projectile").forGetter(DataRecord::projectile),
                        ShotDataRecord.CODEC.fieldOf("shot").forGetter(DataRecord::shot),
                        Codec.FLOAT.fieldOf("mobility").forGetter(DataRecord::mobility),
                        Codec.BOOL.optionalFieldOf("full_damage_to_mobs").forGetter(DataRecord::bypassesMobDamage)
                ).apply(instance, DataRecord::new)
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
}
