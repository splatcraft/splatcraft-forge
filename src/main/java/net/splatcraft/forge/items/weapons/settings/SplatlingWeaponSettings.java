package net.splatcraft.forge.items.weapons.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.splatcraft.forge.util.WeaponTooltip;

import java.util.Optional;

public class SplatlingWeaponSettings extends AbstractWeaponSettings<SplatlingWeaponSettings, SplatlingWeaponSettings.DataRecord>
{
    public FiringData firstChargeLevelData = new FiringData();
    public FiringData secondChargeLevelData = new FiringData();
    public boolean bypassesMobDamage = false;
    
    public int firstLevelChargeTime;
    public int secondLevelChargeTime;
    public int emptyTankFirstLevelChargeTime;
    public int emptyTankSecondLevelChargeTime;
    public int firingDuration;
    public int chargeStorageTime = 0;
    public boolean canRechargeWhileFiring = false;

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

        public Float getAirInaccuracy() {
            return airInaccuracy;
        }

        public Float getBaseDamage() {
            return baseDamage;
        }

        public Float getDamageDecayPerTick() {
            return damageDecayPerTick;
        }

        public Float getDecayedDamage() {
            return decayedDamage;
        }

        public Float getGroundInaccuracy() {
            return groundInaccuracy;
        }

        public Float getInkConsumption() {
            return inkConsumption;
        }

        public Float getPitchCompensation() {
            return pitchCompensation;
        }

        public Float getProjectileDecayedSpeed() {
            return projectileDecayedSpeed;
        }

        public Float getProjectileGravity() {
            return projectileGravity;
        }

        public Float getProjectileInkCoverage() {
            return projectileInkCoverage;
        }

        public Float getProjectileInkTrailCoverage() {
            return projectileInkTrailCoverage;
        }

        public Float getProjectileSize() {
            return projectileSize;
        }

        public Float getProjectileSpeed() {
            return projectileSpeed;
        }

        public Float getStraightShotDistance() {
            return straightShotDistance;
        }

        public int getStraightShotTickTime() {
            return straightShotTickTime;
        }

        public Integer getDamageDecayStartTick() {
            return damageDecayStartTick;
        }

        public Integer getFiringSpeed() {
            return firingSpeed;
        }

        public Integer getInkRecoveryCooldown() {
            return inkRecoveryCooldown;
        }

        public Integer getProjectileCount() {
            return projectileCount;
        }

        public Integer getProjectileInkTrailCooldown() {
            return projectileInkTrailCooldown;
        }

        public Integer getProjectileLifeTicks() {
            return projectileLifeTicks;
        }
    }
    
    public static final SplatlingWeaponSettings DEFAULT = new SplatlingWeaponSettings("default");

    public SplatlingWeaponSettings(String name) {
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

        int e = tickCount - firstChargeLevelData.damageDecayStartTick;
        return Math.max(e > 0 ? firstChargeLevelData.baseDamage - (e * firstChargeLevelData.damageDecayPerTick) : firstChargeLevelData.baseDamage, firstChargeLevelData.decayedDamage);
    }

    @Override
    public float getMinDamage() {
        return firstChargeLevelData.decayedDamage;
    }

    @Override
    public WeaponTooltip<SplatlingWeaponSettings>[] tooltipsToRegister()
    {
        return new WeaponTooltip[]
                {
                        new WeaponTooltip<SplatlingWeaponSettings>("range", WeaponTooltip.Metrics.BLOCKS, settings -> Math.max(settings.firstChargeLevelData.straightShotDistance, settings.secondChargeLevelData.straightShotDistance), WeaponTooltip.RANKER_ASCENDING),
                        new WeaponTooltip<SplatlingWeaponSettings>("charge_speed", WeaponTooltip.Metrics.SECONDS, settings -> (settings.firstLevelChargeTime + settings.secondLevelChargeTime) / 20f, WeaponTooltip.RANKER_DESCENDING),
                        new WeaponTooltip<SplatlingWeaponSettings>("mobility", WeaponTooltip.Metrics.MULTIPLIER, settings -> settings.moveSpeed, WeaponTooltip.RANKER_ASCENDING)
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

        ChargeDataRecord charge = data.charge;

        setFirstLevelChargeTime(charge.firstChargeTime);
        setSecondLevelChargeTime(charge.secondChargeTime);
        charge.emptyTankFirstChargeTime.ifPresent(this::setEmptyTankFirstLevelChargeTime);
        charge.emptyTankSecondChargeTime.ifPresent(this::setEmptyTankSecondLevelChargeTime);
        setFiringDuration(charge.firingDuration);

        charge.chargeStorageTime.ifPresent(this::setChargeStorageTime);
        charge.canRechargeWhileFiring.ifPresent(this::setCanRechargeWhileFiring);
        setMoveSpeed(data.moveSpeed);

        if(data.secondChargeLevelProjectile.isPresent())
        {
            OptionalProjectileDataRecord secondChargeLevelProjectile = data.secondChargeLevelProjectile.get();

            secondChargeLevelProjectile.size.ifPresent(this::setSecondChargeLevelProjectileSize);
            secondChargeLevelProjectile.lifeTicks.ifPresent(this::setSecondChargeLevelProjectileLifeTicks);

            secondChargeLevelProjectile.speed.ifPresent(this::setSecondChargeLevelProjectileSpeed);
            secondChargeLevelProjectile.decayedSpeed.ifPresent(this::setSecondChargeLevelProjectileDecayedSpeed);
            secondChargeLevelProjectile.count.ifPresent(this::setSecondChargeLevelProjectileCount);

            secondChargeLevelProjectile.inkCoverageImpact.ifPresent(this::setSecondChargeLevelProjectileInkCoverage);
            secondChargeLevelProjectile.inkTrailCoverage.ifPresent(this::setSecondChargeLevelProjectileInkTrailCoverage);
            secondChargeLevelProjectile.inkTrailCooldown.ifPresent(this::setSecondChargeLevelProjectileInkTrailCooldown);

            secondChargeLevelProjectile.straightShotDistance.ifPresent(this::setSecondChargeLevelStraightShotDistance);
            secondChargeLevelProjectile.gravity.ifPresent(this::setSecondChargeLevelProjectileGravity);
            
            if(secondChargeLevelProjectile.baseDamage.isPresent())
            {
                setSecondChargeLevelBaseDamage(secondChargeLevelProjectile.baseDamage.get());
                secondChargeLevelProjectile.decayedDamage.ifPresent(this::setSecondChargeLevelDecayedDamage);
                setSecondChargeLevelDamageDecayStartTick(secondChargeLevelProjectile.damageDecayStartTick.orElse(0));
                setSecondChargeLevelDamageDecayPerTick(secondChargeLevelProjectile.damageDecayPerTick.orElse(0f));
            }
        }

        if(data.secondChargeLevelShot.isPresent())
        {
            OptionalShotDataRecord secondChargeLevelShot = data.secondChargeLevelShot.get();

            secondChargeLevelShot.startupTicks.ifPresent(this::setSecondChargeLevelStartupTicks);
            secondChargeLevelShot.firingSpeed.ifPresent(this::setSecondChargeLevelFiringSpeed);

            secondChargeLevelShot.groundInaccuracy.ifPresent(this::setSecondChargeLevelGroundInaccuracy);
            secondChargeLevelShot.airborneInaccuracy.ifPresent(this::setSecondChargeLevelAirInaccuracy);
            secondChargeLevelShot.pitchCompensation.ifPresent(this::setSecondChargeLevelPitchCompensation);

            secondChargeLevelShot.inkConsumption.ifPresent(this::setSecondChargeLevelInkConsumption);
            secondChargeLevelShot.inkRecoveryCooldown.ifPresent(this::setSecondChargeLevelInkRecoveryCooldown);
        }
    }

    @Override
    public DataRecord serialize() {
        return new DataRecord(new ProjectileDataRecord(firstChargeLevelData.projectileSize, Optional.of(firstChargeLevelData.projectileLifeTicks), firstChargeLevelData.projectileSpeed,
                Optional.of(firstChargeLevelData.projectileDecayedSpeed), firstChargeLevelData.straightShotDistance, Optional.of(firstChargeLevelData.projectileGravity), Optional.of(firstChargeLevelData.projectileCount),
                Optional.of(firstChargeLevelData.projectileInkCoverage), Optional.of(firstChargeLevelData.projectileInkTrailCoverage), Optional.of(firstChargeLevelData.projectileInkTrailCooldown),
                firstChargeLevelData.baseDamage, Optional.of(firstChargeLevelData.decayedDamage),
                Optional.of(firstChargeLevelData.damageDecayStartTick), Optional.of(firstChargeLevelData.damageDecayPerTick)),

                new ShotDataRecord(Optional.of(firstChargeLevelData.startupTicks), firstChargeLevelData.firingSpeed, firstChargeLevelData.groundInaccuracy, Optional.of(firstChargeLevelData.airInaccuracy),
                        Optional.of(firstChargeLevelData.pitchCompensation), firstChargeLevelData.inkConsumption, firstChargeLevelData.inkRecoveryCooldown),

                Optional.of(new OptionalProjectileDataRecord(Optional.of(secondChargeLevelData.projectileSize), Optional.of(secondChargeLevelData.projectileLifeTicks), Optional.of(secondChargeLevelData.projectileSpeed),
				        Optional.of(secondChargeLevelData.projectileDecayedSpeed), Optional.of(secondChargeLevelData.straightShotDistance), Optional.of(secondChargeLevelData.projectileGravity), Optional.of(secondChargeLevelData.projectileCount),
				        Optional.of(secondChargeLevelData.projectileInkCoverage), Optional.of(secondChargeLevelData.projectileInkTrailCoverage), Optional.of(secondChargeLevelData.projectileInkTrailCooldown),
				        Optional.of(secondChargeLevelData.baseDamage), Optional.of(secondChargeLevelData.decayedDamage),
				        Optional.of(secondChargeLevelData.damageDecayStartTick), Optional.of(secondChargeLevelData.damageDecayPerTick))),

                Optional.of(new OptionalShotDataRecord(Optional.of(secondChargeLevelData.startupTicks), Optional.of(secondChargeLevelData.firingSpeed), Optional.of(secondChargeLevelData.groundInaccuracy), Optional.of(secondChargeLevelData.airInaccuracy),
				        Optional.of(secondChargeLevelData.pitchCompensation), Optional.of(secondChargeLevelData.inkConsumption), Optional.of(secondChargeLevelData.inkRecoveryCooldown))),

                new ChargeDataRecord(firstLevelChargeTime, secondLevelChargeTime, Optional.of(emptyTankFirstLevelChargeTime), Optional.of(emptyTankSecondLevelChargeTime), firingDuration, Optional.of(chargeStorageTime),
                        Optional.of(canRechargeWhileFiring)),
                moveSpeed, Optional.of(bypassesMobDamage));
    }

    public SplatlingWeaponSettings setBypassesMobDamage(boolean bypassesMobDamage) {
        this.bypassesMobDamage = bypassesMobDamage;
        return this;
    }

    public SplatlingWeaponSettings setCanRechargeWhileFiring(boolean canRechargeWhileFiring) {
        this.canRechargeWhileFiring = canRechargeWhileFiring;
        return this;
    }

    public SplatlingWeaponSettings setChargeStorageTime(int chargeStorageTime) {
        this.chargeStorageTime = chargeStorageTime;
        return this;
    }

    public SplatlingWeaponSettings setFirstLevelChargeTime(int firstLevelChargeTime) {
        this.firstLevelChargeTime = firstLevelChargeTime;
        this.emptyTankFirstLevelChargeTime = firstLevelChargeTime * 6;
        return this;
    }

    public SplatlingWeaponSettings setSecondLevelChargeTime(int secondLevelChargeTime)
    {
        this.secondLevelChargeTime = secondLevelChargeTime;
        this.emptyTankSecondLevelChargeTime = secondLevelChargeTime * 6;
        return this;
    }

    public SplatlingWeaponSettings setEmptyTankFirstLevelChargeTime(int emptyTankFirstLevelChargeTime) {
        this.emptyTankFirstLevelChargeTime = emptyTankFirstLevelChargeTime;
        return this;
    }

    public SplatlingWeaponSettings setEmptyTankSecondLevelChargeTime(int emptyTankSecondLevelChargeTime) {
        this.emptyTankSecondLevelChargeTime = emptyTankSecondLevelChargeTime;
        return this;
    }

    public SplatlingWeaponSettings setFiringDuration(int firingDuration) {
        this.firingDuration = firingDuration;
        return this;
    }

    public SplatlingWeaponSettings setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
        return this;
    }


    public SplatlingWeaponSettings setProjectileSize(float projectileSize)
    {
        firstChargeLevelData.projectileSize = projectileSize;
        firstChargeLevelData.projectileInkCoverage = projectileSize * 0.85f;
        firstChargeLevelData.projectileInkTrailCoverage = projectileSize * 0.75f;
        
        secondChargeLevelData.projectileSize = projectileSize;
        secondChargeLevelData.projectileInkCoverage = projectileSize * 0.85f;
        secondChargeLevelData.projectileInkTrailCoverage = projectileSize * 0.75f;
        return this;
    }

    public SplatlingWeaponSettings setProjectileLifeTicks(int projectileLifeTicks) {
        firstChargeLevelData.projectileLifeTicks = projectileLifeTicks;
        secondChargeLevelData.projectileLifeTicks = projectileLifeTicks;
        return this;
    }

    public SplatlingWeaponSettings setProjectileSpeed(float projectileSpeed) {
        firstChargeLevelData.projectileSpeed = projectileSpeed;
        firstChargeLevelData.projectileDecayedSpeed = projectileSpeed;
        secondChargeLevelData.projectileSpeed = projectileSpeed;
        secondChargeLevelData.projectileDecayedSpeed = projectileSpeed;
        return this;
    }

    public SplatlingWeaponSettings setProjectileDecayedSpeed(float projectileDecayedSpeed) {
        firstChargeLevelData.projectileDecayedSpeed = projectileDecayedSpeed;
        secondChargeLevelData.projectileDecayedSpeed = projectileDecayedSpeed;
        return this;
    }

    public SplatlingWeaponSettings setProjectileCount(int projectileCount) {
        firstChargeLevelData.projectileCount = projectileCount;
        secondChargeLevelData.projectileCount = projectileCount;
        return this;
    }

    public SplatlingWeaponSettings setPitchCompensation(float pitchCompensation) {
        firstChargeLevelData.pitchCompensation = pitchCompensation;
        secondChargeLevelData.pitchCompensation = pitchCompensation;
        return this;
    }

    public SplatlingWeaponSettings setFiringSpeed(int firingSpeed) {
        firstChargeLevelData.firingSpeed = firingSpeed;
        secondChargeLevelData.firingSpeed = firingSpeed;
        return this;
    }

    public SplatlingWeaponSettings setStartupTicks(int startupTicks) {
        firstChargeLevelData.startupTicks = startupTicks;
        secondChargeLevelData.startupTicks = startupTicks;
        return this;
    }

    public SplatlingWeaponSettings setGroundInaccuracy(float groundInaccuracy) {
        firstChargeLevelData.groundInaccuracy = groundInaccuracy;
        firstChargeLevelData.airInaccuracy = groundInaccuracy;
        secondChargeLevelData.groundInaccuracy = groundInaccuracy;
        secondChargeLevelData.airInaccuracy = groundInaccuracy;
        return this;
    }

    public SplatlingWeaponSettings setAirInaccuracy(float airInaccuracy) {
        firstChargeLevelData.airInaccuracy = airInaccuracy;
        secondChargeLevelData.airInaccuracy = airInaccuracy;
        return this;
    }

    public SplatlingWeaponSettings setInkConsumption(float inkConsumption) {
        firstChargeLevelData.inkConsumption = inkConsumption;
        secondChargeLevelData.inkConsumption = inkConsumption;
        return this;
    }

    public SplatlingWeaponSettings setInkRecoveryCooldown(int inkRecoveryCooldown) {
        firstChargeLevelData.inkRecoveryCooldown = inkRecoveryCooldown;
        secondChargeLevelData.inkRecoveryCooldown = inkRecoveryCooldown;
        return this;
    }

    public SplatlingWeaponSettings setBaseDamage(float baseDamage) {
        firstChargeLevelData.baseDamage = baseDamage;
        firstChargeLevelData.decayedDamage = baseDamage;
        secondChargeLevelData.baseDamage = baseDamage;
        secondChargeLevelData.decayedDamage = baseDamage;
        return this;
    }

    public SplatlingWeaponSettings setDecayedDamage(float decayedDamage) {
        firstChargeLevelData.decayedDamage = decayedDamage;
        secondChargeLevelData.decayedDamage = decayedDamage;
        return this;
    }

    public SplatlingWeaponSettings setDamageDecayStartTick(int damageDecayStartTick) {
        firstChargeLevelData.damageDecayStartTick = damageDecayStartTick;
        secondChargeLevelData.damageDecayStartTick = damageDecayStartTick;
        return this;
    }

    public SplatlingWeaponSettings setDamageDecayPerTick(float damageDecayPerTick) {
        firstChargeLevelData.damageDecayPerTick = damageDecayPerTick;
        secondChargeLevelData.damageDecayPerTick = damageDecayPerTick;
        return this;
    }

    public SplatlingWeaponSettings setProjectileGravity(float projectileGravity) {
        firstChargeLevelData.projectileGravity = projectileGravity;
        secondChargeLevelData.projectileGravity = projectileGravity;
        return this;
    }

    public SplatlingWeaponSettings setProjectileInkCoverage(float projectileInkCoverage) {
        firstChargeLevelData.projectileInkCoverage = projectileInkCoverage;
        secondChargeLevelData.projectileInkCoverage = projectileInkCoverage;
        return this;
    }

    public SplatlingWeaponSettings setProjectileInkTrailCooldown(int projectileInkTrailCooldown) {
        firstChargeLevelData.projectileInkTrailCooldown = projectileInkTrailCooldown;
        secondChargeLevelData.projectileInkTrailCooldown = projectileInkTrailCooldown;
        return this;
    }

    public SplatlingWeaponSettings setProjectileInkTrailCoverage(float projectileInkTrailCoverage) {
        firstChargeLevelData.projectileInkTrailCoverage = projectileInkTrailCoverage;
        secondChargeLevelData.projectileInkTrailCoverage = projectileInkTrailCoverage;
        return this;
    }

    public SplatlingWeaponSettings setStraightShotDistance(float blocks)
    {
        float speedAvg = (firstChargeLevelData.projectileSpeed + firstChargeLevelData.projectileDecayedSpeed) * 0.5f;
        firstChargeLevelData.straightShotTickTime = (int) (blocks / speedAvg);
        firstChargeLevelData.straightShotDistance = firstChargeLevelData.straightShotTickTime * speedAvg; //math so that weapon stat tooltips always yield accurate results
        return this;
    }

    public SplatlingWeaponSettings setSecondChargeLevelProjectileSize(float projectileSize)
    {
        secondChargeLevelData.projectileSize = projectileSize;
        secondChargeLevelData.projectileInkCoverage = projectileSize * 0.85f;
        secondChargeLevelData.projectileInkTrailCoverage = projectileSize * 0.75f;
        return this;
    }

    public SplatlingWeaponSettings setSecondChargeLevelProjectileLifeTicks(int projectileLifeTicks) {
        secondChargeLevelData.projectileLifeTicks = projectileLifeTicks;
        return this;
    }

    public SplatlingWeaponSettings setSecondChargeLevelProjectileSpeed(float projectileSpeed) {
        secondChargeLevelData.projectileSpeed = projectileSpeed;
        secondChargeLevelData.projectileDecayedSpeed = projectileSpeed;
        return this;
    }

    public SplatlingWeaponSettings setSecondChargeLevelProjectileDecayedSpeed(float projectileDecayedSpeed) {
        secondChargeLevelData.projectileDecayedSpeed = projectileDecayedSpeed;
        return this;
    }

    public SplatlingWeaponSettings setSecondChargeLevelProjectileCount(int projectileCount) {
        secondChargeLevelData.projectileCount = projectileCount;
        return this;
    }

    public SplatlingWeaponSettings setSecondChargeLevelPitchCompensation(float pitchCompensation) {
        secondChargeLevelData.pitchCompensation = pitchCompensation;
        return this;
    }

    public SplatlingWeaponSettings setSecondChargeLevelFiringSpeed(int firingSpeed) {
        secondChargeLevelData.firingSpeed = firingSpeed;
        return this;
    }

    public SplatlingWeaponSettings setSecondChargeLevelStartupTicks(int startupTicks) {
        secondChargeLevelData.startupTicks = startupTicks;
        return this;
    }

    public SplatlingWeaponSettings setSecondChargeLevelGroundInaccuracy(float groundInaccuracy) {
        secondChargeLevelData.groundInaccuracy = groundInaccuracy;
        secondChargeLevelData.airInaccuracy = groundInaccuracy;
        return this;
    }

    public SplatlingWeaponSettings setSecondChargeLevelAirInaccuracy(float airInaccuracy) {
        secondChargeLevelData.airInaccuracy = airInaccuracy;
        return this;
    }

    public SplatlingWeaponSettings setSecondChargeLevelInkConsumption(float inkConsumption) {
        secondChargeLevelData.inkConsumption = inkConsumption;
        return this;
    }

    public SplatlingWeaponSettings setSecondChargeLevelInkRecoveryCooldown(int inkRecoveryCooldown) {
        secondChargeLevelData.inkRecoveryCooldown = inkRecoveryCooldown;
        return this;
    }

    public SplatlingWeaponSettings setSecondChargeLevelBaseDamage(float baseDamage) {
        secondChargeLevelData.baseDamage = baseDamage;
        secondChargeLevelData.decayedDamage = baseDamage;
        return this;
    }

    public SplatlingWeaponSettings setSecondChargeLevelDecayedDamage(float decayedDamage) {
        secondChargeLevelData.decayedDamage = decayedDamage;
        return this;
    }

    public SplatlingWeaponSettings setSecondChargeLevelDamageDecayStartTick(int damageDecayStartTick) {
        secondChargeLevelData.damageDecayStartTick = damageDecayStartTick;
        return this;
    }

    public SplatlingWeaponSettings setSecondChargeLevelDamageDecayPerTick(float damageDecayPerTick) {
        secondChargeLevelData.damageDecayPerTick = damageDecayPerTick;
        return this;
    }

    public SplatlingWeaponSettings setSecondChargeLevelProjectileGravity(float projectileGravity) {
        secondChargeLevelData.projectileGravity = projectileGravity;
        return this;
    }

    public SplatlingWeaponSettings setSecondChargeLevelProjectileInkCoverage(float projectileInkCoverage) {
        secondChargeLevelData.projectileInkCoverage = projectileInkCoverage;
        return this;
    }

    public SplatlingWeaponSettings setSecondChargeLevelProjectileInkTrailCooldown(int projectileInkTrailCooldown) {
        secondChargeLevelData.projectileInkTrailCooldown = projectileInkTrailCooldown;
        return this;
    }

    public SplatlingWeaponSettings setSecondChargeLevelProjectileInkTrailCoverage(float projectileInkTrailCoverage) {
        secondChargeLevelData.projectileInkTrailCoverage = projectileInkTrailCoverage;
        return this;
    }

    public SplatlingWeaponSettings setSecondChargeLevelStraightShotDistance(float blocks)
    {
        float speedAvg = (secondChargeLevelData.projectileSpeed + secondChargeLevelData.projectileDecayedSpeed) * 0.5f;
        secondChargeLevelData.straightShotTickTime = (int) (blocks / speedAvg);
        secondChargeLevelData.straightShotDistance = secondChargeLevelData.straightShotTickTime * speedAvg; //math so that weapon stat tooltips always yield accurate results
        return this;
    }

    public int getDualieOffhandFiringOffset(boolean secondChargeLevel)
    {
        return firstChargeLevelData.firingSpeed / 2;
    }

    public record DataRecord(
        ProjectileDataRecord projectile,
        ShotDataRecord shot,
        Optional<OptionalProjectileDataRecord> secondChargeLevelProjectile,
        Optional<OptionalShotDataRecord> secondChargeLevelShot,
        ChargeDataRecord charge,
        float moveSpeed,
        Optional<Boolean> bypassesMobDamage
    )
    {
        public static final Codec<DataRecord> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        ProjectileDataRecord.CODEC.fieldOf("projectile").forGetter(DataRecord::projectile),
                        ShotDataRecord.CODEC.fieldOf("shot").forGetter(DataRecord::shot),
                        OptionalProjectileDataRecord.CODEC.optionalFieldOf("second_charge_projectile").forGetter(DataRecord::secondChargeLevelProjectile),
                        OptionalShotDataRecord.CODEC.optionalFieldOf("second_charge_shot").forGetter(DataRecord::secondChargeLevelShot),
                        ChargeDataRecord.CODEC.fieldOf("charge").forGetter(DataRecord::charge),
                        Codec.FLOAT.fieldOf("mobility").forGetter(DataRecord::moveSpeed),
                        Codec.BOOL.optionalFieldOf("full_damage_to_mobs").forGetter(DataRecord::bypassesMobDamage)
                ).apply(instance, DataRecord::new)
        );
    }


    public record ChargeDataRecord(
            int firstChargeTime,
            int secondChargeTime,
            Optional<Integer> emptyTankFirstChargeTime,
            Optional<Integer> emptyTankSecondChargeTime,
            int firingDuration,
            Optional<Integer> chargeStorageTime,
            Optional<Boolean> canRechargeWhileFiring

    )
    {
        public static final Codec<ChargeDataRecord> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.INT.fieldOf("first_charge_time_ticks").forGetter(ChargeDataRecord::firstChargeTime),
                        Codec.INT.fieldOf("second_charge_time_ticks").forGetter(ChargeDataRecord::secondChargeTime),
                        Codec.INT.optionalFieldOf("empty_tank_first_charge_time_ticks").forGetter(ChargeDataRecord::emptyTankFirstChargeTime),
                        Codec.INT.optionalFieldOf("empty_tank_second_charge_time_ticks").forGetter(ChargeDataRecord::emptyTankSecondChargeTime),
                        Codec.INT.fieldOf("total_firing_duration").forGetter(ChargeDataRecord::firingDuration),
                        Codec.INT.optionalFieldOf("charge_storage_ticks").forGetter(ChargeDataRecord::chargeStorageTime),
                        Codec.BOOL.optionalFieldOf("can_recharge_while_firing").forGetter(ChargeDataRecord::canRechargeWhileFiring)
                ).apply(instance, ChargeDataRecord::new)
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
