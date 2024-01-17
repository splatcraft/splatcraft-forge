package net.splatcraft.forge.items.weapons.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.splatcraft.forge.util.WeaponTooltip;

import java.util.Optional;

public class SlosherWeaponSettings extends AbstractWeaponSettings<SlosherWeaponSettings, SlosherWeaponSettings.DataRecord>
{
    public float projectileSize;

    public float projectileInkCoverage;
    public float projectileInkTrailCoverage;
    public int projectileInkTrailCooldown = 4;
    public float projectileSpeed;
    public int projectileCount;

    public float pitchCompensation;
    public int startupTicks;
    public int endlagTicks;

    public float angleOffset;

    public float inkConsumption;
    public int inkRecoveryCooldown;

    public float directDamage;
    public float splashDamage = 0;

    public boolean bypassesMobDamage;

    public static final SlosherWeaponSettings DEFAULT = new SlosherWeaponSettings("default");

    public SlosherWeaponSettings(String name) {
        super(name);
    }

    @Override
    public float calculateDamage(int tickCount, boolean airborne, float charge, boolean isOnRollCooldown)
    {
        return directDamage;
    }

    @Override
    public float getMinDamage() {
        return splashDamage;
    }

    @Override
    public WeaponTooltip<SlosherWeaponSettings>[] tooltipsToRegister()
    {
        return new WeaponTooltip[]
                {
                        new WeaponTooltip<SlosherWeaponSettings>("speed", WeaponTooltip.Metrics.BPT, settings -> settings.projectileSpeed, WeaponTooltip.RANKER_ASCENDING),
                        new WeaponTooltip<SlosherWeaponSettings>("damage", WeaponTooltip.Metrics.HEALTH, settings -> settings.directDamage, WeaponTooltip.RANKER_ASCENDING),
                        new WeaponTooltip<SlosherWeaponSettings>("handling", WeaponTooltip.Metrics.TICKS, settings -> settings.startupTicks + settings.endlagTicks, WeaponTooltip.RANKER_DESCENDING)
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

        data.mobility.ifPresent(this::setMoveSpeed);
        data.fullDamageToMobs.ifPresent(this::setBypassesMobDamage);
        data.isSecret.ifPresent(this::setSecret);

        setProjectileSize(projectile.size);
        setProjectileSpeed(projectile.speed);

        projectile.inkCoverageImpact.ifPresent(this::setProjectileInkCoverage);
        projectile.inkTrailCoverage.ifPresent(this::setProjectileInkTrailCoverage);
        projectile.inkTrailCooldown.ifPresent(this::setProjectileInkTrailCooldown);

        setProjectileCount(projectile.count);
        setAngleOffset(projectile.offsetAngle);

        setDirectDamage(projectile.directDamage);
        projectile.splashDamage.ifPresent(this::setSplashDamage);

        ShotDataRecord shot = data.shot;

        setPitchCompensation(shot.pitchCompensation);
        setStartupTicks(shot.startupTicks);
        setEndlagTicks(shot.endlagTicks);
        setInkConsumption(shot.inkConsumption);
        setInkRecoveryCooldown(shot.inkRecoveryCooldown);
    }

    @Override
    public DataRecord serialize() {
        return new DataRecord(new ProjectileDataRecord(projectileSize, projectileSpeed,
                Optional.of(projectileInkCoverage), Optional.of(projectileInkTrailCoverage), Optional.of(projectileInkTrailCooldown),
                projectileCount, angleOffset, directDamage, Optional.of(splashDamage)),
                new ShotDataRecord(pitchCompensation, startupTicks, endlagTicks, inkConsumption, inkRecoveryCooldown), Optional.of(moveSpeed),
                Optional.of(bypassesMobDamage), Optional.of(isSecret));
    }

    public SlosherWeaponSettings setProjectileSize(float projectileSize) {
        this.projectileSize = projectileSize;
        this.projectileInkCoverage = projectileSize * 0.85f;
        this.projectileInkTrailCoverage = projectileSize * 0.75f;

        return this;
    }

    public SlosherWeaponSettings setProjectileInkCoverage(float projectileInkCoverage) {
        this.projectileInkCoverage = projectileInkCoverage;
        return this;
    }

    public SlosherWeaponSettings setProjectileInkTrailCooldown(int projectileInkTrailCooldown) {
        this.projectileInkTrailCooldown = projectileInkTrailCooldown;
        return this;
    }

    public SlosherWeaponSettings setProjectileInkTrailCoverage(float projectileInkTrailCoverage) {
        this.projectileInkTrailCoverage = projectileInkTrailCoverage;
        return this;
    }

    public SlosherWeaponSettings setProjectileSpeed(float projectileSpeed) {
        this.projectileSpeed = projectileSpeed;
        return this;
    }

    public SlosherWeaponSettings setProjectileCount(int projectileCount) {
        this.projectileCount = projectileCount;
        return this;
    }

    public SlosherWeaponSettings setAngleOffset(float angleOffset) {
        this.angleOffset = angleOffset;
        return this;
    }

    public SlosherWeaponSettings setPitchCompensation(float pitchCompensation) {
        this.pitchCompensation = pitchCompensation;
        return this;
    }


    public SlosherWeaponSettings setStartupTicks(int startupTicks) {
        this.startupTicks = startupTicks;
        return this;
    }

    public SlosherWeaponSettings setEndlagTicks(int endlagTicks) {
        this.endlagTicks = endlagTicks;
        return this;
    }

    public SlosherWeaponSettings setInkConsumption(float inkConsumption) {
        this.inkConsumption = inkConsumption;
        return this;
    }

    public SlosherWeaponSettings setInkRecoveryCooldown(int inkRecoveryCooldown) {
        this.inkRecoveryCooldown = inkRecoveryCooldown;
        return this;
    }

    public SlosherWeaponSettings setDirectDamage(float directDamage) {
        this.directDamage = directDamage;
        return this;
    }

    public SlosherWeaponSettings setSplashDamage(float splashDamage) {
        this.splashDamage = splashDamage;
        return this;
    }

    public SlosherWeaponSettings setBypassesMobDamage(boolean bypassesMobDamage) {
        this.bypassesMobDamage = bypassesMobDamage;
        return this;
    }

    public record DataRecord(
        ProjectileDataRecord projectile,
        ShotDataRecord shot,
        Optional<Float> mobility,
        Optional<Boolean> fullDamageToMobs,
        Optional<Boolean> isSecret
    )
    {
        public static final Codec<DataRecord> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        ProjectileDataRecord.CODEC.fieldOf("projectile").forGetter(DataRecord::projectile),
                        ShotDataRecord.CODEC.fieldOf("shot").forGetter(DataRecord::shot),
                        Codec.FLOAT.optionalFieldOf("mobility").forGetter(DataRecord::mobility),
                        Codec.BOOL.optionalFieldOf("full_damage_to_mobs").forGetter(DataRecord::fullDamageToMobs),
                        Codec.BOOL.optionalFieldOf("is_secret").forGetter(DataRecord::isSecret)
                ).apply(instance, DataRecord::new)
        );
    }

    record ProjectileDataRecord(
            float size,
            float speed,
            Optional<Float> inkCoverageImpact,
            Optional<Float> inkTrailCoverage,
            Optional<Integer> inkTrailCooldown,
            int count,
            float offsetAngle,
            float directDamage,
            Optional<Float> splashDamage
    )
    {
        public static final Codec<ProjectileDataRecord> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.FLOAT.fieldOf("size").forGetter(ProjectileDataRecord::size),
                        Codec.FLOAT.fieldOf("speed").forGetter(ProjectileDataRecord::speed),
                        Codec.FLOAT.optionalFieldOf("ink_coverage_on_impact").forGetter(ProjectileDataRecord::inkCoverageImpact),
                        Codec.FLOAT.optionalFieldOf("ink_trail_coverage").forGetter(ProjectileDataRecord::inkTrailCoverage),
                        Codec.INT.optionalFieldOf("ink_trail_tick_interval").forGetter(ProjectileDataRecord::inkTrailCooldown),
                        Codec.INT.fieldOf("count").forGetter(ProjectileDataRecord::count),
                        Codec.FLOAT.fieldOf("angle_between_projectiles").forGetter(ProjectileDataRecord::offsetAngle),
                        Codec.FLOAT.fieldOf("direct_damage").forGetter(ProjectileDataRecord::directDamage),
                        Codec.FLOAT.optionalFieldOf("splash_damage").forGetter(ProjectileDataRecord::splashDamage)
                ).apply(instance, ProjectileDataRecord::new)
        );
    }

    public record ShotDataRecord(
            float pitchCompensation,
            int startupTicks,
            int endlagTicks,
            float inkConsumption,
            int inkRecoveryCooldown
    )
    {
        public static final Codec<ShotDataRecord> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.FLOAT.fieldOf("pitch_compensation").forGetter(ShotDataRecord::pitchCompensation),
                        Codec.INT.fieldOf("startup_ticks").forGetter(ShotDataRecord::startupTicks),
                        Codec.INT.fieldOf("endlag_ticks").forGetter(ShotDataRecord::endlagTicks),
                        Codec.FLOAT.fieldOf("ink_consumption").forGetter(ShotDataRecord::inkConsumption),
                        Codec.INT.fieldOf("ink_recovery_cooldown").forGetter(ShotDataRecord::inkRecoveryCooldown)
                ).apply(instance, ShotDataRecord::new)
        );
    }
}
