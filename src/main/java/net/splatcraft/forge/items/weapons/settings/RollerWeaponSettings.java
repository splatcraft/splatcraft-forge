package net.splatcraft.forge.items.weapons.settings;

public class RollerWeaponSettings {
    public String name;
    public boolean isBrush;

    public int rollSize;
    public float rollConsumption;
    public int rollInkRecoveryCooldown;
    public float rollDamage;
    public float rollMobility;

    public float dashMobility;
    public float dashConsumption;
    public int dashTime;

    public float swingMobility;
    public float swingConsumption;
    public int swingInkRecoveryCooldown;
    public float swingDamage;
    public float swingProjectileSpeed;
    public int swingTime;

    public float flingConsumption;
    public int flingInkRecoveryCooldown;
    public float flingDamage;
    public float flingProjectileSpeed;
    public int flingTime;

    public RollerWeaponSettings(String name) {
        this.name = name;
    }

    public RollerWeaponSettings setName(String name) {
        this.name = name;
        return this;
    }

    public RollerWeaponSettings setBrush(boolean brush) {
        isBrush = brush;
        return this;
    }

    public RollerWeaponSettings setRollSize(int rollSize) {
        this.rollSize = rollSize;
        return this;
    }

    public RollerWeaponSettings setRollConsumption(float rollConsumption) {
        this.rollConsumption = rollConsumption;
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

    public RollerWeaponSettings setSwingDamage(float swingDamage) {
        this.swingDamage = swingDamage;
        this.flingDamage = swingDamage;
        return this;
    }

    public RollerWeaponSettings setSwingProjectileSpeed(float swingProjectileSpeed) {
        this.swingProjectileSpeed = swingProjectileSpeed;
        this.flingProjectileSpeed = swingProjectileSpeed;
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

    public RollerWeaponSettings setFlingDamage(float flingDamage) {
        this.flingDamage = flingDamage;
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
