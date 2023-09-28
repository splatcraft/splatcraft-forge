package net.splatcraft.forge.items.weapons.settings;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.GsonHelper;

import java.util.Optional;

public class RollerWeaponSettings extends AbstractWeaponSettings<RollerWeaponSettings.DataRecord> {
    public static final RollerWeaponSettings DEFAULT = new RollerWeaponSettings("default");
    public String name;
    public boolean isBrush;

    public int rollSize;
    public int rollHitboxSize;
    public float rollConsumption;
    public int rollInkRecoveryCooldown;
    public float rollDamage;
    public float rollMobility;

    public float dashMobility;
    public float dashConsumption;
    public int dashTime = 1;

    public float swingMobility;
    public float swingConsumption;
    public int swingInkRecoveryCooldown;
    public float swingBaseDamage;
    public int swingDamageDecayStartTick;
    public float swingDamageDecayPerTick;
    public float swingMinDamage;
    public float swingProjectileSpeed;
    public int swingTime;
    public float swingProjectilePitchCompensation;

    public float flingConsumption;
    public int flingInkRecoveryCooldown;
    public float flingBaseDamage;
    public int flingDamageDecayStartTick;
    public float flingDamageDecayPerTick;
    public float flingMinDamage;
    public float flingProjectileSpeed;
    public int flingTime;

    public RollerWeaponSettings(String name) {
        super(name);
    }

    public float calculateDamage(int tickCount, boolean airborne, float charge, boolean isOnRollCooldown) {
        if (airborne) {
            int e = tickCount - flingDamageDecayStartTick;
            return Math.max(e > 0 ? flingBaseDamage - (e * flingDamageDecayPerTick) : flingBaseDamage, flingMinDamage);
        }
        int e = tickCount - swingDamageDecayStartTick;
        return Math.max(e > 0 ? swingBaseDamage - (e * swingDamageDecayPerTick) : swingBaseDamage, swingMinDamage);
    }

    public float getMinDamage() {
        return 0;
    }

    @Override
    public Codec<DataRecord> getCodec() {
        return DataRecord.CODEC;
    }

    @Override
    public void deserialize(DataRecord data)
    {
        setBrush(data.isBrush);

        RollDataRecord roll = data.roll;

        setRollSize(roll.inkSize);
        setRollHitboxSize(roll.hitboxSize);
        setRollConsumption(roll.inkConsumption);
        setRollInkRecoveryCooldown(roll.inkRecoveryCooldown);
        setRollDamage(roll.damage);
        setRollMobility(roll.mobility);

        setDashMobility(roll.dashMobility);
        setDashConsumption(roll.dashConsumption);
        setDashTime(roll.dashTime);

        SwingDataRecord swing = data.swing;

        setSwingMobility(swing.mobility);
        setSwingConsumption(swing.inkConsumption);
        setSwingInkRecoveryCooldown(swing.inkRecoveryCooldown);
        setSwingProjectileSpeed(swing.projectileSpeed);
        setSwingTime(swing.startupTime);
        setSwingProjectilePitchCompensation(swing.projectilePitchCompensation);
        setSwingBaseDamage(swing.baseDamage);
        setSwingMinDamage(swing.minDamage);
        setSwingDamageDecayStartTick(swing.damageDecayStartTick);
        setSwingDamageDecayPerTick(swing.damageDecayPerTick);

        FlingDataRecord fling = data.fling;
        setFlingConsumption(fling.inkConsumption);
        setFlingInkRecoveryCooldown(fling.inkRecoveryCooldown);
        setFlingProjectileSpeed(fling.projectileSpeed);
        setFlingTime(fling.startupTime);
        setFlingBaseDamage(fling.baseDamage);
        setFlingMinDamage(fling.minDamage);
        setFlingDamageDecayStartTick(fling.damageDecayStartTick);
        setFlingDamageDecayPerTick(fling.damageDecayPerTick);
    }

    @Override
    public DataRecord serialize()
    {
        return new DataRecord(isBrush, new RollDataRecord(rollSize, rollHitboxSize, rollConsumption, rollInkRecoveryCooldown, rollDamage, rollMobility, dashMobility, dashConsumption, dashTime),
                new SwingDataRecord(swingMobility, swingConsumption, swingInkRecoveryCooldown, swingProjectileSpeed, swingTime, swingProjectilePitchCompensation, swingBaseDamage, swingMinDamage, swingDamageDecayStartTick, swingDamageDecayPerTick),
                new FlingDataRecord(flingConsumption, flingInkRecoveryCooldown, flingProjectileSpeed, flingTime, flingBaseDamage, flingMinDamage, flingDamageDecayStartTick, flingDamageDecayPerTick));
    }

    public RollerWeaponSettings setName(String name) {
        this.name = name;
        return this;
    }

    public RollerWeaponSettings setBrush(boolean brush) {
        this.isBrush = brush;
        return this;
    }

    public RollerWeaponSettings setRollSize(int rollSize) {
        this.rollSize = rollSize;
        this.rollHitboxSize = rollSize;
        return this;
    }

    public RollerWeaponSettings setRollHitboxSize(int rollHitboxSize) {
        this.rollHitboxSize = rollHitboxSize;
        return this;
    }

    public RollerWeaponSettings setRollConsumption(float rollConsumption) {
        this.rollConsumption = rollConsumption;
        this.dashConsumption = rollConsumption;
        return this;
    }

    public RollerWeaponSettings setRollInkRecoveryCooldown(int rollInkRecoveryCooldown) {
        this.rollInkRecoveryCooldown = rollInkRecoveryCooldown;
        return this;
    }

    public RollerWeaponSettings setRollDamage(float rollDamage) {
        this.rollDamage = rollDamage;
        return this;
    }

    public RollerWeaponSettings setRollMobility(float rollMobility) {
        this.rollMobility = rollMobility;
        this.dashMobility = rollMobility;
        return this;
    }

    public RollerWeaponSettings setDashMobility(float dashMobility) {
        this.dashMobility = dashMobility;
        return this;
    }

    public RollerWeaponSettings setDashConsumption(float dashConsumption) {
        this.dashConsumption = dashConsumption;
        return this;
    }

    public RollerWeaponSettings setDashTime(int dashTime) {
        this.dashTime = dashTime;
        return this;
    }

    public RollerWeaponSettings setSwingMobility(float swingMobility) {
        this.swingMobility = swingMobility;
        return this;
    }

    public RollerWeaponSettings setSwingConsumption(float swingConsumption) {
        this.swingConsumption = swingConsumption;
        this.flingConsumption = swingConsumption;
        return this;
    }

    public RollerWeaponSettings setSwingInkRecoveryCooldown(int swingInkRecoveryCooldown) {
        this.swingInkRecoveryCooldown = swingInkRecoveryCooldown;
        this.flingInkRecoveryCooldown = swingInkRecoveryCooldown;
        return this;
    }

    public RollerWeaponSettings setSwingBaseDamage(float swingBaseDamage) {
        this.swingBaseDamage = swingBaseDamage;
        this.flingBaseDamage = swingBaseDamage;
        return this;
    }

    public RollerWeaponSettings setSwingDamageDecayStartTick(int swingDamageDecayStartTick) {
        this.swingDamageDecayStartTick = swingDamageDecayStartTick;
        this.flingDamageDecayStartTick = swingDamageDecayStartTick;
        return this;
    }

    public RollerWeaponSettings setSwingDamageDecayPerTick(float swingDamageDecayPerTick) {
        this.swingDamageDecayPerTick = swingDamageDecayPerTick;
        this.flingDamageDecayPerTick = swingDamageDecayPerTick;
        return this;
    }

    public RollerWeaponSettings setSwingMinDamage(float swingMinDamage) {
        this.swingMinDamage = swingMinDamage;
        this.flingMinDamage = swingMinDamage;
        return this;
    }

    public RollerWeaponSettings setSwingProjectileSpeed(float swingProjectileSpeed) {
        this.swingProjectileSpeed = swingProjectileSpeed;
        this.flingProjectileSpeed = swingProjectileSpeed * (isBrush ? 1 : 1.3f);
        return this;
    }

    public RollerWeaponSettings setSwingTime(int swingTime) {
        this.swingTime = swingTime;
        this.flingTime = swingTime;
        return this;
    }

    public RollerWeaponSettings setSwingProjectilePitchCompensation(float swingProjectilePitchCompensation) {
        this.swingProjectilePitchCompensation = swingProjectilePitchCompensation;
        return this;
    }

    public RollerWeaponSettings setFlingConsumption(float flingConsumption) {
        this.flingConsumption = flingConsumption;
        return this;
    }

    public RollerWeaponSettings setFlingInkRecoveryCooldown(int flingInkRecoveryCooldown) {
        this.flingInkRecoveryCooldown = flingInkRecoveryCooldown;
        return this;
    }

    public RollerWeaponSettings setFlingBaseDamage(float flingBaseDamage) {
        this.flingBaseDamage = flingBaseDamage;
        return this;
    }

    public RollerWeaponSettings setFlingDamageDecayStartTick(int flingDamageDecayStartTick) {
        this.flingDamageDecayStartTick = flingDamageDecayStartTick;
        return this;
    }

    public RollerWeaponSettings setFlingDamageDecayPerTick(float flingDamageDecayPerTick) {
        this.flingDamageDecayPerTick = flingDamageDecayPerTick;
        return this;
    }

    public RollerWeaponSettings setFlingMinDamage(float flingMinDamage) {
        this.flingMinDamage = flingMinDamage;
        return this;
    }

    public RollerWeaponSettings setFlingProjectileSpeed(float flingProjectileSpeed) {
        this.flingProjectileSpeed = flingProjectileSpeed;
        return this;
    }

    public RollerWeaponSettings setFlingTime(int flingTime) {
        this.flingTime = flingTime;
        return this;
    }

    public record DataRecord(
        boolean isBrush,
        RollDataRecord roll,
        SwingDataRecord swing,
        FlingDataRecord fling
    )
    {
        public static final Codec<DataRecord> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.BOOL.fieldOf("mobility").forGetter(DataRecord::isBrush),
                        RollDataRecord.CODEC.fieldOf("roll").forGetter(DataRecord::roll),
                        SwingDataRecord.CODEC.fieldOf("swing").forGetter(DataRecord::swing),
                        FlingDataRecord.CODEC.fieldOf("fling").forGetter(DataRecord::fling)
                ).apply(instance, DataRecord::new)
        );
    }

    record RollDataRecord(
           int inkSize,
           int hitboxSize,
           float inkConsumption,
           int inkRecoveryCooldown,
           float damage,
           float mobility,
           float dashMobility,
           float dashConsumption,
           int dashTime
    )
    {
        public static final Codec<RollDataRecord> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.INT.fieldOf("ink_size").forGetter(RollDataRecord::inkSize),
                        Codec.INT.fieldOf("hitbox_size").forGetter(RollDataRecord::hitboxSize),
                        Codec.FLOAT.fieldOf("ink_consumption").forGetter(RollDataRecord::inkConsumption),
                        Codec.INT.fieldOf("ink_recovery_cooldown").forGetter(RollDataRecord::inkRecoveryCooldown),
                        Codec.FLOAT.fieldOf("damage").forGetter(RollDataRecord::damage),
                        Codec.FLOAT.fieldOf("mobility").forGetter(RollDataRecord::mobility),
                        Codec.FLOAT.fieldOf("dash_mobility").forGetter(RollDataRecord::dashMobility),
                        Codec.FLOAT.fieldOf("dash_consumption").forGetter(RollDataRecord::dashConsumption),
                        Codec.INT.fieldOf("dash_time").forGetter(RollDataRecord::dashTime)
                ).apply(instance, RollDataRecord::new)
        );
    }

    record SwingDataRecord(
            float mobility,
            float inkConsumption,
            int inkRecoveryCooldown,
            float projectileSpeed,
            int startupTime,
            float projectilePitchCompensation,
            float baseDamage,
            float minDamage,
            int damageDecayStartTick,
            float damageDecayPerTick
    )
    {
        public static final Codec<SwingDataRecord> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.FLOAT.fieldOf("mobility").forGetter(SwingDataRecord::mobility),
                        Codec.FLOAT.fieldOf("ink_consumption").forGetter(SwingDataRecord::inkConsumption),
                        Codec.INT.fieldOf("ink_recovery_cooldown").forGetter(SwingDataRecord::inkRecoveryCooldown),
                        Codec.FLOAT.fieldOf("projectile_speed").forGetter(SwingDataRecord::projectileSpeed),
                        Codec.INT.fieldOf("startup_time").forGetter(SwingDataRecord::startupTime),
                        Codec.FLOAT.fieldOf("projectile_pitch_compensation").forGetter(SwingDataRecord::projectilePitchCompensation),
                        Codec.FLOAT.fieldOf("base_damage").forGetter(SwingDataRecord::baseDamage),
                        Codec.FLOAT.fieldOf("min_damage").forGetter(SwingDataRecord::minDamage),
                        Codec.INT.fieldOf("damage_decay_start_tick").forGetter(SwingDataRecord::damageDecayStartTick),
                        Codec.FLOAT.fieldOf("damage_decay_per_tick").forGetter(SwingDataRecord::damageDecayPerTick)
                ).apply(instance, SwingDataRecord::new)
        );
    }

    record FlingDataRecord(
            float inkConsumption,
            int inkRecoveryCooldown,
            float projectileSpeed,
            int startupTime,
            float baseDamage,
            float minDamage,
            int damageDecayStartTick,
            float damageDecayPerTick
    )
    {
        public static final Codec<FlingDataRecord> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.FLOAT.fieldOf("ink_consumption").forGetter(FlingDataRecord::inkConsumption),
                        Codec.INT.fieldOf("ink_recovery_cooldown").forGetter(FlingDataRecord::inkRecoveryCooldown),
                        Codec.FLOAT.fieldOf("projectile_speed").forGetter(FlingDataRecord::projectileSpeed),
                        Codec.INT.fieldOf("startup_time").forGetter(FlingDataRecord::startupTime),
                        Codec.FLOAT.fieldOf("base_damage").forGetter(FlingDataRecord::baseDamage),
                        Codec.FLOAT.fieldOf("min_damage").forGetter(FlingDataRecord::minDamage),
                        Codec.INT.fieldOf("damage_decay_start_tick").forGetter(FlingDataRecord::damageDecayStartTick),
                        Codec.FLOAT.fieldOf("damage_decay_per_tick").forGetter(FlingDataRecord::damageDecayPerTick)
                ).apply(instance, FlingDataRecord::new)
        );
    }
}
