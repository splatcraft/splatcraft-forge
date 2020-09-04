package com.cibernet.splatcraft.util;

import com.cibernet.splatcraft.capabilities.playerinfo.IPlayerInfo;
import com.cibernet.splatcraft.capabilities.playerinfo.PlayerInfoCapability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public class PlayerCooldown
{
	int time;
	final int maxTime;
	final int slotIndex;
	final boolean canMove;
	final boolean forceCrouch;
	final boolean preventWeaponUse;
	
	public PlayerCooldown(int time, int maxTime, int slotIndex, boolean canMove, boolean forceCrouch, boolean preventWeaponUse)
	{
		this.time = ++time;
		this.maxTime = maxTime;
		this.slotIndex = slotIndex;
		this.canMove = canMove;
		this.forceCrouch = forceCrouch;
		this.preventWeaponUse = preventWeaponUse;
		System.out.println(preventWeaponUse);
	}
	
	public PlayerCooldown(int time, int slotIndex, boolean canMove, boolean forceCrouch, boolean preventWeaponUse)
	{
		this(time, time, slotIndex, canMove, forceCrouch, preventWeaponUse);
	}
	
	public boolean canMove() {return canMove;}
	public boolean forceCrouch() {return forceCrouch;}
	public boolean preventWeaponUse() {return preventWeaponUse;}
	public int getTime() {return time;}
	public int getMaxTime() {return maxTime;}
	public int getSlotIndex() {return slotIndex;}
	public PlayerCooldown setTime(int v) {time = v; return this;}
	public PlayerCooldown shrinkTime(int v) {time -= v; return this;}
	
	public static PlayerCooldown readNBT(CompoundNBT nbt)
	{
		return new PlayerCooldown(nbt.getInt("Time"), nbt.getInt("MaxTime"), nbt.getInt("SlotIndex"), nbt.getBoolean("CanMove"), nbt.getBoolean("ForceCrouch"), nbt.getBoolean("PreventWeaponUse"));
	}
	
	public CompoundNBT writeNBT(CompoundNBT nbt)
	{
		nbt.putInt("Time", time);
		nbt.putInt("MaxTime", maxTime);
		nbt.putInt("SlotIndex", slotIndex);
		nbt.putBoolean("CanMove", canMove);
		nbt.putBoolean("ForceCrouch", forceCrouch);
		nbt.putBoolean("PreventWeaponUse", preventWeaponUse);
		return nbt;
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
		
		if(capability.getPlayerCooldown() == null)
		{
			return null;
		}
		else capability.getPlayerCooldown().setTime(time);
		
		return capability.getPlayerCooldown();
	}
	
	public static PlayerCooldown shrinkCooldownTime(PlayerEntity player, int time)
	{
		return hasPlayerCooldown(player) ? setCooldownTime(player, PlayerInfoCapability.get(player).getPlayerCooldown().getTime()-time) : null;
	}
	
	public static boolean hasPlayerCooldown(PlayerEntity player)
	{
		if(PlayerInfoCapability.get(player) == null) return false;
		PlayerCooldown cooldown = PlayerInfoCapability.get(player).getPlayerCooldown();
		return cooldown != null && cooldown.getTime() > 0;
	}
}
