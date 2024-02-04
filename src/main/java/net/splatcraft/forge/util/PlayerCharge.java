package net.splatcraft.forge.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfo;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.items.weapons.IChargeableWeapon;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.c2s.UpdateChargeStatePacket;

public class PlayerCharge {
    private static final Map<UUID, Boolean> hasChargeServerPlayerMap = new HashMap<>();

    public ItemStack chargedWeapon;
    public float charge;
    public float maxCharge;
    public float prevCharge;
    public int dischargedTicks;
    public int prevDischargedTicks;
    public int totalCharges;
    public boolean storePartial;

    public PlayerCharge(ItemStack stack, float charge, boolean chargeDecay)
    {
        this(stack, charge, chargeDecay, 1);
    }

    public PlayerCharge(ItemStack stack, float charge, boolean chargeDecay, int totalCharges)
    {
        this.chargedWeapon = stack;
        this.charge = charge;
        this.maxCharge = charge;
        this.totalCharges = totalCharges;
        this.storePartial = chargeDecay;
    }

    public static PlayerCharge getCharge(Player player) {
        return PlayerInfoCapability.get(player).getPlayerCharge();
    }

    public static void setCharge(Player player, PlayerCharge charge) {
        PlayerInfoCapability.get(player).setPlayerCharge(charge);
    }

    public static boolean hasCharge(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Attempted to retrieve charge for a null player");
        }

        if (player instanceof ServerPlayer serverPlayer) {
            return hasChargeServerPlayerMap.getOrDefault(serverPlayer.getUUID(), false);
        }

        if (!PlayerInfoCapability.hasCapability(player)) {
            return false;
        }

        PlayerInfo capability = PlayerInfoCapability.get(player);
        return capability.getPlayerCharge() != null && capability.getPlayerCharge().charge > 0;
    }


    public static boolean shouldCreateCharge(Player player) {
        if (player == null) {
            return false;
        }
        PlayerInfo capability = PlayerInfoCapability.get(player);
        return capability.getPlayerCharge() == null;
    }

    public static boolean chargeMatches(Player player, ItemStack stack) {
        return hasCharge(player) && getCharge(player).chargedWeapon.sameItem(stack);
    }

    public static void addChargeValue(Player player, ItemStack stack, float value, boolean storePartial)
    {
        addChargeValue(player, stack, value, storePartial, 1);
    }
    public static void addChargeValue(Player player, ItemStack stack, float value, boolean storePartial, int totalCharges)
    {
        if (value < 0.0f) {
            throw new IllegalArgumentException("Attempted to add negative charge: " + value);
        }
        if (shouldCreateCharge(player)) {
            setCharge(player, new PlayerCharge(stack, 0, storePartial, totalCharges));
        }

        PlayerCharge charge = getCharge(player);
        if ((charge.prevCharge > charge.charge || charge.charge <= 0.0f) && value > 0.0f && player.equals(Minecraft.getInstance().player)) {
            SplatcraftPacketHandler.sendToServer(new UpdateChargeStatePacket(true));
        }

        if (chargeMatches(player, stack)) {
            charge.prevCharge = charge.charge;
            charge.charge = Math.max(0.0f, Math.min(charge.totalCharges, charge.charge + value));
            charge.maxCharge = charge.charge;

            charge.dischargedTicks = 0;
            charge.prevDischargedTicks = 0;
        } else {
            setCharge(player, new PlayerCharge(stack, value, storePartial, totalCharges));
        }
    }

    public static float getChargeValue(Player player, ItemStack stack) {
        return chargeMatches(player, stack) ? getCharge(player).charge : 0;
    }

    public float getDischargeValue(float partialTicks)
    {
        if(chargedWeapon.getItem() instanceof IChargeableWeapon chargeable)
        {
            float maxDischargeTicks = chargeable.getDischargeTicks(chargedWeapon);
            return maxDischargeTicks <= 0 ? 1 : 1 - Mth.lerp(partialTicks, prevDischargedTicks / maxDischargeTicks, dischargedTicks / maxDischargeTicks);
        }

        return 1;
    }

    public static void dischargeWeapon(Player player)
    {
        if (!player.level.isClientSide || !hasCharge(player))
            return;

        PlayerCharge charge = getCharge(player);
        Item dischargeItem = charge.chargedWeapon.getItem();

        charge.prevDischargedTicks = charge.dischargedTicks;
        charge.prevCharge = charge.charge;


        if (dischargeItem instanceof IChargeableWeapon chargeable)
        {
            if(!PlayerInfoCapability.isSquid(player))
            {
                int decayTicks = chargeable.getDecayTicks(charge.chargedWeapon);
                if(decayTicks > 0)
                {
                    charge.charge = Math.max(0, charge.charge - 1f/decayTicks);
                    return;
                }
            }
            else if((charge.storePartial || charge.charge >= 1.0f) &&
                    charge.dischargedTicks < chargeable.getDischargeTicks(charge.chargedWeapon))
            {
                charge.dischargedTicks++;
                return;
            }

        }

        charge.charge = 0f;
        charge.prevCharge = 0;
        charge.dischargedTicks = 0;

        if(player.equals(Minecraft.getInstance().player))
            SplatcraftPacketHandler.sendToServer(new UpdateChargeStatePacket(false));
    }

    public static void updateServerMap(Player player, boolean hasCharge) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            throw new IllegalStateException("Client attempted to modify server charge map");
        }

        if (hasChargeServerPlayerMap.containsKey(serverPlayer.getUUID()) && hasChargeServerPlayerMap.get(serverPlayer.getUUID()) == hasCharge) {
            throw new IllegalStateException("Charge state did not change: " + hasCharge);
        }

        hasChargeServerPlayerMap.put(serverPlayer.getUUID(), hasCharge);
    }

    public void reset() {
        chargedWeapon = ItemStack.EMPTY;
        charge = 0;
        prevCharge = 0;
        dischargedTicks = 0;
        prevDischargedTicks = 0;
    }

    @Override
    public String toString() {
        return "PlayerCharge: [" + chargedWeapon + " x " + charge + "] (" + super.toString() + ")";
    }
}
