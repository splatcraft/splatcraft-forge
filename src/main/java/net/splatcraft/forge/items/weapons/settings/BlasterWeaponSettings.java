package net.splatcraft.forge.items.weapons.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.splatcraft.forge.util.WeaponTooltip;

import java.util.Optional;

public class BlasterWeaponSettings extends AbstractWeaponSettings<BlasterWeaponSettings, BlasterWeaponSettings.DataRecord>
{
    public float projectileSize;
    public float projectileExplosionRadius;
    public float projectileInkTrailCoverage;
    public int projectileInkTrailCooldown = 0;
    public int projectileLifeTicks;
    public float projectileRange;
    public float projectileSpeed;
    public int endlagTicks;
    public int startupTicks;

    public float groundInaccuracy;
    public float airInaccuracy;

    public float inkConsumption;
    public int inkRecoveryCooldown;

    public float directDamage;
    public float splashDamage;

    public boolean bypassesMobDamage;

    public static final BlasterWeaponSettings DEFAULT = new BlasterWeaponSettings("default");

    public BlasterWeaponSettings(String name) {
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
    public WeaponTooltip<BlasterWeaponSettings>[] tooltipsToRegister()
    {
        return new WeaponTooltip[]
        {
            new WeaponTooltip<BlasterWeaponSettings>("range", WeaponTooltip.Metrics.BLOCKS, settings -> settings.projectileRange, WeaponTooltip.RANKER_ASCENDING),
            new WeaponTooltip<BlasterWeaponSettings>("direct_damage", WeaponTooltip.Metrics.HEALTH, settings -> settings.directDamage, WeaponTooltip.RANKER_ASCENDING),
            new WeaponTooltip<BlasterWeaponSettings>("fire_rate", WeaponTooltip.Metrics.TICKS, settings -> settings.startupTicks + settings.endlagTicks, WeaponTooltip.RANKER_DESCENDING)
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

        data.fullDamageToMobs.ifPresent(this::setBypassesMobDamage);

        setProjectileSize(projectile.size);
        setProjectileSpeed(projectile.speed);
        setProjectileRange(projectile.range);

        projectile.inkExplosionRadius.ifPresent(this::setProjectileExplosionRadius);
        projectile.inkTrailCoverage.ifPresent(this::setProjectileInkTrailCoverage);
        projectile.inkTrailCooldown.ifPresent(this::setProjectileInkTrailCooldown);

        setDirectDamage(projectile.directDamage);
        setSplashDamage(projectile.splashDamage);

        ShotDataRecord shot = data.shot;

        setStartupTicks(shot.startupTicks);
        setEndlagTicks(shot.endlagTicks);
        setGroundInaccuracy(shot.groundInaccuracy.orElse(0f));
        setAirInaccuracy(shot.airborneInaccuracy.orElse(0f));

        setInkConsumption(shot.inkConsumption);
        setInkRecoveryCooldown(shot.inkRecoveryCooldown);

    }

    @Override
    public DataRecord serialize() {
        return new DataRecord(new ProjectileDataRecord(projectileSize, Optional.of(projectileExplosionRadius), projectileRange, projectileSpeed, Optional.of(projectileInkTrailCoverage), Optional.of(projectileInkTrailCooldown),
                directDamage, splashDamage), new ShotDataRecord(startupTicks, endlagTicks, Optional.of(groundInaccuracy), Optional.of(airInaccuracy), inkConsumption, inkRecoveryCooldown), Optional.of(bypassesMobDamage));
    }

    public BlasterWeaponSettings setBypassesMobDamage(boolean bypassesMobDamage) {
        this.bypassesMobDamage = bypassesMobDamage;
        return this;
    }

    public BlasterWeaponSettings setProjectileSize(float projectileSize)
    {
        this.projectileSize = projectileSize;
        projectileExplosionRadius = projectileSize * .85f;
        projectileInkTrailCoverage = projectileSize * 0.5f;

        return this;
    }

    public BlasterWeaponSettings setProjectileExplosionRadius(float projectileInkExplosionCoverage) {
        this.projectileExplosionRadius = projectileInkExplosionCoverage;
        return this;
    }

    public BlasterWeaponSettings setProjectileInkTrailCooldown(int projectileInkTrailCooldown) {
        this.projectileInkTrailCooldown = projectileInkTrailCooldown;
        return this;
    }

    public BlasterWeaponSettings setProjectileInkTrailCoverage(float projectileInkTrailCoverage) {
        this.projectileInkTrailCoverage = projectileInkTrailCoverage;
        return this;
    }

    public BlasterWeaponSettings setProjectileRange(float blocks)
    {
        this.projectileLifeTicks = (int) (blocks/ projectileSpeed);
        this.projectileRange = projectileLifeTicks * projectileSpeed; //math so that weapon stat tooltips always yield accurate results
        return this;
    }

    public BlasterWeaponSettings setProjectileSpeed(float projectileSpeed) {
        this.projectileSpeed = projectileSpeed;
        return this;
    }


    public BlasterWeaponSettings setEndlagTicks(int endlagTicks) {
        this.endlagTicks = endlagTicks;
        return this;
    }

    public BlasterWeaponSettings setStartupTicks(int startupTicks) {
        this.startupTicks = startupTicks;
        return this;
    }

    public BlasterWeaponSettings setGroundInaccuracy(float groundInaccuracy) {
        this.groundInaccuracy = groundInaccuracy;
        this.airInaccuracy = groundInaccuracy;
        return this;
    }

    public BlasterWeaponSettings setAirInaccuracy(float airInaccuracy) {
        this.airInaccuracy = airInaccuracy;
        return this;
    }

    public BlasterWeaponSettings setInkConsumption(float inkConsumption) {
        this.inkConsumption = inkConsumption;
        return this;
    }

    public BlasterWeaponSettings setInkRecoveryCooldown(int inkRecoveryCooldown) {
        this.inkRecoveryCooldown = inkRecoveryCooldown;
        return this;
    }

    public BlasterWeaponSettings setDirectDamage(float directDamage) {
        this.directDamage = directDamage;
        return this;
    }

    public BlasterWeaponSettings setSplashDamage(float splashDamage) {
        this.splashDamage = splashDamage;
        return this;
    }

    public record DataRecord(
        ProjectileDataRecord projectile,
        ShotDataRecord shot,
        Optional<Boolean> fullDamageToMobs
    )
    {
        public static final Codec<DataRecord> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        ProjectileDataRecord.CODEC.fieldOf("projectile").forGetter(DataRecord::projectile),
                        ShotDataRecord.CODEC.fieldOf("shot").forGetter(DataRecord::shot),
                        Codec.BOOL.optionalFieldOf("full_damage_to_mobs").forGetter(DataRecord::fullDamageToMobs)
                ).apply(instance, DataRecord::new)
        );
    }

    record ProjectileDataRecord(
            float size,
            Optional<Float> inkExplosionRadius,
            float range,
            float speed,
            Optional<Float> inkTrailCoverage,
            Optional<Integer> inkTrailCooldown,
            float directDamage,
            float splashDamage
    )
    {
        public static final Codec<ProjectileDataRecord> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.FLOAT.fieldOf("size").forGetter(ProjectileDataRecord::size),
                        Codec.FLOAT.optionalFieldOf("ink_explosion_radius").forGetter(ProjectileDataRecord::inkExplosionRadius),
                        Codec.FLOAT.fieldOf("range").forGetter(ProjectileDataRecord::range),
                        Codec.FLOAT.fieldOf("speed").forGetter(ProjectileDataRecord::speed),
                        Codec.FLOAT.optionalFieldOf("ink_trail_coverage").forGetter(ProjectileDataRecord::inkTrailCoverage),
                        Codec.INT.optionalFieldOf("ink_trail_tick_interval").forGetter(ProjectileDataRecord::inkTrailCooldown),
                        Codec.FLOAT.fieldOf("direct_damage").forGetter(ProjectileDataRecord::directDamage),
                        Codec.FLOAT.fieldOf("splash_damage").forGetter(ProjectileDataRecord::splashDamage)
                ).apply(instance, ProjectileDataRecord::new)
        );
    }

    public record ShotDataRecord(
            int startupTicks,
            int endlagTicks,
            Optional<Float> groundInaccuracy,
            Optional<Float> airborneInaccuracy,
            float inkConsumption,
            int inkRecoveryCooldown
    )
    {
        public static final Codec<ShotDataRecord> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.INT.fieldOf("startup_ticks").forGetter(ShotDataRecord::startupTicks),
            Codec.INT.fieldOf("endlag_ticks").forGetter(ShotDataRecord::endlagTicks),
            Codec.FLOAT.optionalFieldOf("ground_inaccuracy").forGetter(ShotDataRecord::groundInaccuracy),
            Codec.FLOAT.optionalFieldOf("airborne_inaccuracy").forGetter(ShotDataRecord::airborneInaccuracy),
            Codec.FLOAT.fieldOf("ink_consumption").forGetter(ShotDataRecord::inkConsumption),
            Codec.INT.fieldOf("ink_recovery_cooldown").forGetter(ShotDataRecord::inkRecoveryCooldown)
                ).apply(instance, ShotDataRecord::new)
        );
    }
}
