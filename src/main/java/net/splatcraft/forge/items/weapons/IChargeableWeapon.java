package net.splatcraft.forge.items.weapons;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IChargeableWeapon {
    int getDischargeTicks(ItemStack stack);
    int getDecayTicks(ItemStack stack);

    void onReleaseCharge(Level level, Player player, ItemStack stack, float charge);
}
