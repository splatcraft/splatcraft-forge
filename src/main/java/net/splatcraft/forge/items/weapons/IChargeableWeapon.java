package net.splatcraft.forge.items.weapons;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IChargeableWeapon
{
    float getDischargeSpeed();

    float getChargeSpeed();

    void onRelease(World levelIn, PlayerEntity playerIn, ItemStack stack, float charge);
}
