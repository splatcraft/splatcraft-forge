package net.splatcraft.forge.util;

import net.splatcraft.forge.data.capabilities.playerinfo.IPlayerInfo;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class PlayerCooldown
{
    final int maxTime;
    final int slotIndex;
    final Hand hand;
    final boolean canMove;
    final boolean forceCrouch;
    final boolean preventWeaponUse;
    final boolean isGrounded;
    public Item storedItem = Items.AIR;
    int time;

    public static final int OVERLOAD_LIMIT = -28800;

    public PlayerCooldown(int time, int maxTime, int slotIndex, Hand hand, boolean canMove, boolean forceCrouch, boolean preventWeaponUse, boolean isGrounded)
    {
        this.time = ++time;
        this.maxTime = maxTime;
        this.slotIndex = slotIndex;
        this.hand = hand;
        this.canMove = canMove;
        this.forceCrouch = forceCrouch;
        this.preventWeaponUse = preventWeaponUse;
        this.isGrounded = isGrounded;
    }

    public PlayerCooldown(int time, int slotIndex, Hand hand, boolean canMove, boolean forceCrouch, boolean preventWeaponUse, boolean isGrounded)
    {
        this(time, time, slotIndex, hand, canMove, forceCrouch, preventWeaponUse, isGrounded);
    }

    public static PlayerCooldown readNBT(CompoundNBT nbt)
    {
        PlayerCooldown result = new PlayerCooldown(nbt.getInt("Time"), nbt.getInt("MaxTime"), nbt.getInt("SlotIndex"), nbt.getBoolean("MainHand") ? Hand.MAIN_HAND : Hand.OFF_HAND, nbt.getBoolean("CanMove"), nbt.getBoolean("ForceCrouch"), nbt.getBoolean("PreventWeaponUse"), nbt.getBoolean("IsGrounded"));
        if (nbt.contains("StoredItem"))
        {
            result.storedItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(nbt.getString("StoredItem")));
        }
        return result;
    }

    public static PlayerCooldown getPlayerCooldown(PlayerEntity player)
    {
        return PlayerInfoCapability.get(player).getPlayerCooldown();
    }

    public static void setPlayerCooldown(PlayerEntity player, PlayerCooldown playerCooldown)
    {
        PlayerInfoCapability.get(player).setPlayerCooldown(playerCooldown);
    }

    public static PlayerCooldown setCooldownTime(PlayerEntity player, int time)
    {
        IPlayerInfo capability = PlayerInfoCapability.get(player);

        if (capability.getPlayerCooldown() == null)
        {
            return null;
        } else
        {
            capability.getPlayerCooldown().setTime(time);
        }

        return capability.getPlayerCooldown();
    }

    public static PlayerCooldown shrinkCooldownTime(PlayerEntity player, int time)
    {
        if(!hasOverloadedPlayerCooldown(player))
            return null;
        PlayerCooldown cooldown = setCooldownTime(player, Math.max(OVERLOAD_LIMIT, PlayerInfoCapability.get(player).getPlayerCooldown().getTime() - time));
        return hasPlayerCooldown(player) ? cooldown : null;
    }

    public static boolean hasPlayerCooldown(PlayerEntity player)
    {
        if(player == null || !PlayerInfoCapability.hasCapability(player))
            return false;
        PlayerCooldown cooldown = PlayerInfoCapability.get(player).getPlayerCooldown();
        return cooldown != null && cooldown.getTime() > 0;
    }

    public static boolean hasOverloadedPlayerCooldown(PlayerEntity player)
    {
        if(player == null || !PlayerInfoCapability.hasCapability(player))
            return false;
        PlayerCooldown cooldown = PlayerInfoCapability.get(player).getPlayerCooldown();
        return cooldown != null;
    }

    public boolean canMove()
    {
        return canMove;
    }

    public boolean forceCrouch()
    {
        return forceCrouch;
    }

    public boolean preventWeaponUse()
    {
        return preventWeaponUse;
    }

    public boolean isGrounded()
    {
        return isGrounded;
    }

    public int getTime()
    {
        return time;
    }

    public PlayerCooldown setTime(int v)
    {
        time = v;
        return this;
    }

    public int getMaxTime()
    {
        return maxTime;
    }

    public int getSlotIndex()
    {
        return slotIndex;
    }
    public Hand getHand()
    {
        return hand;
    }

    public CompoundNBT writeNBT(CompoundNBT nbt)
    {
        nbt.putInt("Time", time);
        nbt.putInt("MaxTime", maxTime);
        nbt.putInt("SlotIndex", slotIndex);
        nbt.putBoolean("CanMove", canMove);
        nbt.putBoolean("ForceCrouch", forceCrouch);
        nbt.putBoolean("PreventWeaponUse", preventWeaponUse);
        nbt.putBoolean("IsGrounded", isGrounded);
        nbt.putBoolean("MainHand", hand.equals(Hand.MAIN_HAND));
        if (storedItem != Items.AIR)
        {
            nbt.putString("StoredItem", Objects.requireNonNull(storedItem.getRegistryName()).toString());
        }

        return nbt;
    }
}
