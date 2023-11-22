package net.splatcraft.forge.items.weapons.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.splatcraft.forge.util.WeaponTooltip;

import java.util.Optional;

public class ChargerWeaponSettings extends AbstractWeaponSettings<ChargerWeaponSettings, ChargerWeaponSettings.DataRecord>
{
    public float projectileSize;
    public float projectileInkCoverage;
    public float projectileInkTrailCoverage;
    public int projectileInkTrailCooldown = 0;
    public float projectileSpeed;
    public int minProjectileLifeTicks;
    public float minProjectileRange;
    public int maxProjectileLifeTicks;
    public float maxProjectileRange;
    public int endLagTicks;

    public float minInkConsumption;
    public float maxInkConsumption;
    public int inkRecoveryCooldown;

    public float baseChargeDamage;
    public float minChargeDamage;
    public float chargedDamage;

    public float chargingWalkSpeed;
    public float piercesAtCharge = 2;
    public int chargeTimeTicks;
    public float chargeSpeed;
    public int airborneChargeTimeTicks;
    public float airborneChargeSpeed;
    public int emptyTankChargeTimeTicks;
    public float emptyTankChargeSpeed;
    public int chargeStorageTicks;
    public float chargeStorageSpeed;
    public boolean bypassesMobDamage;

    public static final ChargerWeaponSettings DEFAULT = new ChargerWeaponSettings("default");

    public ChargerWeaponSettings(String name) {
        super(name);
    }

    @Override
    public float calculateDamage(int tickCount, boolean airborne, float charge, boolean isOnRollCooldown)
    {
        return charge >= 1.0f ? chargedDamage : minChargeDamage + (baseChargeDamage - minChargeDamage) * charge;
    }

    @Override
    public float getMinDamage() {
        return minChargeDamage;
    }

    @Override
    public WeaponTooltip<ChargerWeaponSettings>[] tooltipsToRegister()
    {
        return new WeaponTooltip[]
                {
                        new WeaponTooltip<ChargerWeaponSettings>("range", WeaponTooltip.Metrics.BLOCKS, settings -> settings.maxProjectileRange, WeaponTooltip.RANKER_ASCENDING),
                        new WeaponTooltip<ChargerWeaponSettings>("charge_speed", WeaponTooltip.Metrics.SECONDS, settings -> settings.chargeTimeTicks / 20f, WeaponTooltip.RANKER_DESCENDING),
                        new WeaponTooltip<ChargerWeaponSettings>("mobility", WeaponTooltip.Metrics.MULTIPLIER, settings -> settings.chargingWalkSpeed, WeaponTooltip.RANKER_ASCENDING)
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
        projectile.inkCoverageImpact.ifPresent(this::setProjectileInkCoverage);
        setProjectileSpeed(projectile.speed);
        setMinProjectileRange(projectile.minRange);
        setMaxProjectileRange(projectile.maxRange);

        projectile.inkTrailCoverage.ifPresent(this::setProjectileInkTrailCoverage);
        projectile.inkTrailCooldown.ifPresent(this::setProjectileInkTrailCooldown);

        setMinChargeDamage(projectile.minChargeDamage);
        setBaseChargeDamage(projectile.baseChargeDamage);
        projectile.fullyChargedDamage.ifPresent(this::setChargedDamage);
        projectile.piercesAtCharge.ifPresent(this::setPiercesAtCharge);

        ShotDataRecord shot = data.shot;

        setEndLagTicks(shot.endlagTicks);
        setMinInkConsumption(shot.minInkConsumption);
        setMaxInkConsumption(shot.maxInkConsumption);
        setInkRecoveryCooldown(shot.inkRecoveryCooldown);

        ChargeDataRecord charge = data.charge;

        setChargeTimeTicks(charge.chargeTime);
        charge.airborneChargeTime.ifPresent(this::setAirborneChargeTimeTicks);
        charge.emptyTankChargeTime.ifPresent(this::setEmptyTankChargeTimeTicks);
        setChargeStorageTicks(charge.chargeStorageTime);
        setChargingWalkSpeed(charge.chargeMoveSpeed);
    }

    @Override
    public DataRecord serialize() {
        return new DataRecord(new ProjectileDataRecord(projectileSize, Optional.of(projectileInkCoverage), minProjectileRange, maxProjectileRange, projectileSpeed,
                Optional.of(projectileInkTrailCoverage), Optional.of(projectileInkTrailCooldown), minChargeDamage, baseChargeDamage, Optional.of(chargedDamage), Optional.of(piercesAtCharge)),
                new ShotDataRecord(endLagTicks, minInkConsumption, maxInkConsumption, inkRecoveryCooldown),
                new ChargeDataRecord(chargeTimeTicks, Optional.of(airborneChargeTimeTicks), Optional.of(emptyTankChargeTimeTicks), chargeStorageTicks, chargingWalkSpeed),
                Optional.of(bypassesMobDamage));
    }

    public ChargerWeaponSettings setProjectileSize(float projectileSize) {
        this.projectileSize = projectileSize;
        projectileInkCoverage = projectileSize * .85f;
        projectileInkTrailCoverage = projectileSize * 1.1f;
        return this;
    }

    public ChargerWeaponSettings setProjectileSpeed(float projectileSpeed) {
        this.projectileSpeed = projectileSpeed;
        return this;
    }

    public ChargerWeaponSettings setMaxInkConsumption(float maxInkConsumption) {
        this.maxInkConsumption = maxInkConsumption;
        return this;
    }

    public ChargerWeaponSettings setMinInkConsumption(float minInkConsumption) {
        this.minInkConsumption = minInkConsumption;
        return this;
    }

    public ChargerWeaponSettings setInkRecoveryCooldown(int inkRecoveryCooldown) {
        this.inkRecoveryCooldown = inkRecoveryCooldown;
        return this;
    }

    public ChargerWeaponSettings setBaseChargeDamage(float baseChargeDamage) {
        this.baseChargeDamage = baseChargeDamage;
        this.chargedDamage = baseChargeDamage;
        return this;
    }

    public ChargerWeaponSettings setChargedDamage(float chargedDamage) {
        this.chargedDamage = chargedDamage;
        return this;
    }


    public ChargerWeaponSettings setChargeStorageTicks(int chargeStorageTicks) {
        this.chargeStorageTicks = chargeStorageTicks;
        this.chargeStorageSpeed = 1f/chargeStorageTicks;
        return this;
    }

    public ChargerWeaponSettings setChargeTimeTicks(int chargeTimeTicks)
    {
        this.chargeTimeTicks = chargeTimeTicks;
        chargeSpeed = 1f/chargeTimeTicks;
        setAirborneChargeTimeTicks((int) (chargeTimeTicks / 0.33f));
        setEmptyTankChargeTimeTicks((int) (chargeTimeTicks / 0.33f));

        return this;
    }

    public ChargerWeaponSettings setAirborneChargeTimeTicks(int airborneChargeTimeTicks)
    {
        this.airborneChargeTimeTicks = airborneChargeTimeTicks;
        airborneChargeSpeed = 1f/airborneChargeTimeTicks;
        return this;
    }

    public ChargerWeaponSettings setEmptyTankChargeTimeTicks(int emptyTankChargeTimeTicks)
    {
        this.emptyTankChargeTimeTicks = emptyTankChargeTimeTicks;
        emptyTankChargeSpeed = 1f/emptyTankChargeTimeTicks;
        return this;
    }

    public ChargerWeaponSettings setChargingWalkSpeed(float chargingWalkSpeed) {
        this.chargingWalkSpeed = chargingWalkSpeed;
        return this;
    }

    public ChargerWeaponSettings setEndLagTicks(int endLagTicks) {
        this.endLagTicks = endLagTicks;
        return this;
    }

    public ChargerWeaponSettings setMinChargeDamage(float minChargeDamage) {
        this.minChargeDamage = minChargeDamage;
        return this;
    }

    public ChargerWeaponSettings setPiercesAtCharge(float piercesAtCharge) {
        this.piercesAtCharge = piercesAtCharge;
        return this;
    }

    public ChargerWeaponSettings setProjectileInkCoverage(float projectileInkCoverage) {
        this.projectileInkCoverage = projectileInkCoverage;
        return this;
    }

    public ChargerWeaponSettings setMinProjectileRange(float blocks)
    {
        this.minProjectileLifeTicks = (int) (blocks/ projectileSpeed);
        this.minProjectileRange = minProjectileLifeTicks * projectileSpeed; //math so that weapon stat tooltips always yield accurate results
        return this;
    }

    public ChargerWeaponSettings setMaxProjectileRange(float blocks)
    {
        this.maxProjectileLifeTicks = (int) (blocks/ projectileSpeed);
        this.maxProjectileRange = maxProjectileLifeTicks * projectileSpeed; //math so that weapon stat tooltips always yield accurate results
        return this;
    }

    public ChargerWeaponSettings setProjectileInkTrailCooldown(int projectileInkTrailCooldown) {
        this.projectileInkTrailCooldown = projectileInkTrailCooldown;
        return this;
    }

    public ChargerWeaponSettings setProjectileInkTrailCoverage(float projectileInkTrailCoverage) {
        this.projectileInkTrailCoverage = projectileInkTrailCoverage;
        return this;
    }

    public ChargerWeaponSettings setBypassesMobDamage(boolean bypassesMobDamage) {
        this.bypassesMobDamage = bypassesMobDamage;
        return this;
    }

    public record DataRecord(
        ProjectileDataRecord projectile,
        ShotDataRecord shot,
        ChargeDataRecord charge,
        Optional<Boolean> fullDamageToMobs
    )
    {
        public static final Codec<DataRecord> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        ProjectileDataRecord.CODEC.fieldOf("projectile").forGetter(DataRecord::projectile),
                        ShotDataRecord.CODEC.fieldOf("shot").forGetter(DataRecord::shot),
                        ChargeDataRecord.CODEC.fieldOf("charge").forGetter(DataRecord::charge),
                        Codec.BOOL.optionalFieldOf("full_damage_to_mobs").forGetter(DataRecord::fullDamageToMobs)
                ).apply(instance, DataRecord::new)
        );
    }

    record ProjectileDataRecord(
            float size,
            Optional<Float> inkCoverageImpact,
            float minRange,
            float maxRange,
            float speed,
            Optional<Float> inkTrailCoverage,
            Optional<Integer> inkTrailCooldown,
            float minChargeDamage,
            float baseChargeDamage,
            Optional<Float> fullyChargedDamage,
            Optional<Float> piercesAtCharge
    )
    {
        public static final Codec<ProjectileDataRecord> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.FLOAT.fieldOf("size").forGetter(ProjectileDataRecord::size),
                        Codec.FLOAT.optionalFieldOf("ink_coverage_on_impact").forGetter(ProjectileDataRecord::inkCoverageImpact),
                        Codec.FLOAT.fieldOf("min_charge_range").forGetter(ProjectileDataRecord::minRange),
                        Codec.FLOAT.fieldOf("max_charge_range").forGetter(ProjectileDataRecord::maxRange),
                        Codec.FLOAT.fieldOf("speed").forGetter(ProjectileDataRecord::speed),
                        Codec.FLOAT.optionalFieldOf("ink_trail_coverage").forGetter(ProjectileDataRecord::inkTrailCoverage),
                        Codec.INT.optionalFieldOf("ink_trail_tick_interval").forGetter(ProjectileDataRecord::inkTrailCooldown),
                        Codec.FLOAT.fieldOf("min_partial_charge_damage").forGetter(ProjectileDataRecord::minChargeDamage),
                        Codec.FLOAT.fieldOf("max_partial_charge_damage").forGetter(ProjectileDataRecord::baseChargeDamage),
                        Codec.FLOAT.optionalFieldOf("fully_charged_damage").forGetter(ProjectileDataRecord::fullyChargedDamage),
                        Codec.FLOAT.optionalFieldOf("pierces_at_charge").forGetter(ProjectileDataRecord::piercesAtCharge)
                ).apply(instance, ProjectileDataRecord::new)
        );
    }

    public record ChargeDataRecord(
            int chargeTime,
            Optional<Integer> airborneChargeTime,
            Optional<Integer> emptyTankChargeTime,
            int chargeStorageTime,
            float chargeMoveSpeed
    )
    {
        public static final Codec<ChargeDataRecord> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.INT.fieldOf("charge_time_ticks").forGetter(ChargeDataRecord::chargeTime),
                        Codec.INT.optionalFieldOf("airborne_charge_time_ticks").forGetter(ChargeDataRecord::airborneChargeTime),
                        Codec.INT.optionalFieldOf("empty_tank_charge_time_ticks").forGetter(ChargeDataRecord::emptyTankChargeTime),
                        Codec.INT.fieldOf("charge_storage_ticks").forGetter(ChargeDataRecord::chargeStorageTime),
                        Codec.FLOAT.fieldOf("charging_move_speed").forGetter(ChargeDataRecord::chargeMoveSpeed)
                ).apply(instance, ChargeDataRecord::new)
        );
    }

    public record ShotDataRecord(
            int endlagTicks,
            float minInkConsumption,
            float maxInkConsumption,
            int inkRecoveryCooldown

    )
    {
        public static final Codec<ShotDataRecord> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.INT.fieldOf("endlag_ticks").forGetter(ShotDataRecord::endlagTicks),
                        Codec.FLOAT.fieldOf("min_charge_ink_consumption").forGetter(ShotDataRecord::minInkConsumption),
                        Codec.FLOAT.fieldOf("full_charge_ink_consumption").forGetter(ShotDataRecord::maxInkConsumption),
                        Codec.INT.fieldOf("ink_recovery_cooldown").forGetter(ShotDataRecord::inkRecoveryCooldown)
                ).apply(instance, ShotDataRecord::new)
        );
    }
}
