package com.cibernet.splatcraft.entities.subs;

import com.cibernet.splatcraft.registries.SplatcraftItems;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class SplatBombEntity extends AbstractSubWeaponEntity
{
    public SplatBombEntity(EntityType<? extends AbstractSubWeaponEntity> type, World level) {
        super(type, level);
    }

    @Override
    protected Item getDefaultItem() {
        return SplatcraftItems.splatBomb;
    }
}
