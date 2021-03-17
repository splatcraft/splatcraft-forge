package com.cibernet.splatcraft.data.capabilities.inkoverlay;

import com.cibernet.splatcraft.util.PlayerCharge;
import com.cibernet.splatcraft.util.PlayerCooldown;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;

public interface IInkOverlayInfo
{
	int getColor();
	void setColor(int color);

	float getAmount();
	void setAmount(float v);
	void addAmount(float v);

	CompoundNBT writeNBT(CompoundNBT nbt);
	void readNBT(CompoundNBT nbt);

}
