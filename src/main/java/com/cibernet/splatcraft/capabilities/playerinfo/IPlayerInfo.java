package com.cibernet.splatcraft.capabilities.playerinfo;

import com.cibernet.splatcraft.util.PlayerCharge;
import com.cibernet.splatcraft.util.PlayerCooldown;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.vector.Vector3d;

public interface IPlayerInfo
{
	boolean isInitialized();
	void setInitialized(boolean init);
	
	int getColor();
	void setColor(int color);
	
	boolean isSquid();
	void setIsSquid(boolean isSquid);
	
	NonNullList<ItemStack> getMatchInventory();
	void setMatchInventory(NonNullList<ItemStack> inventory);
	
	PlayerCooldown getPlayerCooldown();
	void setPlayerCooldown(PlayerCooldown cooldown);
	
	PlayerCharge getPlayerCharge();
	void setPlayerCharge(PlayerCharge charge);
	
	CompoundNBT writeNBT(CompoundNBT nbt);
	void readNBT(CompoundNBT nbt);
}
