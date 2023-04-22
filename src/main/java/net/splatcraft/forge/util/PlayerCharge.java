package net.splatcraft.forge.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.splatcraft.forge.data.capabilities.playerinfo.IPlayerInfo;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.items.weapons.ChargerItem;

public class PlayerCharge
{
    public ItemStack chargedWeapon;
    public float charge;
    public boolean discharging;

    public PlayerCharge(ItemStack stack, float charge)
    {
        this.chargedWeapon = stack;
        this.charge = charge;
    }

    public static PlayerCharge getCharge(PlayerEntity player)
    {
        return PlayerInfoCapability.get(player).getPlayerCharge();
    }

    public static void setCharge(PlayerEntity player, PlayerCharge charge)
    {
        PlayerInfoCapability.get(player).setPlayerCharge(charge);
    }

    public static boolean hasCharge(PlayerEntity player)
    {
        if (player == null || !PlayerInfoCapability.hasCapability(player))
            return false;
        IPlayerInfo capability = PlayerInfoCapability.get(player);
        return capability.getPlayerCharge() != null && capability.getPlayerCharge().charge > 0;
    }

    public static boolean shouldCreateCharge(PlayerEntity player)
    {
        if (player == null)
        {
            return false;
        }
        IPlayerInfo capability = PlayerInfoCapability.get(player);
        return capability.getPlayerCharge() == null;
    }

    public static boolean chargeMatches(PlayerEntity player, ItemStack stack) {
        return hasCharge(player) && getCharge(player).chargedWeapon.sameItem(stack);
    }

    public static void addChargeValue(PlayerEntity player, ItemStack stack, float value) {
        if (shouldCreateCharge(player)) {
            setCharge(player, new PlayerCharge(stack, 0));
        }

        PlayerCharge charge = getCharge(player);
        charge.discharging = false;

        if (chargeMatches(player, stack)) {
            charge.charge = Math.max(0, Math.min(charge.charge + value, 1f));
        } else {
            setCharge(player, new PlayerCharge(stack, value));
        }
    }

    public static float getChargeValue(PlayerEntity player, ItemStack stack)
    {
        return chargeMatches(player, stack) ? getCharge(player).charge : 0;
    }

    public static void reset(PlayerEntity entity)
    {
        if (shouldCreateCharge(entity))
        {
            setCharge(entity, new PlayerCharge(ItemStack.EMPTY, 0));
        } else
        {
            PlayerCharge.getCharge(entity).reset();
        }
    }

    public static void dischargeWeapon(PlayerEntity player) {
        if (!player.level.isClientSide || !hasCharge(player)) {
            return;
        }
        PlayerCharge charge = getCharge(player);
        Item dischargeItem = charge.chargedWeapon.getItem();

        if (!(dischargeItem instanceof ChargerItem) || !charge.discharging && charge.charge < 1.0f) {
            charge.charge = 0f;
        } else {
            charge.charge = Math.max(0, charge.charge - ((ChargerItem) dischargeItem).dischargeSpeed);
            charge.discharging = true;
        }
    }

    @Override
    public String toString()
    {
        return "PlayerCharge: [" + chargedWeapon + " x " + charge + "] (" + super.toString() + ")";
    }

    public void reset()
    {
        chargedWeapon = ItemStack.EMPTY;
        charge = 0;
    }
}
