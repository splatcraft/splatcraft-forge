package com.cibernet.splatcraft.data.capabilities.inkoverlay;

import com.cibernet.splatcraft.registries.SplatcraftInkColors;
import com.cibernet.splatcraft.util.ColorUtils;
import com.cibernet.splatcraft.util.PlayerCharge;
import com.cibernet.splatcraft.util.PlayerCooldown;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;

public class InkOverlayInfo implements IInkOverlayInfo
{
	private int color = ColorUtils.DEFAULT;
	private float amount = 0;

	public InkOverlayInfo()
	{
	}

	@Override
	public int getColor() {
		return color;
	}

	@Override
	public void setColor(int color) {
		this.color = color;
	}

	@Override
	public float getAmount() {
		return amount;
	}

	@Override
	public void setAmount(float v) {
		amount = v;
	}
	@Override
	public void addAmount(float v) {
		amount += v;
	}

	@Override
	public CompoundNBT writeNBT(CompoundNBT nbt)
	{
		nbt.putInt("Color",getColor());
		nbt.putFloat("Amount",getAmount());
		return nbt;
	}
	
	@Override
	public void readNBT(CompoundNBT nbt)
	{
		setColor(nbt.getInt("Color"));
		setAmount(nbt.getFloat("Amount"));
	}

	@Override
	public String toString() {
		return "Color: " + color + " Amount: " + amount;
	}
}
