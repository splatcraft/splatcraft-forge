package net.splatcraft.forge.items.weapons.settings;

public abstract class AbstractWeaponSettings {
    public String name;
    public boolean secret;

    public abstract float calculateDamage(int tickCount, boolean airborne, float charge, boolean isOnRollCooldown);

    public abstract float getMinDamage();
}
