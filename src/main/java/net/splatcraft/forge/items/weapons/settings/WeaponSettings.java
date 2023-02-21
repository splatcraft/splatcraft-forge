package net.splatcraft.forge.items.weapons.settings;

public class WeaponSettings {
    public String name;

    public float projectileSize;
    public int projectileLifespan;
    public float projectileSpeed;

    public int firingSpeed;
    public int startupTicks;

    public float groundInaccuracy;
    public float airInaccuracy;

    public float inkConsumption;
    public int inkRecoveryCooldown;

    public float baseDamage;
    public float minDamage;
    public int damageDecayStartTick;
    public float damageDecayPerTick;

    public WeaponSettings(String name) {
        this.name = name;
    }

    public float calculateDamage(int tickCount) {
        int e = tickCount - damageDecayStartTick;
        return Math.max(e > 0 ? baseDamage - (e * damageDecayPerTick) : baseDamage, minDamage);
    }

    public WeaponSettings setName(String setName) {
        this.name = setName;
        return this;
    }

    public WeaponSettings setProjectileSize(float projectileSize) {
        this.projectileSize = projectileSize;
        return this;
    }

    public WeaponSettings setProjectileLifespan(int projectileLifespan) {
        this.projectileLifespan = projectileLifespan;
        return this;
    }

    public WeaponSettings setProjectileSpeed(float projectileSpeed) {
        this.projectileSpeed = projectileSpeed;
        return this;
    }

    public WeaponSettings setFiringSpeed(int firingSpeed) {
        this.firingSpeed = firingSpeed;
        return this;
    }

    public WeaponSettings setStartupTicks(int startupTicks) {
        this.startupTicks = startupTicks;
        return this;
    }

    public WeaponSettings setGroundInaccuracy(float groundInaccuracy) {
        this.groundInaccuracy = groundInaccuracy;
        return this;
    }

    public WeaponSettings setAirInaccuracy(float airInaccuracy) {
        this.airInaccuracy = airInaccuracy;
        return this;
    }

    public WeaponSettings setInkConsumption(float inkConsumption) {
        this.inkConsumption = inkConsumption;
        return this;
    }

    public WeaponSettings setInkRecoveryCooldown(int inkRecoveryCooldown) {
        this.inkRecoveryCooldown = inkRecoveryCooldown;
        return this;
    }

    public WeaponSettings setBaseDamage(float baseDamage) {
        this.baseDamage = baseDamage;
        return this;
    }

    public WeaponSettings setMinDamage(float minDamage) {
        this.minDamage = minDamage;
        return this;
    }

    public WeaponSettings setDamageDecayStartTick(int damageDecayStartTick) {
        this.damageDecayStartTick = damageDecayStartTick;
        return this;
    }

    public WeaponSettings setDamageDecayPerTick(float damageDecayPerTick) {
        this.damageDecayPerTick = damageDecayPerTick;
        return this;
    }
}
