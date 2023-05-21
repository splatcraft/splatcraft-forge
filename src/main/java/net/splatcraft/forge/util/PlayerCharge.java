package net.splatcraft.forge.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfo;
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

    public static PlayerCharge getCharge(Player player)
    {
        return PlayerInfoCapability.get(player).getPlayerCharge();
    }

    public static void setCharge(Player player, PlayerCharge charge)
    {
        PlayerInfoCapability.get(player).setPlayerCharge(charge);
    }

    public static boolean hasCharge(Player player)
    {
        if (player == null || !PlayerInfoCapability.hasCapability(player))
            return false;
        PlayerInfo capability = PlayerInfoCapability.get(player);
        return capability.getPlayerCharge() != null && capability.getPlayerCharge().charge > 0;
    }

    public static boolean shouldCreateCharge(Player player)
    {
        if (player == null)
        {
            return false;
        }
        PlayerInfo capability = PlayerInfoCapability.get(player);
        return capability.getPlayerCharge() == null;
    }

    public static boolean chargeMatches(Player player, ItemStack stack) {
        return hasCharge(player) && getCharge(player).chargedWeapon.sameItem(stack);
    }

    public static void addChargeValue(Player player, ItemStack stack, float value) {
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

    public static float getChargeValue(Player player, ItemStack stack)
    {
        return chargeMatches(player, stack) ? getCharge(player).charge : 0;
    }

    public static void reset(Player entity) {
        if (shouldCreateCharge(entity)) {
            setCharge(entity, new PlayerCharge(ItemStack.EMPTY, 0));
        } else {
            PlayerCharge.getCharge(entity).reset();
        }
    }

    public static void dischargeWeapon(Player player) {
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
