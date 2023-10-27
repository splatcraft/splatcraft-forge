package net.splatcraft.forge.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.splatcraft.forge.commands.SuperJumpCommand;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfo;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;

public class PlayerCooldown
{
    final int maxTime;
    final int slotIndex;
    final InteractionHand hand;
    final boolean canMove;
    final boolean forceCrouch;
    final boolean preventWeaponUse;
    final boolean isGrounded;
    public ItemStack storedStack;
    int time;

    public static final int OVERLOAD_LIMIT = -28800;

    public PlayerCooldown(ItemStack stack, int time, int maxTime, int slotIndex, InteractionHand hand, boolean canMove, boolean forceCrouch, boolean preventWeaponUse, boolean isGrounded) {
        this.storedStack = stack;
        this.time = ++time;
        this.maxTime = maxTime;
        this.slotIndex = slotIndex;
        this.hand = hand;
        this.canMove = canMove;
        this.forceCrouch = forceCrouch;
        this.preventWeaponUse = preventWeaponUse;
        this.isGrounded = isGrounded;
    }

    public PlayerCooldown(ItemStack stack, int time, int slotIndex, InteractionHand hand, boolean canMove, boolean forceCrouch, boolean preventWeaponUse, boolean isGrounded) {
        this(stack, time, time, slotIndex, hand, canMove, forceCrouch, preventWeaponUse, isGrounded);
    }

    public PlayerCooldown(CompoundTag nbt)
    {
        this(ItemStack.of(nbt.getCompound("StoredStack")), nbt.getInt("Time"), nbt.getInt("MaxTime"), nbt.getInt("SlotIndex"), nbt.getBoolean("MainHand") ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, nbt.getBoolean("CanMove"), nbt.getBoolean("ForceCrouch"), nbt.getBoolean("PreventWeaponUse"), nbt.getBoolean("IsGrounded"));
        if (nbt.contains("StoredStack"))
            storedStack = ItemStack.of(nbt.getCompound("StoredStack"));

    }

    public static PlayerCooldown readNBT(CompoundTag nbt)
    {
        if(nbt.getBoolean("SuperJump"))
            return new SuperJumpCommand.SuperJump(nbt);
        else return new PlayerCooldown(nbt);

        //PlayerCooldown result = new PlayerCooldown(ItemStack.of(nbt.getCompound("StoredStack")), nbt.getInt("Time"), nbt.getInt("MaxTime"), nbt.getInt("SlotIndex"), nbt.getBoolean("MainHand") ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, nbt.getBoolean("CanMove"), nbt.getBoolean("ForceCrouch"), nbt.getBoolean("PreventWeaponUse"), nbt.getBoolean("IsGrounded"));
    }



    public static PlayerCooldown getPlayerCooldown(Player player)
    {
        return PlayerInfoCapability.get(player).getPlayerCooldown();
    }

    public static void setPlayerCooldown(Player player, PlayerCooldown playerCooldown)
    {
        PlayerInfoCapability.get(player).setPlayerCooldown(playerCooldown);
    }

    public static PlayerCooldown setCooldownTime(Player player, int time)
    {
        PlayerInfo capability = PlayerInfoCapability.get(player);

        if (capability.getPlayerCooldown() == null)
        {
            return null;
        } else
        {
            capability.getPlayerCooldown().setTime(time);
        }

        return capability.getPlayerCooldown();
    }

    public static PlayerCooldown shrinkCooldownTime(Player player, int time)
    {
        if(!hasOverloadedPlayerCooldown(player))
            return null;
        PlayerCooldown cooldown = setCooldownTime(player, Math.max(OVERLOAD_LIMIT, PlayerInfoCapability.get(player).getPlayerCooldown().getTime() - time));
        return hasPlayerCooldown(player) ? cooldown : null;
    }

    public static boolean hasPlayerCooldown(Player player)
    {
        if(player == null || !PlayerInfoCapability.hasCapability(player))
            return false;
        PlayerCooldown cooldown = PlayerInfoCapability.get(player).getPlayerCooldown();
        return cooldown != null && cooldown.getTime() > 0;
    }

    public static boolean hasOverloadedPlayerCooldown(Player player)
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
    public InteractionHand getHand()
    {
        return hand;
    }

    public CompoundTag writeNBT(CompoundTag nbt)
    {
        nbt.putInt("Time", time);
        nbt.putInt("MaxTime", maxTime);
        nbt.putInt("SlotIndex", slotIndex);
        nbt.putBoolean("CanMove", canMove);
        nbt.putBoolean("ForceCrouch", forceCrouch);
        nbt.putBoolean("PreventWeaponUse", preventWeaponUse);
        nbt.putBoolean("IsGrounded", isGrounded);
        nbt.putBoolean("MainHand", hand.equals(InteractionHand.MAIN_HAND));
        if (storedStack.getItem() != Items.AIR) {
            nbt.put("StoredStack", storedStack.serializeNBT());
        }

        return nbt;
    }
}
