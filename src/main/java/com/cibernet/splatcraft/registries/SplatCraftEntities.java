package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.entities.classes.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class SplatCraftEntities
{
    public static int currentEntityIdOffset = 0;

    public static void registerEntities()
    {
        registerEntity(EntityNPCSquid.class, "squid");
        registerEntity(EntitySquidBumper.class, "squid_bumper");
        
        registerEntity(EntityInkProjectile.class, "ink_projectile");
        registerEntity(EntityChargerProjectile.class, "charger_projectile");
        registerEntity(EntityBlasterProjectile.class, "blaster_projectile");
    }

    public static void registerEntity(Class<? extends Entity> entityClass, String name) {
        registerEntity(entityClass, name, name, 80, 3, true);
    }

    public static void registerEntity(Class<? extends Entity> entityClass, String name, int eggPrimary, int eggSecondary) {
        registerEntity(entityClass, name, name, 80, 3, true, eggPrimary, eggSecondary);
    }

    public static void registerEntity(Class<? extends Entity> entityClass, String name, String registryName) {
        registerEntity(entityClass, name, registryName, 80, 3, true);
    }

    public static void registerEntity(Class<? extends Entity> entityClass, String name, String registryName, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates) {
        EntityRegistry.registerModEntity(new ResourceLocation(SplatCraft.MODID, registryName), entityClass, SplatCraft.MODID +"."+ name, currentEntityIdOffset, SplatCraft.instance, trackingRange, updateFrequency, sendsVelocityUpdates);
        ++currentEntityIdOffset;
    }

    public static void registerEntity(Class<? extends Entity> entityClass, String name, String registryName, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates, int eggPrimary, int eggSecondary) {
        EntityRegistry.registerModEntity(new ResourceLocation(SplatCraft.MODID, registryName), entityClass, SplatCraft.MODID + name, currentEntityIdOffset, SplatCraft.instance, trackingRange, updateFrequency, sendsVelocityUpdates, eggPrimary, eggSecondary);
        ++currentEntityIdOffset;
    }
}
