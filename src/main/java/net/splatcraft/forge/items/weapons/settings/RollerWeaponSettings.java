package net.splatcraft.forge.items.weapons.settings;

public class RollerWeaponSettings extends AbstractWeaponSettings {
    public String name;
    public boolean isBrush;

    public int rollSize;
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

    public float flingConsumption;
    public int flingInkRecoveryCooldown;
    public float flingBaseDamage;
    public int flingDamageDecayStartTick;
    public float flingDamageDecayPerTick;
    public float flingMinDamage;
    public float flingProjectileSpeed;
    public int flingTime;

    public RollerWeaponSettings(String name) {
        this.name = name;
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
}
